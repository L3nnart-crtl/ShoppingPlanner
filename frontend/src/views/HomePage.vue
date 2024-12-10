<template>
  <div class="homepage-container">
    <div class="content-container">
      <!-- Rezept hinzufügen -->
      <div class="add-recipe-container">
        <AddRecipeForm @recipe-added="addRecipe" />
      </div>

      <!-- Rezeptliste und Kalender -->
      <div class="right-container">
        <!-- Rezeptliste -->
        <div class="recipe-list-container">
          <RecipeList :recipes="recipes" @recipe-removed="removeRecipe" ref="recipeList" />
        </div>

        <!-- Kalender-Komponente mit MealPlan-Optionen -->
        <div class="calendar-container">
          <CalendarComponent :recipes="recipes" />
        </div>

        <!-- ShoppingList Formular -->
        <div class="shopping-list-container">
          <ShoppingList />
        </div>

        <!-- Statistiken -->
        <div class="statistics-dashboard-container">
          <StatisticsDashboard />
        </div>
      </div>
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
    await this.reloadRecipes(); // Rezepte beim Erstellen der Komponente laden
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

    // Rezept zur Liste hinzufügen
    async addRecipe(newRecipe) {
      this.recipes.push(newRecipe); // Rezept zur Liste hinzufügen
      this.updateRecipeList(); // Rezeptliste in RecipeList-Komponente aktualisieren
    },

    // Rezept aus der Liste entfernen
    removeRecipe(recipeId) {
      this.recipes = this.recipes.filter((recipe) => recipe.id !== recipeId);
      this.updateRecipeList();
    },

    // Methode zum Aktualisieren der Rezeptliste in der RecipeList-Komponente
    updateRecipeList() {
      if (this.$refs.recipeList) {
        this.$refs.recipeList.recipesUpdated(this.recipes);
      }
    },
  },
};
</script>

<style scoped>
.homepage-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content-container {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 20px;
}

.add-recipe-container,
.recipe-list-container,
.calendar-container,
.shopping-list-container,
.statistics-dashboard-container {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.statistics-dashboard-container {
  grid-column: span 2; /* Die Statistik-Komponente über die gesamte Breite anzeigen */
}
</style>
