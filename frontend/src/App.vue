<template>
  <div id="app">
    <!-- Heading for the app -->
    <h1>Einkaufsplaner</h1>

    <!-- The router view that will display the Login or HomePage depending on the route -->
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>
