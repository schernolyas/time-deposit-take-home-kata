package org.ikigaidigital.domain.interest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry that resolves the {@link InterestPolicy} for a plan type. Adding a plan means adding a policy
 * to {@link #defaults()} (or building a custom registry) - the calculator never changes (Open/Closed).
 */
public final class InterestPolicies {

    private final Map<String, InterestPolicy> policiesByPlanType;

    public InterestPolicies(Collection<? extends InterestPolicy> policies) {
        Map<String, InterestPolicy> byPlanType = new HashMap<>();
        for (InterestPolicy policy : policies) {
            InterestPolicy previous = byPlanType.put(policy.planType(), policy);
            if (previous != null) {
                throw new IllegalArgumentException("Duplicate interest policy for plan type: " + policy.planType());
            }
        }
        // HashMap (not Map.copyOf) on purpose: lookups with a null plan type must not throw.
        this.policiesByPlanType = Collections.unmodifiableMap(byPlanType);
    }

    /** The plans offered by XA Bank today. */
    public static InterestPolicies defaults() {
        return new InterestPolicies(List.of(
                new BasicPlanInterestPolicy(),
                new StudentPlanInterestPolicy(),
                new PremiumPlanInterestPolicy()));
    }

    /**
     * ASSUMPTION: plan types are matched case-sensitively and unknown plan types earn no interest,
     * exactly as in the original implementation (a deposit with an unknown plan keeps its balance).
     */
    public InterestPolicy forPlanType(String planType) {
        return policiesByPlanType.getOrDefault(planType, InterestPolicy.NO_INTEREST);
    }
}
