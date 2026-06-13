#!/bin/bash
# replay-events.sh
# Triggers a replay of events for the current_account_view processing group.
# Usage: ./replay-events.sh [processing_group]
# Default processing group: current_account_view

set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
PROCESSING_GROUP="${1:-current_account_view}"

echo "Triggering event replay for processing group: $PROCESSING_GROUP"
echo "Target: $BASE_URL/api/admin/replay/$PROCESSING_GROUP"

RESPONSE=$(curl -s -X POST "$BASE_URL/api/admin/replay/$PROCESSING_GROUP" \
  -H "Content-Type: application/json")

echo "Response: $RESPONSE"
echo ""
echo "Replay triggered successfully. The projection will be rebuilt from the event store."
