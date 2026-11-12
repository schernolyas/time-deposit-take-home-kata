---
description: Full verification before a commit or a hand-off
---

1. Run `mvn -f java/pom.xml verify` (requires Docker for Testcontainers).
   If Docker is not available, run `mvn -f java/pom.xml test` and state clearly
   that integration tests were not executed.
2. Count HTTP operations in `java/src/main/resources/static/openapi/time-deposit-api.yaml`;
   there must be exactly two.
3. Delegate a review of the pending diff to the `code-reviewer` subagent.
4. Open `java/target/site/jacoco/index.html` (or `jacoco.xml`) and list hand-written classes or branches that
   no test exercises. Judge whether each gap hides real behaviour worth a test; do not propose tests that only
   raise the percentage.
5. Report: build result, test counts, coverage gaps worth closing, reviewer verdict, and anything you could
   not verify. Never claim a step passed if you did not run it.
