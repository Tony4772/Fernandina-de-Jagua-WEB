import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import {defineConfig} from 'vite';

const PROJECT_ID = 'fernandina-de-jagua-web';

export default defineConfig(() => {
  return {
    plugins: [react(), tailwindcss()],
    resolve: {
      alias: {
        '@': import.meta.dirname,
      },
    },
    server: {
      host: '0.0.0.0',
      port: 3000,
      // Permite abrir la app desde el host de preview del sandbox.
      allowedHosts: true,
      // HMR is disabled in AI Studio via DISABLE_HMR env var.
      // Do not modify—file watching is disabled to prevent flickering during agent edits.
      hmr: process.env.DISABLE_HMR !== 'true',
      // Disable file watching when DISABLE_HMR is true to save CPU during agent edits.
      watch: process.env.DISABLE_HMR === 'true' ? null : {},
      // En local, /api/payments se resuelve contra el emulador de Functions
      // (`npm run emulators`). En producción lo resuelve el rewrite de Hosting.
      proxy: {
        '/api/payments': {
          target: `http://127.0.0.1:5001/${PROJECT_ID}/us-central1/payments`,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api\/payments/, ''),
        },
      },
    },
  };
});
