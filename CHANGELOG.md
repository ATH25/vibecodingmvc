### Changelog

All notable changes to this project will be documented in this file.

#### 2025-11-26 — Maintenance & Security Updates

- XSS hardening in `BeerController`:
  - Escapes user input in `GET /api/v1/beers` query param `beerName` using `HtmlUtils.htmlEscape(...)`.
  - Sanitizes request bodies for `POST /api/v1/beers` and `PUT /api/v1/beers/{beerId}` via a helper `sanitizeDto(BeerRequestDto)` which escapes `beerName`, `beerStyle`, and `upc` before passing to the service.
- Test cleanup:
  - `BeerControllerTest` now uses `List.getFirst()` instead of index-based access for clarity.
- Dependency updates to mitigate CVEs (managed via `pom.xml` properties/overrides):
  - Spring Framework (`spring-core`, `spring-beans`, `spring-webmvc`): 6.2.11
  - Tomcat Embed Core: 10.1.45
  - Logback Core: 1.5.20 (pinned)
  - Commons Lang 3: 3.14.0 (added direct dependency and excluded the vulnerable transitive from Springdoc)

Verification steps executed at the time:
- Build and tests
  - `mvn clean test`
  - `mvn clean install`
- Dependency spot checks
  - `mvn -q dependency:tree -Dincludes=org.springframework:spring-core,org.springframework:spring-beans,org.springframework:spring-webmvc,org.apache.tomcat.embed:tomcat-embed-core,org.apache.commons:commons-lang3,ch.qos.logback:logback-core -DoutputType=text`
- OpenAPI (Redocly)
  - `cd openapi-starter-main && npm install && npm test`

Files touched:
- `src/main/java/tom/springframework/vibecodingmvc/controllers/BeerController.java`
- `src/test/java/tom/springframework/vibecodingmvc/controllers/BeerControllerTest.java`
- `pom.xml`

Notes:
- No API or contract changes were made. All endpoints and DTOs remain the same.
- Server-side escaping reduces XSS risk if user-supplied text is echoed in responses or logs.
