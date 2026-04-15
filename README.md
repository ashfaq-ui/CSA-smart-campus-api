# Smart Campus API

A RESTful API built with JAX-RS (Jersey) and Grizzly for managing rooms and
sensors across a university smart campus.

## Technology Stack
- Java 11
- JAX-RS (Jersey 2.35)
- Grizzly HTTP Server
- Jackson (JSON)
- Maven

## API Base URL
```
http://localhost:8080/api/v1
```

## Resource Hierarchy
```
/api/v1
├── /rooms
│   ├── GET    - list all rooms
│   ├── POST   - create a room
│   ├── GET    /{roomId} - get room by ID
│   ├── PUT    /{roomId} - update a room
│   └── DELETE /{roomId} - delete room
├── /sensors
│   ├── GET    - list all sensors (optional ?type= filter)
│   ├── POST   - create a sensor
│   ├── GET    /{sensorId} - get sensor by ID
│   ├── PUT    /{sensorId} - update a sensor
│   ├── DELETE /{sensorId} - delete sensor
│   └── /{sensorId}/readings
│       ├── GET  - get all readings
│       ├── POST - add a new reading
│       └── GET  /{readingId} - get reading by ID
└── GET / - API discovery endpoint
```
---
## Complete API Endpoints Table

| Method | Endpoint | Description | Success Code | Error Codes |
|--------|----------|-------------|--------------|-------------|
| GET | /api/v1 | API discovery and HATEOAS links | 200 | - |
| GET | /api/v1/rooms | Get all rooms | 200 | - |
| POST | /api/v1/rooms | Create a new room | 201 | 400, 409 |
| GET | /api/v1/rooms/{roomId} | Get room by ID | 200 | 404 |
| PUT | /api/v1/rooms/{roomId} | Update a room | 200 | 404 |
| DELETE | /api/v1/rooms/{roomId} | Delete a room | 204 | 404, 409 |
| GET | /api/v1/sensors | Get all sensors | 200 | - |
| GET | /api/v1/sensors?type={type} | Filter sensors by type | 200 | - |
| POST | /api/v1/sensors | Create a new sensor | 201 | 400, 409, 422 |
| GET | /api/v1/sensors/{sensorId} | Get sensor by ID | 200 | 404 |
| PUT | /api/v1/sensors/{sensorId} | Update a sensor | 200 | 404 |
| DELETE | /api/v1/sensors/{sensorId} | Delete a sensor | 204 | 404 |
| GET | /api/v1/sensors/{sensorId}/readings | Get all readings | 200 | 404 |
| POST | /api/v1/sensors/{sensorId}/readings | Add a new reading | 201 | 403, 404 |
| GET | /api/v1/sensors/{sensorId}/readings/{readingId} | Get reading by ID | 200 | 404 |
| GET | /api/v1/rooms/crash | Demo 500 error handler | - | 500 |
---

## How to Build and Run

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Step 1 — Clone the repository
```bash
git clone https://github.com/ashfaq-ui/CSA-smart-campus-api.git
cd CSA-smart-campus-api
```

### Step 2 — Build the project
```bash
mvn clean compile
```

### Step 3 — Run the server
```bash
mvn exec:java -Dexec.mainClass="com.smartcampus.Main"
```

Or run directly from IntelliJ:
- Open `Main.java`
- Right-click → Run 'Main.main()'

### Step 4 — Verify server is running
```bash
curl http://localhost:8080/api/v1
```

You should see:
```json
{
  "api": "Smart Campus API",
  "version": "v1",
  "status": "running"
}
```

---

## Seed Data

The server automatically loads sample data on startup:

| ID | Name | Type |
|----|------|------|
| LIB-301 | Library Quiet Study | Room |
| CS-101 | Computer Science Lab | Room |
| ENG-205 | Engineering Workshop | Room |
| TEMP-001 | Temperature Sensor | Sensor (ACTIVE) |
| CO2-001 | CO2 Sensor (CS-101) | Sensor (ACTIVE) |
| OCC-001 | Occupancy Sensor (ENG-205) | Sensor (MAINTENANCE) |

---

## API Endpoints

