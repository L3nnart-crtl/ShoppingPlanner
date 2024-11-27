<template>
  <div class="calendar-container">
    <h2>Meal Plan Kalender</h2>

    <div class="week-navigation">
      <button @click="changeWeek(-1)">&#8592; Vorherige Woche</button>
      <span>{{ currentWeekRange }}</span>
      <button @click="changeWeek(1)">Nächste Woche &#8594;</button>
    </div>

    <div class="calendar">
      <div class="calendar-week">
        <div v-for="day in currentWeekDays" :key="day.date" class="calendar-day">
          <div class="day-header">
            <span>{{ day.shortWeekday }}</span>
            <span>{{ day.date }}</span>
          </div>
          <div v-if="mealPlans[day.date]">
            <p class="meal-field">Frühstück: {{ mealPlans[day.date].breakfastRecipeName }} ({{ mealPlans[day.date].breakfastPortionSize }} Portionen)</p>
            <p class="meal-field">Mittagessen: {{ mealPlans[day.date].lunchRecipeName }} ({{ mealPlans[day.date].lunchPortionSize }} Portionen)</p>
            <p class="meal-field">Abendessen: {{ mealPlans[day.date].dinnerRecipeName }} ({{ mealPlans[day.date].dinnerPortionSize }} Portionen)</p>
            <button @click="removeMealPlan(day.date)" class="remove-button">Entfernen</button>
          </div>
          <div v-else>
            <p>Kein MealPlan vorhanden</p>
            <button @click="openModal(day.date)" class="remove-button">MealPlan hinzufügen</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal zum Hinzufügen eines MealPlans -->
    <div v-if="isModalVisible" class="modal-overlay">
      <div class="modal-content">
        <h3>MealPlan für {{ selectedDate }}</h3>
        <div>
          <label>Frühstück:</label>
          <select v-model="mealPlan.breakfastId">
            <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
          </select>
          <label>Portionen:</label>
          <input type="number" v-model="mealPlan.breakfastPortionSize" min="1" />
        </div>

        <div>
          <label>Mittagessen:</label>
          <select v-model="mealPlan.lunchId">
            <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
          </select>
          <label>Portionen:</label>
          <input type="number" v-model="mealPlan.lunchPortionSize" min="1" />
        </div>

        <div>
          <label>Abendessen:</label>
          <select v-model="mealPlan.dinnerId">
            <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
          </select>
          <label>Portionen:</label>
          <input type="number" v-model="mealPlan.dinnerPortionSize" min="1" />
        </div>

        <div class="modal-actions">
          <button @click="saveMealPlan">Speichern</button>
          <button @click="closeModal">Abbrechen</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';

