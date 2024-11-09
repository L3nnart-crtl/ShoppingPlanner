@echo off
echo Starting the backend and frontend...

:: Start backend (Spring Boot) - Wurzelverzeichnis
start cmd /k "gradlew bootRun"

:: Start frontend (Vue.js mit Vite) - Nur ins Frontend-Verzeichnis wechseln
start cmd /k "cd frontend && npm run dev"
