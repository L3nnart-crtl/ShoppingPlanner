# Shopping Planner

## Installation and Execution

# Local Project Setup and Start Guide

Prerequisites

Make sure the following software is installed:

    Java: Version 21
    Gradle: Version 7 or higher
    Node.js: Version 18 or higher
    npm: Version 8 or higher
    MariaDB: Version 10.4 or higher (or any compatible version)

Folder Structure

    backend/: Contains the Spring Boot backend.
    frontend/: Contains the Vue frontend.

## Backend and Frontend Startup

You can automate the startup of both backend and frontend using the following scripts.

1. **Navigate to the frontend directory**:
   ```bash
   cd frontend
   ```

2. **Install the frontend dependencies using npm**:
   ```bash
   npm install
   ```
Now you can start the Application using the following start skripts:

### Windows: `start.bat`

### Linux: `start.sh`

On Linux, you can use a bash script.


Make the script executable:

```bash
chmod +x start.sh
```

Execute the script:

```bash
./start.sh
```

This will open two separate processes: one for the Spring Boot backend and one for the Vue.js frontend.
The Vue.js frontend should now be running on `http://localhost:80`.
---

## Additional Notes

- **Backend Troubleshooting**: If the backend doesn’t start, make sure that all dependencies are correctly installed and there are no port conflicts. Check the console output for any errors.

- **Frontend Troubleshooting**: If the frontend doesn’t start, check the npm error messages and ensure that all necessary dependencies are installed. You might need to update the `package.json` file.

- **Port Conflicts**: If the default ports (8080 for the backend and 3000 for the frontend) are already in use by other applications, you can change the ports in the configuration files:
  - **Spring Boot**: Modify `application.properties` or `application.yml`.
  - **Vue.js**: Change the port in the `vite.config.js` file.
