<template>
  <div class="calendar-container">
    <h2>Kalender</h2>
    <div class="calendar">
      <div v-for="day in daysInMonth" :key="day.date" class="calendar-day">
        <div>{{ day.date }}</div>
        <div v-if="mealPlans[day.date]">
          <p>Frühstück: {{ mealPlans[day.date].breakfastRecipe.name }}</p>
          <p>Mittagessen: {{ mealPlans[day.date].lunchRecipe.name }}</p>
          <p>Abendessen: {{ mealPlans[day.date].dinnerRecipe.name }}</p>
          <button @click="removeMealPlan(day.date)" class="remove-button">Entfernen</button>
        </div>
        <div v-else>
          <p>Kein MealPlan vorhanden</p>
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
      daysInMonth: [],
    };
  },
  async created() {
    this.loadCalendar();
  },
  methods: {
    async loadCalendar() {
      const currentDate = new Date();
      const currentMonth = currentDate.getMonth();
      const currentYear = currentDate.getFullYear();
      const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();

      this.daysInMonth = [];
      for (let i = 1; i <= daysInMonth; i++) {
        this.daysInMonth.push({
          date: `${currentYear}-${(currentMonth + 1).toString().padStart(2, '0')}-${i.toString().padStart(2, '0')}`,
        });
      }

      this.loadMealPlansForMonth();
    },

    async loadMealPlansForMonth() {
      for (let day of this.daysInMonth) {
        try {
          const response = await this.$axios.get(`/api/mealplans/${day.date}`);
          if (response.data) {
            this.$set(this.mealPlans, day.date, response.data);
          }
        } catch (error) {
          console.error('Fehler beim Laden des MealPlans für den Tag:', error);
        }
      }
    },

    async removeMealPlan(date) {
      try {
        await this.$axios.delete(`/api/mealplans/${date}`);
        this.$delete(this.mealPlans, date);
      } catch (error) {
        console.error('Fehler beim Entfernen des MealPlans:', error);
      }
    },
  },
};
</script>