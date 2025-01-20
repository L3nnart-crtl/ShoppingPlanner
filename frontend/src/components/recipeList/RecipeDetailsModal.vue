<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <div class="button-container">
        <button @click="editRecipe" class="modal-button edit-button">Edit Recipe</button>
        <button @click="deleteRecipe" class="modal-button delete-button">Delete Recipe</button>
        <button @click="toggleFavorite" class="modal-button favorite-button">
          {{ selectedRecipe.favorite ? 'Remove from Favorites' : 'Mark as Favorite' }}
        </button>
      </div>
      <h4>{{ selectedRecipe.name }}</h4>
      <p><strong>Description:</strong> {{ selectedRecipe.description || 'No description available.' }}</p>

      <p><strong>Ingredients:</strong></p>
      <div class="ingredient-list-container">
        <ul class="ingredient-list">
          <li v-for="(ingredient, index) in selectedRecipe.ingredients" :key="index" class="ingredient-item">
            <span class="ingredient-name">{{ ingredient.name }} - {{ ingredient.quantity }} {{ getUnitLabel(ingredient.unit) }}</span>
            <span class="ingredient-nutrients">
              Calories: {{ ingredient.calories }} kcal | Protein: {{ ingredient.proteins }} g | Fat: {{ ingredient.fats }} g | Carbs: {{ ingredient.carbohydrates }} g
            </span>
          </li>
        </ul>
      </div>

      <p><strong>Tags:</strong></p>
      <div class="tags-container">
        <div v-for="(tag, index) in selectedRecipe.tags" :key="index" class="tag-box">
          {{ getTagLabel(tag) }}
        </div>
      </div>

      <p><strong>Cooking Time:</strong> {{ selectedRecipe.cookingTime || 'No cooking time provided' }} minutes</p>

      <button @click="closeModal" class="modal-button close-button">Close</button>
    </div>
  </div>
</template>


<script>
import { quantityUnits, tagMapping } from "@/assets/TagsAndUnits.js";

export default {
  props: {
    selectedRecipe: Object,
    isVisible: Boolean,
  },
  methods: {
    closeModal() {
      this.$emit("closeModal");
    },
    editRecipe() {
      this.$emit("openEdit", this.selectedRecipe);
    },
    deleteRecipe() {
      this.$emit("confirmDelete", this.selectedRecipe);
    },
    toggleFavorite() {
      this.$emit("toggleFavorite");
    },
    getTagLabel(tag) {
      return tagMapping[tag] || tag;
    },
    getUnitLabel(unit) {
      const unitObj = quantityUnits.find(u => u.value === unit);
      return unitObj ? unitObj.label : unit;
    }
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
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9998;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 500px;
  width: 100%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.button-container {
  display: flex;
  justify-content: space-between;
  gap: 15px;
}

.modal-button {
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 30%;
}

.edit-button {
  background-color: #ff9800;
}

.delete-button {
  background-color: #f44336;
}

.favorite-button {
  background-color: #4CAF50;
}

.modal-button:hover {
  opacity: 0.9;
}

h4 {
  margin-bottom: 10px;
  font-size: 1.5em;
}

p {
  margin-bottom: 10px;
}

.ingredient-list-container {
  overflow-y: auto;
  max-height: 200px;
}

.ingredient-list {
  list-style-type: none;
  padding-left: 0;
}

.ingredient-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f1f1f1;
}

.ingredient-name {
  font-weight: bold;
}

.ingredient-nutrients {
  font-size: 0.9em;
  color: #555;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-box {
  background-color: #e0e0e0;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 0.9em;
}

.close-button {
  background-color: #007bff;
  color: white;
}
</style>
