package org.ikigaidigital.domain.interest;

/** Student plan: 3% per year, paid monthly, after the 30-day grace period and only during the first year. */
public final class StudentPlanInterestPolicy extends AnnualRateInterestPolicy {

    public static final String PLAN_TYPE = "student";
    private static final double ANNUAL_RATE = 0.03;

    /**
     * ASSUMPTION: "no interest after 1 year" means a deposit earns interest up to and including day 365
     * and nothing from day 366 on - this is exactly what the original implementation does ({@code days < 366}).
     */
    static final int LAST_DAY_WITH_INTEREST = 365;

    public StudentPlanInterestPolicy() {
        super(PLAN_TYPE, ANNUAL_RATE);
    }

    @Override
    protected boolean accruesInterestOn(int days) {
        return days <= LAST_DAY_WITH_INTEREST;
    }
}
