# MCP_Comms

## Overview
Spring Boot + WebSocket MCP server with PostgreSQL, frontend test UI, and JWT/OAuth2 security skeleton.

## Run Backend
1. Ensure PostgreSQL is running and database `mcp_comms` exists.
2. Update username/password in `application.properties`.
3. Run backend:
```bash
./gradlew bootRun
```

## Open Frontend
Open `src/main/resources/static/index.html` in browser and test WebSocket features.



###########################################################################

Steps to Run MCP_Comms Project
1. Extract the Project

Unzip MCP_Comms.zip into a directory of your choice.

unzip MCP_Comms.zip
cd MCP_Comms

2. Install Prerequisites

Make sure you have installed:

Java 17 or higher

Gradle (wrapper is included, so no need to install manually)

PostgreSQL (running locally)

3. Setup PostgreSQL Database

Login to PostgreSQL:

psql -U postgres


Create database:

CREATE DATABASE mcp_comms;


(Optional) Create user and grant access:

CREATE USER mcp_user WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE mcp_comms TO mcp_user;


Update credentials in backend/src/main/resources/application.properties if needed:

spring.datasource.url=jdbc:postgresql://localhost:5432/mcp_comms
spring.datasource.username=postgres
spring.datasource.password=postgres

4. Build the Backend

From the project root:

./gradlew clean build

5. Run the Backend Server
./gradlew bootRun


Server will start on http://localhost:8080

WebSocket endpoint: ws://localhost:8080/mcp

6. Test with Frontend UI

Open the file in your browser:

backend/src/main/resources/static/index.html


👉 Features in UI:

Connect MCP Server → opens WebSocket connection

Subscribe Quotes → sends subscribe_resource JSON-RPC request

7. Verify Functionality

When you click Connect, the log should show Connected to MCP Server.

When you click Subscribe Quotes, you’ll see Server received: { ... }.

8. Optional Enhancements

Add JWT/OAuth2 authentication later (already scaffolded).

Deploy with Docker/Kubernetes when ready.