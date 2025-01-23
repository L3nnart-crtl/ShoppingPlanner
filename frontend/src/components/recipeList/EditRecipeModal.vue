<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h2>Edit Recipe</h2>
      <form @submit.prevent="submitEdit">
        <div class="input-group">
          <label for="name">Recipe Name:</label>
          <input
              type="text"
              v-model="selectedRecipe.name"
              placeholder="Enter Recipe Name"
              required
          />
        </div>

        <div class="input-group">
          <label for="description">Description:</label>
          <textarea
              v-model="selectedRecipe.description"
              placeholder="Enter Description"
              required
          ></textarea>
        </div>

        <div class="input-group">
          <label for="cookingTime">Cooking Time (in minutes):</label>
          <input
              type="number"
              v-model="selectedRecipe.cookingTime"
              placeholder="Enter Cooking Time"
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
              <label>Ingredient {{ index + 1 }}:</label>
              <label>{{ ingredient.name }}</label>
              <label>{{ ingredient.quantity }}</label>
              <label>{{ getUnitLabel(ingredient.unit) }}</label>

              <button
                  type="button"
                  @click="openIngredientModal(ingredient)"
                  class="edit-button"
              >
                Edit
              </button>
              <button
                  type="button"
                  @click="removeIngredient(index)"
                  class="remove-button"
              >
                Remove
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
            Add Ingredient
          </button>

          <div class="tag-selection">
            <label for="tags">Select Tags:</label>
            <Multiselect
                ref="multiselect"
                v-model="newTags"
                :options="availableTags"
                :multiple="true"
                :close-on-select="false"
                placeholder="Select Tags"
                label="name"
                track-by="name"
                :tag-placeholder="'Add Tag'"
                class="tag-dropdown"
            />
          </div>
          <div class="button-group">
            <button type="submit" class="submit-button">Save Recipe</button>
            <button @click="closeEditModal" class="close-button">Close</button>
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
import { quantityUnits, tagsForList } from "@/assets/TagsAndUnits.js";

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

