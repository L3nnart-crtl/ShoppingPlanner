<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <div class="header">
        <h4>{{ selectedRecipe.name }}</h4>
        <div class="button-container">
          <button @click="editRecipe" class="modal-button edit-button">Rezept bearbeiten</button> <!-- Button to edit recipe -->
          <button @click="deleteRecipe" class="modal-button delete-button">Rezept löschen</button> <!-- Button to delete recipe -->
          <button @click="toggleFavorite" class="modal-button favorite-button">
            {{ selectedRecipe.favorite ? 'Aus Favoriten entfernen' : 'Als Favorit markieren' }} <!-- Toggle favorite button text -->
          </button>
        </div>
      </div>

      <div class="main-content">
        <div class="content-section">
          <div class="description-section">
            <h5>Beschreibung</h5> <!-- Description title -->
            <p>{{ selectedRecipe.description || 'Keine Beschreibung verfügbar' }}</p> <!-- Description content -->
            <h5>Kochzeit</h5> <!-- Cooking time title -->
            <p>{{ selectedRecipe.cookingTime || 'Keine Kochzeit angegeben' }} Minuten</p> <!-- Cooking time content -->
            <h5>Tags</h5> <!-- Tags title -->
            <div class="tags-container">
              <div v-for="(tag, index) in selectedTags" :key="index" class="tag-box">
                {{ tag.name }}
              </div>
            </div>
          </div>

          <div class="ingredients-section">
            <h5>Zutaten (pro 100g)</h5> <!-- Ingredients per 100g title -->
            <div class="ingredient-list-container">
              <ul class="ingredient-list">
                <li v-for="(ingredient, index) in selectedRecipe.ingredients" :key="index" class="ingredient-item">
                  <span class="ingredient-name">{{ ingredient.name }} <br> {{ ingredient.quantity }} {{ getUnitLabel(ingredient.unit) }}</span>
                  <span class="ingredient-nutrients">
                    Kalorien: {{ ingredient.calories }} kcal<br> <!-- Nutritional info labels -->
                    Protein: {{ ingredient.proteins }} g<br>
                    Fett: {{ ingredient.fats }} g<br>
                    Kohlenhydrate: {{ ingredient.carbohydrates }} g
                  </span>
                </li>
              </ul>
            </div>
          </div>

          <div class="nutrition-section">
            <h5>Gesamt-Nährwerte</h5> <!-- Total nutrition title -->
            <div class="nutrition-content">
              <div class="nutrition-details">
                <div class="nutrition-item">
                  <div class="nutrition-label">Kalorien</div> <!-- Calorie label -->
                  <div class="nutrition-value">{{ selectedRecipe.totalCalories }} kcal</div> <!-- Calorie value -->
                </div>
                <div class="nutrition-item">
                  <div class="nutrition-label">Protein</div> <!-- Protein label -->
                  <div class="nutrition-value">{{ selectedRecipe.totalProteins }} g</div> <!-- Protein value -->
                </div>
                <div class="nutrition-item">
                  <div class="nutrition-label">Fett</div> <!-- Fat label -->
                  <div class="nutrition-value">{{ selectedRecipe.totalFats }} g</div> <!-- Fat value -->
                </div>
                <div class="nutrition-item">
                  <div class="nutrition-label">Kohlenhydrate</div> <!-- Carbohydrates label -->
                  <div class="nutrition-value">{{ selectedRecipe.totalCarbohydrates }} g</div> <!-- Carbohydrates value -->
                </div>
              </div>

              <div class="pie-chart-container">
                <!-- Pie chart will reload whenever chartKey changes -->
                <pie-chart :key="chartKey" :data="nutritionChartData" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <button @click="closeModal" class="modal-button close-button">Schließen</button> <!-- Close button -->
    </div>
  </div>
</template>

<script>
import { quantityUnits } from "@/assets/TagsAndUnits.js";
import { Pie } from "vue-chartjs";
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement, CategoryScale, LinearScale } from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, ArcElement, CategoryScale, LinearScale);

export default {
  components: {
    PieChart: Pie,
  },
  props: {
    selectedRecipe: Object,
    selectedTags: Object,
    isVisible: Boolean,
  },
  data() {
    return {
      chartKey: 0, // Used to force re-render of PieChart
    };
  },
  computed: {
    nutritionChartData() {
      return {
        labels: ['Protein', 'Fett', 'Kohlenhydrate'], // Labels for the nutrition chart
        datasets: [
          {
            data: [
              this.selectedRecipe.proteinPercentage,
              this.selectedRecipe.fatPercentage,
              this.selectedRecipe.carbohydratePercentage,
            ],
            backgroundColor: ['#FF6347', '#4CAF50', '#1E90FF'],
            hoverOffset: 4,
          },
        ],
      };
    },
  },
  methods: {
    toggleFavorite() {
      this.$emit("toggleFavorite");
    },
    getUnitLabel(unit) {
      const unitObj = quantityUnits.find((u) => u.value === unit);
      return unitObj ? unitObj.label : unit;
    },
    closeModal() {
      this.$emit("closeModal");
    },
    editRecipe() {
      this.$emit("openEdit", this.selectedRecipe);
    },
    deleteRecipe() {
      this.$emit("confirmDelete", this.selectedRecipe);
    },
  },
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9996;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 1000px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  align-items: center;
}

.button-container {
  display: flex;
  align-items: center;
  gap: 2px;
}

.modal-button {
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;

  transition: background-color 0.3s ease;
}

.edit-button {
  background-color: #ff9800;
}

.delete-button {
  background-color: #f44336;
}

.favorite-button {
  background-color: #4CAF50;
}

.modal-button:hover {
  opacity: 0.9;
}

h4 {
  margin-bottom: 10px;
  font-size: 1.5em;
  text-align: center;
}

.main-content {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.content-section {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  width: 100%;
  height: 450px;
}

.description-section, .ingredients-section, .nutrition-section {
  flex: 1;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.description-section {
  width: 500px;
  overflow-x: auto;
  word-wrap: break-word;
  white-space: normal;
}

.ingredients-section {
  overflow-y: auto;
  width: 200px;
}

.nutrition-section {
  max-width: 400px;
}

h5 {
  font-size: 1.5em;
  margin-bottom: 15px;
  text-align: center;
  font-weight: bold;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  justify-content: flex-start;
}

.tag-box {
  background-color: #e0e0e0;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 0.8em;
}


.ingredient-list {
  list-style-type: none;
  padding-left: 0;
}

.ingredient-item {
  display: flex;
  justify-content: flex-start;
  padding: 4px 0;
  border-bottom: 1px solid #f1f1f1;
}

.ingredient-name {
  font-weight: bold;
  margin-right: 10px;
  flex: 2;
}

.ingredient-nutrients {
  font-size: 0.9em;
  color: #555;
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 3;
}

.nutrition-content {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.nutrition-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  font-size: 0.9em;
  gap: 2px;
}

.nutrition-item {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 3px;
}

.nutrition-label {
  font-weight: bold;
  font-size: 0.9em;
  flex: 1;
  text-align: left; /* Align labels to the left */
}

.nutrition-value {
  font-size: 0.9em;
  color: #333;
  flex: 1;
  text-align: right; /* Align values to the right */
  padding-left: 10px; /* Added spacing for better separation */
}

.pie-chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
  max-width: 250px;
}

.close-button {
  background-color: #007bff;
  color: white;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  width: 100%;
}

.close-button:hover {
  opacity: 0.9;
}
</style>
