import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';
import './assets/style.css';



const app = createApp(App);

// Globale Axios-Konfiguration
app.config.globalProperties.$axios = axios.create({
    baseURL: 'http://localhost:8080/api',  // Backend läuft auf localhost:8080
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

app.mount('#app');
