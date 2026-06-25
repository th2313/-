import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 将前端相对路径的 API 请求代理到后端 Spring Boot 服务
      '/tUser': 'http://localhost:8080',
      '/patientinfo': 'http://localhost:8080',
      '/medrecordinfo': 'http://localhost:8080',
      '/templateinfo': 'http://localhost:8080',
      '/drugs': 'http://localhost:8080',
      '/api': 'http://localhost:8080',
      '/doctorSchedule': 'http://localhost:8080',
      '/visitRecord': 'http://localhost:8080',
      '/departinfo': 'http://localhost:8080',
      '/doctorinfo': 'http://localhost:8080',
    },
  },
})
