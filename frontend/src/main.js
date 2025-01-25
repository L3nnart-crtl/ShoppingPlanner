import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';
import { createRouter, createWebHistory } from 'vue-router';
import LoginRegister from './components/login/Login.vue';
import HomePage from './views/HomePage.vue';

// Function to get a specific cookie value by name
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();  // Returns the cookie value if found
}

// Creating an axios instance with base URL and default headers
const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080/api',  // Backend API base URL
    headers: {
        'Content-Type': 'application/json',  // Setting content type as JSON
        'Accept': 'application/json',  // Accepting JSON responses
    },
    withCredentials: true, // Ensures cookies and session data are sent with requests
});

// Set CSRF token from cookie
axiosInstance.defaults.headers['X-XSRF-TOKEN'] = getCookie('XSRF-TOKEN');  // Setting CSRF token for protection

// Defining application routes
const routes = [
    { path: '/auth', component: LoginRegister },  // Route for login/register page
    { path: '/', component: HomePage, meta: { requiresAuth: true } },  // Home page route with authentication requirement
];

// Creating the router instance
const router = createRouter({
    history: createWebHistory(),  // Enabling history mode for clean URLs
    routes,  // Assigning the defined routes
});

// Navigation guard to check authentication before accessing routes requiring it
router.beforeEach(async (to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {  // Checking if the route requires authentication
        try {
            const response = await axiosInstance.get('/auth/status');  // Checking the authentication status
            if (response.status === 200) {  // If authenticated, proceed to the route
                next();
            } else {  // If not authenticated, redirect to the login page
                next('/auth');
            }
        } catch (error) {
            console.error('Authentication check failed', error);  // Log any errors during authentication check
            next('/auth');  // Redirect to login page if an error occurs
        }
    } else {
        next();  // Allow access to the route if no authentication is required
    }
});

// Creating the Vue application and mounting it to the DOM
const app = createApp(App);
app.config.globalProperties.$axios = axiosInstance;  // Making the axios instance globally accessible
app.use(router);  // Applying the router to the application
app.mount('#app');  // Mounting the Vue app to the DOM element with ID "app"
