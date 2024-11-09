# Shopping Planner

## Beschreibung

Wir haben das Critical-Feature "Rezept mit Zutaten und Mengenangabe hinzufügen" implementiert.

## Installation und Ausführung

### 1. Website auf der virtuellen Maschine (VM)

Die Website sollte auf der VM laufen, solange diese nicht neu startet. Die URL lautet:

http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]:80


**Starten auf der VM:**

- **Backend starten:**

   Wechseln Sie in das Verzeichnis `/shoppingPlanner/Backend` und starten Sie das Backend mit folgendem Befehl:

nohup sudo java -jar ShoppingPlanner-0.0.1-SNAPSHOT.jar &


- **Frontend starten:**

Wechseln Sie in das Verzeichnis `/shoppingPlanner/Frontend/dist` und starten Sie das Frontend mit folgendem Befehl:

sudo nohup python3 -m http.server 80 --bind 2001:7c0:2320:1:f816:3eff:fe50:6f6d &> /dev/null &

### 2. Lokale Ausführung

Die Adressen für das lokale Setup sind im Git-Repository auf den folgenden Ports konfiguriert:

- **Backend:** `localhost:8080`
- **Frontend:** `localhost:80`
- **Datenbank:** `localhost:3306`

**Datenbank-Schema erstellen:**

Das SQL-Skript zur Erstellung der Datenbank finden Sie unter:

ShoppingPlanner/src/main/resources/create_schema.sql


**Website starten:**

Die Website kann mit der RunConfiguration `Start_website_local` gestartet werden.
URL: http://localhost:80
