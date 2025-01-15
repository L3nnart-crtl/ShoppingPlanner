// Import necessary modules
import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';
import VCalendar from 'v-calendar';
import './assets/style.css';

// Vue 3 Router imports
import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/components/Login/Login.vue';
import HomePage from '@/views/HomePage.vue';

// Create a Vue app instance
const app = createApp(App);

// Use the VCalendar plugin globally with a prefix
app.use(VCalendar, {
    componentPrefix: 'v',  // optional: so that the calendar components have "v-" as the prefix
});

// Define routes
const routes = [
    { path: '/login', component: Login },
    { path: '/home', component: HomePage, meta: { requiresAuth: true } }, // Protect the Home page
    { path: '/', redirect: '/login' },
];

// Create the router instance with history mode
const router = createRouter({
    history: createWebHistory(),  // Vue 3 history mode for cleaner URLs
    routes,
});

// Navigation Guard: Check if the user is logged in before accessing protected routes
router.beforeEach(async (to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        try {
            const response = await fetch('http://localhost:8080/api/auth/status', {
                credentials: 'include',  // Send cookies along with the request
            });

            if (response.ok) {
                next();  // If user is authenticated, continue to the requested route
            } else {
                next('/login');  // Redirect to login page if not authenticated
            }
        } catch (error) {
            next('/login');  // In case of an error, redirect to login page
        }
    } else {
        next();  // If no authentication is required, continue to the route
    }
});

// Set up global Axios configuration
app.config.globalProperties.$axios = axios.create({
    baseURL: 'http://localhost:8080/api',  // Backend is running on localhost:8080
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
    withCredentials: true,  // Important: send credentials (cookies) with each request
});

// Use the router with the Vue app
app.use(router);

// Mount the app to the DOM
app.mount('#app');
