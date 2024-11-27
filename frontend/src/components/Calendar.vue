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
            <p class="meal-field">Frühstück: {{ mealPlans[day.date].breakfastRecipeName }}</p>
            <p class="meal-field">Mittagessen: {{ mealPlans[day.date].lunchRecipeName }}</p>
            <p class="meal-field">Abendessen: {{ mealPlans[day.date].dinnerRecipeName }}</p>
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
        </div>

        <div>
          <label>Mittagessen:</label>
          <select v-model="mealPlan.lunchId">
            <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
          </select>
        </div>

        <div>
          <label>Abendessen:</label>
          <select v-model="mealPlan.dinnerId">
            <option v-for="recipe in recipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
          </select>
        </div>

        <div class="modal-actions">
          <button @click="saveMealPlan">Speichern</button>
          <button @click="closeModal">Abbrechen</button>
        </div>
      </div>
    </div>

    <!-- Einkaufsliste anzeigen -->
    <ShoppingList :mealPlans="mealPlans" :recipes="recipes" />
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import ShoppingList from './ShoppingList.vue'; // Import der neuen Komponente

export default {
  components: {
    ShoppingList,
  },
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
      lunchId: null,
      dinnerId: null,
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
          lunchRecipeName: data.lunchRecipeName,
          dinnerRecipeName: data.dinnerRecipeName,
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
        days.push({ date: day.toISOString().split('T')[0], shortWeekday: weekday });
      }
      return days;
    }

    function openModal(date) {
      selectedDate.value = date;
      isModalVisible.value = true;
    }

    function closeModal() {
      isModalVisible.value = false;
    }

    function saveMealPlan() {
      mealPlans.value[selectedDate.value] = {
        breakfastRecipeName: mealPlan.breakfastId,
        lunchRecipeName: mealPlan.lunchId,
        dinnerRecipeName: mealPlan.dinnerId,
      };
      closeModal();
    }

    function removeMealPlan(date) {
      delete mealPlans.value[date];
    }

    function changeWeek(direction) {
      const newDate = new Date(currentDate.value);
      newDate.setDate(currentDate.value.getDate() + direction * 7);
      currentDate.value = newDate;
      updateWeekDays();
    }

    return {
      mealPlans,
      currentWeekDays,
      currentWeekRange,
      isModalVisible,
      selectedDate,
      mealPlan,
      openModal,
      closeModal,
      saveMealPlan,
      removeMealPlan,
      changeWeek,
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
