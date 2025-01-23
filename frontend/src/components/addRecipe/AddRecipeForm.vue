<template>
  <div class="modal-overlay" v-if="isAddModalVisible" @click="closeModal">
    <div class="modal" @click.stop>
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
          />
        </div>

        <div class="button-container">
          <button type="submit" class="submit-button" :disabled="isSubmitting">Rezept speichern</button>
          <button type="button" @click="closeModal" class="close-button">Schließen</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import IngredientModal from './IngredientModal.vue';
import Multiselect from 'vue-multiselect';
import 'vue-multiselect/dist/vue-multiselect.min.css';
import { quantityUnits, tagsForList } from "@/assets/TagsAndUnits.js";

export default {
  components: {
    IngredientModal,
    Multiselect
  },
  props: {
    isAddModalVisible: Boolean, // Modal visibility control
    recipes: Array
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
      allTags: tagsForList,
      quantityUnits,
      isSubmitting: false,
    };
  },
  methods: {
    closeModal() {
      this.$emit("closeAddModal");
      this.recipe = { name: '', description: '', cookingTime: null, ingredients: [], tags: [] };
      this.selectedTags = [];
    },
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
      };
    },
    addIngredient(ingredient) {
      this.recipe.ingredients.push(ingredient);
    },

    async submitRecipe() {
      if (this.isSubmitting) return;
      this.isSubmitting = true;

      try {
        this.recipe.tags = this.selectedTags.map(tag => tag.value);
        const response = await this.$axios.post('/recipes', this.recipe);
        this.$emit('recipe-added', this.recipes.push(response.data));
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
/* Modal Overlay */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* Modal Box */
.modal {
  width: 100%;
  max-width: 600px;
  background-color: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

/* Titel des Formulars */
.form-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 20px;
}

/* Inputgruppen */
.input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
}

input, textarea {
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-top: 8px;
  transition: border-color 0.3s ease;
}

input:focus, textarea:focus {
  border-color: #4CAF50;
  outline: none;
}

textarea {
  resize: vertical;
  min-height: 100px;
}

/* Buttons */
.add-ingredient-button,
.submit-button {
  background-color: #4CAF50;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}
.close-button {
  background-color: #f44336;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}
.close-button:hover {
  background-color: #e53935;
}

.add-ingredient-button:hover,
.submit-button:hover {
  background-color: #45a049;
}

.submit-button:disabled {
  background-color: #d6d6d6;
  cursor: not-allowed;
}

/* Zutatenliste */
.ingredient-list {
  max-height: 300px;
  overflow-y: auto;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: #f9f9f9;
  margin-bottom: 20px;
}

/* Tag-Auswahl */
.tag-selection {
  margin-top: 20px;
}

/* Tag Dropdown */
.tag-dropdown {
  width: 100%;
}

/* Button Container */
.button-container {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
