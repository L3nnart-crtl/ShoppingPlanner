<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h4>Bestätigung</h4> <!-- Confirmation Header -->
      <p>Bist du sicher, dass du das Rezept "{{ selectedRecipe.name }}" löschen möchtest?</p> <!-- Confirmation message -->
      <button @click="deleteRecipe" class="modal-button confirm">Ja, löschen</button> <!-- Confirm deletion button -->
      <button @click="closeDeleteModal" class="modal-button cancel">Abbrechen</button> <!-- Cancel button -->
    </div>
  </div>
</template>

<script>
export default {
  props: {
    selectedRecipe: Object, // The recipe selected for deletion
  },
  methods: {
    deleteRecipe() {
      this.$emit("deleteRecipe"); // Emit event to delete the recipe
    },
    closeDeleteModal() {
      this.$emit("closeDeleteModal"); // Emit event to close the modal
    },
  },
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5); /* Overlay background */
  display: flex;
  justify-content: center;
  align-items: flex-start;
  z-index: 9999;
  padding-top: 20px; /* Padding to place the modal correctly */
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 500px;
  width: 100%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* Shadow for the modal */
  display: flex;
  flex-direction: column;
  gap: 15px; /* Gap between modal elements */
}

.modal-button {
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  border: none;
}

.confirm {
  background-color: #dc3545; /* Red color for confirm button */
  color: white;
}

.cancel {
  background-color: #6c757d; /* Grey color for cancel button */
  color: white;
}

.modal-button:hover {
  opacity: 0.8; /* Hover effect for buttons */
}

h4 {
  margin-bottom: 10px;
  font-size: 1.5em;
}

p {
  margin-bottom: 10px;
}
</style>
