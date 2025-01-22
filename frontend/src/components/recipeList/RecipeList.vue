<template>
  <div class="recipe-container">
    <h2>Rezeptliste und Suche</h2>
    <!-- Add Recipe Button -->
    <button @click="openAddRecipeModal" class="add-recipe-button">Rezept hinzufügen</button>

    <!-- Recipe Modal -->
    <AddRecipeForm
        v-if="isAddModalVisible"
        :isAddModalVisible="isAddModalVisible"
        :recipes="recipes"
        @closeAddModal="closeAddModal"
        @recipe-added="emitRecipeAdd"
        @update:recipes="recipes = $event"
    />
    <!-- Search fields and filters -->
    <div class="search-fields">
      <div class="input-group">
        <input v-model="searchQuery.name" type="text" placeholder="Rezeptname eingeben" class="search-input" />
        <Multiselect
            ref="multiselect"
            v-model="searchQuery.tags"
            :options="availableTags"
            :multiple="true"
            :close-on-select="false"
            placeholder="Tags auswählen"
            label="name"
            track-by="name"
            class="tag-dropdown"
        />
      </div>

      <div class="input-group">
        <div class="cooking-time-filter">
          <label for="cookingtime">Kochzeit (Minuten):</label>
          <input v-model="searchQuery.cookingTime" type="number" placeholder="Kochzeit eingeben" class="search-input" />
        </div>
      </div>

      <div class="buttons">
        <button @click="toggleFavoriteFilter" class="filter-favorites-button">
          {{ filterFavorites ? 'Alle Rezepte anzeigen' : 'Nur Favoriten anzeigen' }}
        </button>
        <button @click="searchRecipes" class="search-button">Suchen</button>
      </div>
    </div>

    <!-- Recipe List -->
    <div class="recipe-cards-container" v-if="recipes.length">
      <div v-for="recipe in recipes" :key="recipe.id" class="recipe-card" @click="openRecipeDetails(recipe)">
        <h3 class="recipe-name">{{ recipe.name || 'Kein Name' }}</h3>
      </div>
    </div>
    <p v-else class="no-recipes">Keine Rezepte gefunden.</p>

    <!-- Recipe Modals -->
    <RecipeDetailsModal v-if="isModalVisible" :selectedRecipe="selectedRecipe" :selectedTags="selectedTags" :isVisible="isModalVisible" @closeModal="closeModal" @openEdit="openEditRecipe" @confirmDelete="confirmDeleteRecipe" @toggleFavorite="toggleFavorite" />
    <DeleteConfirmationModal v-if="isDeleteModalVisible" :selectedRecipe="selectedRecipe" @closeDeleteModal="closeDeleteModal" @deleteRecipe="deleteRecipe" />
    <EditRecipeModal v-if="isEditModalVisible" :selectedRecipe="selectedRecipe" :selectedTags="selectedTags" @closeEditModal="closeEditModal" @submitEditRecipe="submitEditRecipe" @update:selectedTags="selectedTags = $event" />
  </div>
</template>


