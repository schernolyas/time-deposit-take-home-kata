package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;

/**
 * Strategy that calculates the interest a time deposit earns for one monthly balance update.
 * <p>
 * One implementation per plan type. The hierarchy is sealed: the set of plans is a closed product catalogue, so
 * a new plan is an explicit decision - a new policy class, an entry in the {@code permits} clause and a registration
 * in {@link InterestPolicies}. The calculator itself never changes.
 */
public sealed interface InterestPolicy permits AnnualRateInterestPolicy, NoInterestPolicy {

    /** Policy for plan types nobody has registered a rule for: such deposits earn nothing. */
    InterestPolicy NO_INTEREST = new NoInterestPolicy();

    /** The {@link TimeDeposit#getPlanType() plan type} this policy applies to. */
    String planType();

    /**
     * Interest earned for one month, NOT yet rounded. Rounding to cents is the calculator's job so that
     * every plan is rounded the same way.
     */
    double monthlyInterest(TimeDeposit deposit);
}
