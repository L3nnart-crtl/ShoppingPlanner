<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h4>Confirmation</h4>
      <p>Are you sure you want to delete the recipe "{{ selectedRecipe.name }}"?</p>
      <button @click="deleteRecipe" class="modal-button confirm">Yes, delete</button>
      <button @click="closeDeleteModal" class="modal-button cancel">Cancel</button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    selectedRecipe: Object,
  },
  methods: {
    deleteRecipe() {
      this.$emit("deleteRecipe");
    },
    closeDeleteModal() {
      this.$emit("closeDeleteModal");
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
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  z-index: 9999;
  padding-top: 20px;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 500px;
  width: 100%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.modal-button {
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  border: none;
}

.confirm {
  background-color: #dc3545;
  color: white;
}

.cancel {
  background-color: #6c757d;
  color: white;
}

.modal-button:hover {
  opacity: 0.8;
}

h4 {
  margin-bottom: 10px;
  font-size: 1.5em;
}

p {
  margin-bottom: 10px;
}

</style>
