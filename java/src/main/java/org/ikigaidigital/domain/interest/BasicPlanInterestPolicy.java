package org.ikigaidigital.domain.interest;

/** Basic plan: 1% per year, paid monthly, after the 30-day grace period. */
public final class BasicPlanInterestPolicy extends AnnualRateInterestPolicy {

    public static final String PLAN_TYPE = "basic";
    private static final double ANNUAL_RATE = 0.01;

    public BasicPlanInterestPolicy() {
        super(PLAN_TYPE, ANNUAL_RATE);
    }
}
