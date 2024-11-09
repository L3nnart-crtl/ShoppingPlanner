# Shopping Planner

## Beschreibung

Wir haben das Critical-Feature "Rezept mit Zutaten und Mengenangabe hinzufügen" implementiert. Diese Funktion ermöglicht es Benutzern, Rezepte mit den entsprechenden Zutaten und Mengenangaben zu erstellen und zu verwalten.

## Installation und Ausführung

### 1. Website auf der virtuellen Maschine (VM)

Die Website sollte auf der VM laufen, solange diese nicht neu startet. Die URL lautet:

[http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]:80](http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]:80)

**Starten auf der VM:**

- **Backend starten:**

   Gehe in das Verzeichnis `/shoppingPlanner/Backend` und starte das Backend mit folgendem Befehl:

   ```bash
   nohup sudo java -jar ShoppingPlanner-0.0.1-SNAPSHOT.jar &
