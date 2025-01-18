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

      <multiselect
          v-model="searchQuery.tags"
          :options="availableTags"
          :multiple="true"
          :close-on-select="false"
          placeholder="Tags auswählen"
          label="name"
          track-by="name"
          class="tag-dropdown"
      />

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
      <!-- Filter nach Favoriten -->
      <button @click="toggleFavoriteFilter" class="filter-favorites-button">
        {{ filterFavorites ? 'Alle Rezepte anzeigen' : 'Nur Favoriten anzeigen' }}
      </button>

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

        <!-- Favorite Toggle -->
        <button @click="toggleFavorite" :class="['favorite-button', { active: selectedRecipe.favorite }]">
          {{ selectedRecipe.favorite ? 'Favorit entfernen' : 'Als Favorit markieren' }}
        </button>

        <h4>{{ selectedRecipe.name }}</h4>
        <p><strong>Beschreibung:</strong> {{ selectedRecipe.description || 'Keine Beschreibung verfügbar.' }}</p>

        <p><strong>Zutaten:</strong></p>
        <div class="ingredient-list-container">
          <ul class="ingredient-list">
            <li v-for="(ingredient, index) in selectedRecipe.ingredients" :key="index">
              {{ ingredient.name }} - {{ ingredient.quantity }} {{ getUnitLabel(ingredient.unit) }}
            </li>
          </ul>
        </div>

        <p><strong>Tags:</strong></p>
        <div class="tags-container">
          <div v-for="(tag, index) in selectedRecipe.tags" :key="index" class="tag-box">
            {{ getTagLabel(tag) }}
          </div>
        </div>

        <p><strong>Kochzeit:</strong> {{ selectedRecipe.cookingTime || 'Keine Kochzeit angegeben' }} Minuten</p>

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
      <div class="modal-content large-modal">
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
          <div class="ingredient-list-container">
            <div v-for="(ingredient, index) in selectedRecipe.ingredients" :key="index" class="ingredient-group">
              <div class="ingredient-item">
                <label>Zutat {{ index + 1 }}:</label>
                <input type="text" v-model="ingredient.name" placeholder="Zutat" required />
                <input type="text" v-model="ingredient.quantity" placeholder="Menge" required />

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
        <div class="modal-actions">
          <button @click="closeEditModal" class="close-button">Schließen</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.min.css";
