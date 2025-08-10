#!/bin/bash

# Clean up any previous containers
docker compose down -v >/dev/null 2>&1

# Build and start services
echo "🚀 Building and starting services..."
docker compose up --build -d

# Wait for MySQL to be ready
echo "⏳ Waiting for MySQL to initialize..."
while ! docker exec mysql-db mysqladmin ping -uuser1 -ppass1 --silent; do
    sleep 2
done

# Wait for Spring Boot to start - More robust approach
echo "⏳ Waiting for application to start..."
MAX_WAIT=120  # 2 minutes maximum wait
WAITED=0
SUCCESS=0

while [ $WAITED -lt $MAX_WAIT ]; do
    # Check via HTTP endpoint
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)

    # Check via logs (multiple possible success messages)
    LOGS_CHECK=$(docker logs transaction-service 2>&1 | grep -E "Started Transaction|Tomcat started|Netty started")

    if [ "$HTTP_STATUS" == "200" ] || [ -n "$LOGS_CHECK" ]; then
        SUCCESS=1
        break
    fi

    sleep 5
    WAITED=$((WAITED + 5))
    echo "   ... waited $WAITED seconds"
done

# Verify if application started
if [ $SUCCESS -eq 0 ]; then
    echo "❌ Application failed to start within $MAX_WAIT seconds"
    echo "🔍 Last logs from transaction-service:"
    docker logs transaction-service --tail 50
    exit 1
fi

# Get API URL
API_URL="http://localhost:8080"
echo -e "\n✅ Services ready!"
echo -e "📦 MySQL: localhost:3306 (user1/pass1)"
echo -e "🌐 API: $API_URL"
echo -e "📚 Swagger UI: $API_URL/swagger-ui.html"