<template>
  <div class="recipe-container">
    <h2>Rezeptliste und Suche</h2>

    <!-- Suchfelder -->
    <div class="search-fields">
      <input
          v-model="searchQuery.name"
          type="text"
          placeholder="Rezeptname eingeben"
          class="search-input"
      />
      <multiselect
          v-model="searchQuery.tags"
          :options="availableTags"
          :multiple="true"
          :close-on-select="false"
          placeholder="Tags auswählen"
          label="name"
          track-by="name"
          class="tag-dropdown"
      />
      <div class="cooking-time-filter">
        <label for="cookingtime">Kochzeit (Minuten):</label>
        <input
            v-model="searchQuery.cookingTime"
            type="number"
            placeholder="Kochzeit eingeben"
            class="search-input"
        />
      </div>

      <button @click="toggleFavoriteFilter" class="filter-favorites-button">
        {{ filterFavorites ? 'Alle Rezepte anzeigen' : 'Nur Favoriten anzeigen' }}
      </button>
      <button @click="searchRecipes" class="search-button">Suchen</button>
    </div>

    <div class="recipe-cards-container" v-if="recipes.length">
      <div
          v-for="recipe in recipes"
          :key="recipe.id"
          class="recipe-card"
          @click="openRecipeDetails(recipe)"
      >
        <h3 class="recipe-name">{{ recipe.name || 'Kein Name' }}</h3>
      </div>
    </div>
    <p v-else class="no-recipes">Keine Rezepte gefunden.</p>

    <!-- Recipe Modals -->
    <RecipeDetailsModal
        v-if="isModalVisible"
        :selectedRecipe="selectedRecipe"
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
        :availableTags="availableTags"
        :selectedTags="selectedTags"
        @closeEditModal="closeEditModal"
        @submitEditRecipe="submitEditRecipe"
        @update:selectedTags="selectedTags = $event"
    />
  </div>
</template>

<script>
import axios from "axios";
import RecipeDetailsModal from "@/components/recipeList/RecipeDetailsModal.vue";
import DeleteConfirmationModal from "@/components/recipeList/DeleteConfirmationModal.vue";
import EditRecipeModal from "@/components/recipeList/EditRecipeModal.vue";
import Multiselect from 'vue-multiselect'; // Importing the Multiselect component
import {quantityUnits, tagMapping, tags} from "@/assets/TagsAndUnits.js"; // Import your helper data

