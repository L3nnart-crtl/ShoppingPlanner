<template>
  <div id="app" class="app-container">
    <!-- Heading for the app -->
    <h1 class="app-title">Einkaufsplaner</h1>
    <!-- The router view that will display the Login or HomePage depending on the route -->
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f4f8, #d9e9f1);
  font-family: Arial, sans-serif;
  padding: 20px;
  box-sizing: border-box;
}

.app-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #007BFF;
  text-align: center;
  margin-bottom: 20px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.logout-container {
  position: absolute;
  top: 20px;
  right: 20px;
}

.logout-button {
  padding: 10px 20px;
  font-size: 1rem;
  color: white;
  background-color: #007BFF;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease;
}

.logout-button:hover {
  background-color: #0056b3;
}
</style>
