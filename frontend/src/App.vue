<template>
  <div id="app">
    <!-- Heading for the app -->
    <h1>Einkaufsplaner</h1>

    <!-- HomePage component, which includes AddRecipeForm and RecipeList -->
    <HomePage />
  </div>
</template>

<script>
import HomePage from '@/views/HomePage.vue';

export default {
  components: {
    HomePage,
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  margin-top: 20px;
}

h1 {
  color: #008cba;
}
</style>
