#!/bin/bash

echo "🛑 Stopping services..."
docker compose down

# Optional: Uncomment to remove volumes (deletes database data)
# docker compose down -v