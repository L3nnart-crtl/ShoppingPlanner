#!/bin/bash
echo "Starting the backend, frontend"

# Start backend (Spring Boot)
(cd backend && ./gradlew bootRun) &

# Start frontend (Vue.js mit Vite)
(cd frontend && npm run dev) &
