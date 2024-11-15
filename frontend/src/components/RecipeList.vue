<template>
  <div class="recipe-list-container">
    <h2 class="list-title">Rezepte</h2>
    <div v-if="recipes.length" class="recipe-cards-container">
      <div v-for="recipe in recipes" :key="recipe.id" class="recipe-card">
        <h3 class="recipe-name">{{ recipe.name || 'Kein Name' }}</h3>
        <p class="recipe-description">{{ recipe.description || 'Keine Beschreibung verfügbar.' }}</p>
        <ul class="ingredient-list">
          <li v-for="(ingredient, index) in recipe.ingredients" :key="index">
            {{ ingredient.name }} - {{ ingredient.quantity }} {{ ingredient.unit }}
          </li>
        </ul>
        <button @click="removeRecipe(recipe.id)" class="delete-button">Rezept löschen</button>
      </div>
    </div>
    <p v-else class="no-recipes">Keine Rezepte verfügbar.</p>
  </div>
</template>

<script>
export default {
  props: {
    recipes: Array,
  },
  methods: {
    async removeRecipe(recipeId) {
      try {
        // Rezept löschen (DELETE-Anfrage an Backend-API)
        await this.$axios.delete(`recipes/${recipeId}`);

        // Erfolgreiches Löschen anzeigen (kann angepasst werden)
        alert('Rezept erfolgreich entfernt');

        // Rezept aus der angezeigten Liste entfernen
        this.$emit('recipe-removed', recipeId);
      } catch (error) {
        console.error('Fehler beim Entfernen des Rezepts:', error);
      }
    },
  },
};
</script>

//Design
<style scoped>
.recipe-list-container {
  max-width: 900px;
  margin: auto;
  padding: 20px;
}

.list-title {
  text-align: center;
  font-size: 28px;
  color: #333;
  font-weight: bold;
  margin-bottom: 20px;
}

.recipe-cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
}

.recipe-card {
  background-color: #fff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.recipe-card:hover {
  transform: translateY(-5px);
  box-shadow: 0px 8px 20px rgba(0, 0, 0, 0.2);
}

.recipe-name {
  font-size: 18px;
  color: #4caf50;
  font-weight: bold;
  margin-bottom: 10px;
}

.recipe-description {
  color: #555;
  font-size: 14px;
  margin-bottom: 15px;
}

.ingredient-list {
  list-style-type: none;
  padding-left: 0;
  margin-top: 10px;
}

.ingredient-list li {
  font-size: 14px;
  color: #333;
}

.no-recipes {
  text-align: center;
  font-size: 16px;
  color: #888;
}
</style>
