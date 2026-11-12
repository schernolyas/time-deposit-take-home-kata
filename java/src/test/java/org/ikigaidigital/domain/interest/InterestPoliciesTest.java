package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterestPoliciesTest {

    private final InterestPolicies defaults = InterestPolicies.defaults();

    @Test
    void defaults_resolveAllExistingPlans() {
        assertThat(defaults.forPlanType("basic")).isInstanceOf(BasicPlanInterestPolicy.class);
        assertThat(defaults.forPlanType("student")).isInstanceOf(StudentPlanInterestPolicy.class);
        assertThat(defaults.forPlanType("premium")).isInstanceOf(PremiumPlanInterestPolicy.class);
    }

    @Test
    void unknownOrNullPlanType_resolvesToNoInterest() {
        assertThat(defaults.forPlanType("gold")).isSameAs(InterestPolicy.NO_INTEREST);
        assertThat(defaults.forPlanType("Basic")).isSameAs(InterestPolicy.NO_INTEREST);
        assertThat(defaults.forPlanType(null)).isSameAs(InterestPolicy.NO_INTEREST);
    }

    @Test
    void duplicatePlanTypes_areRejected() {
        assertThatThrownBy(() -> new InterestPolicies(List.of(new BasicPlanInterestPolicy(), new BasicPlanInterestPolicy())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("basic");
    }

    @Test
    void policiesReturnUnroundedMonthlyInterest() {
        TimeDeposit deposit = new TimeDeposit(1, "basic", 1_200.00, 31);

        assertThat(new BasicPlanInterestPolicy().monthlyInterest(deposit)).isEqualTo(1_200.00 * 0.01 / 12);
        assertThat(new StudentPlanInterestPolicy().monthlyInterest(deposit)).isEqualTo(1_200.00 * 0.03 / 12);
        assertThat(new PremiumPlanInterestPolicy().monthlyInterest(deposit)).isZero();
    }

    @Test
    void newPlanCanBeAddedWithoutChangingTheCalculator() {
        // Example of extension: a hypothetical "corporate" plan with 2% p.a. and a 60-day grace period.
        InterestPolicy corporate = new AnnualRateInterestPolicy("corporate", 0.02, 60) {
        };
        InterestPolicies policies = new InterestPolicies(List.of(new BasicPlanInterestPolicy(), corporate));
        TimeDeposit young = new TimeDeposit(1, "corporate", 12_000.00, 60);
        TimeDeposit old = new TimeDeposit(2, "corporate", 12_000.00, 61);

        new TimeDepositCalculator(policies).updateBalance(List.of(young, old));

        assertThat(young.getBalance()).isEqualTo(12_000.00);
        assertThat(old.getBalance()).isEqualTo(12_020.00);
    }
}
