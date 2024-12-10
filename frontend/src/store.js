import { createStore } from 'vuex';
import axios from "axios";

export default createStore({
    state: {
        recipes: [],
    },
    mutations: {
        setRecipes(state, recipes) {
            state.recipes = recipes;
        },
        addRecipe(state, recipe) {
            state.recipes.push(recipe);
        },
        removeRecipe(state, recipeId) {
            state.recipes = state.recipes.filter(recipe => recipe.id !== recipeId);
        },
        updateRecipe(state, updatedRecipe) {
            const index = state.recipes.findIndex(recipe => recipe.id === updatedRecipe.id);
            if (index !== -1) {
                state.recipes.splice(index, 1, updatedRecipe);
            }
        },
    },
    actions: {
        async fetchRecipes({ commit }) {
            try {
                const response = await axios.get('/api/recipes');
                commit('setRecipes', response.data);
            } catch (error) {
                console.error('Fehler beim Abrufen der Rezepte:', error);
            }
        },
        async addRecipe({ commit }, recipe) {
            try {
                const response = await axios.post('/api/recipes', recipe);
                commit('addRecipe', response.data);
            } catch (error) {
                console.error('Fehler beim Hinzufügen des Rezepts:', error);
            }
        },
        async removeRecipe({ commit }, recipeId) {
            try {
                await axios.delete(`/api/recipes/${recipeId}`);
                commit('removeRecipe', recipeId);
            } catch (error) {
                console.error('Fehler beim Löschen des Rezepts:', error);
            }
        },
        async updateRecipe({ commit }, updatedRecipe) {
            try {
                const response = await axios.put(`/api/recipes/${updatedRecipe.id}`, updatedRecipe);
                commit('updateRecipe', response.data);
            } catch (error) {
                console.error('Fehler beim Aktualisieren des Rezepts:', error);
            }
        },
    },
    getters: {
        allRecipes: state => state.recipes,
    },
});
