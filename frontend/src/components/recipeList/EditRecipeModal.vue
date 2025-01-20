<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h2>Rezept bearbeiten</h2>
      <form @submit.prevent="submitEdit">
        <div class="input-group">
          <label for="name">Rezeptname:</label>
          <input type="text" v-model="selectedRecipe.name" placeholder="Rezeptname eingeben" required />
        </div>

        <div class="input-group">
          <label for="description">Beschreibung:</label>
          <textarea v-model="selectedRecipe.description" placeholder="Beschreibung eingeben" required></textarea>
        </div>

        <div class="input-group">
          <label for="cookingTime">Kochzeit (in Minuten):</label>
          <input type="number" v-model="selectedRecipe.cookingTime" placeholder="Kochzeit in Minuten" min="1" required />
        </div>

        <div class="ingredient-list-container">
          <div v-for="(ingredient, index) in selectedRecipe.ingredients" :key="index" class="ingredient-group">
            <div class="ingredient-item">
              <label>Zutat {{ index + 1 }}:</label>
              <input type="text" v-model="ingredient.name" placeholder="Zutat" required />
              <input type="text" v-model="ingredient.quantity" placeholder="Menge" required />
              <div class="unit-selection">
                <label for="unit">Einheit:</label>
                <select v-model="ingredient.unit" required>
                  <option v-for="unit in quantityUnits()" :key="unit.value" :value="unit.value">{{ unit.label }}</option>
                </select>
              </div>
              <button type="button" @click="removeIngredient(index)" class="remove-button">Entfernen</button>
            </div>
          </div>
        </div>

        <button type="button" @click="addIngredient" class="add-button">Zutat hinzufügen</button>

        <div class="tag-selection">
          <label for="tags">Wähle Tags:</label>
          <multiselect
              :value="selectedTags"
              :options="availableTags"
              :multiple="true"
              :close-on-select="false"
              placeholder="Tags auswählen"
              label="name"
              track-by="name"
              :tag-placeholder="'Tag hinzufügen'"
              class="tag-dropdown"
              @input="updateSelectedTags"
          />
        </div>

        <button type="submit" class="submit-button">Rezept speichern</button>
      </form>
      <button @click="closeEditModal" class="close-button">Schließen</button>
    </div>
  </div>
</template>
<script>
import Multiselect from 'vue-multiselect';
import {quantityUnits} from "@/assets/TagsAndUnits.js";

export default {
  components: {
    Multiselect,
  },
  props: {
    selectedRecipe: Object,
    availableTags: Array,
    selectedTags: Array,
  },
  methods: {
    quantityUnits() {
      return quantityUnits;
    },
    submitEdit() {
      this.$emit("submitEditRecipe", this.selectedRecipe);
    },
    closeEditModal() {
      this.$emit("closeEditModal");
    },
    addIngredient() {
      if (!this.selectedRecipe.ingredients) {
        this.selectedRecipe.ingredients = [];
      }
      this.selectedRecipe.ingredients.push({name: "", quantity: "", unit: ""});
    },
    removeIngredient(index) {
      this.selectedRecipe.ingredients.splice(index, 1);
    },
    updateSelectedTags(newTags) {
      this.$emit("update:selectedTags", newTags);
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
  align-items: center; /* Vertikal und horizontal zentrieren */

  z-index: 9999;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 100%;
  max-width: 600px;  /* Passt die Breite an */
  max-height: 80vh;  /* Verhindert zu große Modal-Boxen */
  overflow-y: auto;  /* Scrollt, falls der Inhalt zu lang ist */
  margin: 0 10px; /* Sorgt für Abstand zu den Rändern */
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

/* Media query for responsiveness */
@media (max-width: 768px) {
  .modal-content {
    width: 90%;
  }
}
</style>
