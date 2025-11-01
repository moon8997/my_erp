import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [vue()],
	server: {
    port: 5173,
    open: true,
    proxy: {
      // API 요청을 스프링 부트(8651)으로 프록시
      '/api': {
        target: 'http://localhost:8651',
        changeOrigin: true,
        secure: false
      }
    }
  }
});


