<template>
  <meta name="csrf-token" content="{{ csrf_token() }}">
  <div class="calendar-container">
    <h2>Meal Plan Kalender</h2>

    <!-- Navigation für die Woche -->
    <div class="week-navigation">
      <button @click="changeWeek(-1)" class="nav-button">&#8592; Vorherige Woche</button>
      <span class="month-name">{{ formatWeekRange(currentWeekRange) }}</span>
      <button @click="changeWeek(1)" class="nav-button">Nächste Woche &#8594;</button>
    </div>

    <!-- Kalenderansicht für die Woche -->
    <div class="calendar">
      <div v-for="day in displayedWeek" :key="day.date" class="calendar-day" @dragover.prevent @drop="handleDrop(day.date)">
        <div class="day-header">
          <span class="weekday">{{ day.shortWeekday }}</span>
          <span class="date">{{ day.date }}</span>
        </div>
        <div v-if="mealPlans[day.date]" class="meal-plan">
          <!-- MealPlan untereinander -->
          <div class="meal-plans-container">
            <div class="meal-box">
              <p class="meal-field">Frühstück: {{ mealPlans[day.date].breakfastRecipeName || mealPlans[day.date].customBreakfastName }} ({{ mealPlans[day.date].breakfastPortionSize }} Portionen)</p>
            </div>
            <div class="meal-box">
              <p class="meal-field">Mittagessen: {{ mealPlans[day.date].lunchRecipeName || mealPlans[day.date].customLunchName }} ({{ mealPlans[day.date].lunchPortionSize }} Portionen)</p>
            </div>
            <div class="meal-box">
              <p class="meal-field">Abendessen: {{ mealPlans[day.date].dinnerRecipeName || mealPlans[day.date].customDinnerName }} ({{ mealPlans[day.date].dinnerPortionSize }} Portionen)</p>
            </div>
          </div>
          <div class="action-buttons">
            <button @click="openModal(day.date, true)" class="edit-button">Bearbeiten</button>
            <button @click="removeMealPlan(day.date)" class="remove-button">Entfernen</button>
          </div>
        </div>
        <div v-else class="no-meal-plan">
          <p>Kein MealPlan vorhanden</p>
          <button @click="openModal(day.date)" class="add-button">MealPlan hinzufügen</button>
        </div>
      </div>
    </div>

    <!-- Modal zum Hinzufügen oder Bearbeiten eines MealPlans -->
    <div v-if="isModalVisible" class="modal-overlay">
      <div class="modal-content">
        <h3>{{ selectedDate }} MealPlan</h3>
        <div class="search-container">
          <input type="text" v-model="searchQuery" placeholder="Rezepte durchsuchen" class="search-input"/>
        </div>
        <div class="meal-selection">
          <div class="meal-item">
            <label>Frühstück:</label>
            <select v-if="filteredRecipes.length > 0" v-model="mealPlan.breakfastId" class="meal-select">
              <option v-for="recipe in filteredRecipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
              <option value="none">Kein Rezept</option>
            </select>
            <input v-else type="text" v-model="mealPlan.customBreakfastName" placeholder="Frühstückname" class="meal-select" />
            <input type="number" v-model="mealPlan.breakfastPortionSize" min="1" class="portion-input"/>
          </div>

          <div class="meal-item">
            <label>Mittagessen:</label>
            <select v-if="filteredRecipes.length > 0" v-model="mealPlan.lunchId" class="meal-select">
              <option v-for="recipe in filteredRecipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
              <option value="none">Kein Rezept</option>
            </select>
            <input v-else type="text" v-model="mealPlan.customLunchName" placeholder="Mittagessenname" class="meal-select" />
            <input type="number" v-model="mealPlan.lunchPortionSize" min="1" class="portion-input"/>
          </div>

          <div class="meal-item">
            <label>Abendessen:</label>
            <select v-if="filteredRecipes.length > 0" v-model="mealPlan.dinnerId" class="meal-select">
              <option v-for="recipe in filteredRecipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
              <option value="none">Kein Rezept</option>
            </select>
            <input v-else type="text" v-model="mealPlan.customDinnerName" placeholder="Abendessenname" class="meal-select" />
            <input type="number" v-model="mealPlan.dinnerPortionSize" min="1" class="portion-input"/>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="saveMealPlan" class="save-button">Speichern</button>
          <button @click="closeModal" class="cancel-button">Abbrechen</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import axios from 'axios';

