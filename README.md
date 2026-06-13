# Bank Account Management System with Axon Framework and Event Sourcing

A Spring Boot application demonstrating Event Sourcing and CQRS patterns using Axon Framework with PostgreSQL as the event store.

## Tech Stack

- Java 17, Spring Boot 3.3.0
- Axon Framework 4.9.1 (Event Sourcing, CQRS)
- PostgreSQL 15 (Event Store + Projections)
- Docker / Docker Compose

## Architecture

### Event Sourcing
All state changes are stored as immutable events in the `domain_event_entry` table. The current state of an account is reconstructed by replaying these events.

### CQRS
- **Command side**: `BankAccount` aggregate processes commands and produces events.
- **Query side**: Projections (`CurrentAccountViewProjection`, `TransactionHistoryProjection`) listen to events and maintain optimized read models.

### Snapshots
Snapshots are created every 5 events per aggregate to speed up replay. Stored in `snapshot_event_entry`.

## Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven

## Setup & Running

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

### 2. Build the application

```bash
mvn clean package -DskipTests
```

### 3. Run the application

```bash
java -jar target/bank-account-management-1.0.0.jar
```

Or using Docker:

```bash
docker build -t bank-account-service .
docker run --network host --env-file .env bank-account-service
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

## Verification

Check the PostgreSQL tables:
- `domain_event_entry` - All events
- `snapshot_event_entry` - Aggregate snapshots
- `current_account_view` - Current account states
- `transaction_history` - Transaction logs
- `token_entry` - Event processor tokens

## Environment Variables

See `.env.example` for required environment variables.
