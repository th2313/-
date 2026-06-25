// main.js 完整配置
import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router' // 引入路由配置

const app = createApp(App)
app.use(ElementPlus)
app.use(router) // 关键：挂载路由
app.mount('#app')