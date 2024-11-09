@echo off
echo Starting the backend and frontend...

:: Start backend (Spring Boot) - Gehe ins Backend-Verzeichnis
start cmd /k "cd backend && gradlew bootRun"

:: Start frontend (Vue.js mit Vite) - Gehe ins Frontend-Verzeichnis
start cmd /k "cd frontend && npm run dev"
