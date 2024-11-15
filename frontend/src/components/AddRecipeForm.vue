<template>
  <div class="form-container">
    <h2 class="form-title">Rezept hinzufügen</h2>
    <form @submit.prevent="submitRecipe" class="recipe-form">
      <!-- Rezeptname -->
      <div class="input-group">
        <label for="name">Rezeptname:</label>
        <input type="text" v-model="recipe.name" placeholder="Rezeptname eingeben" required />
      </div>

      <!-- Beschreibung -->
      <div class="input-group">
        <label for="description">Beschreibung:</label>
        <textarea v-model="recipe.description" placeholder="Beschreibung eingeben" required></textarea>
      </div>

      <!-- Zutaten hinzufügen -->
      <div v-for="(ingredient, index) in recipe.ingredients" :key="index" class="ingredient-group">
        <div class="ingredient-item">
          <label>Zutat {{ index + 1 }}:</label>
          <input type="text" v-model="ingredient.name" placeholder="Zutat" required />
          <input type="text" v-model="ingredient.quantity" placeholder="Menge" required />
          <button type="button" @click="removeIngredient(index)" class="remove-button">Entfernen</button>
        </div>
      </div>

      <button type="button" @click="addIngredient" class="add-button">Zutat hinzufügen</button>
      <button type="submit" class="submit-button">Rezept speichern</button>
    </form>

    <!-- Button zum Entfernen des Rezepts -->
    <button v-if="recipe.id" @click="removeRecipe" class="remove-recipe-button">Rezept entfernen</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      recipe: {
        id: null,  // ID des Rezepts, das entfernt werden soll
        name: '',
        description: '',
        ingredients: [{ name: '', quantity: '', unit: '' }],
      },
    };
  },
  methods: {
    addIngredient() {
      this.recipe.ingredients.push({ name: '', quantity: '', unit: '' });
    },
    removeIngredient(index) {
      this.recipe.ingredients.splice(index, 1);
    },
    async submitRecipe() {
      try {
        let response;
        if (this.recipe.id) {
          // Update des Rezepts (wenn eine ID vorhanden ist)
          response = await this.$axios.put(`/recipes/${this.recipe.id}`, this.recipe);
        } else {
          // Neues Rezept erstellen
          response = await this.$axios.post('/recipes', this.recipe);
        }

        // Rezept sofort in die Rezeptliste einfügen
        this.$emit('recipe-added', response.data);

        // Seite neu laden
        window.location.reload();

        // Formular nach der Übertragung zurücksetzen
        this.recipe = { name: '', description: '', ingredients: [{ name: '', quantity: '', unit: '' }] };
      } catch (error) {
        console.error('Fehler beim Hinzufügen des Rezepts:', error);
      }
    },
    async removeRecipe() {
      try {
        // Rezept entfernen
        await this.$axios.delete(`/recipes/${this.recipe.id}`);

        // Erfolgreiches Löschen anzeigen (kann angepasst werden)
        alert('Rezept erfolgreich entfernt');

        // Formular zurücksetzen
        this.recipe = { name: '', description: '', ingredients: [{ name: '', quantity: '', unit: '' }] };
        this.$emit('recipe-removed');
      } catch (error) {
        console.error('Fehler beim Entfernen des Rezepts:', error);
      }
    },
  },
};
</script>

// Design
<style scoped>
.form-container {
  max-width: 700px;
  margin: auto;
  padding: 30px;
  border-radius: 12px;
  background: linear-gradient(145deg, #f3f4f9, #e5e8ef);
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.1);
}

.form-title {
  text-align: center;
  color: #4caf50;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 30px;
}

.recipe-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group label {
  font-size: 18px;
  color: #333;
}

input,
textarea {
  padding: 12px;
  font-size: 16px;
  border-radius: 10px;
  border: 1px solid #ddd;
  box-sizing: border-box;
  margin-top: 8px;
  width: 100%;
  background-color: #fff;
  transition: all 0.3s ease;
}

input:focus,
textarea:focus {
  border-color: #4caf50;
  outline: none;
  box-shadow: 0 0 5px rgba(76, 175, 80, 0.6);
}

textarea {
  resize: vertical;
}

button {
  padding: 14px;
  font-size: 16px;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 10px;
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
}

.remove-button:hover {
  background-color: #d32f2f;
}
</style>
