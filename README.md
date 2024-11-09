# team-6
Readme:
Wir haben das Critical-Feature Rezept mit Zutaten und Mengenangabe hinzufügen implementiert. 
Website sollte auf VM laufen (solange sie nicht neustartet):
URL: http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]:80
Kann auf VM gestartet werden mit:
Backend starten in:
/shoppingPlanner/Backend
Mit:
nohup sudo java -jar ShoppingPlanner-0.0.1-SNAPSHOT.jar &
Frontend starten in:
/shoppingPlanner/Frontend/dist
Mit:
sudo python3 -m http.server 80 --bind [2001:7c0:2320:1:f816:3eff:fe50:6f6d]

Local:
Die Adressen sind im Git-Repository Projekt auf localhost gesetzt mit Port 8080 für Backend und Port 80 für Frontend und Port 3306 für DB.
Das SQL-Skript zu Erstellung der Datenbank findet sich in:
ShoppingPlanner\src\main\resources\create_schema.sql
Die Website kann gestartet werden mit der RunConfiguration: Start_website_local
