<template>
  <div class="container">
    <!-- Logout button -->
    <div class="logout-container">
      <button v-if="isAuthenticated" @click="logout" class="logout-button">Logout</button>
    </div>

    <!-- Rezeptliste -->
    <div>
      <RecipeList :recipes="recipes" @recipe-removed="removeRecipe"  @recipe-added="addRecipe" ref="recipeList" />
    </div>
    <div>
      <CalendarComponent :recipes="recipes" />
      <div class="dashboard-list">
        <StatisticsDashboard />
        <ShoppingList />
      </div>
    </div>
  </div>
</template>


<script>
import AddRecipeForm from "@/components/addRecipe/AddRecipeForm.vue";
import RecipeList from "@/components/recipeList/RecipeList.vue";
import CalendarComponent from "@/components/Calendar.vue";
import ShoppingList from "@/components/ShoppingList.vue";
import StatisticsDashboard from "@/components/StatisticsDashboard.vue";

export default {
  components: {
    AddRecipeForm,
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
    // CSRF-Token aus dem Cookie holen und im Header setzen
    const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
    this.$axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

    await this.checkAuthStatus();
    await this.reloadRecipes();
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
    async addRecipe(newRecipe) {
      this.recipes.push(newRecipe);
      this.updateRecipeList();
    },
    removeRecipe(recipeId) {
      this.recipes = this.recipes.filter((recipe) => recipe.id !== recipeId);
      this.updateRecipeList();
    },
    updateRecipeList() {
      if (this.$refs.recipeList) {
        this.$refs.recipeList.recipesUpdated(this.recipes);
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
        console.error("Logout fehlgeschlagen:", error);
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

}

.component {
  background-color: #f9f9f9;
}
.dashboard-list {
  display: flex;
  max-height: 600px;
  flex-direction: row;
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

@media (max-width: 600px) {
  .component {
    flex: 1 1 100%;
  }
}
</style>
