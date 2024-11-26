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
            {{ ingredient.name }} - {{ ingredient.quantity }} {{ ingredient.unit }}
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

export default {
  components: { Multiselect },
  data() {
    return {
      searchQuery: {
        name: "",
        tags: [],
      },
      recipes: [],
      availableTags: [
        { name: "Vegetarisch", value: "VEGETARIAN" },
        { name: "Vegan", value: "VEGAN" },
        { name: "Glutenfrei", value: "GLUTEN_FREE" },
        { name: "Laktosefrei", value: "LACTOSE_FREE" },
        { name: "Keto", value: "KETO" },
        { name: "Paleo", value: "PALEO" },
        { name: "Low Carb", value: "LOW_CARB" },
        { name: "High Protein", value: "HIGH_PROTEIN" },
        { name: "Diabetikerfreundlich", value: "DIABETIC_FRIENDLY" },
        { name: "Low Fat", value: "LOW_FAT" },
        { name: "Halal", value: "HALAL" },
        { name: "Koscher", value: "KOSHER" },
        { name: "Ausschlussdiät", value: "EXCLUSION_DIET" },
        { name: "Schnell", value: "QUICK" },
        { name: "Meal Prep", value: "MEAL_PREP" },
        { name: "Mahlzeit für Kinder", value: "KIDS_MEAL" },
        { name: "Süß", value: "SWEET" },
        { name: "Herzhaft", value: "SAVORY" },
        { name: "Scharf", value: "SPICY" },
        { name: "Mild", value: "MILD" },
        { name: "Exotisch", value: "EXOTIC" },
        { name: "Saisonale Rezepte", value: "SEASONAL" },
        { name: "Grillrezepte", value: "GRILL" },
        { name: "Frühstück", value: "BREAKFAST" },
        { name: "Mittagessen", value: "LUNCH" },
        { name: "Abendessen", value: "DINNER" },
        { name: "Snacks", value: "SNACKS" },
        { name: "Suppen", value: "SOUPS" },
        { name: "Salate", value: "SALADS" },
        { name: "Zuckerfrei", value: "SUGAR_FREE" },
        { name: "Fisch", value: "FISH" },
        { name: "Fleisch", value: "MEAT" },
        { name: "Nüsse", value: "NUTS" },
        { name: "Vollkorn", value: "WHOLEGRAIN" },
        { name: "Hülsenfrüchte", value: "LEGUMES" },
        { name: "Nudeln", value: "PASTA" },
        { name: "Reis", value: "RICE" },
        { name: "Budgetfreundlich", value: "BUDGET_FRIENDLY" },
        { name: "Für 2 Personen", value: "FOR_2_PEOPLE" },
        { name: "Familientauglich", value: "FAMILY_FRIENDLY" },
        { name: "Für Anfänger", value: "FOR_BEGGINERS" },
        { name: "Gourmet", value: "GOURMET" },
        { name: "Lagerung", value: "STORAGE" },
        { name: "Nachtisch", value: "DESSERT" },
        { name: "Langsame Zubereitung", value: "SLOW_COOK" },
        { name: "Ein-Pfannen-Gerichte", value: "ONE_POT" },
        { name: "Frittieren", value: "FRYING" },
        { name: "Backen", value: "BAKING" },
        { name: "Low Calorie", value: "LOW_CALORIE" },
        { name: "Anti-Aging", value: "ANTI_AGEING" },
        { name: "Entgiftend", value: "DETOX" },
        { name: "Herzgesund", value: "HEART_HEALTH" },
      ],
      isModalVisible: false,
      isDeleteModalVisible: false,
      selectedRecipe: null,
    };
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

    openRecipeDetails(recipe) {
      this.selectedRecipe = recipe;
      this.isModalVisible = true;
    },

    closeModal() {
      this.isModalVisible = false;
      this.selectedRecipe = null;
    },

    confirmDeleteRecipe(recipe) {
      this.isModalVisible = false;
      this.selectedRecipe = recipe;
      this.isDeleteModalVisible = true;
    },

    async deleteRecipe() {
      try {
        await axios.delete(`/api/recipes/${this.selectedRecipe.id}`);
        this.recipes = this.recipes.filter(r => r.id !== this.selectedRecipe.id);
        this.closeDeleteModal();
      } catch (error) {
        console.error("Fehler beim Löschen des Rezepts:", error);
      }
    },

    closeDeleteModal() {
      this.isDeleteModalVisible = false;
      this.selectedRecipe = null;
    },

    getTagLabel(tagValue) {
      const tag = this.availableTags.find(t => t.value === tagValue);
      return tag ? tag.name : tagValue;
    }
  },
  created() {
    // Initiales Laden von Rezepten ohne Filter
    this.searchRecipes();
  },
};
</script>

<style scoped>
/* Stil für die Rezeptkarte */
.recipe-container {
  margin: 20px;
}

.search-fields {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.search-input {
  padding: 8px;
  font-size: 14px;
}

.tag-dropdown {
  width: 100%;
  font-size: 14px;
}

.search-button {
  padding: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
}

.recipe-cards-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  max-height: 500px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #888 #ddd;
  border-radius: 12px;
  border: 1px solid #ddd;
  padding: 10px;
}

.recipe-card {
  flex: 1 0 30%;
  padding: 15px;
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
  font-size: 1.2em;
  font-weight: bold;
}

.no-recipes {
  font-size: 16px;
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

.modal-content {
  background-color: white;
  padding: 20px;
  max-width: 600px;
  width: 100%;
  border-radius: 8px;
}

.delete-button {
  background-color: red;
  color: white;
  border: none;
  padding: 5px;
  cursor: pointer;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
}

.delete-modal {
  width: 400px;
}

.delete-modal .modal-actions {
  gap: 10px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-box {
  background-color: #f1f1f1;
  padding: 5px;
  border-radius: 5px;
}
</style>