### Discovery

#### GET /api/v1
Returns API metadata and HATEOAS links.

**Request:**
```bash
curl http://localhost:8080/api/v1
```

**Response 200:**
```json
{
  "api": "Smart Campus API",
  "version": "v1",
  "status": "running",
  "contact": "admin@smartcampus.ac.uk",
  "timestamp": 1776089672921,
  "documentation": "See README.md for full API docs",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

---

### Rooms

#### GET /api/v1/rooms
Returns all rooms.

**Request:**
```bash
curl http://localhost:8080/api/v1/rooms
```

**Response 200:**
```json
[
  {
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 50,
    "sensorIds": ["TEMP-001"]
  }
]
```

---

#### POST /api/v1/rooms
Creates a new room.

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"ROOM-001","name":"Test Room","capacity":30}'
```

**Response 201:**
```json
{
  "id": "ROOM-001",
  "name": "Test Room",
  "capacity": 30,
  "sensorIds": []
}
```

**Response 409 (room already exists):**
```json
{
  "error": "Room already exists"
}
```

---

#### GET /api/v1/rooms/{roomId}
Returns a specific room by ID.

**Request:**
```bash
curl http://localhost:8080/api/v1/rooms/LIB-301
```

**Response 200:**
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50,
  "sensorIds": ["TEMP-001"]
}
```

**Response 404:**
```json
{
  "error": "Room not found: LIB-301"
}
```

---

#### PUT /api/v1/rooms/{roomId}
Updates an existing room.

**Request:**
```bash
curl -X PUT http://localhost:8080/api/v1/rooms/LIB-301 \
  -H "Content-Type: application/json" \
  -d '{"name":"Library Main Hall","capacity":100}'
```

**Response 200:**
```json
{
  "id": "LIB-301",
  "name": "Library Main Hall",
  "capacity": 100,
  "sensorIds": ["TEMP-001"]
}
```

**Response 404:**
```json
{
  "error": "Room not found: LIB-301"
}
```

---

#### DELETE /api/v1/rooms/{roomId}
Deletes a room. Blocked if room has sensors.

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/ROOM-001
```

**Response 204:** No content — success

**Response 404:**
```json
{
  "error": "Room not found: ROOM-001"
}
```

**Response 409 (room has sensors):**
```json
{
  "hint": "Remove all sensors from this room before deleting it",
  "error": "409 Conflict",
  "message": "Room LIB-301 still has sensors assigned to it",
  "roomId": "LIB-301"
}
```

---

### Sensors

#### GET /api/v1/sensors
Returns all sensors. Supports optional type filter.

**Request — all sensors:**
```bash
curl http://localhost:8080/api/v1/sensors
```

**Request — filtered by type:**
```bash
curl "http://localhost:8080/api/v1/sensors?type=Temperature"
```

**Response 200:**
```json
[
  {
    "id": "TEMP-001",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 22.5,
    "roomId": "LIB-301"
  }
]
```

---

#### POST /api/v1/sensors
Creates a new sensor. roomId must exist.

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-002","type":"Temperature","status":"ACTIVE","currentValue":22.5,"roomId":"LIB-301"}'
```

**Response 201:**
```json
{
  "id": "TEMP-002",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}
```

**Response 422 (roomId does not exist):**
```json
{
  "hint": "The referenced resource does not exist in the system",
  "error": "422 Unprocessable Entity",
  "message": "Room not found with ID: FAKE-999",
  "resourceType": "Room",
  "resourceId": "FAKE-999"
}
```

---

#### GET /api/v1/sensors/{sensorId}
Returns a specific sensor by ID.

**Request:**
```bash
curl http://localhost:8080/api/v1/sensors/TEMP-001
```

**Response 200:**
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}
```

**Response 404:**
```json
{
  "error": "Sensor not found: TEMP-001"
}
```

---

#### PUT /api/v1/sensors/{sensorId}
Updates an existing sensor.

**Request:**
```bash
curl -X PUT http://localhost:8080/api/v1/sensors/TEMP-001 \
  -H "Content-Type: application/json" \
  -d '{"status":"MAINTENANCE"}'
```

