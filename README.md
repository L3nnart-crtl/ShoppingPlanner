# Shopping Planner

## Installation and Execution

### 1. Website on Virtual Machine (VM)

The website should run on the VM as long as it is not restarted. The URL is:

http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]:80

#### Starting on the VM:

- **Start Backend:**

  Change to the directory `/shoppingPlanner/Backend` and start the backend with the following command:

  nohup sudo java -jar ShoppingPlanner-0.0.1-SNAPSHOT.jar &

- **Start Frontend:**

  Change to the directory `/shoppingPlanner/Frontend/dist` and start the frontend with the following command:

  sudo nohup python3 -m http.server 80 --bind 2001:7c0:2320:1:f816:3eff:fe50:6f6d &> /dev/null &

# Local Project Setup and Start Guide
*(Java 21, Spring Boot, Vue.js)*

This project consists of two main components:
1. **Backend**: A Spring Boot project built and run with Gradle.
2. **Frontend**: A Vue.js project running with Vite as the development server.

This guide explains the steps required to get both parts of the project up and running locally.

---

## Prerequisites

Ensure the following software packages are installed on your system:

### Java Development Kit (JDK) 21

The Spring Boot backend requires Java 21. Install JDK 21.

- **Ubuntu/Linux**:
  ```bash
  sudo apt update
  sudo apt install openjdk-21-jdk
  ```

- **Windows**:
  Download JDK 21 from the official Oracle website and install it.

- **Verify installation**:
  ```bash
  java -version
  ```

Ensure Java 21 is shown as the installed version.

---

### Maven

Maven is required to build and run the Spring Boot backend.

- **Ubuntu/Linux**:
  ```bash
  sudo apt install gradle
  ```

- **Windows**:
  Download Maven from the official website and install it.

- **Verify installation**:
  ```bash
  gradle -v
  ```

---

### Node.js and npm

The Vue.js frontend requires Node.js and npm to install dependencies and start the development server.

- **Ubuntu/Linux**:
  ```bash
  sudo apt update
  sudo apt install nodejs npm
  ```

- **Windows**:
  Download Node.js from the official Node.js website and install it.

- **Verify installation**:
  ```bash
  node -v
  npm -v
  ```

---

## Project Setup

1. **Clone the repository**:
   If you haven't cloned the project yet, do so by running:
   ```bash
   git clone <Repository URL>
   cd <Project Directory>
   ```

---

## Backend (Spring Boot)

1. **Navigate to the backend directory**:
   ```bash
   cd backend
   ```

2. **Build the Spring Boot project with Maven**:
   ```bash
   gradle build
   ```

3. **Start the backend**:
   ```bash
   gradle bootRun
   ```

   Alternatively, you can run the backend directly using the `.jar` file:
   ```bash
   java -jar build/libs/ShoppingPlanner-0.0.1-SNAPSHOT.jar
   ```

   The Spring Boot backend should now be running on `http://localhost:8080`. Open this URL in your browser to verify that it works.

---

## Frontend (Vue.js with Vite)

1. **Navigate to the frontend directory**:
   ```bash
   cd frontend
   ```

2. **Install the frontend dependencies using npm**:
   ```bash
   npm install
   ```

3. **Start the development server**:
   ```bash
   npm run dev
   ```

   The Vue.js frontend should now be running on `http://localhost:80`. Open this URL in your browser to verify that it works.

---

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
