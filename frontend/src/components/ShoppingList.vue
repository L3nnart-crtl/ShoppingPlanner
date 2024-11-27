<template>
  <div class="shopping-list-container">
    <h2>Einkaufsliste</h2>

    <!-- Anzahl der Portionen -->
    <div class="portion-scale">
      <label for="portion-count">Anzahl der Portionen:</label>
      <input
          v-model.number="portionCount"
          type="number"
          min="1"
          placeholder="Anzahl der Portionen"
      />
    </div>

    <!-- Einkaufsliste -->
    <div v-if="shoppingList.length">
      <h3>Zu kaufende Zutaten:</h3>
      <ul>
        <li
            v-for="(ingredient, index) in shoppingList"
            :key="index"
            class="ingredient-item"
        >
          {{ ingredient.name }}: {{ ingredient.scaledQuantity }} {{ ingredient.unit }}
        </li>
      </ul>
    </div>
    <div v-else>
      <p>Keine Zutaten zum Einkaufen gefunden.</p>
    </div>

    <button @click="downloadShoppingList">Einkaufsliste herunterladen</button>
  </div>
</template>

<script>
export default {
  props: {
    mealPlans: Array,
    recipes: Array,
  },
  data() {
    return {
      portionCount: 1,
      shoppingList: [],
    };
  },
  computed: {
    // Berechnen der Zutaten für die gesamte Woche
    aggregatedIngredients() {
      const ingredients = {};

      this.mealPlans.forEach((mealPlan) => {
        ['breakfast', 'lunch', 'dinner'].forEach((mealType) => {
          const recipe = this.recipes.find(
              (recipe) => recipe.id === mealPlan[`${mealType}Id`]
          );

          if (recipe) {
            recipe.ingredients.forEach((ingredient) => {
              if (ingredients[ingredient.name]) {
                ingredients[ingredient.name].quantity += ingredient.quantity;
              } else {
                ingredients[ingredient.name] = { ...ingredient };
              }
            });
          }
        });
      });

      return ingredients;
    },
    // Zutaten nach Skalierung berechnen
    scaledIngredients() {
      return Object.values(this.aggregatedIngredients).map((ingredient) => {
        const scaledQuantity = ingredient.quantity * this.portionCount;
        return {
          ...ingredient,
          scaledQuantity,
        };
      });
    },
  },
  watch: {
    scaledIngredients(newVal) {
      this.shoppingList = newVal;
    },
  },
  methods: {
    downloadShoppingList() {
      const listContent = this.shoppingList
          .map(
              (ingredient) =>
                  `${ingredient.name}: ${ingredient.scaledQuantity} ${ingredient.unit}`
          )
          .join('\n');

      const blob = new Blob([listContent], { type: 'text/plain' });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'einkaufsliste.txt';
      link.click();
    },
  },
};
</script>

<style scoped>
.shopping-list-container {
  padding: 1rem;
  border: 1px solid #ccc;
  margin-top: 2rem;
}
.portion-scale {
  margin-bottom: 1rem;
}
</style>
