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
  </div>
</template>

<script>
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.min.css";
import axios from "axios";
// In deiner Vue-Komponente
import {quantityUnits, tagsForList} from '@/assets/TagsAndUnits.js';


export default {
  components: { Multiselect },
  data() {
    return {
      searchQuery: {
        name: "",
        tags: [],
      },
      recipes: [],
      availableTags: tagsForList,
      quantityUnits: quantityUnits,
      isModalVisible: false,
      isDeleteModalVisible: false,
      selectedRecipe: null,
    };
  },
  mounted() {
    // Beim Laden der Seite nach allen Rezepten suchen
    this.searchRecipes();
  },
  methods: {

    async searchRecipes() {
      try {
        const params = {};

        if (this.searchQuery.name) {
          params.name = this.searchQuery.name;
        }

        if (this.searchQuery.tags.length > 0) {
          params.tags = this.searchQuery.tags.map(tag => tag.value).join(",");
        }

        const response = await axios.get("/api/recipes/search", { params });
        this.recipes = response.data;
      } catch (error) {
        console.error("Fehler bei der Rezeptsuche:", error);
      }
    },

    recipesUpdated(newRecipes) {
      this.recipes = newRecipes;
    },
    removeRecipe(recipeId) {
      this.$emit('recipe-removed', recipeId);
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
          this.removeRecipe(this.selectedRecipe.id);
        } catch (error) {
          console.error("Fehler beim Löschen des Rezepts:", error);
        }
      }
    },

    getUnitLabel(unit) {
      const unitObj = this.quantityUnits.find(u => u.value === unit);
      return unitObj ? unitObj.label : unit; // Falls keine Übersetzung gefunden wird, den Wert direkt verwenden
    },

    getTagLabel(tag) {
      const tagObj = this.availableTags.find(t => t.value === tag);
      return tagObj ? tagObj.name : tag; // Falls keine Übersetzung gefunden wird, den Wert direkt verwenden
    }
  }
};
</script>

<style scoped>
/* Stil für die Rezeptkarte */
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
  transition: transform 0.3s ease-in-out;
}

.recipe-card:hover {
  transform: scale(1.05);
}

.recipe-name {
  font-size: 1em;
  font-weight: bold;
}

.no-recipes {
  font-size: 14px;
  color: gray;
}

/* Modal Overlay und Inhalt */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Größere Modal-Größe für mehr Inhalt */
.modal-content {
  background-color: white;
  padding: 16px;
  max-width: 700px;   /* Reduzierte Breite */
  width: 100%;
  max-height: 80vh;   /* Maximale Höhe des Modals */
  overflow-y: auto;   /* Ermöglicht Scrollen innerhalb des Modals */
  border-radius: 8px;
}

.delete-button {
  background-color: red;
  color: white;
  border: none;
  padding: 4px;
  cursor: pointer;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
}

.delete-modal {
  width: 350px;
}

.delete-modal .modal-actions {
  gap: 8px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px; /* Reduzierter Abstand */
}

.tag-box {
  background-color: #f1f1f1;
  padding: 4px;
  border-radius: 5px;
}

/* Stil für die Zutatenliste im Modal */
.ingredient-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
  font-size: 18px; /* Kleinere Schriftgröße */
  line-height: 1.3; /* Reduzierter Zeilenabstand */
  max-height: 400px; /* Höhere Liste, wenn nötig */
  overflow-y: auto;  /* Aktiviert das Scrollen bei vielen Zutaten */
}

.ingredient-list li {
  margin-bottom: 6px; /* Weniger Abstand zwischen den Listeneinträgen */
}
</style>
