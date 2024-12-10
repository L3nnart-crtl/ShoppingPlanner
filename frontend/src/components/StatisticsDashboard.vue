<template>
  <div class="statistics-dashboard">
    <h1>Statistiken</h1>
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
    <div v-if="chartData && chartData.labels.length > 0">
      <bar-chart :chart-data="chartData" />
    </div>
    <div v-else>
      <p>Bitte wählen Sie eine Statistik aus, um sie anzuzeigen.</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import BarChart from "./BarChart.vue";
import { tagsForList } from "@/assets/TagsAndUnits.js"; // Importiere das Tag-Mapping

export default {
  components: {
    BarChart,
  },
  data() {
    return {
      statistics: null, // API-Daten
      selectedData: null, // Aktuell ausgewählte Statistik
      chartData: null, // Daten für das Chart
    };
  },
  async created() {
    try {
      const response = await axios.get("/api/statistics");
      this.statistics = response.data;
      this.updateChart(); // Stelle sicher, dass das Diagramm beim ersten Laden angezeigt wird
    } catch (error) {
      console.error("Fehler beim Abrufen der Statistiken:", error);
    }
  },
  watch: {
    selectedData(newSelectedData) {
      this.updateChart(); // Diagramm bei Auswahländerung neu rendern
    },
  },
  methods: {
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

      // Direkte Zuweisung an chartData ohne $set
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
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.dropdown {
  margin-bottom: 20px;
}

#data-select {
  margin-left: 10px;
  padding: 5px;
}

canvas {
  margin-top: 20px;
}
</style>
