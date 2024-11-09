@echo off

REM Backend (Spring Boot mit Gradle) starten
echo Starting the backend (Spring Boot with Gradle)...
gradlew bootRun 2>&1  REM Fehlerausgaben anzeigen
if %ERRORLEVEL% neq 0 (
    echo Backend start failed. Check the error messages above.
    pause
    exit /b 1
)
echo Backend started successfully.

REM Frontend (Vue.js) starten
echo Starting the frontend (Vue.js)...
cd frontend  REM Wechsel ins Frontend-Verzeichnis
npm run serve 2>&1  REM Fehlerausgaben anzeigen
if %ERRORLEVEL% neq 0 (
    echo Frontend start failed. Check the error messages above.
    pause
    exit /b 1
)

pause  REM Lässt das Konsolenfenster offen
