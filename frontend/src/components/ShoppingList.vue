<template>
  <div>
    <h1>Einkaufsliste für die nächsten Tage</h1>

    <!-- Formular zur Auswahl von Start- und Enddatum -->
    <form @submit.prevent="generateShoppingList">
      <label for="startDate">Start Date:</label>
      <input type="date" v-model="startDate" required><br><br>

      <label for="endDate">End Date:</label>
      <input type="date" v-model="endDate" required><br><br>

      <button type="submit">Einkaufsliste generieren</button>
    </form>

    <h2>Generierte Einkaufsliste</h2>
    <!-- Anzeige der generierten Einkaufsliste -->
    <ul v-if="shoppingList.length">
      <li v-for="item in shoppingList" :key="item.ingredientName">
        {{ item.ingredientName }}: {{ item.amount }} Einheiten
      </li>
    </ul>

    <!-- Anzeige einer Nachricht, falls keine Einkaufsliste vorhanden ist -->
    <p v-else v-if="isListGenerated">Keine Zutaten für diesen Zeitraum gefunden.</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      startDate: '',  // Startdatum
      endDate: '',    // Enddatum
      shoppingList: [], // Die generierte Einkaufsliste
      isListGenerated: false, // Flag, ob die Einkaufsliste erfolgreich generiert wurde
    };
  },
  methods: {
    async generateShoppingList() {
      // Überprüfe, ob beide Daten gesetzt sind
      if (!this.startDate || !this.endDate) {
        alert('Bitte wähle sowohl Start- als auch Enddatum.');
        return;
      }

      try {
        // Sende eine POST-Anfrage an den Server mit den angegebenen Daten
        const response = await fetch('/api/shopping-list/generate', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            startDate: this.startDate,
            endDate: this.endDate,
          }),
        });

        if (!response.ok) {
          throw new Error('Fehler bei der Generierung der Einkaufsliste');
        }

        // Die Antwort vom Server (die Einkaufsliste) wird hier verarbeitet
        const data = await response.json();
        this.shoppingList = data;
        this.isListGenerated = true; // Erfolg: Liste wurde generiert
      } catch (error) {
        console.error('Fehler:', error);
        alert('Fehler beim Abrufen der Einkaufsliste.');
      }
    },
  },
};
</script>

<style scoped>
form {
  margin-bottom: 20px;
}

button {
  margin-top: 10px;
  padding: 8px 15px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

ul {
  list-style-type: none;
  padding-left: 0;
}

li {
  margin: 5px 0;
}
</style>
