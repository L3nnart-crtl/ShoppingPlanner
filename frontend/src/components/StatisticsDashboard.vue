<template>
  <div class="statistics-dashboard">
    <h1>Statistiken</h1>

    <!-- Dropdown zur Auswahl der Statistik -->
    <div class="dropdown">
      <label for="data-select">Wählen Sie eine Statistik:</label>
      <select id="data-select" v-model="selectedData" @change="updateChart">
        <option value="AmountOfCookedRecipes">Gekochte Rezepte</option>
        <option value="AveragePortions">Durchschnittliche Portionen</option>
        <option value="AverageCookingTime">Durchschnittliche Kochzeit</option>
        <option value="Tags">Tags</option>
        <option value="FavouriteRecipes">Beliebteste Rezepte</option>
        <option value="FavouriteIngredients">Beliebteste Zutaten</option>
      </select>
    </div>

    <!-- Anzeige des Balkendiagramms -->
    <div v-if="chartData && chartData.labels.length > 0" class="chart-container">
      <bar-chart :chart-data="chartData" :selectedData="selectedData"/>
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
import { tagsForList } from "@/assets/TagsAndUnits.js";
import { EventBus } from "@/assets/event-bus.js"; // Import EventBus

export default {
  components: {
    BarChart,
  },
  data() {
    return {
      statistics: null,
      selectedData: null,
      chartData: null,
    };
  },

  // Listen for the event as soon as the component is mounted
  mounted() {
    this.fetchStatistics();
    this.setupEventListeners();
  },

  beforeDestroy() {
    // Clean up the event listener when the component is destroyed
    EventBus.off('mealPlanUpdated', this.fetchStatistics);
  },

  methods: {
    setupEventListeners() {
      // Add listener for the 'mealPlanUpdated' event
      EventBus.on('mealPlanUpdated', this.fetchStatistics);
    },

    async fetchStatistics() {
      try {
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
        this.$axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        const response = await this.$axios.get("/statistics");
        this.statistics = response.data;
        this.updateChart(); // Re-render the chart with the updated data
      } catch (error) {
        console.error("Fehler beim Abrufen der Statistiken:", error);
      }
    },

    async updateChart() {

      let labels = [];
      let data = [];

      if (this.selectedData === "AmountOfCookedRecipes" && this.statistics.amountOfCookedRecipes != null) {
        labels = ["Gekochte Rezepte"];
        data = [this.statistics.amountOfCookedRecipes];
      } else if (this.selectedData === "AveragePortions" && this.statistics.averagePortions != null) {
        labels = ["Durchschnittliche Portionen"];
        data = [this.statistics.averagePortions];
      } else if (this.selectedData === "AverageCookingTime" && this.statistics.averageCookingTime != null) {
        labels = ["Durchschnittliche Kochzeit"];
        data = [this.statistics.averageCookingTime];
      } else if (this.selectedData === "Tags" && this.statistics.attributes) {
        const attributesArray = Array.isArray(this.statistics.attributes)
            ? this.statistics.attributes
            : Object.entries(this.statistics.attributes);

        labels = attributesArray.map(([tag, value]) => {
          const translatedTag = this.translateTags([tag])[0];
          return translatedTag;
        });

        data = attributesArray.map(([tag, value]) => value);
      } else if (this.selectedData === "FavouriteRecipes" && this.statistics.favouriteRecipes) {
        // Umwandlung von Lieblingsrezepten in Labels und Daten
        labels = Object.keys(this.statistics.favouriteRecipes);
        data = Object.values(this.statistics.favouriteRecipes);
      } else if (this.selectedData === "FavouriteIngredients" && this.statistics.favouriteIngredients) {
        // Umwandlung von Lieblingszutaten in Labels und Daten
        labels = Object.keys(this.statistics.favouriteIngredients);
        data = Object.values(this.statistics.favouriteIngredients);
      }

      if (labels.length === 0 || data.length === 0 || data[0] === 0 ) {
        this.chartData = null;  // Setze chartData auf null, falls keine Daten vorhanden sind
      } else {
        this.chartData = {
          labels,
          datasets: [
            {
              label: this.selectedData,
              data,
              backgroundColor: "#42A5F5",
            },
          ],
        };
      }
    },

    translateTags(tags) {
      return tags.map(tag => {
        const tagEntry = tagsForList.find(entry => entry.value === tag);
        return tagEntry ? tagEntry.name : tag;
      });
    },
  },
};
</script>

<style scoped>
.statistics-dashboard {
  width: 500px;
  height:650px;
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
