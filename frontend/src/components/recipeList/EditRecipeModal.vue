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

        <button type="submit" class="submit-button">Save Recipe</button>
      </form>
      <button @click="closeEditModal" class="close-button">Close</button>
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

export default {
  emits: ['closeEditModal', 'submitEditRecipe', 'update:selectedTags'],
  components: {
    Multiselect,
    IngredientModal,
  },
  props: {
    selectedRecipe: Object,
    selectedTags: Object,  // Ensure selectedTags is an array
  },
  data() {
    return {
      newTags: this.selectedTags, // Convert to array if it's an object
      isIngredientModalVisible: false,
      selectedIngredient: null,
      availableTags: tagsForList,
      quantityUnits
    };
  },
  methods: {
    //Ingredient
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
    //Modal
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
    //Helper functions
    getUnitLabel(unit) {
      const unitObj = quantityUnits.find(u => u.value === unit);
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
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9997;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 100%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  margin: 0 10px;
}

h2 {
  font-size: 1.75rem;
  text-align: center;
}

.input-group {
  margin-bottom: 15px;
}

input, textarea, select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

textarea {
  resize: vertical;
}

button {
  padding: 10px;
  background-color: #007BFF;
  color: white;
  border-radius: 4px;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.tag-dropdown {
  width: 100%;
}

.ingredient-list-container {
  max-height: 250px;
  overflow-y: auto;
}

.ingredient-item {
  display: flex;
  gap: 10px;
}

.unit-selection {
  flex-grow: 1;
}

.close-button {
  padding: 10px;
  border-radius: 4px;
  background-color: #6c757d;
  color: white;
  border: none;
  cursor: pointer;
}

.close-button:hover {
  background-color: #5a6268;
}

@media (max-width: 768px) {
  .modal-content {
    width: 90%;
  }
}
</style>
