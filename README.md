# Shopping Planner

## Beschreibung

Wir haben das Critical-Feature "Rezept mit Zutaten und Mengenangabe hinzufügen" implementiert. Diese Funktion ermöglicht es Benutzern, Rezepte mit den entsprechenden Zutaten und Mengenangaben zu erstellen und zu verwalten.

## Installation und Ausführung

### 1. Website auf der virtuellen Maschine (VM)

Die Website sollte auf der VM laufen, solange diese nicht neu startet. Die URL lautet:

http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]:80


**Starten auf der VM:**

- **Backend starten:**

   Gehe in das Verzeichnis `/shoppingPlanner/Backend` und starte das Backend mit folgendem Befehl:

nohup sudo java -jar ShoppingPlanner-0.0.1-SNAPSHOT.jar &


- **Frontend starten:**

Gehe in das Verzeichnis `/shoppingPlanner/Frontend/dist` und starte das Frontend mit folgendem Befehl:

sudo python3 -m http.server 80 --bind [2001:7c0:2320:1:f816:3eff:fe50:6f6d]


### 2. Lokale Ausführung

Die Adressen für das lokale Setup sind im Git-Repository auf den folgenden Ports konfiguriert:

- **Backend:** `localhost:8080`
- **Frontend:** `localhost:80`
- **Datenbank:** `localhost:3306`

**Datenbank-Schema erstellen:**

Das SQL-Skript zur Erstellung der Datenbank findest du unter:

ShoppingPlanner/src/main/resources/create_schema.sql


**Website starten:**

Die Website kann mit der RunConfiguration `Start_website_local` gestartet werden.
