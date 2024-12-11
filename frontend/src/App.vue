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

