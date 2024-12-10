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
import axios from "axios";

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
        ingredients: [{ name: '', quantity: '', unit: '' }],
        tags: [],
      },
      selectedTags: [],
      allTags: tags,
      quantityUnits: quantityUnits,
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
          ingredients: [{ name: '', quantity: '', unit: '' }],
          tags: []
        };

        // Tags zurücksetzen
        this.selectedTags = [];

        // Sicherstellen, dass Multiselect zurückgesetzt wird
        await this.$nextTick(() => {
          if (this.$refs.multiselect) {
            this.$refs.multiselect.clearSelection();
          }
        });
        // Rezept wurde hinzugefügt

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
.form-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 6px;
}

.input-group input, .input-group textarea {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.tag-selection {
  margin-bottom: 20px;
}

.submit-button, .add-button, .remove-button {
  padding: 12px;
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
  margin-top: 20px;
}

.unit-selection {
  margin-top: 12px;
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
