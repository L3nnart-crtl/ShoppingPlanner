<template>
  <div class="recipe-container">
    <h2>Rezeptliste und Suche</h2>

    <!-- Suchfelder -->
    <div class="search-fields">
      <input
          v-model="searchQuery.name"
          type="text"
          placeholder="Rezeptname eingeben"
          class="search-input"
      />

      <div class="tags-filter">
        <label for="tags">Tags:</label>
        <multiselect
            v-model="searchQuery.tags"
            :options="availableTags"
            :multiple="true"
            :close-on-select="false"
            placeholder="Tags auswählen"
            label="name"
            track-by="value"
            class="tag-dropdown"
        />
      </div>

      <!-- Filter für Kochzeit -->
      <div class="cooking-time-filter">
        <label for="cookingtime">Kochzeit (Minuten):</label>
        <input
            v-model="searchQuery.cookingTime"
            type="number"
            placeholder="Kochzeit eingeben"
            class="search-input"
        />
      </div>

      <button @click="searchRecipes" class="search-button">Suchen</button>
    </div>

    <!-- Anzeige der Rezepte -->
    <div class="recipe-cards-container" v-if="recipes.length">
      <div
          v-for="recipe in recipes"
          :key="recipe.id"
          class="recipe-card"
          @click="openRecipeDetails(recipe)"
      >
        <h3 class="recipe-name">{{ recipe.name || 'Kein Name' }}</h3>
      </div>
    </div>

    <!-- Wenn keine Rezepte vorhanden sind -->
    <p v-else class="no-recipes">Keine Rezepte gefunden.</p>

    <!-- Modal für Rezeptdetails -->
    <div v-if="isModalVisible" class="modal-overlay">
      <div class="modal-content">
        <button v-if="selectedRecipe" @click="confirmDeleteRecipe(selectedRecipe)" class="delete-button">Rezept löschen</button>
        <button v-if="selectedRecipe" @click="openEditRecipe(selectedRecipe)" class="edit-button">Rezept bearbeiten</button>

        <h4>{{ selectedRecipe.name }}</h4>
        <p><strong>Beschreibung:</strong> {{ selectedRecipe.description || 'Keine Beschreibung verfügbar.' }}</p>

        <p><strong>Zutaten:</strong></p>
        <ul class="ingredient-list">
          <li v-for="(ingredient, index) in selectedRecipe.ingredients" :key="index">
            {{ ingredient.name }} - {{ ingredient.quantity }} {{ getUnitLabel(ingredient.unit) }}
          </li>
        </ul>

        <p><strong>Tags:</strong></p>
        <div class="tags-container">
          <div v-for="(tag, index) in selectedRecipe.tags" :key="index" class="tag-box">
            {{ getTagLabel(tag) }}
          </div>
        </div>

        <!-- Anzeige der Kochzeit -->
        <p><strong>Kochzeit:</strong> {{ selectedRecipe.cookingTime|| 'Keine Kochzeit angegeben' }} Minuten</p>

        <div class="modal-actions">
          <button @click="closeModal">Schließen</button>
        </div>
      </div>
    </div>

    <!-- Modal für Bestätigung des Löschens -->
    <div v-if="isDeleteModalVisible" class="modal-overlay">
      <div class="modal-content delete-modal">
        <h4>Bestätigung</h4>
        <p>Bist du sicher, dass du das Rezept "{{ selectedRecipe.name }}" löschen möchtest?</p>
        <div class="modal-actions">
          <button @click="deleteRecipe">Ja, löschen</button>
          <button @click="closeDeleteModal">Abbrechen</button>
        </div>
      </div>
    </div>

    <!-- Formular für Rezeptbearbeitung -->
    <div v-if="isEditModalVisible" class="modal-overlay">
      <div class="modal-content">
        <h2>Rezept bearbeiten</h2>
        <form @submit.prevent="submitEditRecipe" class="recipe-form">
          <!-- Rezeptname -->
          <div class="input-group">
            <label for="name">Rezeptname:</label>
            <input type="text" v-model="selectedRecipe.name" placeholder="Rezeptname eingeben" required />
          </div>

          <!-- Beschreibung -->
          <div class="input-group">
            <label for="description">Beschreibung:</label>
            <textarea v-model="selectedRecipe.description" placeholder="Beschreibung eingeben" required></textarea>
          </div>

          <!-- Kochzeit -->
          <div class="input-group">
            <label for="cookingTime">Kochzeit (in Minuten):</label>
            <input type="number" v-model="selectedRecipe.cookingTime" placeholder="Kochzeit in Minuten" min="1" required />
          </div>

          <!-- Zutaten hinzufügen -->
          <div v-for="(ingredient, index) in selectedRecipe.ingredients" :key="index" class="ingredient-group">
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
                v-model="selectedTags"
                :options="availableTags"
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

          <button type="submit" class="submit-button">Rezept speichern</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.min.css";
import axios from "axios";
import {quantityUnits, tagMapping, tagsForList} from "@/assets/TagsAndUnits.js";

