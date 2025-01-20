<template>
  <div v-if="isVisible" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <h2>Füge Zutat hinzu</h2>

      <!-- API-Suche nach Zutaten -->
      <div class="input-group">
        <input
            type="text"
            v-model="ingredientSearchQuery"
            placeholder="Zutat suchen..."
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
        <h4>Manuelle Zutat</h4>
        <div class="input-group compact">
          <input type="text" v-model="newManualIngredient.name" placeholder="Zutatname" class="input-small" />
          <input type="number" v-model="newManualIngredient.quantity" placeholder="Menge" class="input-small" />
          <select v-model="newManualIngredient.unit" class="input-small">
            <option v-for="unit in quantityUnits" :key="unit.value" :value="unit.value">{{ unit.label }}</option>
          </select>
          <input type="number" v-model="newManualIngredient.calories" placeholder="Kalorien" class="input-small" />
          <input type="number" v-model="newManualIngredient.carbohydrates" placeholder="KH" class="input-small" />
          <input type="number" v-model="newManualIngredient.proteins" placeholder="Proteine" class="input-small" />
          <input type="number" v-model="newManualIngredient.fats" placeholder="Fette" class="input-small" />
        </div>
        <button type="button" @click="addManualIngredient" class="add-button">Hinzufügen</button>
      </div>

      <!-- Eingabefelder für Menge und Einheit -->
      <div v-if="selectedIngredient" class="selected-ingredient-preview">
        <h4>{{ selectedIngredient.name }} ({{ selectedIngredient.calories }} kcal)</h4>
        <p>Carbs: {{ selectedIngredient.carbohydrates }}g, Protein: {{ selectedIngredient.proteins }}g, Fats: {{ selectedIngredient.fats }}g</p>
        <div class="input-group">
          <input
              type="text"
              v-model="newIngredient.quantity"
              placeholder="Menge"
          />
          <label for="unit">Einheit:</label>
          <select v-model="newIngredient.unit">
            <option v-for="unit in quantityUnits" :key="unit.value" :value="unit.value">{{ unit.label }}</option>
          </select>
          <button type="button" @click="addIngredient" class="add-button">Zutat hinzufügen</button>
        </div>
      </div>

      <button @click="closeModal" class="close-modal-button">Schließen</button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    isVisible: Boolean,
    quantityUnits: Array,
  },
  data() {
    return {
      ingredientSearchQuery: '',
      searchResults: [],
      selectedIngredient: null,
      newIngredient: {
        name: '',
        quantity: '',
        unit: '',
        calories: 0,
        carbohydrates: 0,
        proteins: 0,
        fats: 0,
      },
      newManualIngredient: {
        name: '',
        quantity: '',
        unit: '',
        calories: null,
        carbohydrates: null,
        proteins: null,
        fats: null,
      },
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
      this.selectedIngredient = ingredient;
      this.newIngredient = {
        name: ingredient.name,
        calories: ingredient.calories,
        carbohydrates: ingredient.carbohydrates,
        proteins: ingredient.proteins,
        fats: ingredient.fats,
        quantity: '',
        unit: '',
      };
      this.ingredientSearchQuery = '';
      this.searchResults = [];
    },
    addIngredient() {
      this.$emit('add-ingredient', this.newIngredient);
      this.resetNewIngredient();
    },
    addManualIngredient() {
      this.$emit('add-ingredient', this.newManualIngredient);
      this.resetNewManualIngredient();
    },
    closeModal() {
      this.$emit('close-modal');
    },
    resetNewIngredient() {
      this.newIngredient = {
        name: '',
        quantity: '',
        unit: '',
        calories: 0,
        carbohydrates: 0,
        proteins: 0,
        fats: 0,
      };
      this.selectedIngredient = null;
    },
    resetNewManualIngredient() {
      this.newManualIngredient = {
        name: '',
        quantity: '',
        unit: '',
        calories: null,
        carbohydrates: null,
        proteins: null,
        fats: null,
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

.input-group.compact {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.input-small {
  width: 100px; /* Verkleinert die Eingabefelder */
}

input, select, button {
  padding: 10px;
  margin: 5px 0;
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

.manual-ingredient-entry h4 {
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
</style>
