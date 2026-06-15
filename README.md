# sbommaker

A Spring Boot web application that maintains an inventory of software components and generates [CycloneDX 1.5](https://cyclonedx.org/) JSON Software Bills of Materials (SBOMs).

## Features

- Manage software components with manufacturer and source URL
- Track multiple versions per component
- Assemble releases by tagging specific component versions
- Generate and download CycloneDX 1.5 JSON BOMs per release
- Web UI for manual data entry
- REST API for automated ingestion (API key protected)
- H2 file-based database with persistence across restarts

---

## Running the Application

### Prerequisites

- Java 17+
- Maven 3.8+

### From source

```bash
mvn spring-boot:run
```

### As a JAR

```bash
mvn package -DskipTests
java -jar target/sbommaker-0.0.1-SNAPSHOT.jar
```

### With Docker Compose

Place your TLS certificate files in an `ssl/` directory (see [HTTPS](#https)), then:

```bash
docker compose up --build      # first run
docker compose up -d           # subsequent starts (detached)
docker compose down            # stop, preserve data
docker compose down -v         # stop and wipe database
```

The application is available at `https://localhost:6565` by default.

---

## Configuration

All settings live in `application.properties` and can be overridden with environment variables (Spring Boot maps `APP_FOO_BAR` → `app.foo-bar`).

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `6565` | HTTP/HTTPS listen port |
| `server.ssl.enabled` | `true` | Enable TLS |
| `server.ssl.certificate` | `file:./ssl/server.crt` | PEM certificate (or chain) |
| `server.ssl.certificate-private-key` | `file:./ssl/server.key` | PEM private key |
| `app.name` | `SBOM Maker` | Application name shown in the UI |
| `app.logo-url` | `/images/logo.svg` | Logo displayed in the nav bar (local path or external URL) |
| `app.api-key` | `change-me-in-production` | API key required for all REST endpoints |
| `spring.datasource.url` | `jdbc:h2:file:./data/sbommaker` | H2 database path |
| `spring.h2.console.enabled` | `true` | Enable H2 web console at `/h2-console` |

---

## HTTPS

The application requires a TLS certificate. Drop PEM files into the `ssl/` directory:

```
ssl/
  server.crt   # PEM certificate or full chain
  server.key   # PEM private key
```

### Generate a self-signed certificate for testing

```bash
openssl req -x509 -newkey rsa:2048 \
  -keyout ssl/server.key -out ssl/server.crt \
  -days 365 -nodes \
  -subj "/CN=localhost/O=SBOM Maker/C=US" \
  -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"
```

To trust it in your browser:

```bash
# macOS
sudo security add-trusted-cert -d -r trustRoot \
  -k /Library/Keychains/System.keychain ssl/server.crt

# Linux (Debian/Ubuntu)
sudo cp ssl/server.crt /usr/local/share/ca-certificates/sbommaker.crt
sudo update-ca-certificates
```

---

## API Key

All REST endpoints require an `X-API-Key` header.

### Generate a key

```bash
python3 scripts/genkey.py
```

The script prints the key with ready-to-paste `application.properties` and environment variable forms. Use `--bytes N` to control key length (default: 32 bytes → 43-char base64url string).

### Set the key

```properties
# application.properties
app.api-key=your-generated-key
```

```bash
# Environment variable (e.g. Docker)
APP_API_KEY=your-generated-key docker compose up
```

---

## REST API

Base URL: `https://localhost:6565/api`

All requests must include the header:

```
X-API-Key: <your-api-key>
```

### Components

#### Upsert a component
Creates the component if it does not exist; updates `manufacturer` and `sourceUrl` if it does. Match is case-insensitive on `name`.

```
PUT /api/components
```

```json
{
  "name": "Log4j",
  "manufacturer": "Apache Software Foundation",
  "sourceUrl": "https://logging.apache.org/log4j"
}
```

Response: `201 Created` (new) or `200 OK` (updated)

```json
{
  "id": 1,
  "name": "Log4j",
  "manufacturer": "Apache Software Foundation",
  "sourceUrl": "https://logging.apache.org/log4j",
  "versionCount": 0,
  "created": true
}
```

#### Get a component by ID

```
GET /api/components/{id}
```

#### Upsert a component version
Adds the version if it does not exist; updates `notes` if it does. Match is case-insensitive on `version`.

```
PUT /api/components/{name}/versions
```

```json
{
  "version": "2.24.1",
  "notes": "Latest stable"
}
```

Response: `201 Created` or `200 OK`

```json
{
  "id": 5,
  "componentName": "Log4j",
  "version": "2.24.1",
  "notes": "Latest stable",
  "created": true
}
```

### Releases

#### Upsert a release
Creates the release if the `tag` does not exist; updates `name` and `description` if it does.

```
PUT /api/releases
```

```json
{
  "name": "My Application 2.0",
  "tag": "v2.0.0",
  "description": "Second major release"
}
```

Response: `201 Created` or `200 OK`

```json
{
  "id": 1,
  "name": "My Application 2.0",
  "tag": "v2.0.0",
  "description": "Second major release",
  "componentCount": 0,
  "created": true
}
```

#### Get a release by tag

```
GET /api/releases/{tag}
```

#### Add a component version to a release

```
PUT /api/releases/{tag}/components
```

```json
{
  "componentName": "Log4j",
  "version": "2.24.1"
}
```

Adding the same component version twice is a no-op. The component and version must already exist.

#### Download a CycloneDX BOM

```
GET /api/releases/{tag}/bom
```

Returns a CycloneDX 1.5 JSON BOM as a downloadable file. The BOM can also be downloaded from the web UI release detail page.

---

## Typical API Workflow

```bash
KEY="your-api-key"
BASE="https://localhost:6565/api"

# 1. Register a component
curl -sk -H "X-API-Key: $KEY" -X PUT $BASE/components \
  -H "Content-Type: application/json" \
  -d '{"name":"Spring Boot","manufacturer":"Broadcom","sourceUrl":"https://spring.io"}'

# 2. Add a version
curl -sk -H "X-API-Key: $KEY" -X PUT "$BASE/components/Spring Boot/versions" \
  -H "Content-Type: application/json" \
  -d '{"version":"3.3.4","notes":"Current stable"}'

# 3. Create a release
curl -sk -H "X-API-Key: $KEY" -X PUT $BASE/releases \
  -H "Content-Type: application/json" \
  -d '{"name":"My App 1.0","tag":"v1.0.0","description":"Initial release"}'

# 4. Add the component version to the release
curl -sk -H "X-API-Key: $KEY" -X PUT "$BASE/releases/v1.0.0/components" \
  -H "Content-Type: application/json" \
  -d '{"componentName":"Spring Boot","version":"3.3.4"}'

# 5. Download the BOM
curl -sk -H "X-API-Key: $KEY" "$BASE/releases/v1.0.0/bom"
```

---

## Web UI

The web UI is available without authentication at `https://localhost:6565`.

| Path | Description |
|------|-------------|
| `/components` | List, create, edit, and delete components and their versions |
| `/releases` | List, create, edit, and delete releases; add/remove component versions |
| `/releases/{id}` | Release detail with a **Download BOM JSON** button |
| `/h2-console` | H2 database web console (disable in production) |