export default {
  components: { Multiselect },
  data() {
    return {
      searchQuery: {
        name: "",
        tags: [],
        cookingTime: "",
      },
      recipes: [],
      availableTags: tagsForList,
      quantityUnits: quantityUnits,
      isModalVisible: false,
      isDeleteModalVisible: false,
      isEditModalVisible: false,
      selectedRecipe: null,
      selectedTags: [],
    };
  },
  mounted() {
    this.searchRecipes();
  },
  methods: {
    recipesUpdated(updatedRecipes) {
      this.recipes = updatedRecipes;
    },
    async searchRecipes() {
      try {
        const params = {};
        if (this.searchQuery.name) {
          params.name = this.searchQuery.name;
        }
        if (this.searchQuery.tags.length > 0) {
          params.tags = this.searchQuery.tags.map(tag => tag.value).join(",");
        }
        if (this.searchQuery.cookingTime) {
          params.cookingtime = this.searchQuery.cookingTime;
        }

        const response = await axios.get("/api/recipes/search", { params });
        this.recipes = response.data;
      } catch (error) {
        console.error("Fehler bei der Rezeptsuche:", error);
      }
    },
    openRecipeDetails(recipe) {
      this.selectedRecipe = recipe;
      this.isModalVisible = true;
    },
    closeModal() {
      this.isModalVisible = false;
    },
    confirmDeleteRecipe(recipe) {
      this.selectedRecipe = recipe;
      this.isDeleteModalVisible = true;
    },
    closeDeleteModal() {
      this.isDeleteModalVisible = false;
    },
    async deleteRecipe() {
      if (this.selectedRecipe) {
        try {
          await axios.delete(`/api/recipes/${this.selectedRecipe.id}`);
          this.closeDeleteModal();
          this.closeModal();
          this.recipes = this.recipes.filter(r => r.id !== this.selectedRecipe.id);
        } catch (error) {
          console.error("Fehler beim Löschen des Rezepts:", error);
        }
      }
    },
    translateTags(tags) {
      return tags.map(tag => tagMapping[tag] || tag);
    },
    openEditRecipe(recipe) {
      this.selectedRecipe = JSON.parse(JSON.stringify(recipe)); // Deep clone
      this.selectedTags = recipe.tags.map(tag => ({ name: tag }));
      this.isEditModalVisible = true;
    },
    async submitEditRecipe() {
      try {
        const updatedRecipe = { ...this.selectedRecipe, tags: this.translateTags(this.selectedTags.map(tag => tag.name)) };
        await axios.put(`/api/recipes/${this.selectedRecipe.id}`, updatedRecipe);
        this.isEditModalVisible = false;
        await this.searchRecipes();
      } catch (error) {
        console.error("Fehler beim Aktualisieren des Rezepts:", error);
      }
    },
    addIngredient() {
      this.selectedRecipe.ingredients.push({ name: '', quantity: '', unit: '' });
    },
    removeIngredient(index) {
      this.selectedRecipe.ingredients.splice(index, 1);
    },
    getUnitLabel(unit) {
      const unitObj = this.quantityUnits.find(u => u.value === unit);
      return unitObj ? unitObj.label : unit;
    },
    getTagLabel(tag) {
      const tagObj = this.availableTags.find(t => t.value === tag);
      return tagObj ? tagObj.name : tag;
    },
  },
};
</script>

<style scoped>
.recipe-container {
  margin: 15px;
}

.search-fields {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.search-input {
  padding: 6px;
  font-size: 12px;
}

.tag-dropdown {
  width: 100%;
  font-size: 12px;
}

.search-button {
  padding: 8px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 12px;
}

.recipe-cards-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #888 #ddd;
  border-radius: 10px;
  border: 1px solid #ddd;
  padding: 8px;
}

.recipe-card {
  flex: 1 0 30%;
  padding: 12px;
  background-color: #f4f4f4;
  border: 1px solid #ccc;
  border-radius: 8px;
  cursor: pointer;
  box-sizing: border-box;
  transition: transform 0.3s ease;
}

.recipe-card:hover {
  transform: scale(1.05);
}

.no-recipes {
  font-size: 16px;
  color: #777;
}

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
  border-radius: 10px;
  width: 400px;
  position: relative;
}

.delete-button, .edit-button, .submit-button {
  background-color: #f44336;
  color: white;
  padding: 10px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  border-radius: 5px;
}

.submit-button {
  background-color: #4caf50;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}

.input-group {
  margin-bottom: 12px;
}

.ingredient-group {
  margin-bottom: 10px;
}

.remove-button {
  background-color: #f44336;
  color: white;
  padding: 5px;
  border: none;
  cursor: pointer;
  font-size: 10px;
}

.add-button {
  background-color: #4caf50;
  color: white;
  padding: 6px;
  border: none;
  cursor: pointer;
  font-size: 12px;
}

.tag-selection {
  margin-bottom: 15px;
}
</style>
