import { fileURLToPath, URL } from 'node:url';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    host: '::',  // Lauscht auf allen IPv4-Adressen, auch localhost
    port: 80,          // Frontend läuft auf Port 80
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // Dein Backend auf localhost:8080
        changeOrigin: true,
        secure: false,  // Deaktiviert die SSL/TLS-Prüfung
        rewrite: (path) => path.replace(/^\/api/, ''),  // Entfernt "/api" aus dem Pfad
      },
    },
  },
});