export default {
  props: ['recipes'],
  data() {
    return {
      mealPlans: {},
      currentDate: new Date(),
      displayedWeek: [],
      currentWeekRange: '',
      isModalVisible: false,
      selectedDate: '',
      mealPlan: {
        breakfastId: null,
        breakfastPortionSize: 1,
        lunchId: null,
        lunchPortionSize: 1,
        dinnerId: null,
        dinnerPortionSize: 1,
        customBreakfastName: '',
        customLunchName: '',
        customDinnerName: '',
      },
      searchQuery: '',
    };
  },
  computed: {
    filteredRecipes() {
      if (!this.searchQuery) return this.recipes;
      return this.recipes.filter(recipe =>
          recipe.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    },
  },
  mounted() {
    this.fetchMealPlans();
    this.updateWeek();
  },
  methods: {
    fetchMealPlans() {
      this.$axios.get('/mealplans')
          .then(response => {
            const mealPlansData = response.data;
            mealPlansData.forEach(plan => {
              this.mealPlans[plan.date] = {
                breakfastId: plan.breakfastRecipeId,
                breakfastRecipeName: plan.breakfastRecipeName,
                breakfastPortionSize: plan.breakfastPortionSize,
                lunchId: plan.lunchRecipeId,
                lunchRecipeName: plan.lunchRecipeName,
                lunchPortionSize: plan.lunchPortionSize,
                dinnerId: plan.dinnerRecipeId,
                dinnerRecipeName: plan.dinnerRecipeName,
                dinnerPortionSize: plan.dinnerPortionSize,
                customBreakfastName: plan.customBreakfastName,
                customLunchName: plan.customLunchName,
                customDinnerName: plan.customDinnerName,
              };
            });
          })
          .catch(error => console.error('Fehler beim Abrufen der MealPlans:', error));
    },
    saveMealPlan() {
      if (!this.selectedDate) return;

      const mealPlanData = {
        date: this.selectedDate,
        breakfastRecipeId: this.mealPlan.breakfastId === 'none' ? null : this.mealPlan.breakfastId,
        breakfastPortionSize: this.mealPlan.breakfastPortionSize,
        lunchRecipeId: this.mealPlan.lunchId === 'none' ? null : this.mealPlan.lunchId,
        lunchPortionSize: this.mealPlan.lunchPortionSize,
        dinnerRecipeId: this.mealPlan.dinnerId === 'none' ? null : this.mealPlan.dinnerId,
        dinnerPortionSize: this.mealPlan.dinnerPortionSize,
        customBreakfastName: this.mealPlan.customBreakfastName,
        customLunchName: this.mealPlan.customLunchName,
        customDinnerName: this.mealPlan.customDinnerName,
      };

      const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
      axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

      if (this.mealPlans[this.selectedDate]) {
        this.$axios.put(`/mealplans/${this.selectedDate}`, mealPlanData)
            .then(response => {
              this.mealPlans[this.selectedDate] = response.data;
              this.closeModal();
            })
            .catch(error => {
              console.error('Fehler beim Aktualisieren des MealPlans:', error.response);
            });
      } else {
        this.$axios.post('/mealplans', mealPlanData)
            .then(response => {
              this.mealPlans[this.selectedDate] = response.data;
              this.closeModal();
            })
            .catch(error => {
              console.error('Fehler beim Hinzufügen des MealPlans:', error.response);
            });
      }
    },
    removeMealPlan(date) {
      if (!this.mealPlans[date]) return;

      const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
      axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

      this.$axios.delete(`/mealplans/${date}`)
          .then(() => {
            const updatedMealPlans = { ...this.mealPlans };
            delete updatedMealPlans[date];
            this.mealPlans = updatedMealPlans;
          })
          .catch(error => {
            console.error('Fehler beim Entfernen des MealPlans:', error);
          });
    },
    updateWeek() {
      this.displayedWeek = this.getDaysOfWeek(this.getStartOfWeek(this.currentDate));
      this.currentWeekRange = `${this.formatDate(this.displayedWeek[0].date)} - ${this.formatDate(this.displayedWeek[6].date)}`;
    },
    changeWeek(amount) {
      const newDate = new Date(this.currentDate);
      newDate.setDate(this.currentDate.getDate() + amount * 7);
      this.currentDate = newDate;
      this.updateWeek();
    },
    getStartOfWeek(date) {
      const startOfWeek = new Date(date);
      const day = startOfWeek.getDay();
      const diff = startOfWeek.getDate() - day + (day === 0 ? -6 : 1);
      startOfWeek.setDate(diff);
      return startOfWeek;
    },
    getDaysOfWeek(startOfWeek) {
      const days = [];
      for (let i = 0; i < 7; i++) {
        const day = new Date(startOfWeek);
        day.setDate(startOfWeek.getDate() + i);
        days.push({
          date: day.toISOString().split('T')[0],
          shortWeekday: day.toLocaleDateString('de-DE', { weekday: 'short' }),
        });
      }
      return days;
    },
    formatWeekRange(range) {
      return range;
    },
    formatDate(date) {
      const d = new Date(date);
      return `${d.getDate()}.${d.getMonth() + 1}.${d.getFullYear()}`;
    },
    openModal(date, isEdit = false) {
      this.selectedDate = date;
      if (isEdit && this.mealPlans[date]) {
        const currentPlan = this.mealPlans[date];
        this.mealPlan = {
          breakfastId: currentPlan.breakfastId || null,
          breakfastPortionSize: currentPlan.breakfastPortionSize || 1,
          lunchId: currentPlan.lunchId || null,
          lunchPortionSize: currentPlan.lunchPortionSize || 1,
          dinnerId: currentPlan.dinnerId || null,
          dinnerPortionSize: currentPlan.dinnerPortionSize || 1,
          customBreakfastName: currentPlan.customBreakfastName || '',
          customLunchName: currentPlan.customLunchName || '',
          customDinnerName: currentPlan.customDinnerName || '',
        };
      }
      this.isModalVisible = true;
    },
    closeModal() {
      this.isModalVisible = false;
    },
  },
};
</script>

<style scoped>
/* Allgemeine Container-Stile */
.calendar-container {
  width: 100%;
  font-family: Arial, sans-serif;
  padding: 20px;
  box-sizing: border-box;
}

.week-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.nav-button {
  padding: 10px 15px;
  cursor: pointer;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
}

.month-name {
  font-size: 18px;
  font-weight: bold;
}

/* Kalender-Stile */
.calendar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-start;
}

