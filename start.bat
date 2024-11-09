@echo off

REM Backend starten
echo Starting backend...
start gradlew bootRun  REM Das Backend in einem neuen Fenster starten

REM Warte kurz, um sicherzustellen, dass das Backend läuft
timeout /t 5

REM Frontend starten
echo Starting frontend...
cd frontend  REM Wechsel ins Frontend-Verzeichnis
npm run serve  REM Das Frontend starten

pause  REM Hält das Fenster offen
