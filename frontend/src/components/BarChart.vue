<template>
  <div>
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

// Registriere die Chart.js-Komponenten
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
  watch: {
    chartData: {
      deep: true,
      handler(newData) {
        if (this.chartInstance) {
          this.destroyChart();
        }
        this.initializeChart(newData);
      },
    },
  },
  mounted() {
    this.initializeChart(this.chartData);
  },
  methods: {
    initializeChart(data) {
      if (!this.$refs.canvas) {
        console.error("Canvas-Element nicht gefunden!");
        return;
      }

      const ctx = this.$refs.canvas.getContext("2d");

      this.chartInstance = new ChartJS(ctx, {
        type: "bar",
        data: data,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: true,
              position: "top",
            },
            title: {
              display: true,
              text: "Statistik-Diagramm",
            },
          },
        },
      });
    },
    destroyChart() {
      if (this.chartInstance) {
        this.chartInstance.destroy();
        this.chartInstance = null;
      }
    },
  },
  beforeUnmount() {
    this.destroyChart();
  },
};
</script>

<style scoped>
canvas {
  max-width: 100%;
  height: 400px;
}
</style>
