<template>
  <div class="statistics-dashboard">
    <h1>Statistiken</h1>

    <!-- Dropdown zur Auswahl der Statistik -->
    <div class="dropdown">
      <label for="data-select">Wählen Sie eine Statistik:</label>
      <select id="data-select" v-model="selectedData" @change="handleOptionChange">
        <option v-for="option in filteredOptions" :key="option" :value="option.value">
          {{ option.label }}
        </option>
      </select>
    </div>

    <!-- Datumsauswahl -->
    <div class="date-selection">
      <label for="start-date">Startdatum:</label>
      <input type="date" id="start-date" v-model="startDate" />

      <label for="end-date">Enddatum:</label>
      <input type="date" id="end-date" v-model="endDate" />
      <button @click="fetchStatistics()">Statistiken aktualisieren</button>
      <!-- Button für den gesamten Zeitraum -->
      <button @click="setDefaultDateRange">Zeitraum für die letzten 7 Tage</button>
    </div>

    <!-- Anzeige des Balkendiagramms -->
    <div v-if="newSelectedData && chartData?.labels?.length > 0" class="chart-container">
      <bar-chart :chart-data="chartData" :selectedData="newSelectedData"/>
    </div>

    <!-- Meldung, wenn keine Chart-Daten vorhanden sind -->
    <div v-else-if="chartData === null || chartData.labels.length === 0">
      <p class="no-chart-message">Keine Daten verfügbar. Bitte wählen Sie eine Statistik aus.</p>
    </div>

    <!-- Fallback, wenn keine Daten für das Diagramm vorhanden sind -->
    <div v-else>
      <p class="no-chart-message">Bitte wählen Sie eine Statistik aus, um sie anzuzeigen.</p>
    </div>
  </div>
</template>


<script>
import BarChart from "./BarChart.vue";
import {tagsForList} from "@/assets/TagsAndUnits.js";
import {EventBus} from "@/assets/event-bus.js"; // Import EventBus