export default {
  props: ['recipes'],
  setup() {
    const mealPlans = ref({});
    const currentDate = ref(new Date());
    const currentWeekDays = ref([]);
    const currentWeekRange = ref('');
    const isModalVisible = ref(false);
    const selectedDate = ref('');
    const mealPlan = reactive({
      breakfastId: null,
      breakfastPortionSize: 1,  // Default auf 1 Portion gesetzt
      lunchId: null,
      lunchPortionSize: 1,       // Default auf 1 Portion gesetzt
      dinnerId: null,
      dinnerPortionSize: 1,      // Default auf 1 Portion gesetzt
    });

    onMounted(() => {
      updateWeekDays();
    });

    async function loadMealPlanByDate(date) {
      try {
        const response = await fetch(`/api/mealplans/${date}`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();

        mealPlans.value[date] = {
          breakfastRecipeName: data.breakfastRecipeName,
          breakfastPortionSize: data.breakfastPortionSize,
          lunchRecipeName: data.lunchRecipeName,
          lunchPortionSize: data.lunchPortionSize,
          dinnerRecipeName: data.dinnerRecipeName,
          dinnerPortionSize: data.dinnerPortionSize,
        };
      } catch (error) {
        console.error(`Fehler beim Abrufen des MealPlans für ${date}:`, error);
      }
    }

    function updateWeekDays() {
      const startOfWeek = getStartOfWeek(currentDate.value);
      currentWeekDays.value = getDaysOfWeek(startOfWeek);
      currentWeekRange.value = `${startOfWeek.toLocaleDateString()} - ${getEndOfWeek(startOfWeek).toLocaleDateString()}`;
      currentWeekDays.value.forEach(day => loadMealPlanByDate(day.date));
    }

    function getStartOfWeek(date) {
      const startOfWeek = new Date(date);
      const day = startOfWeek.getDay();
      const diff = startOfWeek.getDate() - day + (day === 0 ? -6 : 1);
      startOfWeek.setDate(diff);
      startOfWeek.setHours(0, 0, 0, 0);
      return startOfWeek;
    }

    function getEndOfWeek(startOfWeek) {
      const endOfWeek = new Date(startOfWeek);
      endOfWeek.setDate(startOfWeek.getDate() + 6);
      return endOfWeek;
    }

    function getDaysOfWeek(startOfWeek) {
      const days = [];
      for (let i = 0; i < 7; i++) {
        const day = new Date(startOfWeek);
        day.setDate(startOfWeek.getDate() + i);
        const weekday = day.toLocaleDateString('de-DE', { weekday: 'short' });
        days.push({
          date: day.toISOString().split('T')[0],
          shortWeekday: weekday,
        });
      }
      return days;
    }

    function changeWeek(direction) {
      currentDate.value.setDate(currentDate.value.getDate() + direction * 7);
      updateWeekDays();
    }

    function openModal(date) {
      selectedDate.value = date;
      isModalVisible.value = true;
    }

    function closeModal() {
      isModalVisible.value = false;
      selectedDate.value = '';
      mealPlan.breakfastId = null;
      mealPlan.breakfastPortionSize = 1;
      mealPlan.lunchId = null;
      mealPlan.lunchPortionSize = 1;
      mealPlan.dinnerId = null;
      mealPlan.dinnerPortionSize = 1;
    }

    async function saveMealPlan() {
      try {
        const response = await fetch('/api/mealplans', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            date: selectedDate.value,
            breakfastRecipeId: mealPlan.breakfastId,
            breakfastPortionSize: mealPlan.breakfastPortionSize,
            lunchRecipeId: mealPlan.lunchId,
            lunchPortionSize: mealPlan.lunchPortionSize,
            dinnerRecipeId: mealPlan.dinnerId,
            dinnerPortionSize: mealPlan.dinnerPortionSize,
          }),
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        const newMealPlan = {
          breakfastRecipeName: data.breakfastRecipeName,
          breakfastPortionSize: data.breakfastPortionSize,
          lunchRecipeName: data.lunchRecipeName,
          lunchPortionSize: data.lunchPortionSize,
          dinnerRecipeName: data.dinnerRecipeName,
          dinnerPortionSize: data.dinnerPortionSize,
        };
        mealPlans.value[selectedDate.value] = newMealPlan;
        closeModal();
      } catch (error) {
        console.error('Fehler beim Speichern des MealPlans:', error);
      }
    }

    async function removeMealPlan(date) {
      try {
        const response = await fetch(`/api/mealplans/${date}`, {
          method: 'DELETE',
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        delete mealPlans.value[date];
      } catch (error) {
        console.error('Fehler beim Entfernen des MealPlans:', error);
      }
    }

    return {
      mealPlans,
      currentWeekDays,
      currentWeekRange,
      isModalVisible,
      selectedDate,
      mealPlan,
      changeWeek,
      openModal,
      closeModal,
      saveMealPlan,
      removeMealPlan,
    };
  },
};
</script>
<style scoped>
/* Styling for the calendar component */
.calendar-container {
  padding: 20px;
  margin-top: 20px;
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
  display: flex; /* Horizontal layout for the week */
  gap: 10px;
}

.calendar-day {
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  text-align: center;
  width: 120px; /* Fixed width for each day cell */
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* Ensures that content is spaced out and buttons are at the bottom */
}

.day-header {
  font-size: 14px;
  font-weight: bold;
}

.meal-field {
  font-size: 14px;
  word-wrap: break-word; /* Text kann in mehrere Zeilen umbrechen, falls nötig */
  white-space: normal; /* Verhindert, dass der Text in einer einzigen Zeile bleibt */
  line-height: 1.5; /* Bessere Lesbarkeit durch einen angenehmen Zeilenabstand */
  margin-bottom: 10px; /* Abstand nach unten für bessere Übersicht */
  padding: 5px; /* Etwas Innenabstand für bessere Sichtbarkeit */
  background-color: #f8f8f8; /* Optional: Hellt den Hintergrund etwas auf */
  border-radius: 5px; /* Runde Ecken für die Felder */
}

.remove-button {
  margin-top: auto;
  padding: 8px;
  background-color: #d9534f;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}

.remove-button:hover {
  background-color: #c9302c;
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
  width: 400px;
  text-align: center;
}

.modal-actions {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}
</style>
