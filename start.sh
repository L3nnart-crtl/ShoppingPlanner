#!/bin/bash

# Backend starten
echo "Starting backend..."
./gradlew bootRun &  # Backend im Hintergrund starten

# Warte kurz, um sicherzustellen, dass das Backend läuft
sleep 5

# Frontend starten
echo "Starting frontend..."
cd frontend  # Wechsel ins Frontend-Verzeichnis
npm run serve  # Startet das Vue.js-Frontend

wait  # Wartet, bis das Skript beendet wird
