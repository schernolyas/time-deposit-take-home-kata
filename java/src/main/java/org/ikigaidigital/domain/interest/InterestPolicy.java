package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;

/**
 * Strategy that calculates the interest a time deposit earns for one monthly balance update.
 * <p>
 * One implementation per plan type; new plans (or more complex rules for an existing plan) are added
 * as new implementations and registered in {@link InterestPolicies}, without touching the calculator.
 */
public interface InterestPolicy {

    /** Policy for plan types nobody has registered a rule for: such deposits earn nothing. */
    InterestPolicy NO_INTEREST = new InterestPolicy() {
        @Override
        public String planType() {
            return "<none>";
        }

        @Override
        public double monthlyInterest(TimeDeposit deposit) {
            return 0;
        }
    };

    /** The {@link TimeDeposit#getPlanType() plan type} this policy applies to. */
    String planType();

    /**
     * Interest earned for one month, NOT yet rounded. Rounding to cents is the calculator's job so that
     * every plan is rounded the same way.
     */
    double monthlyInterest(TimeDeposit deposit);
}
