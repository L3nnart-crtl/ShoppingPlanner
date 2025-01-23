<template>
  <meta name="csrf-token" content="{{ csrf_token() }}">
  <div class="calendar-container">

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
            <div class="meal-box" @click="openRecipeDetailsModal(mealPlans[day.date].breakfastRecipe)">
              <h3 class="meal-title">Frühstück</h3>
              <p class="meal-field">{{ mealPlans[day.date].breakfastRecipeName || mealPlans[day.date].customBreakfastName }} {{ mealPlans[day.date].breakfastPortionSize }}x</p>
            </div>
            <div class="meal-box" @click="openRecipeDetailsModal(mealPlans[day.date].lunchRecipe)">
              <h3 class="meal-title">Mittagessen</h3>
              <p class="meal-field">{{ mealPlans[day.date].lunchRecipeName || mealPlans[day.date].customLunchName }} {{ mealPlans[day.date].lunchPortionSize }}x</p>
            </div>
            <div class="meal-box" @click="openRecipeDetailsModal(mealPlans[day.date].dinnerRecipe)">
              <h3 class="meal-title">Abendessen</h3>
              <p class="meal-field">{{ mealPlans[day.date].dinnerRecipeName || mealPlans[day.date].customDinnerName }} {{ mealPlans[day.date].dinnerPortionSize }}x</p>
            </div>
          </div>
          <div class="action-buttons">
            <button @click="openMealPlanEditModal(day.date, true)" class="edit-button">Bearbeiten</button>
            <button @click="removeMealPlan(day.date)" class="remove-button">Entfernen</button>
          </div>
        </div>
        <div v-else class="no-meal-plan">
          <p>Kein MealPlan vorhanden</p>
          <button @click="openMealPlanEditModal(day.date)" class="add-button">MealPlan hinzufügen</button>
        </div>
      </div>
    </div>

    <!-- Modal zum Hinzufügen oder Bearbeiten eines MealPlans -->
    <div v-if="isMealPlanModalVisible" class="modal-overlay">
      <div class="modal-content">
        <h3>{{ selectedDate }} MealPlan</h3>
        <div class="search-container">
          <input type="text" v-model="searchQuery" placeholder="Rezepte durchsuchen" class="search-input"/>
        </div>
        <div class="meal-selection">
          <div class="meal-item">
            <label>Frühstück:</label>
            <select v-model="mealPlan.breakfastId" class="meal-select">
              <option v-for="recipe in filteredRecipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
              <option value="none">Kein Rezept</option>
            </select>
            <input type="number" v-model="mealPlan.breakfastPortionSize" min="1" class="portion-input"/>
          </div>

          <div class="meal-item">
            <label>Mittagessen:</label>
            <select v-model="mealPlan.lunchId" class="meal-select">
              <option v-for="recipe in filteredRecipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
              <option value="none">Kein Rezept</option>
            </select>
            <input type="number" v-model="mealPlan.lunchPortionSize" min="1" class="portion-input"/>
          </div>

          <div class="meal-item">
            <label>Abendessen:</label>
            <select v-model="mealPlan.dinnerId" class="meal-select">
              <option v-for="recipe in filteredRecipes" :key="recipe.id" :value="recipe.id">{{ recipe.name }}</option>
              <option value="none">Kein Rezept</option>
            </select>
            <input type="number" v-model="mealPlan.dinnerPortionSize" min="1" class="portion-input"/>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="saveMealPlan" class="save-button">Speichern</button>
          <button @click="closeMealPlanModal" class="cancel-button">Abbrechen</button>
        </div>
      </div>
    </div>

    <!-- Recipe Modals -->
    <RecipeDetailsModal
        v-if="isModalVisible"
        :selectedRecipe="selectedRecipe"
        :selectedTags="selectedTags"
        :isVisible="isModalVisible"
        @closeModal="closeModal"
        @openEdit="openEditRecipe"
        @confirmDelete="confirmDeleteRecipe"
        @toggleFavorite="toggleFavorite"
    />
    <DeleteConfirmationModal
        v-if="isDeleteModalVisible"
        :selectedRecipe="selectedRecipe"
        @closeDeleteModal="closeDeleteModal"
        @deleteRecipe="deleteRecipe"
    />
    <EditRecipeModal
        v-if="isEditModalVisible"
        :selectedRecipe="selectedRecipe"
        :selectedTags="selectedTags"
        @closeEditModal="closeEditModal"
        @submitEditRecipe="submitEditRecipe"
        @update:selectedTags="selectedTags = $event"
    />
  </div>
