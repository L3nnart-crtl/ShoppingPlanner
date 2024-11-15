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
        <!-- Delete Button für das Rezept, nur sichtbar wenn das Rezept geöffnet ist -->
        <button v-if="selectedRecipe" @click="confirmDeleteRecipe(selectedRecipe)" class="delete-button">Rezept löschen</button>

        <h4>{{ selectedRecipe.name }}</h4>
        <p><strong>Beschreibung:</strong> {{ selectedRecipe.description || 'Keine Beschreibung verfügbar.' }}</p>
        <p><strong>Zutaten:</strong></p>
        <ul>
          <li v-for="(ingredient, index) in selectedRecipe.ingredients" :key="index">
            {{ ingredient.name }} - {{ ingredient.quantity }} {{ ingredient.unit }}
          </li>
        </ul>
        <p><strong>Zubereitung:</strong> {{ selectedRecipe.instructions || 'Keine Zubereitungshinweise verfügbar.' }}</p>

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
        // Hier könnte deine API-Anfrage stehen, um das Rezept zu löschen
        this.$emit('recipe-removed', this.selectedRecipe.id); // Event auslösen, um das Rezept zu entfernen
        this.isDeleteModalVisible = false; // Bestätigungs-Modal schließen
        this.selectedRecipe = null; // Rezept zurücksetzen
      } catch (error) {
        console.error('Fehler beim Löschen des Rezepts:', error);
      }
    },

    // Schließt das Bestätigungs-Modal
    closeDeleteModal() {
      this.isDeleteModalVisible = false;
      this.selectedRecipe = null; // Rezept zurücksetzen
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

/* Button zum Löschen des Rezepts */
.delete-button {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 10px;
  background-color: #ff4d4d;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.delete-button:hover {
  background-color: #ff1a1a;
}

/* Modal-Stile */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 12px; /* Abgerundete Ecken des Modals */
  max-width: 800px;  /* Hier stellen wir sicher, dass das Modal 3 Rezeptkarten nebeneinander entspricht */
  width: 100%;
  text-align: center;
  position: relative; /* Damit der Delete-Button oben rechts positioniert werden kann */
}

.delete-button {
  position: absolute;
  top: 10px;
  right: 10px;
}

.delete-modal {
  max-width: 33%;  /* Modal auf die Breite von 3 Rezepten begrenzen */
}

.modal-actions button {
  padding: 10px 20px;
  margin: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.modal-actions button:hover {
  opacity: 0.8;
}
</style>
