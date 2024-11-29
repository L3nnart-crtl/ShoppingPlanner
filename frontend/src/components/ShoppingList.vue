<template>
  <div class="shopping-list">
    <h1>Einkaufsliste für die nächsten Tage</h1>

    <!-- Formular zur Auswahl von Start- und Enddatum -->
    <form @submit.prevent="generateShoppingList" class="date-form">
      <div class="form-group">
        <label for="startDate">Startdatum:</label>
        <input type="date" id="startDate" v-model="startDate" required>
      </div>

      <div class="form-group">
        <label for="endDate">Enddatum:</label>
        <input type="date" id="endDate" v-model="endDate" required>
      </div>

      <button type="submit" :disabled="loading">
        <span v-if="loading">Generieren...</span>
        <span v-else>Einkaufsliste generieren</span>
      </button>
    </form>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <h2>Generierte Einkaufsliste</h2>
    <ul v-if="shoppingList.length" class="shopping-list-items">
      <li v-for="item in shoppingList" :key="item.ingredientName">
        {{ item.ingredientName }}: {{ item.unit }}
      </li>
    </ul>

    <!-- Nachricht, falls keine Zutaten gefunden wurden -->
    <p v-else-if="isListGenerated" class="no-items">
      Keine Zutaten für diesen Zeitraum gefunden.
    </p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      startDate: '', // Startdatum
      endDate: '', // Enddatum
      shoppingList: [], // Generierte Einkaufsliste
      isListGenerated: false, // Flag für generierte Liste
      loading: false, // Ladezustand
      errorMessage: '', // Fehlernachricht
    };
  },
  methods: {
    async generateShoppingList() {
      // Überprüfen, ob beide Daten gesetzt sind
      if (!this.startDate || !this.endDate) {
        this.errorMessage = 'Bitte wähle sowohl ein Startdatum als auch ein Enddatum.';
        return;
      }

      // Ladezustand und Fehler zurücksetzen
      this.loading = true;
      this.errorMessage = '';
      this.isListGenerated = false;

      try {
        // Sende eine POST-Anfrage an den Server
        const response = await fetch(
            `/api/shopping-list/generate?startDate=${this.startDate}&endDate=${this.endDate}`,
            {
              method: 'POST',
              headers: {'Content-Type': 'application/json'},
            }
        );

        if (!response.ok) {
          throw new Error('Serverfehler: Die Einkaufsliste konnte nicht generiert werden.');
        }

        // JSON-Antwort verarbeiten
        const data = await response.json();
        this.shoppingList = data;
        this.isListGenerated = true;
      } catch (error) {
        this.errorMessage = error.message || 'Fehler beim Abrufen der Einkaufsliste.';
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.shopping-list {
  max-width: 600px;
  margin: 0 auto;
  font-family: Arial, sans-serif;
}

.date-form {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input[type="date"] {
  width: 100%;
  padding: 8px;
  font-size: 14px;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  color: white;
  background-color: #4caf50;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background-color: #45a049;
}

ul.shopping-list-items {
  list-style-type: none;
  padding-left: 0;
}

ul.shopping-list-items li {
  margin: 5px 0;
  padding: 8px;
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.error-message {
  color: red;
  font-weight: bold;
  margin-top: 10px;
}

.no-items {
  color: gray;
  font-style: italic;
  margin-top: 10px;
}
</style>
