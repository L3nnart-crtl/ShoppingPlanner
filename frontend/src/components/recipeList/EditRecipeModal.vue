<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h2>Rezept bearbeiten</h2> <!-- Edit Recipe Title -->
      <form @submit.prevent="submitEdit">
        <div class="input-group">
          <label for="name">Rezeptname:</label> <!-- Recipe Name Label -->
          <input
              type="text"
              v-model="selectedRecipe.name"
              placeholder="Geben Sie den Rezeptnamen ein"
              required
          />
        </div>

        <div class="input-group">
          <label for="description">Beschreibung:</label> <!-- Description Label -->
          <textarea
              v-model="selectedRecipe.description"
              placeholder="Geben Sie eine Beschreibung ein"
              required
          ></textarea>
        </div>

        <div class="input-group">
          <label for="cookingTime">Kochzeit (in Minuten):</label> <!-- Cooking Time Label -->
          <input
              type="number"
              v-model="selectedRecipe.cookingTime"
              placeholder="Geben Sie die Kochzeit ein"
              min="1"
              required
          />
        </div>

        <div class="ingredient-list-container">
          <div
              v-for="(ingredient, index) in selectedRecipe.ingredients"
              :key="index"
              class="ingredient-group"
          >
            <div class="ingredient-item">
              <label>Zutat {{ index + 1 }}:</label> <!-- Ingredient Label -->
              <label>{{ ingredient.name }}</label>
              <label>{{ ingredient.quantity }}</label>
              <label>{{ getUnitLabel(ingredient.unit) }}</label>

              <button
                  type="button"
                  @click="openIngredientModal(ingredient)"
                  class="edit-button"
              >
                Bearbeiten <!-- Edit Button -->
              </button>
              <button
                  type="button"
                  @click="removeIngredient(index)"
                  class="remove-button"
              >
                Entfernen <!-- Remove Button -->
              </button>
            </div>
          </div>
        </div>
        <div class="buttons">
          <button
              type="button"
              @click="openIngredientModalAdd"
              class="add-button"
          >
            Zutat hinzufügen <!-- Add Ingredient Button -->
          </button>

          <div class="tag-selection">
            <label for="tags">Tags auswählen:</label> <!-- Select Tags Label -->
            <Multiselect
                ref="multiselect"
                v-model="newTags"
                :options="availableTags"
                :multiple="true"
                :close-on-select="false"
                placeholder="Tags auswählen"
                label="name"
                track-by="name"
                :tag-placeholder="'Tag hinzufügen'"
                class="tag-dropdown"
            />
          </div>
          <div class="button-group">
            <button type="submit" class="submit-button">Rezept speichern</button> <!-- Save Recipe Button -->
            <button @click="closeEditModal" class="close-button">Schließen</button> <!-- Close Button -->
          </div>

        </div>
      </form>

    </div>
  </div>

  <IngredientModal
      v-if="isIngredientModalVisible"
      :is-visible="isIngredientModalVisible"
      :newManualIngredient="selectedIngredient"
      :quantity-units="quantityUnits"
      @add-ingredient="addIngredient"
      @close-modal="closeIngredientModal"
  />
</template>

<script>
import Multiselect from "vue-multiselect";
import IngredientModal from "@/components/addRecipe/IngredientModal.vue";
import {quantityUnits, tagsForList} from "@/assets/TagsAndUnits.js";
import {EventBus} from "@/assets/event-bus.js";

export default {
  emits: ['closeEditModal', 'submitEditRecipe', 'update:selectedTags'],
  components: {
    Multiselect,
    IngredientModal,
  },
  props: {
    selectedRecipe: Object,
    selectedTags: Object, // Ensure selectedTags is an array
  },
  data() {
    return {
      newTags: this.selectedTags, // Convert to array if it's an object
      isIngredientModalVisible: false,
      selectedIngredient: null,
      availableTags: tagsForList,
      quantityUnits,
    };
  },
  methods: {
    // Ingredient
    addIngredient(ingredient) {
      const index = this.selectedRecipe.ingredients.findIndex(
          (ing) => ing === this.selectedIngredient
      );

      if (index !== -1) {
        this.selectedRecipe.ingredients.splice(index, 1, ingredient);
      } else {
        this.selectedRecipe.ingredients.push(ingredient);
      }
      this.closeIngredientModal();
    },
    closeIngredientModal() {
      this.isIngredientModalVisible = false;
      this.selectedIngredient = null;
    },
    openIngredientModal(ingredient) {
      this.selectedIngredient = ingredient;
      this.isIngredientModalVisible = true;
    },
    openIngredientModalAdd() {
      this.selectedIngredient = {
        name: "",
        quantity: "",
        unit: "",
        calories: 0,
        carbohydrates: 0,
        proteins: 0,
        fats: 0,
      };
      this.isIngredientModalVisible = true;
    },
    removeIngredient(index) {
      this.selectedRecipe.ingredients.splice(index, 1);
    },
    // Modal
    submitEdit() {
      this.updateSelectedTags();
      this.$emit("submitEditRecipe", this.selectedRecipe);
      EventBus.emit("update-chart");
    },
    updateSelectedTags() {
      this.$emit("update:selectedTags", this.newTags);
    },

    closeEditModal() {
      this.$emit("closeEditModal");
    },
    // Helper functions
    getUnitLabel(unit) {
      const unitObj = quantityUnits.find((u) => u.value === unit);
      return unitObj ? unitObj.label : unit;
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
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-content {
  background-color: #ffffff;
  padding: 24px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  margin: 0 16px;
}

h2 {
  font-family: 'Roboto', sans-serif;
  font-size: 1.8rem;
  font-weight: 600;
  text-align: center;
  color: #333;
  margin-top: -15px;
}

.input-group {
  margin-bottom: 16px;
}

input,
textarea {
  width: 100%;
  padding: 5px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
  transition: border-color 0.3s, box-shadow 0.3s;
}

input:focus,
textarea:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
  outline: none;
}

textarea {
  resize: vertical;
}

.buttons {
  display: flex;
  flex-direction: column;
  gap: 16px; /* This will add space between the buttons */
}

.buttons {
  display: flex;
  flex-direction: column;
  gap: 16px; /* Adds space between the first set of buttons */
}

.buttons .button-group {
  display: flex;
  justify-content: space-between; /* Positions buttons next to each other */
  width: 100%;
}

button {
  padding: 12px 20px;
  background-color: #007bff;
  color: white;
  border-radius: 6px;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
  margin-bottom: 8px; /* Adds space below each button */
}

button:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

button:active {
  transform: translateY(0);
}

.close-button {
  background-color: #6c757d;
}

.close-button:hover {
  background-color: #5a6268;
}

.add-button {
  background-color: #28a745;
}

.add-button:hover {
  background-color: #218838;
}

.remove-button {
  background-color: #dc3545;
}

.remove-button:hover {
  background-color: #c82333;
}


.ingredient-list-container {
  max-height: 250px;
  overflow-y: auto;
  margin-bottom: 20px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background-color: #f8f9fa;
}

.ingredient-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background-color: white;
}

.ingredient-item label {
  font-size: 0.9rem;
  color: #555;
}

.tag-dropdown {
  width: 100%;
}

@media (max-width: 768px) {
  .modal-content {
    width: 90%;
    padding: 16px;
  }

  h2 {
    font-size: 1.5rem;
  }
}
</style>