import axios from "axios";
import { quantityUnits, tagMapping, tags, tagsForList } from "@/assets/TagsAndUnits.js";

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
      filterFavorites: false, // Hier den Filter für Favoriten hinzufügen
    };
  },
  mounted() {
    this.searchRecipes();
  },
  methods: {
    recipesUpdated(updatedRecipes) {
      this.recipes = updatedRecipes;
    },
    // Filter für Favoriten umschalten
    toggleFavoriteFilter() {
      this.filterFavorites = !this.filterFavorites;
      this.searchRecipes(); // Nach Änderung des Filters Rezepte durchsuchen
    },

    // Modifizierte searchRecipes Methode, um Favoriten zu filtern
    async searchRecipes() {
      try {
        const params = {};

        // Name-Filter hinzufügen, wenn vorhanden
        if (this.searchQuery.name) {
          params.name = this.searchQuery.name;
        }

        // Tags-Filter hinzufügen, wenn vorhanden
        if (this.searchQuery.tags.length > 0) {
          params.tags = this.searchQuery.tags.map(tag => tag.value).join(",");
        }

        // Kochzeit-Filter hinzufügen, wenn vorhanden
        if (this.searchQuery.cookingTime) {
          params.cookingTime = this.searchQuery.cookingTime;
        }

        // Favoriten-Filter hinzufügen, wenn aktiviert
        if (this.filterFavorites !== null) {
          params.favorite = this.filterFavorites;
        }

        // CSRF-Token aus dem Cookie holen
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];

        // CSRF-Token in den Header setzen
        axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        // API-Anfrage mit den Filtern
        const response = await this.$axios.get("/recipes/search", { params });
        this.recipes = response.data;
      } catch (error) {
        console.error("Fehler bei der Rezeptsuche:", error);
      }
    },
    openRecipeDetails(recipe) {
      // Update the selected recipe and set its favorite status correctly
      this.selectedRecipe = { ...recipe, favorite: recipe.favorite || false };
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
    closeEditModal() {
      this.isEditModalVisible = false;
    },
    async deleteRecipe() {
      if (this.selectedRecipe) {
        try {
          // CSRF-Token aus dem Cookie holen
          const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];

          // CSRF-Token in den Header setzen
          axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

          await this.$axios.delete(`/recipes/${this.selectedRecipe.id}`);
          this.closeDeleteModal();
          this.closeModal();
          this.recipes = this.recipes.filter(r => r.id !== this.selectedRecipe.id);
        } catch (error) {
          console.error("Fehler beim Löschen des Rezepts:", error);

          if (error.response && error.response.status === 409) {
            alert(`Rezept mit ID ${this.selectedRecipe.id} kann nicht gelöscht werden, da es noch einem MealPlan zugeordnet ist.`);
          } else {
            alert("Beim Löschen des Rezepts ist ein Fehler aufgetreten.");
          }
        }
      }
    },
    translateTags(tags) {
      return tags.map(tag => tagMapping[tag] || tag);
    },
    openEditRecipe(recipe) {
      this.selectedRecipe = JSON.parse(JSON.stringify(recipe));
      this.selectedTags = recipe.tags.map(tag => ({ name: tag }));
      this.isEditModalVisible = true;
    },
    async submitEditRecipe() {
      try {
        const updatedRecipe = {
          ...this.selectedRecipe,
          tags: this.translateTags(this.selectedTags.map(tag => tag.name))
        };

        // CSRF-Token aus dem Cookie holen
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];

        // CSRF-Token in den Header setzen
        axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        await this.$axios.put(`/recipes/${this.selectedRecipe.id}`, updatedRecipe);
        this.isEditModalVisible = false;
        this.recipes = this.recipes.map(recipe =>
            recipe.id === updatedRecipe.id ? updatedRecipe : recipe
        );
      } catch (error) {
        console.error("Fehler beim Bearbeiten des Rezepts:", error);
      }
    },
    addIngredient() {
      if (!this.selectedRecipe.ingredients) {
        this.selectedRecipe.ingredients = [];
      }
      this.selectedRecipe.ingredients.push({ name: "", quantity: "", unit: "" });
    },
    removeIngredient(index) {
      this.selectedRecipe.ingredients.splice(index, 1);
    },
    // This method is used to update the favorite status of the selected recipe.
    async toggleFavorite() {
      try {
        // Toggle the favorite status on the client side first
        this.selectedRecipe.favorite = !this.selectedRecipe.favorite;

        // CSRF-Token aus dem Cookie holen
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];

        // CSRF-Token in den Header setzen
        axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        // Send the updated favorite status to the backend
        await this.$axios.put(`/recipes/${this.selectedRecipe.id}/favorite`);

        // Optionally, you can handle any additional response from the backend
        console.log('Favorite status updated successfully');

        // Update the recipes list to reflect the change immediately
        this.recipes = this.recipes.map(recipe =>
            recipe.id === this.selectedRecipe.id ? { ...recipe, favorite: this.selectedRecipe.favorite } : recipe
        );

        // Optionally close the modal or keep it open based on your design
        // this.closeModal(); // Uncomment this line if you want to close the modal after toggling the favorite
      } catch (error) {
        // If something goes wrong with the request, revert the favorite status change
        this.selectedRecipe.favorite = !this.selectedRecipe.favorite;
        console.error("Error updating favorite status:", error);
      }
    },

    // This method opens the modal and ensures that the selected recipe is properly set.

    getTagLabel(tag) {
      return tagMapping[tag] || tag;
    },
    getUnitLabel(unit) {
      const unitObj = this.quantityUnits.find(u => u.value === unit);
      return unitObj ? unitObj.label : unit;
    }
  }
};
</script>

<style scoped>
.recipe-container {
  display: flex;
  font-family: Arial, sans-serif;
  flex-direction: column;
  padding: 20px;
  height: 650px;
  width: 400px;
  background-color: #f9f9f9;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
  background-color: #f9f9f9;
  border-radius: 8px;
}

h2 {
  font-size: 1.75rem;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
}

.search-fields {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  justify-content: center;
}

.search-input, .search-button, .tags-filter, .cooking-time-filter {
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 1rem;
  font-family: 'Arial', sans-serif;
}

