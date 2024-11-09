#!/bin/bash
echo "Starting the backend and frontend..."

# Start backend (Spring Boot) - Wurzelverzeichnis
./gradlew bootRun &

# Start frontend (Vue.js mit Vite) - Nur ins Frontend-Verzeichnis wechseln
(cd frontend && npm run dev) &
