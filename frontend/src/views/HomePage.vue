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
          <RecipeList :recipes="recipes" @recipe-removed="removeRecipe" ref="recipeList"/>
        </div>

        <!-- Kalender-Komponente mit MealPlan-Optionen -->
        <div class="calendar-container">
          <CalendarComponent :recipes="recipes" />
        </div>

        <!-- ShoppingList Formular -->
        <div class="shopping-list-container">
          <ShoppingList />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AddRecipeForm from '@/components/AddRecipeForm.vue';
import RecipeList from '@/components/RecipeList.vue';
import CalendarComponent from '@/components/Calendar.vue';
import ShoppingList from '@/components/ShoppingList.vue';

export default {
  components: {
    AddRecipeForm,
    RecipeList,
    CalendarComponent,
    ShoppingList,
  },
  data() {
    return {
      recipes: [],
    };
  },
  async created() {
    await this.reloadRecipes();  // Rezepte beim Erstellen der Komponente laden
  },
  methods: {
    async reloadRecipes() {
      try {
        const response = await this.$axios.get('/recipes');
        this.recipes = response.data;
      } catch (error) {
        console.error('Fehler beim Abrufen der Rezepte:', error);
      }
    },

    // Rezept zur Liste hinzufügen
    async addRecipe(newRecipe) {
      this.recipes.push(newRecipe);  // Rezept zur Liste hinzufügen
      this.updateRecipeList();       // Rezeptliste in RecipeList-Komponente aktualisieren
    },

    // Rezept aus der Liste entfernen
    removeRecipe(recipeId) {
      this.recipes = this.recipes.filter(recipe => recipe.id !== recipeId);
      this.updateRecipeList();
    },

    // Methode zum Aktualisieren der Rezeptliste in der RecipeList-Komponente
    updateRecipeList() {
      if (this.$refs.recipeList) {
        this.$refs.recipeList.recipesUpdated(this.recipes);
      }
    }
  },
};
</script>
