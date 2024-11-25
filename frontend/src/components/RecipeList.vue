<template>
  <div class="recipe-list-container">
    <h2 class="list-title">Rezepte</h2>

    <!-- Rezeptliste mit Scrollbalken -->
    <div v-if="recipes.length" class="recipe-cards-container">
      <div v-for="recipe in recipes" :key="recipe.id" class="recipe-card" @click="openRecipeDetails(recipe)">
        <h3 class="recipe-name">{{ recipe.name || 'Kein Name' }}</h3>
      </div>
    </div>

    <!-- Wenn keine Rezepte vorhanden sind -->
    <p v-else class="no-recipes">Keine Rezepte verfügbar.</p>

    <!-- Modal für Rezeptdetails -->
    <div v-if="isModalVisible" class="modal-overlay">
      <div class="modal-content">
        <!-- Löschen Button im Modal oben rechts -->
        <button v-if="selectedRecipe" @click="confirmDeleteRecipe(selectedRecipe)" class="delete-button">Rezept löschen</button>

        <h4>{{ selectedRecipe.name }}</h4>
        <p><strong>Beschreibung:</strong> {{ selectedRecipe.description || 'Keine Beschreibung verfügbar.' }}</p>

        <p><strong>Zutaten:</strong></p>
        <ul class="ingredient-list">
          <li v-for="(ingredient, index) in selectedRecipe.ingredients" :key="index">
            {{ ingredient.name }} - {{ ingredient.quantity }} {{ ingredient.unit }}
          </li>
        </ul>

        <!-- Tags anzeigen -->
        <p><strong>Tags:</strong></p>
        <div class="tags-container">
          <div
              v-for="(tag, index) in selectedRecipe.tags"
              :key="index"
              class="tag-box"
          >
            {{ getTagLabel(tag) }}
          </div>
        </div>

        <!-- Modal Aktionen -->
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
import axios from 'axios';

export default {
  props: {
    recipes: Array,
  },
  data() {
    return {
      isModalVisible: false,
      isDeleteModalVisible: false,
      selectedRecipe: null,
    };
  },
  methods: {
    // Öffnet das Modal mit den Details des Rezepts
    openRecipeDetails(recipe) {
      this.selectedRecipe = recipe;
      this.isModalVisible = true;
    },

    // Schließt das Rezeptmodal
    closeModal() {
      this.isModalVisible = false;
      this.selectedRecipe = null;
    },

    // Zeigt das Bestätigungs-Modal zum Löschen an
    confirmDeleteRecipe(recipe) {
      this.isModalVisible = false; // Rezept-Modal schließen
      this.selectedRecipe = recipe;
      this.isDeleteModalVisible = true; // Bestätigungs-Modal öffnen
    },

    // Löscht das Rezept
    async deleteRecipe() {
      try {
        // Sende eine DELETE-Anfrage an das Backend, um das Rezept zu löschen
        await axios.delete(`/api/recipes/${this.selectedRecipe.id}`); // Ersetze den URL mit deinem Backend-Endpunkt

        // Event auslösen, um das Rezept in der UI zu entfernen
        this.$emit('recipe-removed', this.selectedRecipe.id);

        // Bestätigungs-Modal schließen und Rezept zurücksetzen
        this.isDeleteModalVisible = false;
        this.selectedRecipe = null;
      } catch (error) {
        console.error('Fehler beim Löschen des Rezepts:', error);
        // Hier kannst du auch eine Fehlerbehandlung einbauen, z.B. eine Benachrichtigung für den Benutzer
      }
    },

    // Schließt das Bestätigungs-Modal
    closeDeleteModal() {
      this.isDeleteModalVisible = false;
      this.selectedRecipe = null; // Rezept zurücksetzen
    },

    // Hilfsmethode, um die deutsche Bezeichnung des Tags zu bekommen
    getTagLabel(tag) {
      const tagLabels = {
        VEGETARIAN: "Vegetarisch",
        VEGAN: "Vegan",
        GLUTEN_FREE: "Glutenfrei",
        LACTOSE_FREE: "Laktosefrei",
        KETO: "Keto",
        PALEO: "Paleo",
        LOW_CARB: "Low Carb",
        HIGH_PROTEIN: "High Protein",
        DIABETIC_FRIENDLY: "Diabetikerfreundlich",
        LOW_FAT: "Low Fat",
        HALAL: "Halal",
        KOSHER: "Koscher",
        EXCLUSION_DIET: "Ausschlussdiät",
        QUICK: "Schnell",
        MEAL_PREP: "Meal Prep",
        KIDS_MEAL: "Mahlzeit für Kinder",
        SWEET: "Süß",
        SAVORY: "Herzhaft",
        SPICY: "Scharf",
        MILD: "Mild",
        EXOTIC: "Exotisch",
        SEASONAL: "Saisonale Rezepte",
        GRILL: "Grillrezepte",
        BREAKFAST: "Frühstück",
        LUNCH: "Mittagessen",
        DINNER: "Abendessen",
        SNACKS: "Snacks",
        SOUPS: "Suppen",
        SALADS: "Salate",
        SUGAR_FREE: "Zuckerfrei",
        FISH: "Fisch",
        MEAT: "Fleisch",
        NUTS: "Nüsse",
        WHOLEGRAIN: "Vollkorn",
        LEGUMES: "Hülsenfrüchte",
        PASTA: "Nudeln",
        RICE: "Reis",
        BUDGET_FRIENDLY: "Budgetfreundlich",
        FOR_2_PEOPLE: "Für 2 Personen",
        FAMILY_FRIENDLY: "Familientauglich",
        FOR_BEGGINERS: "Für Anfänger",
        GOURMET: "Gourmet",
        STORAGE: "Lagerung",
        DESSERT: "Nachtisch",
        QUICK_PREP: "Schnell",
        SLOW_COOK: "Langsame Zubereitung",
        ONE_POT: "Ein-Pfannen-Gerichte",
        FRYING: "Frittieren",
        BAKING: "Backen",
        LOW_CALORIE: "Low Calorie",
        ANTI_AGEING: "Anti-Aging",
        DETOX: "Entgiftend",
        HEART_HEALTH: "Herzgesund",
      };

      return tagLabels[tag] || tag; // Rückgabe der Bezeichnung, falls vorhanden, sonst das Tag selbst
    }
  }
};
</script>