.search-input {
  width: 250px;
}

.search-button {
  background-color: #007BFF;
  color: white;
  cursor: pointer;
  width: 150px;
  text-align: center;
  transition: background-color 0.3s ease;
}

.search-button:hover {
  background-color: #0056b3;
}

.tags-filter, .cooking-time-filter {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  width: 400px;
  box-sizing: border-box;
}

.tags-filter label, .cooking-time-filter label {
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

.tag-dropdown, .cooking-time-filter select {
  width: 100%;
  padding: 10px;
  border-radius: 8px;
  background-color: #fafafa;
  border: 1px solid #ccc;
  box-sizing: border-box;
}

.cooking-time-filter input {
  width: 100%;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc;
  background-color: #fafafa;
  box-sizing: border-box;
}

.tags-filter, .cooking-time-filter {
  overflow: hidden;
}

.cooking-time-filter select, .tag-dropdown {
  max-width: 100%;
}

@media (max-width: 768px) {
  .search-input, .search-button, .tags-filter, .cooking-time-filter {
    width: 100%;
  }

  .search-button {
    width: auto;
  }

  .search-fields {
    flex-direction: column;
    align-items: stretch;
  }
}

.recipe-cards-container {
  display: block;
  max-height: 250px;
  overflow-y: auto;
}

.recipe-card {
  max-height: 20px;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
  margin-bottom: 6px;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #81c784;
  color: #333333;
}

.recipe-card:hover {
  background-color: #66bb6a;
  transform: translateY(-2px);
}

.recipe-name {
  font-size: 1.2rem;
  font-weight: bold;
}

.no-recipes {
  font-size: 1.2rem;
  color: #888;
}

/* Modale */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.modal-actions button {
  padding: 10px;
  border-radius: 4px;
  background-color: #007BFF;
  color: white;
  cursor: pointer;
}

.modal-actions button:hover {
  background-color: #0056b3;
}

.delete-button, .edit-button {

  padding: 10px;
  border-radius: 4px;
  background-color: #dc3545;
  color: white;
  border: none;
  cursor: pointer;
  margin-right: 10px;
}

.delete-button:hover {
  background-color: #c82333;
}

.edit-button {
  background-color: #ffc107;
}

.edit-button:hover {
  background-color: #e0a800;
}
.favorite-button {
  padding: 10px;
  border-radius: 4px;
  background-color: #095cbb;
  color: white;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.favorite-button.active {
  background-color: #099abb; /* Active favorite color */
}

.favorite-button:hover {
  background-color: #0056b3; /* Hover effect */
}

.recipe-form .input-group {
  margin-bottom: 15px;
}

.recipe-form input,
.recipe-form textarea,
.recipe-form select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

.recipe-form button {
  padding: 10px;
  background-color: #28a745;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  border: none;
}

.recipe-form button:hover {
  background-color: #218838;
}

.remove-button, .add-button {
  background-color: #dc3545;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.remove-button:hover {
  background-color: #c82333;
}

.add-button {
  background-color: #007BFF;
}

.add-button:hover {
  background-color: #0056b3;
}

.ingredient-group {
  margin-bottom: 15px;
}

.ingredient-item {
  display: flex;
  gap: 10px;
}

.ingredient-item input {
  width: 150px;
}

.unit-selection {
  flex-grow: 1;
}

.ingredient-list {
  list-style: none;
  padding-left: 0;
}

.ingredient-list li {
  font-size: 1rem;
  margin-bottom: 5px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.tag-box {
  background-color: #007BFF;
  color: white;
  padding: 5px 10px;
  border-radius: 20px;
}

.tag-dropdown {
  width: 100%;
}

@media (max-width: 1024px) {
  .recipe-cards-container {
    grid-template-columns: repeat(1, 1fr);
  }
}

@media (max-width: 768px) {
  .search-fields {
    flex-direction: column;
  }
}

.ingredient-list-container {
  max-height: 200px;
  overflow-y: auto;
}

.modal-content.large-modal {
  width: 80%;
  max-width: 1000px;
  height: auto;
  max-height: 90%;
  overflow-y: auto;
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


.filter-favorites-button {
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 1rem;
  background-color: #ff9900;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.filter-favorites-button:hover {
  background-color: #e68900;
}
</style>