</template>
<script>
import axios from 'axios';
import { EventBus } from '@/assets/event-bus.js';
import DeleteConfirmationModal from "@/components/recipeList/DeleteConfirmationModal.vue";
import EditRecipeModal from "@/components/recipeList/EditRecipeModal.vue";
import RecipeDetailsModal from "@/components/recipeList/RecipeDetailsModal.vue";
import {tagsForList} from "@/assets/TagsAndUnits.js"; // import the event bus
export default {
  components: {RecipeDetailsModal, EditRecipeModal, DeleteConfirmationModal},
  props: {
    recipes: Array,
  },
  data() {
    return {
      mealPlans: {},
      currentDate: new Date(),
      displayedWeek: [],
      currentWeekRange: '',


      //Modals
      isModalVisible: false,
      isDeleteModalVisible: false,
      isEditModalVisible: false,
      isMealPlanModalVisible: false,
      selectedRecipe: null,
      filterFavorites: false,
      selectedTags: [],

      selectedDate: '',
      mealPlan: {
        breakfastId: null,
        breakfastPortionSize: 1,
        lunchId: null,
        lunchPortionSize: 1,
        dinnerId: null,
        dinnerPortionSize: 1,
        dinnerRecipe: null,
        lunchRecipe: null,
        breakfastRecipe: null,
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
    EventBus.on('recipeUpdated', this.fetchMealPlans); // Listen for recipe updates
  },
  beforeDestroy() {
    EventBus.off('recipeUpdated', this.fetchMealPlans); // Clean up the event listener
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
                dinnerRecipe: plan.dinnerRecipe,
                lunchRecipe: plan.lunchRecipe,
                breakfastRecipe: plan.breakfastRecipe
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
      };

      const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
      axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

      if (this.mealPlans[this.selectedDate]) {
        this.$axios.put(`/mealplans/${this.selectedDate}`, mealPlanData)
            .then(response => {
              this.mealPlans[this.selectedDate] = response.data;
              this.closeMealPlanModal();
              EventBus.emit('mealPlanUpdated'); // emit the event after saving
            })
            .catch(error => {
              console.error('Fehler beim Aktualisieren des MealPlans:', error.response);
            });
      } else {
        this.$axios.post('/mealplans', mealPlanData)
            .then(response => {
              this.mealPlans[this.selectedDate] = response.data;
              this.closeMealPlanModal();
              EventBus.emit('mealPlanUpdated'); // emit the event after adding
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
      EventBus.emit('mealPlanUpdated');
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
    openMealPlanEditModal(date, isEdit = false) {
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
        };
      }
      this.isMealPlanModalVisible = true;
    },
    //Modal functions
    openRecipeDetailsModal(recipe) {
      if (recipe == null) {
        // Show a notification or message if recipe is null
        alert("Recipe not found. Please select a valid recipe.");  // Example alert, you can customize this
        return;  // Exit the function if recipe is null
      }

      this.selectedRecipe = { ...recipe, favorite: recipe.favorite || false };
      this.selectedTags = this.translateTags(recipe.tags);
      console.log(this.selectedTags);
      this.isModalVisible = true;
    },
    translateTags(tags) {
      return tags.map(tag => {
        const tagEntry = tagsForList.find(entry => entry.value === tag);
        return tagEntry ? { name: tagEntry.name, value: tagEntry.value } : { name: tag, value: tag }; // Falls der Tag nicht gefunden wird, wird der Enum-Wert selbst zurückgegeben
      });
    },
    closeModal() {
      this.isModalVisible = false;
    },
    closeMealPlanModal() {
      this.mealPlan = {
        breakfastId: null,
            breakfastPortionSize: 1,
            lunchId: null,
            lunchPortionSize: 1,
            dinnerId: null,
            dinnerPortionSize: 1,
            dinnerRecipe: null,
            lunchRecipe: null,
            breakfastRecipe: null,
      };
      this.isMealPlanModalVisible = false;
    },
    openEditRecipe(recipe) {
      this.selectedRecipe = JSON.parse(JSON.stringify(recipe));
      this.isEditModalVisible = true;
    },
    confirmDeleteRecipe(recipe) {
      this.selectedRecipe = recipe;
      this.isDeleteModalVisible = true;
    },
    async toggleFavorite() {
      try {
        this.selectedRecipe.favorite = !this.selectedRecipe.favorite;
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
        axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        await this.$axios.put(`/recipes/${this.selectedRecipe.id}/favorite`);

        console.log('Favorite status updated successfully');
      } catch (error) {
        this.selectedRecipe.favorite = !this.selectedRecipe.favorite;
        console.error("Error updating favorite status:", error);
      }
    },
    closeDeleteModal() {
      this.isDeleteModalVisible = false;
    },
    async deleteRecipe() {
      if (this.selectedRecipe) {
        try {
          const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
          axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

          await this.$axios.delete(`/recipes/${this.selectedRecipe.id}`);
          this.closeDeleteModal();
          this.closeModal();
          EventBus.emit('recipeUpdated');
        } catch (error) {
          console.error("Fehler beim Löschen des Rezepts:", error);
          if (error.response && error.response.status === 409) {
            alert(`Rezept mit ID ${this.selectedRecipe.id} kann nicht gelöscht werden, da es noch einem MealPlan zugeordnet ist.`);
          } else {
            alert("Beim Löschen des Rezepts ist ein Fehler aufgetreten.");
          }
        }
      }
    },
    closeEditModal() {
      this.isEditModalVisible = false;
    },
    async submitEditRecipe() {
      try {
        const updatedRecipe = {
          ...this.selectedRecipe,
          tags: this.selectedTags.map(tag => tag.value),
        };

        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
        axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        await this.$axios.put(`/recipes/${this.selectedRecipe.id}`, updatedRecipe);
        this.isEditModalVisible = false;
        EventBus.emit('recipeUpdated'); // Emit event to notify that a recipe has been updated
      } catch (error) {
        console.error("Fehler beim Bearbeiten des Rezepts:", error);
      }
    },
  },
};
</script>

<style scoped>
/* General Styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;

}

body {
  font-family: Arial, sans-serif;
  background-color: #f4f4f4;
  font-family: Arial, sans-serif;
  background-color: #f4f4f4;

  transform: scale(0.2); /* Scale everything down by 80% */
  transform-origin: top left; /* Ensure scaling starts from the top-left */
}
/* Calendar Styles */
.calendar-container {
  max-width: 1350px;
  margin-bottom: 20px;
  margin-left: 25px;
  height: 380px;
  padding: 20px;
  background: linear-gradient(to bottom right, #ffffff, #f7f7f7);
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

/* Week Navigation */
.week-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.nav-button {
  background-color: #4CAF50; /* Green */
  color: white;
  padding: 10px 20px;
  border: none;
  cursor: pointer;
  font-size: 16px;
  border-radius: 5px;
}

.nav-button:hover {
  background-color: #45a049;
}

.month-name {
  font-size: 18px;
  font-weight: bold;
}

/* Calendar Days */
.calendar {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10px;
}

.calendar-day {
  background-color: white;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.day-header {
  text-align: center;
  margin-bottom: 10px;
}

.weekday {
  font-size: 14px;
  font-weight: bold;
}

.date {
  font-size: 16px;
}

/* Meal Plan Section */
.meal-plan {
  margin-top: 10px;
}

.meal-plans-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meal-box {
  background-color: #e0f7fa; /* Light cyan */
  padding: 10px;
  height: 60px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.meal-box:hover {
  background-color: #b2ebf2; /* Lighter cyan */
}

.meal-title {
  font-size: 16px;
  font-weight: bold;
}

.meal-field {
  font-size: 14px;
  color: #666;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}
.edit-button {
  padding: 6px 6px;
  background-color: #ff9800;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 12px;
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
.remove-button {
  background-color: #f44336;
  color: white;
  padding: 6px 6px;
  border-radius: 5px;
  border: none;
}

.remove-button:hover {
  background-color: #e53935;
}
.add-button {
  background-color: #4CAF50; /* Green */
  color: white;
  padding: 10px 20px;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  font-size: 14px;
}

.add-button:hover {
  background-color: #45a049;
}

.no-meal-plan {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.no-meal-plan button {
  background-color: #4CAF50; /* Green */
  color: white;
  padding: 10px 20px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  border-radius: 5px;
}

.no-meal-plan button:hover {
  background-color: #45a049;
}

/* Modal Styles */
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
  z-index: 9999;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-sizing: border-box;
  max-width: 500px;
  width: 100%;
}

.modal-content h3 {
  font-size: 20px;
  margin-bottom: 15px;
}

.search-container {
  margin-bottom: 15px;
}

.search-input {
  width: 100%;
  padding: 8px;
  margin-bottom: 15px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.meal-selection {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meal-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meal-select {
  width: 100%;
  padding: 8px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.portion-input {
  width: 50px;
  padding: 5px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.save-button,
.cancel-button {
  padding: 10px 20px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  border-radius: 5px;
}

.save-button {
  background-color: #4CAF50; /* Green */
  color: white;
}

.cancel-button {
  background-color: #f44336; /* Red */
  color: white;
}

.save-button:hover {
  background-color: #45a049;
}

.cancel-button:hover {
  background-color: #e53935;
}

/* Recipe Modal Styles */
.recipe-modal {
  padding: 20px;
}

.recipe-modal .recipe-details {
  margin-top: 20px;
}

.recipe-modal .recipe-details h4 {
  font-size: 18px;
  margin-bottom: 10px;
}

.recipe-modal .recipe-details p {
  font-size: 14px;
  margin-bottom: 10px;
}

.recipe-modal .action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.recipe-modal .action-buttons button {
  background-color: #4CAF50; /* Green */
  color: white;
  padding: 10px 20px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  border-radius: 5px;
}

.recipe-modal .action-buttons button:hover {
  background-color: #45a049;
}

/* Add this to your CSS */


/* For finer control, you can also scale other elements individually */

h2, .week-navigation, .calendar-day, .meal-box, .meal-select {
  font-size: 0.8rem;  /* Reduce font size */
}

.nav-button {
  padding: 8px 16px;  /* Adjust button padding */
}

.calendar-day {
  padding: 8px; /* Adjust calendar day padding */
}

.meal-plans-container .meal-box {
  padding: 6px; /* Adjust meal box padding */
}

.search-input, .portion-input {
  font-size: 0.8rem; /* Adjust input sizes */
}

</style>
