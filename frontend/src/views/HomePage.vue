<template>
  <div class="homepage-container">
    <div class="content-container">
      <!-- Eingabefeld für Rezept -->
      <AddRecipeForm @recipe-added="addRecipe" />

      <!-- Rezeptliste -->
      <RecipeList :recipes="recipes" @recipe-removed="removeRecipe" />
    </div>
  </div>
</template>

<script>
import AddRecipeForm from '@/components/AddRecipeForm.vue';
import RecipeList from '@/components/RecipeList.vue';

export default {
  components: {
    AddRecipeForm,
    RecipeList,
  },
  data() {
    return {
      recipes: [],
    };
  },
  async created() {
    await this.reloadRecipes();
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

    // Rezept zur Liste hinzufügen, wenn es erfolgreich gespeichert wurde
    addRecipe(newRecipe) {
      this.recipes.push(newRecipe);  // Rezept direkt zur Liste hinzufügen
    },

    // Rezept aus der Liste entfernen
    removeRecipe(recipeId) {
      this.recipes = this.recipes.filter(recipe => recipe.id !== recipeId);
    },
  },
};
</script>


<style scoped>
.homepage-container {
  display: flex;
  justify-content: center;
  padding: 40px;
  background-color: #f8f8f8;
}

.content-container {
  display: flex;
  gap: 50px;
  max-width: 1200px;
  width: 100%;
}

.form-container {
  flex: 1 1 400px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  background-color: #ffffff;
  padding: 30px;
  transition: box-shadow 0.3s ease;
}

.form-container:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.recipe-list-container {
  flex: 2 1 800px;
  max-width: 800px;
  width: 100%;
}

input,
textarea {
  padding: 14px;
  font-size: 16px;
  border-radius: 10px;
  border: 1px solid #ccc;
  background-color: #f5f5f5;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

input:focus,
textarea:focus {
  border-color: #4caf50;
  box-shadow: 0 0 5px rgba(76, 175, 80, 0.6);
}

button {
  padding: 14px 20px;
  font-size: 16px;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #4caf50;
}

button:hover {
  background-color: #45a049;
  transform: translateY(-2px);
}

button:active {
  transform: translateY(2px);
}

.add-button {
  background-color: #4caf50;
}

.add-button:hover {
  background-color: #45a049;
}

.submit-button {
  background-color: #3b82f6;
}

.submit-button:hover {
  background-color: #1e5fd1;
}

.remove-button {
  background-color: #e57373;
  color: white;
  padding: 10px;
  font-size: 14px;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.remove-button:hover {
  background-color: #d32f2f;
}

/* Rezeptkarten */
.recipe-card {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.recipe-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.recipe-name {
  font-size: 24px;
  color: #4caf50;
  font-weight: bold;
  margin-bottom: 15px;
}

.recipe-description {
  color: #555;
  margin-bottom: 20px;
}

.ingredient-list {
  list-style-type: none;
  padding-left: 0;
  margin-top: 15px;
}

.ingredient-list li {
  font-size: 16px;
  color: #333;
}

.no-recipes {
  text-align: center;
  font-size: 18px;
  color: #888;
}
</style>