**Response 200:**
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "MAINTENANCE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}
```

**Response 404:**
```json
{
  "error": "Sensor not found: TEMP-001"
}
```

---

#### DELETE /api/v1/sensors/{sensorId}
Deletes a sensor and unlinks it from its room.

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/v1/sensors/TEMP-001
```

**Response 204:** No content — success

**Response 404:**
```json
{
  "error": "Sensor not found: TEMP-001"
}
```

---

### Sensor Readings

#### GET /api/v1/sensors/{sensorId}/readings
Returns all readings for a sensor.

**Request:**
```bash
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings
```

**Response 200:**
```json
[
  {
    "id": "d01351fc-1eea-4b8c-a3de-d72c8b0a13f9",
    "timestamp": 1776089672921,
    "value": 24.5
  }
]
```

**Response 404:**
```json
{
  "error": "Sensor not found: TEMP-001"
}
```

---

#### POST /api/v1/sensors/{sensorId}/readings
Adds a new reading. Blocked if sensor is MAINTENANCE or OFFLINE.
Also updates the parent sensor currentValue automatically.

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":24.5}'
```

**Response 201:**
```json
{
  "id": "d01351fc-1eea-4b8c-a3de-d72c8b0a13f9",
  "timestamp": 1776089672921,
  "value": 24.5
}
```

**Response 403 (sensor in MAINTENANCE or OFFLINE):**
```json
{
  "hint": "Change sensor status to ACTIVE before posting readings",
  "error": "403 Forbidden",
  "message": "Sensor OCC-001 is currently MAINTENANCE and cannot accept new readings",
  "sensorId": "OCC-001",
  "status": "MAINTENANCE"
}
```

---

#### GET /api/v1/sensors/{sensorId}/readings/{readingId}
Returns a specific reading by ID.

**Request:**
```bash
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings/d01351fc-1eea-4b8c-a3de-d72c8b0a13f9
```

**Response 200:**
```json
{
  "id": "d01351fc-1eea-4b8c-a3de-d72c8b0a13f9",
  "timestamp": 1776089672921,
  "value": 24.5
}
```

**Response 404:**
```json
{
  "error": "Reading not found: d01351fc-1eea-4b8c-a3de-d72c8b0a13f9"
}
```

---

### Global Error Responses

#### 500 Internal Server Error
Returned when an unexpected error occurs.
No internal details are exposed for security reasons.

**Request:**
```bash
curl http://localhost:8080/api/v1/rooms/crash
```

**Response 500:**
```json
{
  "hint": "Please contact the API administrator",
  "error": "500 Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## Error Codes Summary

| Code | Meaning | Scenario |
|------|---------|----------|
| 200 | OK | Successful GET |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Missing required fields |
| 403 | Forbidden | Sensor in MAINTENANCE or OFFLINE |
| 404 | Not Found | Resource does not exist |
| 409 | Conflict | Delete room with sensors |
| 422 | Unprocessable Entity | Invalid roomId reference |
| 500 | Internal Server Error | Unexpected server error |

---

## Conceptual Report — Answers to Questions

### Part 1 — Q1: JAX-RS Resource Class Lifecycle
By default, JAX-RS creates a new instance of a resource class for every
incoming HTTP request. This is called per-request lifecycle. This means
each request gets its own fresh object with no shared state between requests.

Since a new instance is created per request, instance variables inside
resource classes cannot be used to store shared data. This is why a
singleton DataStore class using ConcurrentHashMap was implemented to manage
all in-memory data. ConcurrentHashMap ensures thread safety when multiple
requests access or modify data simultaneously, preventing race conditions
and data loss.

### Part 1 — Q2: HATEOAS
HATEOAS (Hypermedia as the Engine of Application State) is considered a
hallmark of advanced RESTful design because it makes APIs self-discoverable.
Instead of relying on static documentation, clients can navigate the API
dynamically by following links embedded in responses.

This benefits client developers because they do not need to hardcode URLs.
If the API changes its URL structure, clients that follow links will
automatically adapt. It also reduces coupling between client and server,
making the API easier to evolve and maintain over time.

### Part 2 — Q1: Returning IDs vs Full Objects
Returning only IDs when listing rooms reduces network bandwidth since
less data is transferred. However, clients must make additional requests
to fetch details for each room, increasing the number of HTTP calls.

Returning full room objects increases the response payload size but
reduces the number of round trips. For a campus management system where
clients typically need room details immediately, returning full objects
is more practical. For very large datasets, pagination with full objects
or returning only IDs with a summary field would be a better compromise.

### Part 2 — Q2: DELETE Idempotency
Yes, the DELETE operation is idempotent in this implementation. Idempotent
means that making the same request multiple times produces the same result
as making it once.

In this implementation, the first DELETE request for a room that exists
and has no sensors will successfully delete it and return 204 No Content.
If the same DELETE request is sent again, the room no longer exists in the
DataStore, so the service returns 404 Not Found. The server state remains
the same after the first successful deletion, which satisfies the
idempotency requirement. The client receives a different status code on
subsequent calls but the server state does not change.

### Part 3 — Q1: @Consumes Annotation
When a client sends data in a format other than application/json, such as
text/plain or application/xml, JAX-RS returns an HTTP 415 Unsupported
Media Type response automatically. The resource method is never invoked.
Jersey checks the Content-Type header of the incoming request and matches
it against the @Consumes annotation. If they do not match, the request is
rejected before reaching the method body. This protects the API from
receiving unexpected data formats that Jackson cannot deserialize.

### Part 3 — Q2: @QueryParam vs Path Parameter for Filtering
Using @QueryParam for filtering is superior because query parameters are
optional by nature. A client can call GET /api/v1/sensors to get all
sensors or GET /api/v1/sensors?type=CO2 to filter. The same endpoint
serves both purposes cleanly.

Using a path parameter like /api/v1/sensors/type/CO2 implies that type
is a mandatory part of the resource identity, which is semantically
incorrect. It also makes the URL harder to read and requires a separate
endpoint for unfiltered results. Query parameters are the REST standard
for filtering, searching, and sorting collections.

### Part 4 — Q1: Sub-Resource Locator Pattern
The Sub-Resource Locator pattern improves API design by delegating
responsibility to dedicated classes. Instead of one massive resource class
handling every nested path, each resource owns its own logic.

This separation of concerns makes the codebase easier to maintain and
test. SensorReadingResource only handles reading logic, while
SensorResource handles sensor logic. As the API grows, new sub-resources
can be added without touching existing classes. It also makes the code
more readable and aligns with the Single Responsibility Principle.

### Part 5 — Q1: HTTP 422 vs 404
HTTP 404 Not Found means the requested URL does not exist on the server.
HTTP 422 Unprocessable Entity means the request was well-formed and the
URL exists, but the server cannot process the instructions because of
semantic errors in the payload.

When a client posts a sensor with a roomId that does not exist, the
/api/v1/sensors endpoint itself is valid and exists. The problem is
inside the JSON payload — the referenced roomId is invalid. Using 422
communicates that the request was received and understood but cannot be
processed due to a logical error in the data, which is more semantically
accurate than 404.

### Part 5 — Q2: Stack Trace Security Risk
Exposing Java stack traces to external API consumers is a serious security
risk. Stack traces reveal internal class names, package structures, method
names, and line numbers.

An attacker can use this information to identify the frameworks and
libraries being used, find known vulnerabilities in those specific
versions, understand the application architecture and identify weak
points, and craft targeted attacks based on the internal structure of
the application. The global ExceptionMapper catches all unexpected errors
and returns a generic 500 message without any internal details, preventing
information leakage while still informing the client that an error occurred.

### Part 5 — Q3: JAX-RS Filters vs Manual Logging
Using JAX-RS filters for cross-cutting concerns like logging is
advantageous because it follows the DRY principle. Instead of adding
Logger.info() calls inside every single resource method, one filter
class handles all logging automatically for every request and response.

This means if the logging format needs to change, only one class needs
to be updated. It also keeps resource methods clean and focused on
business logic only. Filters are applied consistently to all endpoints
without any risk of forgetting to add logging to a new method. This
is the same principle behind Aspect-Oriented Programming.