package org.ikigaidigital;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Readable specification of {@link TimeDepositCalculator#updateBalance(List)}.
 * Expected values were obtained from the original implementation (see {@link LegacyTimeDepositCalculator}).
 * Rates are annual and applied monthly: {@code balance * annualRate / 12}, interest rounded to cents.
 */
class TimeDepositCalculatorTest {

    private static final double BALANCE = 1_234_567.00;

    private final TimeDepositCalculator calculator = new TimeDepositCalculator();

    @Test
    @DisplayName("basic: no interest during the first 30 days")
    void basic_day30_noInterest() {
        assertThat(balanceAfterUpdate("basic", BALANCE, 30)).isEqualTo(1_234_567.00);
    }

    @Test
    @DisplayName("basic: 1% p.a. from day 31")
    void basic_day31_onePercentPerYear() {
        assertThat(balanceAfterUpdate("basic", BALANCE, 31)).isEqualTo(1_235_595.81);
    }

    @Test
    @DisplayName("student: 3% p.a. from day 31")
    void student_day31_threePercentPerYear() {
        assertThat(balanceAfterUpdate("student", BALANCE, 31)).isEqualTo(1_237_653.42);
    }

    @Test
    @DisplayName("student: still earns interest on day 365")
    void student_day365_earnsInterest() {
        assertThat(balanceAfterUpdate("student", BALANCE, 365)).isEqualTo(1_237_653.42);
    }

    @Test
    @DisplayName("student: no interest after one year (day 366)")
    void student_day366_noInterest() {
        assertThat(balanceAfterUpdate("student", BALANCE, 366)).isEqualTo(1_234_567.00);
    }

    @Test
    @DisplayName("premium: no interest up to day 45")
    void premium_day45_noInterest() {
        assertThat(balanceAfterUpdate("premium", BALANCE, 45)).isEqualTo(1_234_567.00);
    }

    @Test
    @DisplayName("premium: 5% p.a. from day 46")
    void premium_day46_fivePercentPerYear() {
        assertThat(balanceAfterUpdate("premium", BALANCE, 46)).isEqualTo(1_239_711.03);
    }

    @Test
    @DisplayName("unknown plan type: balance is unchanged")
    void unknownPlan_noInterest() {
        assertThat(balanceAfterUpdate("gold", BALANCE, 100)).isEqualTo(1_234_567.00);
    }

    @Test
    @DisplayName("rounding uses the exact binary value of the interest (0.015 in double rounds to 0.01)")
    void rounding_usesExactBinaryValue() {
        // 6.00 * 0.03 / 12 is 0.01499999999999999944... in binary floating point,
        // so HALF_UP gives 0.01, not 0.02. This pins the legacy new BigDecimal(double) behaviour.
        assertThat(balanceAfterUpdate("student", 6.00, 100)).isEqualTo(6.01);
    }

    @Test
    @DisplayName("updates every deposit in the list independently")
    void updatesAllDeposits() {
        List<TimeDeposit> deposits = List.of(
                new TimeDeposit(1, "basic", BALANCE, 31),
                new TimeDeposit(2, "student", BALANCE, 366),
                new TimeDeposit(3, "premium", BALANCE, 46));

        calculator.updateBalance(deposits);

        assertThat(deposits)
                .extracting(TimeDeposit::getBalance)
                .containsExactly(1_235_595.81, 1_234_567.00, 1_239_711.03);
    }

    private double balanceAfterUpdate(String planType, double balance, int days) {
        TimeDeposit deposit = new TimeDeposit(1, planType, balance, days);
        calculator.updateBalance(List.of(deposit));
        return deposit.getBalance();
    }
}