<style scoped>
.recipe-container {
  font-family: 'Roboto', sans-serif;
  color: #333;
  max-width: 700px;
  margin: 0 auto;
  padding: 20px;
  background: linear-gradient(to bottom right, #ffffff, #f7f7f7);
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  font-size: 24px;
  margin-bottom: 20px;
  text-align: center;
  color: #444;
  font-weight: bold;
}

/* Add Recipe Button */
.add-recipe-button {
  display: inline-block;
  margin-left: 100px;
  padding: 12px 25px;
  background: linear-gradient(145deg, #6c63ff, #3c3d99);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.add-recipe-button:hover {
  background: linear-gradient(145deg, #5a52e0, #2c2d7a);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

.add-recipe-button:focus {
  outline: none;
}

.search-fields {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.input-group {
  display: flex;
  gap: 10px;
}

.search-input, .tag-dropdown {
  padding: 10px 15px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.cooking-time-filter {
  flex-grow: 1;
}

.buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.filter-favorites-button, .search-button {
  padding: 10px 15px;
  font-size: 14px;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.filter-favorites-button {
  background: #007bff;
}

.filter-favorites-button:hover {
  background: #0056b3;
}

.search-button {
  background: #28a745;
}

.search-button:hover {
  background: #218838;
}

.recipe-cards-container {
  max-height: 400px;
  overflow-y: auto;
  padding: 15px;
  background: #ffffff;
  border: 1px solid #ddd;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.recipe-card {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  margin-bottom: 10px;
  background: linear-gradient(to right, #f9f9f9, #ffffff);
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.recipe-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.no-recipes {
  text-align: center;
  font-size: 16px;
  color: #888;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
}
</style>




<script>
import axios from "axios";
import RecipeDetailsModal from "@/components/recipeList/RecipeDetailsModal.vue";
import DeleteConfirmationModal from "@/components/recipeList/DeleteConfirmationModal.vue";
import EditRecipeModal from "@/components/recipeList/EditRecipeModal.vue";
import Multiselect from 'vue-multiselect'; // Vergewissere dich, dass dieser Import korrekt ist
import {quantityUnits,tagsForList} from "@/assets/TagsAndUnits.js";
import {EventBus} from "@/assets/event-bus.js";
import AddRecipeForm from "@/components/addRecipe/AddRecipeForm.vue"; // Import your helper data

export default {
  components: {
    AddRecipeForm,
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
      availableTags: tagsForList, // Make sure this is populated with available tags
      isModalVisible: false,
      isAddModalVisible: false,
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

    //Search and Filter
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
    toggleFavoriteFilter() {
      this.filterFavorites = !this.filterFavorites;
      this.searchRecipes(); // Fetch recipes with the updated filter
    },
    //Add and delete
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
        this.recipes = this.recipes.map(recipe =>
            recipe.id === updatedRecipe.id ? updatedRecipe : recipe
        );
        EventBus.emit('recipeUpdated'); // Emit event to notify that a recipe has been updated
      } catch (error) {
        console.error("Fehler beim Bearbeiten des Rezepts:", error);
      }
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
    async deleteRecipe() {
      if (this.selectedRecipe) {
        try {
          const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
          axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

          await this.$axios.delete(`/recipes/${this.selectedRecipe.id}`);
          this.closeDeleteModal();
          this.closeModal();
          this.recipes = this.recipes.filter(r => r.id !== this.selectedRecipe.id);
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
    openAddRecipeModal() {
      this.isAddModalVisible = true;
    },
    //Modal functions
    openRecipeDetails(recipe) {
      this.selectedRecipe = { ...recipe, favorite: recipe.favorite || false };
      this.selectedTags = this.translateTags(recipe.tags);
      console.log(this.selectedTags);
      this.isModalVisible = true;
    },
    openEditRecipe(recipe) {
      this.selectedRecipe = JSON.parse(JSON.stringify(recipe));
      this.isEditModalVisible = true;
    },
    confirmDeleteRecipe(recipe) {
      this.selectedRecipe = recipe;
      this.isDeleteModalVisible = true;
    },
    closeModal() {
      this.isModalVisible = false;
    },
    closeAddModal() {
      this.isAddModalVisible = false;
    },
    closeDeleteModal() {
      this.isDeleteModalVisible = false;
    },
    closeEditModal() {
      this.isEditModalVisible = false;
    },
    recipesUpdated(updatedRecipes) {
      this.recipes = updatedRecipes;
    },
    emitRecipeAdd() {
      this.recipes.push()
    },
    //Helper functions
    translateTags(tags) {
      return tags.map(tag => {
      const tagEntry = tagsForList.find(entry => entry.value === tag);
      return tagEntry ? { name: tagEntry.name, value: tagEntry.value } : { name: tag, value: tag }; // Falls der Tag nicht gefunden wird, wird der Enum-Wert selbst zurückgegeben
      });
    },
  }
};
</script>

