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

export default {
  components: {
    AddRecipeForm,
    RecipeList,
    CalendarComponent,
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
        const response = await this.$axios.get('/recipes');  // Holt Rezepte von der API
        this.recipes = response.data;
      } catch (error) {
        console.error('Fehler beim Abrufen der Rezepte:', error);
      }
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

<style scoped>
/* Stil für die Homepage */
.homepage-container {
  display: flex;
  justify-content: center; /* Horizontale Zentrierung */
  align-items: flex-start; /* Oben ausrichten */
  min-height: 100vh;       /* Höhe auf 100% des Viewports */
  padding: 20px;           /* Abstand zum Rand */
  gap: 20px;               /* Abstand zwischen den Komponenten */
}

.content-container {
  display: flex;
  flex-direction: row;     /* Seitenlayout mit Formular und rechter Spalte */
  gap: 20px;
  flex-wrap: wrap;         /* Damit die Komponenten sich bei Bedarf umsortieren */
}

.add-recipe-container {
  width: 300px;            /* Breite für das Rezeptformular */
}

.right-container {
  display: flex;
  flex-direction: column;  /* Rezeptliste und Kalender untereinander */
  align-items: flex-start; /* Links ausrichten */
  flex: 1;
  max-width: 800px;        /* Maximale Breite für den rechten Bereich */
  flex-grow: 1;
}

.recipe-list-container {
  width: 100%;
  margin-bottom: 20px;     /* Abstand zum nächsten Element */
}

.calendar-container {
  width: 100%;
  margin-top: 10px;        /* Kalender direkt unter der Rezeptliste */
  margin-bottom: 20px;     /* Platz nach unten */
  display: flex;
  flex-direction: column;
  gap: 10px;               /* Kleinerer Abstand */
}

/* Zusätzliche Anpassungen für mobile Geräte */
@media (max-width: 768px) {
  .homepage-container {
    flex-direction: column;
    align-items: center;
  }

  .left-container, .right-container {
    width: 100%;
    max-width: 100%;
  }
}
</style>
