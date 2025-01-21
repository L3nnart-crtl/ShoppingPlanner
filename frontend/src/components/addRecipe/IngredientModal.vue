<template>
  <div v-if="isVisible" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <h2>Füge Zutat hinzu</h2>

      <!-- API-Suche nach Zutaten -->
      <div class="input-group">
        <label for="ingredient-search">Zutat suchen in Openfoodfacts</label>
        <input
            id="ingredient-search"
            type="text"
            v-model="ingredientSearchQuery"
        />
        <button type="button" @click="searchIngredients" class="search-button">Suchen</button>
        <div v-if="searchResults.length" class="search-results">
          <ul>
            <li
                v-for="result in searchResults"
                :key="result.name"
                @click="selectIngredient(result)"
                class="search-result-item"
            >
              {{ result.name }} ({{ result.calories }} kcal,
              {{ result.carbohydrates }}g KH, {{ result.proteins }}g Proteins, {{ result.fats }}g Fats)
            </li>
          </ul>
        </div>
      </div>

      <!-- Kompakte manuelle Eingabe von Zutaten -->
      <div class="manual-ingredient-entry">
        <h3>Manuelle Zutat</h3>
        <div class="input-group compact">
          <label for="ingredient-name">Zutatname</label>
          <input id="ingredient-name" type="text" v-model="localManualIngredient.name" class="input-small" />

          <label for="ingredient-quantity">Menge</label>
          <input id="ingredient-quantity" type="number" v-model="localManualIngredient.quantity" class="input-small" />

          <label for="ingredient-unit">Einheit</label>
          <select id="ingredient-unit" v-model="localManualIngredient.unit" class="input-small">
            <option v-for="unit in quantityUnits" :key="unit.value" :value="unit.value">{{ unit.label }}</option>
          </select>

          <label for="ingredient-calories">Kalorien</label>
          <input id="ingredient-calories" type="number" v-model="localManualIngredient.calories" class="input-small" />

          <label for="ingredient-carbs">Kohlenhydrate</label>
          <input id="ingredient-carbs" type="number" v-model="localManualIngredient.carbohydrates" class="input-small" />

          <label for="ingredient-proteins">Proteine</label>
          <input id="ingredient-proteins" type="number" v-model="localManualIngredient.proteins" class="input-small" />

          <label for="ingredient-fats">Fette</label>
          <input id="ingredient-fats" type="number" v-model="localManualIngredient.fats" class="input-small" />
        </div>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        <button type="button" @click="addManualIngredient" class="add-button">Hinzufügen</button>
      </div>

      <button @click="closeModal" class="close-modal-button">Schließen</button>
    </div>
  </div>
</template>


<script>
import {quantityUnits} from "@/assets/TagsAndUnits.js";

export default {
  props: {
    isVisible: Boolean,
    newManualIngredient: {
      type: Object,
      default: () => ({
        name: '',
        quantity: '',
        unit: '',
        calories: 0,
        carbohydrates: 0,
        proteins: 0,
        fats: 0,
      }),
    },
  },
  data() {
    return {
      ingredientSearchQuery: '',
      searchResults: [],
      selectedIngredient: null,
      errorMessage: '',
      localManualIngredient: { ...this.newManualIngredient }, // Kopie der initialen Props
      quantityUnits
    };
  },
  methods: {
    async searchIngredients() {
      if (!this.ingredientSearchQuery) {
        this.searchResults = [];
        return;
      }
      try {
        const response = await this.$axios.get('/ingredients/search', {
          params: { query: this.ingredientSearchQuery },
        });
        this.searchResults = response.data;
      } catch (error) {
        console.error('Error fetching ingredients:', error);
      }
    },
    selectIngredient(ingredient) {
      // Setze die Felder mit den Werten des ausgewählten Ingredients
      this.localManualIngredient = {
        name: ingredient.name,
        quantity: this.localManualIngredient.quantity || '', // Behalte die vorhandene Menge, falls vorhanden
        unit: this.localManualIngredient.unit || '', // Behalte die vorhandene Einheit, falls vorhanden
        calories: ingredient.calories,
        carbohydrates: ingredient.carbohydrates,
        proteins: ingredient.proteins,
        fats: ingredient.fats,
      };

      // Clear the search query and results after selection
      this.ingredientSearchQuery = '';
      this.searchResults = [];
    },
    addManualIngredient() {
      const { name, quantity, unit } = this.localManualIngredient;

      if (!name || !quantity || !unit) {
        this.errorMessage = 'Bitte füllen Sie Name, Menge und Einheit aus.';
        return;
      }

      this.errorMessage = '';
      this.$emit('add-ingredient', { ...this.localManualIngredient });
      this.resetLocalManualIngredient();
      this.closeModal();
    },
    closeModal() {
      this.$emit('close-modal');
    },
    resetLocalManualIngredient() {
      this.localManualIngredient = {
        name: '',
        quantity: '',
        unit: '',
        calories: 0,
        carbohydrates: 0,
        proteins: 0,
        fats: 0,
      };
    },
  },
};
</script>

<style scoped>
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
  z-index: 9999;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 600px;
  width: 100%;
  max-height: 90%;
  overflow-y: auto;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-group label {
  font-weight: bold;
  margin-bottom: 2px; /* Minimaler Abstand zwischen Label und Eingabefeld */
}

.input-group input,
.input-group select {
  margin-bottom: 10px;
}

.input-group button {
  margin-top: 5px; /* Abstand zum Feld davor */
}

.input-group.compact {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px; /* Mehr Abstand zwischen den Feldern */
}

.input-small {
  width: 100px;
}

input, select, button {
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

button {
  background-color: #28a745;
  color: white;
  cursor: pointer;
}

button:hover {
  background-color: #218838;
}

.search-results {
  max-height: 200px;
  overflow-y: auto;
  margin-top: 10px;
}

.search-result-item {
  padding: 5px;
  cursor: pointer;
}

.search-result-item:hover {
  background-color: #f1f1f1;
}

.manual-ingredient-entry {
  margin-top: 20px;
}

.manual-ingredient-entry h3 {
  margin-bottom: 10px;
}

.close-modal-button {
  background-color: #dc3545;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 100%;
  margin-top: 20px;
}

.close-modal-button:hover {
  background-color: #c82333;
}

.error-message {
  color: red;
  font-size: 0.9rem;
  margin-top: 5px;
}
</style>

