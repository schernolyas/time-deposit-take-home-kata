package org.ikigaidigital;

import org.ikigaidigital.domain.interest.InterestPolicies;
import org.ikigaidigital.domain.interest.InterestPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Applies one month of interest to time deposits.
 * <p>
 * The plan-specific rules live in {@link InterestPolicy} strategies; this class only resolves the policy,
 * rounds the interest to cents and adds it to the balance. The public contract
 * ({@link #TimeDepositCalculator()} and {@link #updateBalance(List)}) and the numeric results are unchanged
 * from the original implementation - see {@code TimeDepositCalculatorCharacterizationTest}.
 */
public class TimeDepositCalculator {

    private static final int CENTS_SCALE = 2;

    private final InterestPolicies interestPolicies;

    public TimeDepositCalculator() {
        this(InterestPolicies.defaults());
    }

    public TimeDepositCalculator(InterestPolicies interestPolicies) {
        this.interestPolicies = Objects.requireNonNull(interestPolicies, "interestPolicies");
    }

    public void updateBalance(List<TimeDeposit> xs) {
        xs.stream().forEach(this::applyMonthlyInterest);
    }

    private void applyMonthlyInterest(TimeDeposit deposit) {
        double interest = interestPolicies.forPlanType(deposit.getPlanType()).monthlyInterest(deposit);
        deposit.setBalance(deposit.getBalance() + roundToCents(interest));
    }

    /**
     * Deliberately {@code new BigDecimal(double)} (the exact binary value) and NOT {@code BigDecimal.valueOf}:
     * e.g. 0.015 as a double is slightly below 0.015 and must round to 0.01, as it always did.
     */
    private static double roundToCents(double amount) {
        return new BigDecimal(amount).setScale(CENTS_SCALE, RoundingMode.HALF_UP).doubleValue();
    }
}
