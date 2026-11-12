# Solution

The solution is in [`java/`](java). Start with [`java/README.md`](java/README.md): build, run, how to trigger
both endpoints through Swagger UI, architecture and assumptions.

- REST contract (OpenAPI 3): [`java/src/main/resources/static/openapi/time-deposit-api.yaml`](java/src/main/resources/static/openapi/time-deposit-api.yaml)
- AI harness: [`CLAUDE.md`](CLAUDE.md), [`.claude/`](.claude), write-up in
  [`docs/AI_ASSISTED_DEVELOPMENT.md`](docs/AI_ASSISTED_DEVELOPMENT.md)

Quick start:

```bash
cd java
mvn verify            # all tests, integration tests on Testcontainers (Docker required)
mvn spring-boot:run   # starts PostgreSQL via Docker Compose, then open http://localhost:8080/swagger-ui.html
```

The other language folders are the untouched originals of the kata.