<style scoped>
/* Stil für die Rezeptkarte */
.recipe-list-container {
  position: relative;
  padding: 20px;
}

.recipe-cards-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  max-height: 500px; /* Maximale Höhe für den scrollbaren Container */
  overflow-y: auto;  /* Scrollbalken aktivieren */
  scrollbar-width: thin; /* Dünnerer Standard-Scrollbar */
  scrollbar-color: #888 #ddd; /* Farbe für Scrollbalken und Hintergrund */
  border-radius: 12px; /* Abgerundete Ecken des Containers */
  border: 1px solid #ddd; /* Dünner Rahmen für den Container */
  padding: 10px; /* Innenabstand */
}

/* Stil für benutzerdefinierten Scrollbalken */
.recipe-cards-container::-webkit-scrollbar {
  width: 12px; /* Breite des Scrollbalkens */
}

.recipe-cards-container::-webkit-scrollbar-thumb {
  background-color: #888;
  border-radius: 10px;
}

.recipe-cards-container::-webkit-scrollbar-track {
  background-color: #ddd;
  border-radius: 10px;
}

.recipe-card {
  flex: 1 0 30%; /* Stellen Sie sicher, dass die Karten 3 nebeneinander sind */
  padding: 15px;
  background-color: #f4f4f4;
  border: 1px solid #ccc;
  border-radius: 8px;
  cursor: pointer;
  box-sizing: border-box;
  transition: transform 0.3s ease-in-out;
}

.recipe-card:hover {
  transform: scale(1.05); /* Leichte Vergrößerung bei Hover */
}

.recipe-name {
  font-size: 1.2em;
  font-weight: bold;
}

/* Button zum Löschen des Rezepts im Modal */
.delete-button {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 10px;
  background-color: #e60000;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.delete-button:hover {
  background-color: #cc0000;
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
  border-radius: 8px;
  max-width: 500px;
  width: 100%;
  position: relative; /* Relativ positioniert für den Löschen-Button */
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

button {
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  background-color: #4CAF50;
  color: white;
}

button:hover {
  background-color: #45a049;
}

.delete-modal {
  background-color: #ffeded;
  color: #ff4d4d;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-box {
  background-color: #f0f0f0;
  padding: 5px 10px;
  border-radius: 12px;
  font-size: 0.9em;
}
</style>
