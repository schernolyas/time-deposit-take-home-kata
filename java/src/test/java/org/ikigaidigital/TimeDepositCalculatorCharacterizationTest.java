package org.ikigaidigital;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Golden-master test: the current {@link TimeDepositCalculator} must produce bit-for-bit the same
 * balances as the original implementation ({@link LegacyTimeDepositCalculator}) for a large,
 * deterministic sample of inputs, including every boundary day of every plan.
 */
class TimeDepositCalculatorCharacterizationTest {

    private static final long SEED = 20261112L;
    private static final int RANDOM_SAMPLES = 20_000;
    private static final String[] PLAN_TYPES = {"basic", "student", "premium", "unknown", "Basic", ""};
    private static final int[] BOUNDARY_DAYS = {0, 1, 29, 30, 31, 32, 44, 45, 46, 47, 364, 365, 366, 367, 1000};

    @Test
    @DisplayName("matches the legacy implementation on boundary days for every plan type")
    void matchesLegacyOnBoundaries() {
        double[] balances = {0.0, 0.01, 6.0, 999.99, 1234567.00, 10_000_000.55, -500.00};
        List<TimeDeposit> sample = new ArrayList<>();
        int id = 1;
        for (String planType : PLAN_TYPES) {
            for (int days : BOUNDARY_DAYS) {
                for (double balance : balances) {
                    sample.add(new TimeDeposit(id++, planType, balance, days));
                }
            }
        }
        assertSameAsLegacy(sample);
    }

    @Test
    @DisplayName("matches the legacy implementation on a large random sample")
    void matchesLegacyOnRandomSample() {
        Random random = new Random(SEED);
        List<TimeDeposit> sample = new ArrayList<>();
        for (int id = 1; id <= RANDOM_SAMPLES; id++) {
            String planType = PLAN_TYPES[random.nextInt(PLAN_TYPES.length)];
            long cents = (long) (random.nextDouble() * 1_000_000_000L);
            int days = random.nextInt(800);
            sample.add(new TimeDeposit(id, planType, cents / 100.0, days));
        }
        assertSameAsLegacy(sample);
    }

    @Test
    @DisplayName("matches the legacy implementation when the same deposits are updated repeatedly")
    void matchesLegacyOverManyMonths() {
        List<TimeDeposit> actual = new ArrayList<>();
        List<TimeDeposit> expected = new ArrayList<>();
        int id = 1;
        for (String planType : PLAN_TYPES) {
            for (int days : BOUNDARY_DAYS) {
                actual.add(new TimeDeposit(id, planType, 98_765.43, days));
                expected.add(new TimeDeposit(id, planType, 98_765.43, days));
                id++;
            }
        }
        TimeDepositCalculator calculator = new TimeDepositCalculator();
        LegacyTimeDepositCalculator legacy = new LegacyTimeDepositCalculator();
        for (int month = 0; month < 36; month++) {
            calculator.updateBalance(actual);
            legacy.updateBalance(expected);
        }
        assertBalancesEqual(actual, expected);
    }

    private static void assertSameAsLegacy(List<TimeDeposit> sample) {
        List<TimeDeposit> actual = copyOf(sample);
        List<TimeDeposit> expected = copyOf(sample);

        new TimeDepositCalculator().updateBalance(actual);
        new LegacyTimeDepositCalculator().updateBalance(expected);

        assertBalancesEqual(actual, expected);
    }

    private static void assertBalancesEqual(List<TimeDeposit> actual, List<TimeDeposit> expected) {
        assertThat(actual).hasSameSizeAs(expected);
        for (int i = 0; i < actual.size(); i++) {
            TimeDeposit a = actual.get(i);
            TimeDeposit e = expected.get(i);
            // Compare raw bits: even a 1-ulp difference is a behaviour change.
            assertThat(Double.doubleToLongBits(a.getBalance()))
                    .as("balance of deposit id=%d plan=%s days=%d (actual=%s expected=%s)",
                            e.getId(), e.getPlanType(), e.getDays(), a.getBalance(), e.getBalance())
                    .isEqualTo(Double.doubleToLongBits(e.getBalance()));
        }
    }

    private static List<TimeDeposit> copyOf(List<TimeDeposit> deposits) {
        List<TimeDeposit> copy = new ArrayList<>(deposits.size());
        for (TimeDeposit d : deposits) {
            copy.add(new TimeDeposit(d.getId(), d.getPlanType(), d.getBalance(), d.getDays()));
        }
        return copy;
    }
}
