<template>
  <div class="chart-container">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script>
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarController,
  BarElement,
  CategoryScale,
  LinearScale,
} from "chart.js";
import {EventBus} from "@/assets/event-bus.js";

ChartJS.register(
    Title,
    Tooltip,
    Legend,
    BarController,
    BarElement,
    CategoryScale,
    LinearScale
);

export default {
  name: "BarChart",
  props: {
    selectedData: String,
    chartData: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      chartInstance: null,
    };
  },

  mounted() {
        this.destroyChart(); // Zerstöre den alten Chart
        this.initializeChart(this.chartData); // Initialisiere den neuen Chart
        this.setupEventListeners();
  },
  beforeDestroy() {
    EventBus.off("chartUpdated");
  },
  methods: {
    setupEventListeners() {
      EventBus.on("chartUpdated",this.newChart);
    },
    newChart() {
      this.destroyChart(); // Destroy the old chart
      this.initializeChart(this.chartData); // Reinitialize the chart with the current chartData

    },
    initializeChart(data) {
      if (!this.$refs.canvas) {
        console.error("Canvas-Element nicht gefunden!");
        return;
      }

      const ctx = this.$refs.canvas.getContext("2d");

      this.chartInstance = new ChartJS(ctx, {
        type: "bar",
        data: {
          ...data,
          datasets: [
            {
              ...data.datasets[0],
              backgroundColor: "#42A5F5",
              borderColor: "#1E88E5",
              borderWidth: 2,
              hoverBackgroundColor: "#1E88E5",
              hoverBorderColor: "#1565C0",
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: true,
              position: "top",
              labels: {
                font: {
                  family: "'Arial', sans-serif",
                  size: 14,
                },
              },
            },
            title: {
              display: true,
              text: this.selectedData,
              font: {
                family: "'Arial', sans-serif",
                size: 18,
              },
              color: "#333",
            },
            tooltip: {
              backgroundColor: "rgba(0, 0, 0, 0.7)",
              titleFont: {
                family: "'Arial', sans-serif",
                size: 16,
              },
              bodyFont: {
                family: "'Arial', sans-serif",
                size: 14,
              },
            },
          },
          scales: {
            x: {
              ticks: {
                font: {
                  family: "'Arial', sans-serif",
                  size: 14,
                },
              },
            },
            y: {
              ticks: {
                font: {
                  family: "'Arial', sans-serif",
                  size: 14,
                },
              },
            },
          },
        },
      });
    },
    destroyChart() {
      if (this.chartInstance) {
        this.chartInstance.destroy(); // Zerstört die bestehende Chart-Instanz
        this.chartInstance = null;
      }
    },
  },
  beforeUnmount() {
    this.destroyChart(); // Bereinigt die Chart-Instanz beim Entfernen der Komponente
  },
};
</script>

<style scoped>
.chart-container {
  width: 100%;
  max-width: 700px;
  margin: 20px auto;
  padding: 40px 20px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: 'Arial', sans-serif;
  min-height: 400px;
}

canvas {
  width: 100% !important;
  height: auto;
  max-height: 500px;
  border-radius: 8px;
}

h2 {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.legend {
  font-size: 16px;
  color: #555;
  text-align: center;
}

.no-chart-message {
  text-align: center;
  font-style: italic;
  color: #888;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .chart-container {
    padding: 15px;
  }

  h2 {
    font-size: 20px;
  }

  .legend {
    font-size: 14px;
  }
}
</style>
