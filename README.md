# Bank Account Management System with Axon Framework and Event Sourcing

A Spring Boot application demonstrating Event Sourcing and CQRS patterns using Axon Framework with PostgreSQL as the event store.

## Tech Stack

- Java 21, Spring Boot 3.3.0
- Axon Framework 4.9.1 (Event Sourcing, CQRS, JPA Event Store)
- PostgreSQL 15 (Event Store + Projections)
- Docker / Docker Compose

## Architecture

### Event Sourcing
All state changes are stored as immutable events in the `domain_event_entry` table. The current state of an account is reconstructed by replaying these events rather than mutating a single row.

### CQRS
- **Command side**: `BankAccount` aggregate processes commands and produces events.
- **Query side**: Projections (`CurrentAccountViewProjection`, `TransactionHistoryProjection`) listen to events and maintain optimized read models in PostgreSQL.

### Snapshots
Snapshots are created every 5 events per aggregate to speed up replay. Stored in `snapshot_event_entry`.

## Prerequisites

- Docker & Docker Compose

## Quick Start

```bash
docker-compose up --build
```

This starts PostgreSQL and the application. The API is available at `http://localhost:8080`.

## Running Locally (without Docker)

### 1. Start PostgreSQL

```bash
docker-compose up -d db
```

### 2. Build and Run

```bash
.\mvnw.cmd clean package -DskipTests
java -Duser.timezone=UTC -jar target/bank-account-management-1.0.0.jar
```

Or use the Maven wrapper:

```bash
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=-Duser.timezone=UTC
```

## API Endpoints

### Create Account
```
POST /api/accounts
{
  "initialBalance": 1000.00,
  "ownerName": "John Doe"
}
```
Response: `201 Created` with `{ "accountId": "uuid" }` and `Location` header.

### Deposit Money
```
POST /api/accounts/{accountId}/deposit
{ "amount": 500.00 }
```
Response: `200 OK`

### Withdraw Money
```
POST /api/accounts/{accountId}/withdraw
{ "amount": 200.00 }
```
Response: `200 OK` or `409 Conflict` if insufficient funds.

### Close Account
```
POST /api/accounts/{accountId}/close
```
Response: `200 OK` or `409 Conflict` if balance is not zero.

### Get Account (from Projection)
```
GET /api/accounts/{accountId}
```
Response: `200 OK` with account details or `404 Not Found`.

### Get Event Stream
```
GET /api/accounts/{accountId}/events
```
Response: `200 OK` with array of events.

### Get Balance at Timestamp (Temporal Query)
```
GET /api/accounts/{accountId}/balance-at/2023-10-27T10:00:00Z
```
Response: `200 OK` with balance as of the given timestamp.

### Admin - Replay Events
```
POST /api/admin/replay/{processingGroup}
```

## Event Replay

To trigger a replay of the current account view projection:

```bash
./replay-events.sh
```

This resets the projection and rebuilds it from all historical events.

### Available Processing Groups
- `current_account_view` — Rebuilds the account balance/status projection
- `transaction_history` — Rebuilds the transaction history log

## Database Tables

| Table | Description |
|-------|-------------|
| `domain_event_entry` | All events |
| `snapshot_event_entry` | Aggregate snapshots (every 5 events) |
| `current_account_view` | Current account states (read model) |
| `transaction_history` | Transaction logs (read model) |
| `token_entry` | Event processor tokens |
| `association_value_entry` | Saga associations (if sagas used) |

## Error Handling

| HTTP Status | Scenario |
|-------------|----------|
| `200 OK` | Successful operation |
| `201 Created` | Account created |
| `404 Not Found` | Account ID not found |
| `409 Conflict` | Insufficient funds, account closed, non-zero close |

## Design Decisions

- **EmbeddedEventStore**: Wraps JPA Event Storage Engine for local event store without Axon Server.
- **sendAndWait()**: Used for commands so exceptions propagate synchronously to the REST controller.
- **Snapshot every 5 events**: Balances replay speed and storage overhead.
- **UTC timezone**: Avoids PostgreSQL timezone parsing issues across environments.

## Environment Variables

See `.env.example` for all configurable variables. Defaults work out of the box with `docker-compose up`.
