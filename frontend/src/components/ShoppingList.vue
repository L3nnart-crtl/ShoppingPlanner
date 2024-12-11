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
      <li v-for="item in shoppingList" :key="item.ingredientName" class="shopping-list-item">
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
      startDate: '',
      endDate: '',
      shoppingList: [],
      isListGenerated: false,
      loading: false,
      errorMessage: '',
    };
  },
  methods: {
    async generateShoppingList() {
      // Überprüfen, ob beide Daten gesetzt sind
      if (!this.startDate || !this.endDate) {
        this.errorMessage = 'Bitte wähle sowohl ein Startdatum als auch ein Enddatum.';
        return;
      }


      this.loading = true;
      this.errorMessage = '';
      this.isListGenerated = false;

      try {

        const response = await fetch(
            `/shopping-list/generate?startDate=${this.startDate}&endDate=${this.endDate}`,
            {
              method: 'POST',
              headers: {'Content-Type': 'application/json'},
            }
        );

        if (!response.ok) {
          throw new Error('Serverfehler: Die Einkaufsliste konnte nicht generiert werden.');
        }


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
  max-width: 400px;
  height: 650px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
  background-color: #f9f9f9;
  border-radius: 8px;
}

h1 {
  text-align: center;
  font-size: 26px;
  color: #333;
}

.date-form {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  font-size: 14px;
  color: #333;
}

input[type="date"] {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border-radius: 5px;
  border: 1px solid #ddd;
  background-color: #fff;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="date"]:focus {
  border-color: #4caf50;
  box-shadow: 0 0 5px rgba(76, 175, 80, 0.3);
}

button {
  padding: 12px 20px;
  font-size: 16px;
  color: white;
  background-color: #4caf50;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background-color: #388e3c;
}

.shopping-list-items {
  list-style-type: none;
  padding-left: 0;
  margin-top: 20px;
  max-height: 300px;
  overflow-y: auto;
}

.shopping-list-item {
  margin: 10px 0;
  padding: 12px;
  background-color: #fff;
  border-radius: 6px;
  border: 1px solid #ddd;
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: background-color 0.3s ease;
}

.shopping-list-item:hover {
  background-color: #f1f1f1;
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
