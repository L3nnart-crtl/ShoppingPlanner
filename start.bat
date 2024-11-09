@echo off

:: Backend starten (in einem neuen Fenster)
echo Starting backend...
start cmd /k "gradlew bootRun"

:: Warte kurz, um sicherzustellen, dass das Backend läuft
timeout /t 5

:: Frontend starten (in einem neuen Fenster)
echo Starting frontend...
start cmd /k "cd frontend && npm run serve"

pause
