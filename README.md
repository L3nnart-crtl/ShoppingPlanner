Shopping Planner
Description

We have implemented the critical feature "Add recipe with ingredients and quantities."
Installation and Execution
1. Website on Virtual Machine (VM)

The website should run on the VM as long as it is not restarted. The URL is:

http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]:80

Starting on the VM:

    Start Backend:

    Change to the directory /shoppingPlanner/Backend and start the backend with the following command:

nohup sudo java -jar ShoppingPlanner-0.0.1-SNAPSHOT.jar &

Start Frontend:

Change to the directory /shoppingPlanner/Frontend/dist and start the frontend with the following command:

    sudo nohup python3 -m http.server 80 --bind 2001:7c0:2320:1:f816:3eff:fe50:6f6d &> /dev/null &

2. Local Execution

The addresses for the local setup are configured on the following ports in the Git repository:

    Backend: localhost:8080
    Frontend: localhost:80
    Database: localhost:3306

Create Database Schema:

The SQL script to create the database can be found at:

ShoppingPlanner/backend/src/main/resources/create_schema.sql

Start Website:

    The website can be started with the RunConfiguration Start_website_local.
    Or with start.bat or start.sh in the root directory. The URL will be:

http://localhost:80
