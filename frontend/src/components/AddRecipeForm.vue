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

          <!-- Einheit auswählen mit Label -->
          <div class="unit-selection">
            <label for="unit">Einheit:</label>
            <select v-model="ingredient.unit" required>
              <option v-for="unit in quantityUnits" :key="unit.value" :value="unit.value">
                {{ unit.label }}
              </option>
            </select>
          </div>

          <button type="button" @click="removeIngredient(index)" class="remove-button">Entfernen</button>
        </div>
      </div>

      <button type="button" @click="addIngredient" class="add-button">Zutat hinzufügen</button>

      <!-- Tag-Auswahl mit vue-multiselect -->
      <div class="tag-selection">
        <label for="tags">Wähle Tags:</label>
        <multiselect
            v-model="selectedTags"
            :options="allTags"
            :multiple="true"
            :close-on-select="false"
            placeholder="Tags auswählen"
            label="name"
            track-by="name"
            :tag-placeholder="'Tag hinzufügen'"
            class="tag-dropdown"
        >
        </multiselect>
      </div>

      <button type="submit" class="submit-button" :disabled="isSubmitting">Rezept speichern</button>
    </form>
  </div>
</template>


<script>
import Multiselect from 'vue-multiselect';
import 'vue-multiselect/dist/vue-multiselect.min.css';
// In deiner Vue-Komponente
import { tags, quantityUnits, tagMapping } from '@/assets/TagsAndUnits.js';

export default {
  components: {
    Multiselect
  },
  data() {
    return {
      recipe: {
        name: '',
        description: '',
        ingredients: [{ name: '', quantity: '', unit: '' }],
        tags: [],
      },
      selectedTags: [],
      allTags: tags, // Hier verwendest du die Tags aus der importierten Datei
      quantityUnits: quantityUnits, // Ebenso für die Mengenangaben
      isSubmitting: false,
    };
  },
  methods: {
    addIngredient() {
      this.recipe.ingredients.push({ name: '', quantity: '', unit: '' });
    },
    removeIngredient(index) {
      this.recipe.ingredients.splice(index, 1);
    },
    translateTags(tags) {
      return tags.map(tag => tagMapping[tag] || tag);
    },
    async submitRecipe() {
      if (this.isSubmitting) return;
      this.isSubmitting = true;

      try {
        this.recipe.tags = this.translateTags(this.selectedTags.map(tag => tag.name));
        const response = await this.$axios.post('/recipes', this.recipe);
        this.$emit('recipe-added', response.data);
        this.recipe = { name: '', description: '', ingredients: [{ name: '', quantity: '', unit: '' }], tags: [] };
        this.selectedTags = [];
      } catch (error) {
        console.error('Fehler beim Hinzufügen des Rezepts:', error);
      } finally {
        this.isSubmitting = false;
      }
    },
  },
};
</script>
<style scoped>
.form-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.input-group {
  margin-bottom: 20px; /* Etwas mehr Abstand zwischen den Feldern */
}

.input-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 6px; /* Mehr Abstand zwischen Label und Eingabefeld */
}

.input-group input, .input-group textarea {
  width: 100%;
  padding: 10px; /* Größeres Padding für mehr Platz im Eingabefeld */
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.tag-selection {
  margin-bottom: 20px; /* Mehr Abstand für die Tag-Auswahl */
}

.submit-button, .add-button, .remove-button {
  padding: 12px; /* Etwas mehr Padding für die Buttons */
  font-size: 16px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.submit-button:disabled {
  background-color: #ccc;
}

.add-button {
  background-color: #008CBA;
}

.remove-button {
  background-color: #f44336;
}

.submit-button {
  width: 100%;
  margin-top: 20px; /* Mehr Abstand zwischen dem letzten Button und anderen Feldern */
}

/* Einheit Dropdown anpassen */
.unit-selection {
  margin-top: 12px; /* Abstand zwischen den Feldern und der Einheit-Auswahl */
}

.unit-selection label {
  display: block;
  font-weight: bold;
  margin-bottom: 6px;
}

.unit-selection select {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-top: 5px;
}

/* Dropdown für die Einheiten */
.unit-dropdown {
  width: 100%;
  max-width: 150px;
  padding: 5px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
  max-height: 120px;
  overflow-y: auto;
}
</style>
