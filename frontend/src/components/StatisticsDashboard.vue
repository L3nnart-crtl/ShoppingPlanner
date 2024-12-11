<template>
  <div class="statistics-dashboard">
    <h1>Statistiken</h1>

    <!-- Dropdown zur Auswahl der Statistik -->
    <div class="dropdown">
      <label for="data-select">Wählen Sie eine Statistik:</label>
      <select id="data-select" v-model="selectedData" @change="updateChart">
        <option value="amountOfCookedRecipes">Gekochte Rezepte</option>
        <option value="averagePortions">Durchschnittliche Portionen</option>
        <option value="averageCookingTime">Durchschnittliche Kochzeit</option>
        <option value="attributes">Attribute</option>
        <option value="favouriteRecipes">Beliebteste Rezepte</option>
        <option value="favouriteIngredients">Beliebteste Zutaten</option>
      </select>
    </div>

    <div v-if="selectedData === 'favouriteIngredients'" class="ingredients-list">
      <h3>Beliebteste Zutaten</h3>
      <ul>
        <li v-for="(count, ingredient) in statistics.favouriteIngredients" :key="ingredient">
          {{ ingredient }}: {{ count }}
        </li>
      </ul>
    </div>

    <div v-else>
      <div v-if="chartData && chartData.labels.length > 0" class="chart-container">
        <bar-chart :chart-data="chartData" />
      </div>

      <div v-else>
        <p class="no-chart-message">Bitte wählen Sie eine Statistik aus, um sie anzuzeigen.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import BarChart from "./BarChart.vue";
import { tagsForList } from "@/assets/TagsAndUnits.js";

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
  async created() {
    try {
      const response = await this.$axios.get("/statistics");
      this.statistics = response.data;
      this.updateChart();
    } catch (error) {
      console.error("Fehler beim Abrufen der Statistiken:", error);
    }
  },
  watch: {
    selectedData(newSelectedData) {
      this.updateChart();
    },
  },
  methods: {
    async fetchStatistics() {
      try {
        const response = await this.$axios.get("/statistics");
        this.statistics = response.data;
      } catch (error) {
        console.error("Fehler beim Abrufen der Statistiken:", error);
      }
    },
    translateTags(tags) {
      return tags.map(tag => {
        const tagEntry = tagsForList.find(entry => entry.value === tag);
        return tagEntry ? tagEntry.name : tag;
      });
    },

    updateChart() {

      if (!this.selectedData || !this.statistics) return;

      let labels = [];
      let data = [];

      if (this.selectedData === "amountOfCookedRecipes") {
        labels = ["Gekochte Rezepte"];
        data = [this.statistics.amountOfCookedRecipes];
      } else if (this.selectedData === "averagePortions") {
        labels = ["Durchschnittliche Portionen"];
        data = [this.statistics.averagePortions];
      } else if (this.selectedData === "averageCookingTime") {
        labels = ["Durchschnittliche Kochzeit"];
        data = [this.statistics.averageCookingTime];
      } else if (this.selectedData === "attributes") {
        const attributesArray = Array.isArray(this.statistics.attributes)
            ? this.statistics.attributes
            : Object.entries(this.statistics.attributes);

        labels = attributesArray.map(([tag, value]) => {
          const translatedTag = this.translateTags([tag])[0];
          return translatedTag;
        });

        data = attributesArray.map(([tag, value]) => value);
      } else if (this.selectedData === "favouriteRecipes") {
        labels = Object.keys(this.statistics.favouriteRecipes);
        data = Object.values(this.statistics.favouriteRecipes);
      } else if (this.selectedData === "favouriteIngredients") {
        labels = Object.keys(this.statistics.favouriteIngredients);
        data = Object.values(this.statistics.favouriteIngredients);
      }

      this.chartData = {
        labels,
        datasets: [
          {
            label: "Anzahl",
            backgroundColor: "#42A5F5",
            borderColor: "#1E88E5",
            borderWidth: 1,
            data,
          },
        ],
      };
    }
  },
};
</script>

<style scoped>
.statistics-dashboard {
  width: 600px;
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
