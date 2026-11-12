package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;

/** Policy for plan types nobody has registered a rule for: such deposits earn nothing. */
final class NoInterestPolicy implements InterestPolicy {

    @Override
    public String planType() {
        return "<none>";
    }

    @Override
    public double monthlyInterest(TimeDeposit deposit) {
        return 0;
    }
}
