<template>
  <div class="shopping-list">
    <h1>Shopping List</h1>

    <!-- Date form with compact input fields -->
    <form @submit.prevent="generateShoppingList" class="date-form">
      <div class="form-group">
        <label for="startDate">Start Date:</label>
        <input type="date" id="startDate" v-model="startDate" required>
      </div>

      <div class="form-group">
        <label for="endDate">End Date:</label>
        <input type="date" id="endDate" v-model="endDate" required>
      </div>

      <!-- Buttons in a compact group -->
      <div class="button-group">
        <button type="submit" :disabled="loading">
          <span v-if="loading">Generating...</span>
          <span v-else>Generate List</span>
        </button>

        <!-- Modal Trigger Button -->
        <button v-if="shoppingList.length > 0" @click="openModal" class="show-list-btn">
          Show Full List
        </button>
      </div>
    </form>

    <!-- Error message -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <!-- Modal for the shopping list -->
    <div v-if="modalOpen" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <h3>Shopping List</h3>
        <ul class="shopping-list-items">
          <li v-for="item in shoppingList" :key="item.ingredientName">
            {{ item.ingredientName }}: {{ item.unit }}
          </li>
        </ul>
        <button @click="closeModal">Close</button>
      </div>
    </div>

    <!-- No items message -->
    <p v-if="!isListGenerated" class="no-items">
      No ingredients found for this period.
    </p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      startDate: '',
      endDate: '',
      shoppingList: [],
      isListGenerated: true,
      loading: false,
      errorMessage: '',
      modalOpen: false,
    };
  },
  methods: {
    async generateShoppingList() {
      if (!this.startDate || !this.endDate) {
        this.errorMessage = 'Please select both a start and an end date.';
        return;
      }

      this.loading = true;
      this.errorMessage = '';

      try {
        const csrfToken = document.cookie.match(/XSRF-TOKEN=([^;]+)/)?.[1];
        this.$axios.defaults.headers.common['X-XSRF-TOKEN'] = csrfToken;

        const response = await this.$axios.post(
            '/shopping-list/generate',
            null,
            {
              params: {
                startDate: this.startDate,
                endDate: this.endDate,
              },
              headers: {
                'Content-Type': 'application/json',
              },
            }
        );

        if (!response) {
          throw new Error('Server error: Shopping list could not be generated.');
        }

        this.shoppingList = response.data;
        this.isListGenerated = this.shoppingList.length > 0;
      } catch (error) {
        this.errorMessage = error.message || 'Error retrieving shopping list.';
      } finally {
        this.loading = false;
      }
    },
    openModal() {
      this.modalOpen = true;
    },
    closeModal() {
      this.modalOpen = false;
    }
  },
};
</script>

<style scoped>
.shopping-list {
  max-width: 350px;
  height: auto;
  margin: 0 auto;
  padding: 15px;
  font-family: Arial, sans-serif;
  background-color: #f9f9f9;
  border-radius: 8px;
}

h1 {
  text-align: center;
  font-size: 20px;
  color: #333;
}

.date-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

label {
  font-weight: bold;
  font-size: 12px;
  color: #333;
}

input[type="date"] {
  width: 150px;
  padding: 8px;
  font-size: 12px;
  border-radius: 5px;
  border: 1px solid #ddd;
  background-color: #fff;
}

.button-group {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-top: 15px;
}

button {
  padding: 8px 15px;
  font-size: 14px;
  color: white;
  background-color: #4caf50;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.shopping-list-items {
  list-style-type: none;
  padding-left: 0;
  margin-top: 15px;
  max-height: 300px;
  overflow-y: auto;
}

.error-message {
  color: red;
  font-weight: bold;
  margin-top: 10px;
}

.no-items {
  color: gray;
  font-style: italic;
  margin-top: 10px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  max-width: 450px;
  width: 100%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.modal h3 {
  text-align: center;
  font-size: 20px;
}

.modal button {
  background-color: #388e3c;
  color: white;
  padding: 8px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.modal button:hover {
  background-color: #2c6e31;
}
</style>
