package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;

/**
 * Template for the common shape of today's plans: a fixed annual rate paid monthly
 * ({@code balance * annualRate / 12}), nothing during an initial grace period, and an optional
 * plan-specific eligibility rule on the deposit's age ({@link #accruesInterestOn(int)}).
 */
public abstract class AnnualRateInterestPolicy implements InterestPolicy {

    /** "No interest is applied for the first 30 days for any existing plans." */
    public static final int DEFAULT_GRACE_PERIOD_DAYS = 30;

    private static final int MONTHS_PER_YEAR = 12;

    private final String planType;
    private final double annualRate;
    private final int gracePeriodDays;

    protected AnnualRateInterestPolicy(String planType, double annualRate) {
        this(planType, annualRate, DEFAULT_GRACE_PERIOD_DAYS);
    }

    protected AnnualRateInterestPolicy(String planType, double annualRate, int gracePeriodDays) {
        this.planType = planType;
        this.annualRate = annualRate;
        this.gracePeriodDays = gracePeriodDays;
    }

    @Override
    public final String planType() {
        return planType;
    }

    @Override
    public final double monthlyInterest(TimeDeposit deposit) {
        int days = deposit.getDays();
        if (days <= gracePeriodDays || !accruesInterestOn(days)) {
            return 0;
        }
        // Same double arithmetic and operation order as the original implementation:
        // (balance * annualRate) / 12. Do not reorder - it changes results in the last bit.
        return deposit.getBalance() * annualRate / MONTHS_PER_YEAR;
    }

    /**
     * Plan-specific rule evaluated after the grace period. Default: always accrues.
     *
     * @param days age of the deposit in days
     */
    protected boolean accruesInterestOn(int days) {
        return true;
    }
}
