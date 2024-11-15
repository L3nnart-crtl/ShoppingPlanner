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
    host: '::',  // Lauscht auf allen IPv6-Adressen
    port: 80,  // Frontend läuft auf Port 80
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // Deine Backend-IPv6-Adresse
        changeOrigin: true,
        secure: false,  // Deaktiviert die SSL/TLS-Prüfung
        rewrite: (path) => path.replace(/^\/api/, ''),  // Entfernt "/api" aus dem Pfad
      },
    },
  },
});
