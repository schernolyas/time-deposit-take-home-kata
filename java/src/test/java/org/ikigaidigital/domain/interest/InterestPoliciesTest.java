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
    void calculatorAppliesOnlyThePoliciesOfTheGivenRegistry() {
        InterestPolicies basicOnly = new InterestPolicies(List.of(new BasicPlanInterestPolicy()));
        TimeDeposit basic = new TimeDeposit(1, "basic", 1_200.00, 31);
        TimeDeposit student = new TimeDeposit(2, "student", 1_200.00, 31);

        new TimeDepositCalculator(basicOnly).updateBalance(List.of(basic, student));

        assertThat(basic.getBalance()).isEqualTo(1_201.00);
        assertThat(student.getBalance()).isEqualTo(1_200.00);
    }

    @Test
    void policyHierarchyIsClosed() {
        assertThat(InterestPolicy.class.isSealed()).isTrue();
        assertThat(AnnualRateInterestPolicy.class.getPermittedSubclasses()).containsExactlyInAnyOrder(
                BasicPlanInterestPolicy.class, StudentPlanInterestPolicy.class, PremiumPlanInterestPolicy.class);
    }
}
