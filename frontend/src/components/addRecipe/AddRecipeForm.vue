<template>
  <div class="form-container">
    <h2 class="form-title">Rezept hinzufügen</h2>
    <form @submit.prevent="submitRecipe" class="recipe-form">
      <!-- Rezeptname -->
      <div class="input-group">
        <input type="text" v-model="recipe.name" placeholder="Rezeptname eingeben" required />
      </div>

      <!-- Beschreibung -->
      <div class="input-group">
        <textarea v-model="recipe.description" placeholder="Beschreibung eingeben" required></textarea>
      </div>

      <!-- Kochzeit -->
      <div class="input-group">
        <input type="number" v-model="recipe.cookingTime" placeholder="Kochzeit in Minuten" min="1" required />
      </div>

      <!-- Zutaten anzeigen -->
      <div class="ingredient-list">
        <h3>Zutaten</h3>
        <ul>
          <li v-for="(ingredient, index) in recipe.ingredients" :key="index">
            - {{ ingredient.name }} - {{ ingredient.quantity }} {{ getUnitLabel(ingredient.unit) }}
          </li>
        </ul>
      </div>

      <!-- Add Ingredient Button -->
      <button @click="showIngredientModal()" type="button" class="add-ingredient-button">Zutat hinzufügen</button>

      <!-- Ingredient Modal -->
      <ingredient-modal
          v-if="isIngredientModalVisible"
          :is-visible="isIngredientModalVisible"
          :newManualIngredient="ingredient"
          @add-ingredient="addIngredient"
          @close-modal="closeIngredientModal"
      />

      <!-- Tag-Auswahl -->
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
import IngredientModal from './IngredientModal.vue';
import Multiselect from 'vue-multiselect';
import 'vue-multiselect/dist/vue-multiselect.min.css';
import {quantityUnits, tagsForList} from "@/assets/TagsAndUnits.js";

export default {
  components: {
    IngredientModal,
    Multiselect
  },
  data() {
    return {
      isIngredientModalVisible: false,
      recipe: {
        name: '',
        description: '',
        ingredients: [],
        cookingTime: null,
        tags: [],
      },
      ingredient: {
        name: '',
        quantity: '',
        unit: '',
        calories: 0,
        carbohydrates: 0,
        proteins: 0,
        fats: 0,
      },
      selectedTags: [],
      allTags: tagsForList, // Tags aus der externen Datei (TagsAndUnits.js)
      quantityUnits,
      isSubmitting: false,
    };
  },
  methods: {
    showIngredientModal() {
      this.isIngredientModalVisible = true;
    },
    closeIngredientModal() {
      this.isIngredientModalVisible = false;
      this.ingredient = {
        name: '',
        quantity: '',
        unit: '',
        calories: 0,
        carbohydrates: 0,
        proteins: 0,
        fats: 0,
      }
    },
    addIngredient(ingredient) {
      // Hinzufügen der Zutat zur Rezeptliste
      this.recipe.ingredients.push(ingredient);
    },

    async submitRecipe() {
      if (this.isSubmitting) return;
      this.isSubmitting = true;

      try {
        // Tags hinzufügen
        // Make sure each tag sent is an object with a 'name' property
        this.recipe.tags = this.selectedTags.map(tag => tag.value);

        const response = await this.$axios.post('/recipes', this.recipe);

        this.$emit('recipe-added', response.data);
        this.recipe = {name: '', description: '', cookingTime: null, ingredients: [], tags: []};
        this.selectedTags = [];
      } catch (error) {
        console.error('Fehler beim Hinzufügen des Rezepts:', error);
      } finally {
        this.isSubmitting = false;
      }
    },
    getUnitLabel(unit) {
      const unitObj = quantityUnits.find(u => u.value === unit);
      return unitObj ? unitObj.label : unit;
    },
  },
};
</script>

<style scoped>
/* Allgemeine Container-Stile */
.form-container {
  width: 350px;
  height: 690px;
  font-family: Arial, sans-serif;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f9f9f9;
  margin: 0 auto;
  border-radius: 8px;
}

.form-title {
  font-size: 24px;
  margin-bottom: 20px;
}

.recipe-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
}

input, textarea {
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

textarea {
  resize: vertical;
}

.submit-button {
  background-color: #28a745;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

.submit-button:disabled {
  background-color: #d6d6d6;
}

.add-ingredient-button {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.add-ingredient-button:hover {
  background-color: #0056b3;
}

.ingredient-list {
  max-height: 300px; /* Höhe des scrollbaren Bereichs */
  max-width: 300px;
  overflow-y: auto; /* Ermöglicht das vertikale Scrollen */
  word-wrap: break-word; /* Zeilenumbruch für lange Wörter */
  white-space: normal; /* Standardzeilenumbruch für Text */
  padding: 10px; /* Abstand innerhalb des Containers */
  border: 1px solid #ccc; /* Rahmen um das Zutatenfeld */
  border-radius: 5px; /* Abgerundete Ecken */
  background-color: #f9f9f9; /* Hintergrundfarbe */
}

.ingredient-list ul {
  list-style-type: none;
  padding: 0;
}

.ingredient-list li {
  margin: 5px 0;
}

.tag-selection {
  margin-top: 20px;
}

.tag-dropdown {
  width: 100%;
}

</style>
