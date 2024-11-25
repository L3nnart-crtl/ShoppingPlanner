<template>
  <div class="homepage-container">
    <div class="content-container">
      <!-- Rezept hinzufügen -->
      <div class="add-recipe-container">
        <AddRecipeForm @recipe-added="addRecipe" />
      </div>

      <!-- Rezeptsuche -->
      <div class="search-container">
        <RecipeSearch @search-results="updateRecipes" />
      </div>

      <!-- Rezeptliste und Kalender -->
      <div class="right-container">
        <!-- Rezeptliste -->
        <div class="recipe-list-container">
          <RecipeList :recipes="recipes" @recipe-removed="removeRecipe" />
        </div>

        <!-- Kalender-Komponente mit MealPlan-Optionen -->
        <div class="calendar-container">
          <CalendarComponent :recipes="recipes" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AddRecipeForm from '@/components/AddRecipeForm.vue';
import RecipeList from '@/components/RecipeList.vue';
import CalendarComponent from '@/components/Calendar.vue';
import RecipeSearch from '@/components/RecipeSearch.vue'; // Importiere die Rezept-Suche

export default {
  components: {
    AddRecipeForm,
    RecipeList,
    CalendarComponent,
    RecipeSearch,  // Füge sie hier hinzu
  },
  data() {
    return {
      recipes: [],             // Array der Rezepte
      mealPlans: {},           // MealPlans für die Woche
    };
  },
  async created() {
    await this.reloadRecipes();  // Rezepte beim Erstellen der Komponente laden
  },
  methods: {
    async reloadRecipes() {
      try {
        const response = await this.$axios.get('/recipes');  // Holt alle Rezepte von der API
        this.recipes = response.data;
      } catch (error) {
        console.error('Fehler beim Abrufen der Rezepte:', error);
      }
    },

    // Rezepte nach der Suche aktualisieren
    updateRecipes(filteredRecipes) {
      this.recipes = filteredRecipes;
    },

    // Rezept zur Liste hinzufügen
    addRecipe(newRecipe) {
      this.recipes.push(newRecipe);
    },

    // Rezept aus der Liste entfernen
    removeRecipe(recipeId) {
      this.recipes = this.recipes.filter(recipe => recipe.id !== recipeId);
    },
  },
};
</script>
