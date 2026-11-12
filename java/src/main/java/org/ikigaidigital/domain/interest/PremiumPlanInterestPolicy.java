package org.ikigaidigital.domain.interest;

/** Premium plan: 5% per year, paid monthly, only once the deposit is older than 45 days. */
public final class PremiumPlanInterestPolicy extends AnnualRateInterestPolicy {

    public static final String PLAN_TYPE = "premium";
    private static final double ANNUAL_RATE = 0.05;

    /**
     * ASSUMPTION: "interest starts after 45 days" means from day 46 on, matching the original
     * implementation ({@code days > 45}). This rule is stricter than, and therefore subsumes, the 30-day grace period.
     */
    static final int INTEREST_STARTS_AFTER_DAYS = 45;

    public PremiumPlanInterestPolicy() {
        super(PLAN_TYPE, ANNUAL_RATE);
    }

    @Override
    protected boolean accruesInterestOn(int days) {
        return days > INTEREST_STARTS_AFTER_DAYS;
    }
}
