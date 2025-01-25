@echo off
echo Starting the backend, frontend

:: Start backend (Spring Boot)
start cmd /k "cd backend && gradlew bootRun"

:: Start frontend (Vue.js Vite)
start cmd /k "cd frontend && npm run dev"
