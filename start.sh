#!/bin/bash
echo "Starting the backend and frontend..."

# Start backend (Spring Boot) - Gehe ins Backend-Verzeichnis
(cd backend && ./gradlew bootRun) &

# Start frontend (Vue.js mit Vite) - Gehe ins Frontend-Verzeichnis
(cd frontend && npm run dev) &
