<template>
  <div class="form-container">
    <h2 class="form-title">Rezept hinzufügen</h2>
    <form @submit.prevent="submitRecipe" class="recipe-form">
      <!-- Rezeptname -->
      <div class="input-group">
        <label for="name">Rezeptname:</label>
        <input type="text" v-model="recipe.name" placeholder="Rezeptname eingeben" required />
      </div>

      <!-- Beschreibung -->
      <div class="input-group">
        <label for="description">Beschreibung:</label>
        <textarea v-model="recipe.description" placeholder="Beschreibung eingeben" required></textarea>
      </div>

      <!-- Zutaten hinzufügen -->
      <div v-for="(ingredient, index) in recipe.ingredients" :key="index" class="ingredient-group">
        <div class="ingredient-item">
          <label>Zutat {{ index + 1 }}:</label>
          <input type="text" v-model="ingredient.name" placeholder="Zutat" required />
          <input type="text" v-model="ingredient.quantity" placeholder="Menge" required />
          <input type="text" v-model="ingredient.unit" placeholder="Einheit" required />
          <button type="button" @click="removeIngredient(index)" class="remove-button">Entfernen</button>
        </div>
      </div>

      <button type="button" @click="addIngredient" class="add-button">Zutat hinzufügen</button>

      <!-- Tag-Auswahl mit vue-multiselect -->
      <div class="tag-selection">
        <label for="tags">Wähle Tags:</label>
        <multiselect
            v-model="selectedTags"
            :options="allTags"
            :multiple="true"
            :close-on-select="false"
            placeholder="Tags auswählen"
            label="name"
            track-by="name"
            :tag-placeholder="'Tag hinzufügen'"
            class="tag-dropdown"
        >
        </multiselect>
      </div>

      <button type="submit" class="submit-button" :disabled="isSubmitting">Rezept speichern</button>
    </form>
  </div>
</template>

<script>
import Multiselect from 'vue-multiselect';
import 'vue-multiselect/dist/vue-multiselect.min.css';

