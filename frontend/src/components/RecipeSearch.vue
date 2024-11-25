<template>
  <div class="recipe-search-container">
    <h2>Rezept Suche</h2>

    <!-- Suchfelder -->
    <div class="search-fields">
      <input
          v-model="searchQuery.name"
          type="text"
          placeholder="Rezeptname eingeben"
          class="search-input"
      />

      <div class="tags-filter">
        <label for="tags">Tags:</label>
        <select v-model="searchQuery.tags" multiple>
          <option v-for="tag in availableTags" :key="tag" :value="tag">
            {{ getTagLabel(tag) }}
          </option>
        </select>
      </div>

      <button @click="searchRecipes" class="search-button">Suchen</button>
    </div>

    <!-- Anzeige der Suchergebnisse -->
    <div v-if="recipes.length" class="recipe-list">
      <div v-for="recipe in recipes" :key="recipe.id" class="recipe-card">
        <h3>{{ recipe.name }}</h3>
        <p>{{ recipe.description || 'Keine Beschreibung verfügbar' }}</p>
        <div class="tags">
          <span v-for="tag in recipe.tags" :key="tag" class="tag">{{ getTagLabel(tag) }}</span>
        </div>
      </div>
    </div>

    <p v-else v-if="recipes.length === 0" class="no-results">Keine Rezepte gefunden.</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchQuery: {
        name: '',
        tags: [],
      },
      recipes: [],
      availableTags: [
        "VEGETARIAN",
        "VEGAN",
        "GLUTEN_FREE",
        "LACTOSE_FREE",
        "KETO",
        "PALEO",
        "LOW_CARB",
        "HIGH_PROTEIN",
        "DIABETIC_FRIENDLY",
        "LOW_FAT",
        "HALAL",
        "KOSHER",
        "EXCLUSION_DIET",
        "QUICK",
        "MEAL_PREP",
        "KIDS_MEAL",
        "SWEET",
        "SAVORY",
        "SPICY",
        "MILD",
        "EXOTIC",
        "SEASONAL",
        "GRILL",
        "BREAKFAST",
        "LUNCH",
        "DINNER",
        "SNACKS",
        "SOUPS",
        "SALADS",
        "SUGAR_FREE",
        "FISH",
        "MEAT",
        "NUTS",
        "WHOLEGRAIN",
        "LEGUMES",
        "PASTA",
        "RICE",
        "BUDGET_FRIENDLY",
        "FOR_2_PEOPLE",
        "FAMILY_FRIENDLY",
        "FOR_BEGGINERS",
        "GOURMET",
        "STORAGE",
        "DESSERT",
        "QUICK_PREP",
        "SLOW_COOK",
        "ONE_POT",
        "FRYING",
        "BAKING",
        "LOW_CALORIE",
        "ANTI_AGEING",
        "DETOX",
        "HEART_HEALTH",
      ], // Vordefinierte Tags
    };
  },
  methods: {
    // Holen der deutschen Bezeichnung des Tags
    getTagLabel(tag) {
      const tagLabels = {
        VEGETARIAN: "Vegetarisch",
        VEGAN: "Vegan",
        GLUTEN_FREE: "Glutenfrei",
        LACTOSE_FREE: "Laktosefrei",
        KETO: "Keto",
        PALEO: "Paleo",
        LOW_CARB: "Low Carb",
        HIGH_PROTEIN: "High Protein",
        DIABETIC_FRIENDLY: "Diabetikerfreundlich",
        LOW_FAT: "Low Fat",
        HALAL: "Halal",
        KOSHER: "Koscher",
        EXCLUSION_DIET: "Ausschlussdiät",
        QUICK: "Schnell",
        MEAL_PREP: "Meal Prep",
        KIDS_MEAL: "Mahlzeit für Kinder",
        SWEET: "Süß",
        SAVORY: "Herzhaft",
        SPICY: "Scharf",
        MILD: "Mild",
        EXOTIC: "Exotisch",
        SEASONAL: "Saisonale Rezepte",
        GRILL: "Grillrezepte",
        BREAKFAST: "Frühstück",
        LUNCH: "Mittagessen",
        DINNER: "Abendessen",
        SNACKS: "Snacks",
        SOUPS: "Suppen",
        SALADS: "Salate",
        SUGAR_FREE: "Zuckerfrei",
        FISH: "Fisch",
        MEAT: "Fleisch",
        NUTS: "Nüsse",
        WHOLEGRAIN: "Vollkorn",
        LEGUMES: "Hülsenfrüchte",
        PASTA: "Nudeln",
        RICE: "Reis",
        BUDGET_FRIENDLY: "Budgetfreundlich",
        FOR_2_PEOPLE: "Für 2 Personen",
        FAMILY_FRIENDLY: "Familientauglich",
        FOR_BEGGINERS: "Für Anfänger",
        GOURMET: "Gourmet",
        STORAGE: "Lagerung",
        DESSERT: "Nachtisch",
        QUICK_PREP: "Schnell",
        SLOW_COOK: "Langsame Zubereitung",
        ONE_POT: "Ein-Pfannen-Gerichte",
        FRYING: "Frittieren",
        BAKING: "Backen",
        LOW_CALORIE: "Low Calorie",
        ANTI_AGEING: "Anti-Aging",
        DETOX: "Entgiftend",
        HEART_HEALTH: "Herzgesund",
      };

      return tagLabels[tag] || tag; // Rückgabe der Bezeichnung, falls vorhanden, sonst das Tag selbst
    },

    // Sucht Rezepte anhand des Namens und der Tags
    async searchRecipes() {
      try {
        const response = await this.$axios.get("/recipes", {
          params: {
            name: this.searchQuery.name,
            tags: this.searchQuery.tags.join(','),
          },
        });
        this.recipes = response.data;
      } catch (error) {
        console.error("Fehler bei der Rezeptsuche:", error);
      }
    },
  },
};
</script>

<style scoped>
.recipe-search-container {
  padding: 20px;
}

.search-fields {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.search-input,
.search-button {
  padding: 10px;
  font-size: 1rem;
}

.search-button {
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
}

.search-button:hover {
  background-color: #45a049;
}

.tags-filter select {
  padding: 8px;
  font-size: 1rem;
}

.recipe-list {
  margin-top: 20px;
}

.recipe-card {
  border: 1px solid #ddd;
  padding: 10px;
  margin-bottom: 10px;
}

.tags {
  margin-top: 10px;
}

.tag {
  display: inline-block;
  background-color: #f0f0f0;
  padding: 5px 10px;
  margin-right: 5px;
  border-radius: 20px;
  font-size: 0.9rem;
}

.no-results {
  color: red;
  font-weight: bold;
}
</style>