.calendar-day {
  width: calc(100% / 7 - 10px);
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  box-sizing: border-box;
}

.day-header {
  font-size: 16px;
  margin-bottom: 10px;
}

.weekday {
  font-weight: bold;
}

.date {
  font-size: 14px;
  color: #555;
}

/* MealPlan Container */
.meal-plans-container {
  margin-top: 10px;
}

.meal-box {
  background-color: #e0f7fa;
  padding: 10px;
  margin-bottom: 5px;
  border-radius: 8px;
  box-sizing: border-box;
}

.meal-field {
  font-size: 14px;
  margin: 0;
}

.meal-selection {
  margin-top: 10px;
}

.meal-item {
  margin-bottom: 10px;
}

.meal-select,
.portion-input {
  width: 100%;
  padding: 8px;
  margin-top: 5px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
}

.save-button,
.cancel-button {
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.save-button {
  background-color: #4CAF50;
  color: white;
}

.cancel-button {
  background-color: #f44336;
  color: white;
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
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-sizing: border-box;
  max-width: 500px;
  width: 100%;
}

.add-button {
  background-color: #4CAF50;
  color: white;
  padding: 10px;
  cursor: pointer;
  border-radius: 5px;
  border: none;
}

.no-meal-plan {
  font-size: 14px;
  color: #555;
}

.remove-button {
  background-color: #f44336;
  color: white;
  padding: 10px;
  border-radius: 5px;
  border: none;
}

.remove-button:hover {
  background-color: #e53935;
}
.edit-button {
  padding: 8px 15px;
  background-color: #ff9800;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.edit-button:hover {
  background-color: #e68900;
}

.edit-button:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(255, 152, 0, 0.5);
}
.search-input {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.search-input:focus {
  border-color: #4CAF50;
  box-shadow: 0 0 5px rgba(76, 175, 80, 0.3);
  outline: none;
}

.search-input::placeholder {
  color: #888;
  font-style: italic;
}

</style>