export default {
  components: {
    Multiselect
  },
  data() {
    return {
      recipe: {
        name: '',
        description: '',
        ingredients: [{ name: '', quantity: '', unit: '' }],
        tags: [],
      },
      selectedTags: [],  // Array für die ausgewählten Tags
      allTags: [
        {name: 'Vegetarisch'},
        {name: 'Vegan'},
        {name: 'Glutenfrei'},
        {name: 'Laktosefrei'},
        {name: 'Keto'},
        {name: 'Paleo'},
        {name: 'Low Carb'},
        {name: 'High Protein'},
        {name: 'Diabetikerfreundlich'},
        {name: 'Low Fat'},
        {name: 'Halal'},
        {name: 'Koscher'},
        {name: 'Ausschlussdiät'},
        {name: 'Schnell'},
        {name: 'Meal Prep'},
        {name: 'Mahlzeit für Kinder'},
        {name: 'Süß'},
        {name: 'Herzhaft'},
        {name: 'Scharf'},
        {name: 'Mild'},
        {name: 'Exotisch'},
        {name: 'Saisonale Rezepte'},
        {name: 'Grillrezepte'},
        {name: 'Frühstück'},
        {name: 'Mittagessen'},
        {name: 'Abendessen'},
        {name: 'Snacks'},
        {name: 'Suppen'},
        {name: 'Salate'},
        {name: 'Zuckerfrei'},
        {name: 'Fisch'},
        {name: 'Fleisch'},
        {name: 'Nüsse'},
        {name: 'Vollkorn'},
        {name: 'Hülsenfrüchte'},
        {name: 'Nudeln'},
        {name: 'Reis'},
        {name: 'Budgetfreundlich'},
        {name: 'Für 2 Personen'},
        {name: 'Familientauglich'},
        {name: 'Für Anfänger'},
        {name: 'Gourmet'},
        {name: 'Lagerung'},
        {name: 'Nachtisch'},
        {name: 'Langsame Zubereitung'},
        {name: 'Ein-Pfannen-Gerichte'},
        {name: 'Frittieren'},
        {name: 'Backen'},
        {name: 'Low Calorie'},
        {name: 'Anti-Aging'},
        {name: 'Entgiftend'},
        {name: 'Herzgesund'}
      ],  // Alle verfügbaren Tags
      isSubmitting: false,
    };
  },
  methods: {
    addIngredient() {
      this.recipe.ingredients.push({name: '', quantity: '', unit: ''});
    },
    removeIngredient(index) {
      this.recipe.ingredients.splice(index, 1);
    },
    // Methode zur Übersetzung von Tags in Enum-Werte
    translateTags(tags) {
      const tagMapping = {
        'Vegetarisch': 'VEGETARIAN',
        'Vegan': 'VEGAN',
        'Glutenfrei': 'GLUTEN_FREE',
        'Laktosefrei': 'LACTOSE_FREE',
        'Keto': 'KETO',
        'Paleo': 'PALEO',
        'Low Carb': 'LOW_CARB',
        'High Protein': 'HIGH_PROTEIN',
        'Diabetikerfreundlich': 'DIABETIC_FRIENDLY',
        'Low Fat': 'LOW_FAT',
        'Halal': 'HALAL',
        'Koscher': 'KOSHER',
        'Ausschlussdiät': 'EXCLUSION_DIET',
        'Schnell': 'QUICK',
        'Meal Prep': 'MEAL_PREP',
        'Mahlzeit für Kinder': 'KIDS_MEAL',
        'Süß': 'SWEET',
        'Herzhaft': 'SAVORY',
        'Scharf': 'SPICY',
        'Mild': 'MILD',
        'Exotisch': 'EXOTIC',
        'Saisonale Rezepte': 'SEASONAL',
        'Grillrezepte': 'GRILL',
        'Frühstück': 'BREAKFAST',
        'Mittagessen': 'LUNCH',
        'Abendessen': 'DINNER',
        'Snacks': 'SNACKS',
        'Suppen': 'SOUPS',
        'Salate': 'SALADS',
        'Zuckerfrei': 'SUGAR_FREE',
        'Fisch': 'FISH',
        'Fleisch': 'MEAT',
        'Nüsse': 'NUTS',
        'Vollkorn': 'WHOLEGRAIN',
        'Hülsenfrüchte': 'LEGUMES',
        'Nudeln': 'PASTA',
        'Reis': 'RICE',
        'Budgetfreundlich': 'BUDGET_FRIENDLY',
        'Für 2 Personen': 'FOR_2_PEOPLE',
        'Familientauglich': 'FAMILY_FRIENDLY',
        'Für Anfänger': 'FOR_BEGGINERS',
        'Gourmet': 'GOURMET',
        'Lagerung': 'STORAGE',
        'Nachtisch': 'DESSERT',
        'Langsame Zubereitung': 'SLOW_COOK',
        'Ein-Pfannen-Gerichte': 'ONE_POT',
        'Frittieren': 'FRYING',
        'Backen': 'BAKING',
        'Low Calorie': 'LOW_CALORIE',
        'Anti-Aging': 'ANTI_AGEING',
        'Entgiftend': 'DETOX',
        'Herzgesund': 'HEART_HEALTH'
      };

      return tags.map(tag => tagMapping[tag] || tag);
    },

    async submitRecipe() {
      if (this.isSubmitting) return;
      this.isSubmitting = true;

      try {
        // Tags übersetzen
        this.recipe.tags = this.translateTags(this.selectedTags.map(tag => tag.name));

        // Sende das Rezept an das Backend
        const response = await this.$axios.post('/recipes', this.recipe);

        // Nur das Rezept zur Liste der Elternkomponente hinzufügen, wenn es erfolgreich gespeichert wurde
        this.$emit('recipe-added', response.data);

        // Formular zurücksetzen
        this.recipe = {name: '', description: '', ingredients: [{name: '', quantity: '', unit: ''}], tags: []};
        this.selectedTags = [];  // Zurücksetzen der ausgewählten Tags
      } catch (error) {
        console.error('Fehler beim Hinzufügen des Rezepts:', error);
      } finally {
        this.isSubmitting = false;
      }
    },
  },
};
</script>

<style scoped>
.form-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.input-group {
  margin-bottom: 16px;
}

.input-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 4px;
}

.input-group input, .input-group textarea {
  width: 100%;
  padding: 8px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.tag-selection {
  margin-bottom: 16px;
}

.submit-button, .add-button, .remove-button {
  padding: 10px;
  font-size: 16px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.submit-button:disabled {
  background-color: #ccc;
}

.add-button {
  background-color: #008CBA;
}

.remove-button {
  background-color: #f44336;
}

.submit-button {
  width: 100%;
  margin-top: 16px;
}
</style>
