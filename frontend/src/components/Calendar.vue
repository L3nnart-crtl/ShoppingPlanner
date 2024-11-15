<template>
  <div class="calendar-container">
    <h2>Kalender</h2>

    <!-- Navigation für die Woche -->
    <div class="week-navigation">
      <button @click="changeWeek(-1)">&#8592; Vorherige Woche</button>
      <span>{{ currentWeekStartDate }} bis {{ currentWeekEndDate }}</span>
      <button @click="changeWeek(1)">Nächste Woche &#8594;</button>
    </div>

    <!-- Kalender: Eine Woche anzeigen -->
    <div class="calendar">
      <div class="calendar-week">
        <div v-for="day in currentWeekDays" :key="day.date" class="calendar-day">
          <div class="day-header">
            <span>{{ day.shortWeekday }}</span> <!-- Nur Wochentagsabkürzung -->
          </div>
          <div v-if="mealPlans[day.date]">
            <p>Frühstück: {{ mealPlans[day.date].breakfastRecipe.name }}</p>
            <p>Mittagessen: {{ mealPlans[day.date].lunchRecipe.name }}</p>
            <p>Abendessen: {{ mealPlans[day.date].dinnerRecipe.name }}</p>
            <button @click="removeMealPlan(day.date)" class="remove-button">Entfernen</button>
          </div>
          <div v-else>
            <p>Kein MealPlan vorhanden</p>
            <button @click="openModal(day.date)">MealPlan hinzufügen</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal zum Hinzufügen eines MealPlans -->
    <div v-if="isModalVisible" class="modal-overlay">
      <div class="modal-content">
        <h3>MealPlan für {{ selectedDate }}</h3>

        <!-- Frühstück -->
        <div>
          <label>Frühstück:</label>
          <select v-model="mealPlan.breakfastId">
            <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
          </select>
        </div>

        <!-- Mittagessen -->
        <div>
          <label>Mittagessen:</label>
          <select v-model="mealPlan.lunchId">
            <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
          </select>
        </div>

        <!-- Abendessen -->
        <div>
          <label>Abendessen:</label>
          <select v-model="mealPlan.dinnerId">
            <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
          </select>
        </div>

        <!-- Aktionen: Speichern / Schließen -->
        <div class="modal-actions">
          <button @click="saveMealPlan">Speichern</button>
          <button @click="closeModal">Abbrechen</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['recipes'],
  data() {
    return {
      mealPlans: {},
      currentDate: new Date(),
      currentWeekDays: [],
      currentWeekStartDate: '',
      currentWeekEndDate: '',
      isModalVisible: false,
      selectedDate: '',
      mealPlan: {
        breakfastId: null,
        lunchId: null,
        dinnerId: null,
      },
    };
  },
  async created() {
    this.loadWeek();
  },
  methods: {
    // Lädt die aktuellen 7 Tage (Woche)
    async loadWeek() {
      const startOfWeek = this.getStartOfWeek(this.currentDate);
      this.currentWeekDays = this.getDaysOfWeek(startOfWeek);
      this.loadMealPlansForWeek();
    },

    // Lädt die MealPlans für die gesamte Woche
    async loadMealPlansForWeek() {
      for (let day of this.currentWeekDays) {
        try {
          const response = await this.$axios.get(`/mealplans/${day.date}`);
          if (response.data) {
            this.$set(this.mealPlans, day.date, response.data);
          }
        } catch (error) {
          console.error('Fehler beim Laden des MealPlans für den Tag:', error);
        }
      }
      this.updateWeekRange();
    },

    // Öffnet das Modal zum Hinzufügen eines MealPlans
    openModal(date) {
      this.selectedDate = date;
      this.isModalVisible = true;
    },

    // Schließt das Modal
    closeModal() {
      this.isModalVisible = false;
      this.selectedDate = '';
      this.mealPlan = { breakfastId: null, lunchId: null, dinnerId: null }; // Reset
    },

    // Speichert den MealPlan und schließt das Modal
    async saveMealPlan() {
      try {
        const response = await this.$axios.post('/mealplans', {
          date: this.selectedDate,
          breakfastRecipeId: this.mealPlan.breakfastId,
          lunchRecipeId: this.mealPlan.lunchId,
          dinnerRecipeId: this.mealPlan.dinnerId,
        });
        this.$set(this.mealPlans, this.selectedDate, response.data);
        this.closeModal();
      } catch (error) {
        console.error('Fehler beim Speichern des MealPlans:', error);
      }
    },

    // Berechnet das Startdatum der aktuellen Woche (Montag)
    getStartOfWeek(date) {
      const startOfWeek = new Date(date);
      const day = startOfWeek.getDay();
      const diff = startOfWeek.getDate() - day + (day === 0 ? -6 : 1); // Montag als ersten Tag der Woche
      startOfWeek.setDate(diff);
      return startOfWeek;
    },

    // Gibt die 7 Tage der aktuellen Woche zurück
    getDaysOfWeek(startOfWeek) {
      const days = [];
      for (let i = 0; i < 7; i++) {
        const day = new Date(startOfWeek);
        day.setDate(startOfWeek.getDate() + i);
        const weekday = day.toLocaleDateString('de-DE', { weekday: 'short' }); // Wochentagsabkürzung
        days.push({ date: day.toISOString().split('T')[0], shortWeekday: weekday });
      }
      return days;
    },

    // Wechsel zwischen den Wochen
    changeWeek(direction) {
      const currentDate = new Date(this.currentDate);
      currentDate.setDate(currentDate.getDate() + (direction * 7));
      this.currentDate = currentDate;
      this.loadWeek();
    },

    // Aktualisiert den angezeigten Wochenbereich
    updateWeekRange() {
      const startOfWeek = this.getStartOfWeek(this.currentDate);
      const endOfWeek = new Date(startOfWeek);
      endOfWeek.setDate(startOfWeek.getDate() + 6);
      this.currentWeekStartDate = startOfWeek.toLocaleDateString();
      this.currentWeekEndDate = endOfWeek.toLocaleDateString();
    },
  },
};
</script>

<style scoped>
/* Stil für die Kalender-Komponente */
.calendar-container {
  padding: 20px;
  margin-top: 20px;  /* Füge hier einen Abstand oben hinzu, um den Kalender weiter nach oben zu schieben */
}

.week-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.calendar {
  display: flex;
  flex-direction: column;
}

.calendar-week {
  display: flex;  /* Horizontaler Layout für die Woche */
  gap: 10px;
}

.calendar-day {
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.day-header {
  font-size: 14px;
  font-weight: bold;
}

.remove-button {
  background-color: #e57373;
  color: white;
  padding: 8px 12px;
  font-size: 14px;
  border-radius: 6px;
  cursor: pointer;
}

.remove-button:hover {
  background-color: #d32f2f;
}

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
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 300px;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

button {
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
}

button:hover {
  background-color: #ddd;
}
</style>
