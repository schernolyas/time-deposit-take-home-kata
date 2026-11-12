---
description: Add a new time-deposit plan type with its interest rule
argument-hint: <planType> <annual rate> <eligibility rule in words>
---

Add a new plan type to the time-deposit domain: $ARGUMENTS

Follow CLAUDE.md. Steps:

1. Add a failing unit test in `java/src/test/java/org/ikigaidigital/domain/interest/`
   that pins the expected monthly interest at the boundary days of the new rule.
2. Create one new `final` class in `org.ikigaidigital.domain.interest` extending
   `AnnualRateInterestPolicy` (or implementing `InterestPolicy` if the rule is
   not "annual rate / 12"). Do not modify the logic of existing policies.
3. Add it to the `permits` clause of its sealed parent (`AnnualRateInterestPolicy`
   or `InterestPolicy`) and register it in `InterestPolicies.defaults()`.
4. Run `mvn -f java/pom.xml test`. The characterization tests must stay green:
   existing plans must produce exactly the same balances.
5. Commit with `feat(interest): add <planType> plan`.
