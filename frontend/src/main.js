import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';
import { createRouter, createWebHistory } from 'vue-router';
import LoginRegister from './components/login/Login.vue';
import HomePage from './views/HomePage.vue';

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

const axiosInstance = axios.create({
    baseURL: 'http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]:8080/api',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
    withCredentials: true, // Include cookies and session info
});

// Set CSRF token from cookie
axiosInstance.defaults.headers['X-XSRF-TOKEN'] = getCookie('XSRF-TOKEN');

const routes = [
    { path: '/auth', component: LoginRegister },
    { path: '/', component: HomePage, meta: { requiresAuth: true } },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach(async (to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        try {
            const response = await axiosInstance.get('/auth/status');
            if (response.status === 200) {
                next();
            } else {
                next('/auth');
            }
        } catch (error) {
            console.error('Authentication check failed', error);
            next('/auth'); // Redirect to login page on error
        }
    } else {
        next();
    }
});

const app = createApp(App);
app.config.globalProperties.$axios = axiosInstance;
app.use(router);
app.mount('#app');
