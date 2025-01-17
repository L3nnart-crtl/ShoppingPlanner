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

      <!-- Kochzeit -->
      <div class="input-group">
        <label for="cookingTime">Kochzeit (in Minuten):</label>
        <input type="number" v-model="recipe.cookingTime" placeholder="Kochzeit in Minuten" min="1" required />
      </div>

      <!-- Zutaten hinzufügen -->
      <div class="ingredients-container">
        <label>Zutaten:</label>
        <div class="ingredient-list" style="max-height: 50px; overflow-y: auto;">
          <div v-for="(ingredient, index) in recipe.ingredients" :key="index" class="ingredient-item">
            <span>{{ ingredient.name }} - {{ ingredient.quantity }} {{ getUnitLabel(ingredient.unit) }}</span>
            <button type="button" @click="removeIngredient(index)" class="remove-button">Entfernen</button>
          </div>
        </div>

        <!-- Eingabefelder für Zutat -->
        <div class="input-group">
          <input type="text" v-model="newIngredient.name" placeholder="Zutat" />
          <input type="text" v-model="newIngredient.quantity" placeholder="Menge" />
          <select v-model="newIngredient.unit">
            <option v-for="unit in quantityUnits" :key="unit.value" :value="unit.value">{{ unit.label }}</option>
          </select>
        </div>

        <button type="button" @click="addIngredient" class="add-button">Zutat hinzufügen</button>
      </div>

      <!-- Tag-Auswahl mit vue-multiselect -->
      <div class="tag-selection">
        <label for="tags">Wähle Tags:</label>
        <multiselect
            ref="multiselect"
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
import { tags, quantityUnits, tagMapping } from '@/assets/TagsAndUnits.js';
import axios from 'axios';

export default {
  components: {
    Multiselect
  },
  data() {
    return {
      recipe: {
        name: '',
        description: '',
        cookingTime: null,
        ingredients: [],
        tags: [],
      },
      newIngredient: { name: '', quantity: '', unit: '' }, // Eingabewerte für neue Zutat
      selectedTags: [],
      allTags: tags,
      quantityUnits: quantityUnits,
      isSubmitting: false,
    };
  },
  methods: {
    addIngredient() {
      if (this.newIngredient.name && this.newIngredient.quantity && this.newIngredient.unit) {
        // Zutat zur Liste hinzufügen
        this.recipe.ingredients.push({
          ...this.newIngredient,  // Fügt die neue Zutat hinzu, ohne sie zu verändern
        });

        // Leere das Eingabefeld
        this.newIngredient = { name: '', quantity: '', unit: '' };
      } else {
        alert('Bitte alle Felder ausfüllen!');
      }
    },
    removeIngredient(index) {
      this.recipe.ingredients.splice(index, 1);
    },
    getUnitLabel(unitValue) {
      const unit = this.quantityUnits.find(unit => unit.value === unitValue);
      return unit ? unit.label : unitValue; // Falls keine Übereinstimmung gefunden wird, den value zurückgeben
    },
    translateTags(tags) {
      return tags.map(tag => tagMapping[tag] || tag);
    },
    async submitRecipe() {
      if (this.isSubmitting) return;
      this.isSubmitting = true;

      try {
        // CSRF-Token aus dem Cookie holen
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];

        // CSRF-Token in den Header der Anfrage setzen
        axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        // Tags übersetzen
        this.recipe.tags = this.translateTags(this.selectedTags.map(tag => tag.name));

        // Rezept an die API senden
        const response = await this.$axios.post('/recipes', this.recipe);

        this.$emit('recipe-added', response.data);

        // Formular und Tags zurücksetzen
        this.recipe = {
          name: '',
          description: '',
          cookingTime: null,
          ingredients: [],
          tags: []
        };

        // Tags zurücksetzen
        this.selectedTags = [];


      } catch (error) {
        console.error('Fehler beim Hinzufügen des Rezepts:', error);
      } finally {
        this.isSubmitting = false;
      }
    }
  },
};
</script>

<style scoped>
/* Allgemeine Container */
.form-container {
  width: 250px;
  max-width: 500px;
  margin: 0 auto;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  font-family: Arial, sans-serif;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.form-title {
  text-align: center;
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}


.recipe-form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

label {
  font-size: 0.875rem;
  color: #333;
}

input, textarea, select {
  padding: 0.5rem;
  font-size: 0.9rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-family: Arial, sans-serif;
  transition: border-color 0.3s;
}

input:focus, textarea:focus, select:focus {
  border-color: #3f51b5;
  outline: none;
}

textarea {
  resize: vertical;
  min-height: 80px;
}

input::placeholder, textarea::placeholder {
  color: #aaa;
}


.ingredients-container {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.ingredient-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-height: 150px;
  overflow-y: auto;
}

.ingredient-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.ingredient-item input {
  flex: 1;
}

.remove-button {
  background-color: #ff4c4c;
  color: white;
  padding: 0.25rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.remove-button:hover {
  background-color: #ff1c1c;
}

.add-button {
  background-color: #4caf50;
  color: white;
  padding: 0.5rem 0.75rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  align-self: flex-start;
}

.add-button:hover {
  background-color: #388e3c;
}


.tag-selection {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.tag-dropdown {
  border-radius: 4px;
}


.submit-button {
  background-color: #3f51b5;
  color: white;
  padding: 0.625rem 0.75rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.submit-button:disabled {
  background-color: #b0bec5;
  cursor: not-allowed;
}

.submit-button:hover:not(:disabled) {
  background-color: #303f9f;
}


@media (max-width: 600px) {
  .form-container {
    padding: 0.75rem;
  }

  .ingredient-item {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