export default {
  components: {
    BarChart,
  },
  data() {
    return {
      statistics: null,
      selectedData: null,
      newSelectedData: null,
      chartData: null,
      startDate: "", // Start date for the date range
      endDate: "",   // End date for the date range
      allOptions: [
        { value: "AmountOfCookedRecipes", label: "Gekochte Rezepte" },
        { value: "AveragePortions", label: "Durchschnittliche Portionen" },
        { value: "AverageCookingTime", label: "Durchschnittliche Kochzeit" },
        { value: "Tags", label: "Tags" },
        { value: "FavouriteRecipes", label: "Beliebteste Rezepte" },
        { value: "FavouriteIngredients", label: "Beliebteste Zutaten" },
        { value: "AverageCaloriesPerRecipe", label: "Durchschnittliche Kalorien pro Rezept" },
        { value: "AverageCaloriesPerDay", label: "Durchschnittliche Kalorien pro Tag" },
        { value: "AverageNutrientDistributionPerRecipe", label: "Durchschnittliche Nährstoffverteilung pro Rezept" },
        { value: "AverageNutrientDistributionPerDay", label: "Durchschnittliche Nährstoffverteilung pro Tag" }
      ],
      filteredOptions: [], // Temporäre Liste für die angezeigten Optionen
    };
  },
  mounted() {
    this.setupEventListeners();
    this.fetchStatistics();
    this.filteredOptions = this.allOptions;
  },
  beforeDestroy() {
    EventBus.off('mealPlanUpdated', this.fetchStatistics);
    EventBus.off('recipeUpdated', this.fetchStatistics);
  },

  methods: {
    setupEventListeners() {
      EventBus.on('mealPlanUpdated', this.fetchStatistics);
      EventBus.on('recipeUpdated', this.fetchStatistics);
    },

    async fetchStatistics() {
      try {
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
        this.$axios.defaults.headers.common["X-XSRF-TOKEN"] = csrfToken;

        const response = await this.$axios.get("/statistics", {
          params: {
            startDate: this.startDate,
            endDate: this.endDate,
          }
        });
        this.statistics = response.data;
        this.updateChart(); // Update chart after fetching statistics
      } catch (error) {
        console.error("Fehler beim Abrufen der Statistiken:", error);
      }
    },

    async updateChart() {
      this.newSelectedData = this.selectedData;

      let labels = [];
      let data = [];
      if (
          this.newSelectedData === "AmountOfCookedRecipes" &&
          this.statistics.amountOfCookedRecipes != null
      ) {
        labels = ["Gekochte Rezepte"];
        data = [this.statistics.amountOfCookedRecipes];
      } else if (
          this.newSelectedData === "AveragePortions" &&
          this.statistics.averagePortions != null
      ) {
        labels = ["Durchschnittliche Portionen"];
        data = [this.statistics.averagePortions];
      } else if (
          this.newSelectedData === "AverageCookingTime" &&
          this.statistics.averageCookingTime != null
      ) {
        labels = ["Durchschnittliche Kochzeit"];
        data = [this.statistics.averageCookingTime];
      } else if (
          this.newSelectedData === "Tags" &&
          this.statistics &&
          this.statistics.attributes
      ) {
        const attributesArray = Array.isArray(this.statistics.attributes)
            ? this.statistics.attributes
            : Object.entries(this.statistics.attributes);

        labels = attributesArray.map(
            ([tag]) => this.translateTags([tag])[0]
        );
        data = attributesArray.map(([, value]) => value);
      } else if (
          this.newSelectedData === "FavouriteRecipes" &&
          this.statistics.favouriteRecipes
      ) {
        labels = Object.keys(this.statistics.favouriteRecipes);
        data = Object.values(this.statistics.favouriteRecipes);
      } else if (
          this.newSelectedData === "FavouriteIngredients" &&
          this.statistics.favouriteIngredients
      ) {
        labels = Object.keys(this.statistics.favouriteIngredients);
        data = Object.values(this.statistics.favouriteIngredients);
      } else if (
          this.newSelectedData === "AverageCaloriesPerRecipe" &&
          this.statistics.averageCaloriesPerRecipe
      ) {
        labels = ["Durchschnittliche Kalorien pro Rezept"];
        data = [this.statistics.averageCaloriesPerRecipe];
      } else if (
          this.newSelectedData === "AverageCaloriesPerDay" &&
          this.statistics.averageCaloriesPerDay
      ) {
        labels = ["Durchschnittliche Kalorien pro Tag"];
        data = [this.statistics.averageCaloriesPerDay];
      } else if (
          this.newSelectedData === "AverageNutrientDistributionPerRecipe" &&
          this.statistics.averageNutrientDistributionPerRecipe != null
      ) {
        labels = ["Protein", "Fett", "Kohlenhydrate"];
        data = [
          this.statistics.averageNutrientDistributionPerRecipe.protein,
          this.statistics.averageNutrientDistributionPerRecipe.fat,
          this.statistics.averageNutrientDistributionPerRecipe.carbohydrate,
        ];
      } else if (
          this.newSelectedData === "AverageNutrientDistributionPerDay" &&
          this.statistics.averageNutrientDistributionPerDay != null
      ) {
        labels = ["Protein", "Fett", "Kohlenhydrate"];
        data = [
          this.statistics.averageNutrientDistributionPerDay.protein,
          this.statistics.averageNutrientDistributionPerDay.fat,
          this.statistics.averageNutrientDistributionPerDay.carbohydrate,
        ];
      }

      // Ensure chartData is properly set
      if (labels.length === 0 || data.length === 0 || data[0] === 0) {
        this.chartData = null;
      } else {
        this.chartData = {
          labels,
          datasets: [
            {
              label: this.newSelectedData,
              data,
              backgroundColor: "#42A5F5",
            },
          ],
        };
      }
    },

    translateTags(tags) {
      return tags.map((tag) => {
        const tagEntry = tagsForList.find((entry) => entry.value === tag);
        return tagEntry ? tagEntry.name : tag;
      });
    },

    handleOptionChange() {
      // Temporär leere Optionen
      this.filteredOptions = [];
      this.newSelectedData = null;

      // Nach 700 ms wieder alle Optionen anzeigen
      setTimeout(() => {
        this.filteredOptions = this.allOptions;
      }, 900);
      this.updateChart();
      // Aktualisiere das Chart
      EventBus.emit("chartUpdated");
    },

    setDefaultDateRange() {
      const today = new Date();
      const start = new Date();
      start.setDate(today.getDate() - 7); // Set start date to 7 days ago

      this.startDate = start.toISOString().split("T")[0]; // Format as YYYY-MM-DD
      this.endDate = today.toISOString().split("T")[0]; // Format as YYYY-MM-DD

      // Call fetchStatistics to update the chart with the default range
      this.fetchStatistics();
    },
  },
};
</script>

<style scoped>
.statistics-dashboard {
  width: 500px;
  height: 650px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  font-family: Arial, sans-serif;
}

h1 {
  text-align: center;
  font-size: 26px;
  color: #333;
}

.dropdown {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  font-size: 14px;
  color: #333;
}

select {
  width: 100%;
  padding: 10px;
  font-size: 14px;
  border-radius: 5px;
  border: 1px solid #ddd;
  background-color: #fff;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

select:focus {
  border-color: #4caf50;
  box-shadow: 0 0 5px rgba(76, 175, 80, 0.3);
}

.chart-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.ingredients-list {
  margin-top: 20px;
  max-height: 300px;
  overflow-y: auto;
}

.ingredients-list ul {
  list-style-type: none;
  padding: 0;
}

.ingredients-list li {
  margin: 5px 0;
  font-size: 14px;
  color: #333;
}

.no-chart-message {
  text-align: center;
  font-style: italic;
  color: #888;
  margin-top: 20px;
}

@media (max-width: 600px) {
  .statistics-dashboard {
    padding: 15px;
  }

  h1 {
    font-size: 22px;
  }

  select {
    padding: 8px;
    font-size: 12px;
  }
}
</style>
