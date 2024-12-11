import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';
import VCalendar from 'v-calendar';
import './assets/style.css';



const app = createApp(App);
app.use(VCalendar, {
    componentPrefix: 'v'  // optional: damit die Kalender-Komponenten mit "v-" als Prefix verwendet werden
});
// Globale Axios-Konfiguration
app.config.globalProperties.$axios = axios.create({
    baseURL: 'http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]:8080/api',  // Backend läuft auf localhost:8080
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

app.mount('#app');
