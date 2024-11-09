#!/bin/bash

# Backend (Spring Boot mit Gradle) starten
echo "Starting the backend (Spring Boot with Gradle)..."
./gradlew bootRun 2>&1  # Startet Spring Boot und gibt Fehler aus
if [ $? -ne 0 ]; then
    echo "Backend start failed. Check the error messages above."
    read -p "Press any key to continue..."
    exit 1
fi
echo "Backend started successfully."

# Frontend (Vue.js) starten
echo "Starting the frontend (Vue.js)..."
cd frontend  # Wechsel ins Frontend-Verzeichnis
npm run serve 2>&1  # Startet das Frontend

read -p "Press any key to exit..."  # Lässt das Terminal offen
