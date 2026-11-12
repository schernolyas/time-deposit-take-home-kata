---
description: Full verification before a commit or a hand-off
---

1. Run `mvn -f java/pom.xml verify` (requires Docker for Testcontainers).
   If Docker is not available, run `mvn -f java/pom.xml test` and state clearly
   that integration tests were not executed.
2. Count HTTP operations in `java/src/main/resources/static/openapi/time-deposit-api.yaml`;
   there must be exactly two.
3. Delegate a review of the pending diff to the `code-reviewer` subagent.
4. Report: build result, test counts, reviewer verdict, and anything you could
   not verify. Never claim a step passed if you did not run it.
