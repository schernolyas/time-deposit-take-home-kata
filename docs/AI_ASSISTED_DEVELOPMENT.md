# AI-assisted development

## Tools and setup

| What | Used for |
|---|---|
| **Claude (Anthropic), model Claude Opus 5.5** in the Claude desktop app, with access to the local repository folder | Analysis of the kata, design, code and test generation, git commits on the feature branch |
| **Claude Code** project configuration in this repository (`CLAUDE.md`, `.claude/`) | Reproducible rules, guard rails and workflows for any agent session on this repo |
| IntelliJ IDEA | Human review, running the Maven build, manual testing through Swagger UI |

### Reproducing the setup

1. Install Claude Code (`npm install -g @anthropic-ai/claude-code`) and run `claude` in the repository root.
2. `CLAUDE.md` is loaded automatically: project map, hard constraints (shared `TimeDeposit` class, unchanged
   `updateBalance` behaviour, exactly two endpoints, hexagonal dependency rule), workflow and conventions.
3. `.claude/settings.json`:
   - **permissions** — Maven, read-only git and local `curl` are pre-approved; `git push`, `git reset --hard`
     and any edit of the golden-master oracle `LegacyTimeDepositCalculator.java` are denied.
   - **PostToolUse hook** — whenever the agent edits a `TimeDeposit*` or interest-policy file, the
     characterization tests run; a failure is fed back to the agent (exit code 2), so it cannot silently
     change the numeric behaviour. Requires `jq`.
4. `.claude/agents/code-reviewer.md` — a review subagent that has not seen the code being written and checks a
   diff against the kata constraints (behaviour preservation, endpoint count, hexagonal boundaries, SOLID).
5. `.claude/commands/` — `/verify` (build + endpoint count + independent review, reporting honestly what was
   not run) and `/new-plan` (the intended way to extend the interest rules).

## Workflow that was followed

1. The agent read the kata, inspected the original code and listed the behaviour that is easy to break
   (annual rate / 12, `new BigDecimal(double)` rounding, the `> 30`, `< 366`, `> 45` boundaries,
   unknown plan types).
2. **Characterize first:** a verbatim copy of the original method became the oracle, and a golden-master test
   compares raw `double` bits. Before any refactoring the agent also ran the comparison outside JUnit on
   ~200 000 deposits over 24 months: 0 differences.
3. Refactor to Strategy + Template Method behind the unchanged calculator API.
4. Hexagon around it: ports and use cases, JPA/Flyway persistence, contract-first REST, then
   Testcontainers and ArchUnit tests.
5. Atomic commits per step (Conventional Commits), each reviewed by a human before pushing.

## Which parts were AI-assisted, and why

| Part | AI involvement | Why |
|---|---|---|
| Requirements analysis, list of behaviour-preservation risks | AI-generated, human-reviewed | Fast exhaustive reading of the legacy code; a human confirmed the risk list |
| Characterization tests and the rounding edge case (6.00 student → 6.01) | AI-generated; the edge case was found by an AI-written brute-force search | Mechanical, high-volume, and exactly what a machine finds faster than a person |
| Interest-policy refactoring | AI-generated against the human-agreed design | Small, well-specified change protected by the golden master |
| Ports, services, JPA adapters, Flyway, OpenAPI contract, controller, configuration | AI-generated, human-reviewed | Boilerplate-heavy; the value of a human is in the review, not the typing |
| Integration and architecture tests | AI-generated, human-reviewed and run locally | Same |
| Design decisions and assumptions (see `java/README.md`) | Proposed by AI, decided by the human | Ambiguities in the requirements are business decisions |
| Documentation | AI-drafted, human-edited | Same |

### Honest limitations of the AI session

The agent's sandbox had no access to Maven Central, so it could not run the Maven build itself. It compiled and
ran the framework-free core (domain + application) with `javac`, executed the golden master outside JUnit and
validated the OpenAPI contract with `openapi-spec-validator`. The full `mvn verify` (Spring context, generated
API, Testcontainers) therefore runs on the developer machine; any fixes it required are separate commits.
