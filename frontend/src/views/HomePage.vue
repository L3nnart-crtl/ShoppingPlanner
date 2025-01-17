<template>
  <div class="container">
    <!-- Formular für Rezepte -->
    <div class="component">
      <AddRecipeForm @recipe-added="addRecipe" />
    </div>

    <!-- Rezeptliste -->
    <div class="component">
      <RecipeList :recipes="recipes" @recipe-removed="removeRecipe" ref="recipeList" />
    </div>

    <!-- Einkaufsliste -->
    <div class="component">
      <ShoppingList />
    </div>
    <!-- Statistiken -->
    <div class="component">
      <StatisticsDashboard />
    </div>

    <!-- Kalender -->
    <div class="component">
      <CalendarComponent :recipes="recipes" />
    </div>
  </div>
</template>

<script>
import AddRecipeForm from "@/components/AddRecipeForm.vue";
import RecipeList from "@/components/RecipeList.vue";
import CalendarComponent from "@/components/Calendar.vue";
import ShoppingList from "@/components/ShoppingList.vue";
import StatisticsDashboard from "@/components/StatisticsDashboard.vue";

export default {
  components: {
    AddRecipeForm,
    RecipeList,
    CalendarComponent,
    ShoppingList,
    StatisticsDashboard,
  },
  data() {
    return {
      recipes: [],
    };
  },
  async created() {
    // CSRF-Token aus dem Cookie holen und im Header setzen
    const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
    this.$axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

    await this.reloadRecipes();
  },
  methods: {
    async reloadRecipes() {
      try {
        const response = await this.$axios.get("/recipes");
        this.recipes = response.data;
      } catch (error) {
        console.error("Fehler beim Abrufen der Rezepte:", error);
      }
    },
    async addRecipe(newRecipe) {
      this.recipes.push(newRecipe);
      this.updateRecipeList();
    },
    removeRecipe(recipeId) {
      this.recipes = this.recipes.filter((recipe) => recipe.id !== recipeId);
      this.updateRecipeList();
    },
    updateRecipeList() {
      if (this.$refs.recipeList) {
        this.$refs.recipeList.recipesUpdated(this.recipes);
      }
    },
  },
};
</script>

<style scoped>
.container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-family: Arial, sans-serif;

  padding: 20px;
}

@media (max-width: 768px) {
  .component {
    flex: 1 1 100%;
  }
}
</style>