export default {
  components: {
    EditRecipeModal,
    DeleteConfirmationModal,
    RecipeDetailsModal,
    Multiselect  // Registering the Multiselect component
  },
  data() {
    return {
      searchQuery: {
        name: "",
        tags: [],
        cookingTime: "",
      },
      recipes: [],
      availableTags: tags, // Make sure this is populated with available tags
      isModalVisible: false,
      isDeleteModalVisible: false,
      isEditModalVisible: false,
      selectedRecipe: null,
      filterFavorites: false,
      selectedTags: []
    };
  },
  mounted() {
    this.searchRecipes(); // Call to fetch recipes initially
  },
  methods: {
    recipesUpdated(updatedRecipes) {
      this.recipes = updatedRecipes;
    },

    toggleFavoriteFilter() {
      this.filterFavorites = !this.filterFavorites;
      this.searchRecipes(); // Fetch recipes with the updated filter
    },

    async searchRecipes() {
      try {
        const params = {};

        // Add search filters if they exist
        if (this.searchQuery.name) {
          params.name = this.searchQuery.name;
        }
        if (this.searchQuery.tags.length > 0) {
          params.tags = this.searchQuery.tags.map(tag => tag.value).join(",");
        }
        if (this.searchQuery.cookingTime) {
          params.cookingTime = this.searchQuery.cookingTime;
        }
        if (this.filterFavorites !== null) {
          params.favorite = this.filterFavorites;
        }

        // CSRF token for security
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
        axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        const response = await this.$axios.get("/recipes/search", { params });
        this.recipes = response.data;
      } catch (error) {
        console.error("Fehler bei der Rezeptsuche:", error);
      }
    },

    openRecipeDetails(recipe) {
      this.selectedRecipe = { ...recipe, favorite: recipe.favorite || false };
      this.isModalVisible = true;
    },

    closeModal() {
      this.isModalVisible = false;
    },

    confirmDeleteRecipe(recipe) {
      this.selectedRecipe = recipe;
      this.isDeleteModalVisible = true;
    },

    closeDeleteModal() {
      this.isDeleteModalVisible = false;
    },

    closeEditModal() {
      this.isEditModalVisible = false;
    },

    async deleteRecipe() {
      if (this.selectedRecipe) {
        try {
          const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
          axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

          await this.$axios.delete(`/recipes/${this.selectedRecipe.id}`);
          this.closeDeleteModal();
          this.closeModal();
          this.recipes = this.recipes.filter(r => r.id !== this.selectedRecipe.id);
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

    translateTags(tags) {
      return tags.map(tag => tagMapping[tag] || tag);
    },

    openEditRecipe(recipe) {
      this.selectedRecipe = JSON.parse(JSON.stringify(recipe));
      this.selectedTags = recipe.tags.map(tag => ({ name: tag }));
      this.isEditModalVisible = true;
    },

    async submitEditRecipe() {
      try {
        const updatedRecipe = {
          ...this.selectedRecipe,
          tags: this.translateTags(this.selectedTags.map(tag => tag.name))
        };

        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
        axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        await this.$axios.put(`/recipes/${this.selectedRecipe.id}`, updatedRecipe);
        this.isEditModalVisible = false;
        this.recipes = this.recipes.map(recipe =>
            recipe.id === updatedRecipe.id ? updatedRecipe : recipe
        );
      } catch (error) {
        console.error("Fehler beim Bearbeiten des Rezepts:", error);
      }
    },

    addIngredient() {
      if (!this.selectedRecipe.ingredients) {
        this.selectedRecipe.ingredients = [];
      }
      this.selectedRecipe.ingredients.push({ name: "", quantity: "", unit: "" });
    },

    removeIngredient(index) {
      this.selectedRecipe.ingredients.splice(index, 1);
    },

    async toggleFavorite() {
      try {
        this.selectedRecipe.favorite = !this.selectedRecipe.favorite;
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
        axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        await this.$axios.put(`/recipes/${this.selectedRecipe.id}/favorite`);

        console.log('Favorite status updated successfully');
        this.recipes = this.recipes.map(recipe =>
            recipe.id === this.selectedRecipe.id ? { ...recipe, favorite: this.selectedRecipe.favorite } : recipe
        );
      } catch (error) {
        this.selectedRecipe.favorite = !this.selectedRecipe.favorite;
        console.error("Error updating favorite status:", error);
      }
    },

    getTagLabel(tag) {
      return tagMapping[tag] || tag;
    },

    getUnitLabel(unit) {
      const unitObj = quantityUnits.find(u => u.value === unit);
      return unitObj ? unitObj.label : unit;
    }
  }
};
</script>

<style scoped>
.recipe-container {
  display: flex;
  font-family: Arial, sans-serif;
  flex-direction: column;
  padding: 20px;
  height: 650px;
  width: 400px;
  background-color: #f9f9f9;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
  background-color: #f9f9f9;
  border-radius: 8px;
}

h2 {
  font-size: 1.75rem;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
}

.search-fields {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  justify-content: center;
}

.search-input, .search-button, .tags-filter, .cooking-time-filter {
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 1rem;
  font-family: 'Arial', sans-serif;
}

.search-input {
  width: 250px;
}

.search-button {
  background-color: #007BFF;
  color: white;
  cursor: pointer;
  width: 150px;
  text-align: center;
  transition: background-color 0.3s ease;
}

.search-button:hover {
  background-color: #0056b3;
}

.tags-filter, .cooking-time-filter {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  width: 400px;
  box-sizing: border-box;
}

.tags-filter label, .cooking-time-filter label {
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

.tag-dropdown, .cooking-time-filter select {
  width: 100%;
  padding: 10px;
  border-radius: 8px;
  background-color: #fafafa;
  border: 1px solid #ccc;
  box-sizing: border-box;
}

.cooking-time-filter input {
  width: 100%;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc;
  background-color: #fafafa;
  box-sizing: border-box;
}

.tags-filter, .cooking-time-filter {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
}

.recipe-cards-container {
  overflow-y: auto;
  max-height: 450px;
}

.recipe-card {
  background-color: #fff;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #ddd;
  margin-bottom: 10px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.recipe-card:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.recipe-name {
  font-size: 1.25rem;
  font-weight: bold;
}

.no-recipes {
  font-size: 1.25rem;
  color: #777;
  text-align: center;
  margin-top: 20px;
}

.filter-favorites-button {
  background-color: #28a745;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 10px;
}

.filter-favorites-button:hover {
  background-color: #218838;
}
</style>
