### Security Policy

This document summarizes how we handle security for this project. For detailed per-release changes and remediation notes, see `CHANGELOG.md`.

#### Supported Versions

This is an active development project. We generally support only the latest `main` branch snapshot. If you discover a vulnerability on an older branch, please open an issue so we can advise next steps.

#### Reporting a Vulnerability

- Prefer opening a private issue or contacting the maintainers directly (if available) rather than filing a public bug for high‑impact vulnerabilities.
- Include steps to reproduce, affected endpoints/modules, and any relevant logs (with sensitive data removed).

#### Dependency Management

- We follow Spring Boot’s BOM where possible and pin/override specific transitive dependencies only when necessary to address CVEs.
- We avoid the `latest` tag; explicit, stable versions are preferred.
- Use `mvn dependency:tree` to verify resolved versions when investigating advisories.

#### Input Validation and XSS

- Controllers validate inputs with Jakarta Bean Validation and sanitize user‑controlled strings that may be rendered back to clients.
- Example: request query/body strings are HTML‑escaped before being echoed in responses or views to reduce XSS risk.

#### Secrets and Configuration

- Do not commit secrets to the repository. Use environment variables or an external secrets manager.
- `application.properties` contains only non‑sensitive defaults suitable for local development.

#### HTTPS and Transport Security

- For production deployments, place the service behind TLS termination (load balancer/reverse proxy) and ensure strong cipher suites.

#### Hardening Guidelines

- Disable Open Session In View (OSIV) to avoid unexpected lazy loading outside transactions.
- Define clear transaction boundaries at the service layer.
- Log via SLF4J; avoid logging sensitive data, and guard verbose logs behind debug level checks.

#### OpenAPI and Contracts

- API changes are documented in the OpenAPI specification and validated with Redocly.
- Error responses should be consistent; consider ProblemDetails format for standardization.
