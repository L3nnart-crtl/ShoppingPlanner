import { fileURLToPath, URL } from 'node:url';  // Importing utilities for working with URLs
import { defineConfig } from 'vite';  // Importing the Vite configuration function
import vue from '@vitejs/plugin-vue';  // Importing the Vue plugin for Vite

export default defineConfig({
  plugins: [
    vue(),  // Adding the Vue plugin to the Vite configuration
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),  // Creating an alias for the 'src' folder
    },
  },
  server: {
    host: '::',  // Listens on all IPv4 addresses, including localhost
    port: 80,  // The frontend will run on port 80
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // Backend running on localhost:8080
        changeOrigin: true,  // Ensures the correct host header is sent to the target
        secure: false,  // Disables SSL/TLS verification
        rewrite: (path) => path.replace(/^\/api/, ''),  // Strips "/api" from the path before forwarding to the target
      },
    },
  },
});
