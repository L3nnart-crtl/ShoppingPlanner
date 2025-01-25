<template>
  <div class="container">
    <!-- Logout button -->
    <div class="logout-container">
      <button v-if="isAuthenticated" @click="logout" class="logout-button">Abmelden</button>
    </div>

    <!-- Recipe List -->
    <div>
      <RecipeList :recipes="recipes" ref="recipeList" />
    </div>
    <div>
      <CalendarComponent :recipes="recipes"/>
      <div class="dashboard-list">
        <StatisticsDashboard />
        <ShoppingList />
      </div>
    </div>
  </div>
</template>

<script>
import RecipeList from "@/components/recipeList/RecipeList.vue";
import CalendarComponent from "@/components/mealPlans/Calendar.vue";
import ShoppingList from "@/components/shoppingList/ShoppingList.vue";
import StatisticsDashboard from "@/components/statistics/StatisticsDashboard.vue";
import {EventBus} from "@/assets/event-bus.js";

export default {
  components: {
    RecipeList,
    CalendarComponent,
    ShoppingList,
    StatisticsDashboard,
  },
  data() {
    return {
      recipes: [],
      isAuthenticated: false, // Track authentication status
    };
  },
  async created() {
    // Get CSRF token from the cookie and set it in the header
    const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
    this.$axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

    await this.checkAuthStatus();
    await this.reloadRecipes();
  },
  mounted() {
    this.reloadRecipes();

    EventBus.on('recipeUpdated', this.reloadRecipes); // Listen for recipe updates
  },
  beforeDestroy() {
    EventBus.off('recipeUpdated', this.reloadRecipes); // Clean up the event listener
  },
  methods: {
    async reloadRecipes() {
      try {
        const response = await this.$axios.get("/recipes");
        this.recipes = response.data;
      } catch (error) {
        console.error("Fehler beim Abrufen der Rezepte:", error);
      }
    },
    async checkAuthStatus() {
      try {
        const response = await this.$axios.get("/auth/status");
        this.isAuthenticated = response.status === 200;
      } catch {
        this.isAuthenticated = false;
      }
    },
    async logout() {
      try {
        await this.$axios.post("/auth/logout");
        this.isAuthenticated = false;
        document.cookie = "XSRF-TOKEN=; Max-Age=0"; // Clear CSRF token
        this.$router.push("/auth");
      } catch (error) {
        console.error("Abmeldung fehlgeschlagen:", error);
      }
    },
  },
};
</script>

<style scoped>
.container {
  display: flex;
  flex-wrap: wrap;
  gap: 1px;
  font-family: Arial, sans-serif;
  padding: 2px;
  margin-top: -5px;
}

.dashboard-list {
  display: flex;
  max-height: 600px;
  flex-direction: row;
  gap: 10px;
}

.logout-container {
  position: fixed;
  top: 20px;
  right: 20px;
}

.logout-button {
  padding: 10px 20px;
  font-size: 1rem;
  color: white;
  background-color: #007BFF;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease;
}

.logout-button:hover {
  background-color: #0056b3;
}
</style>
