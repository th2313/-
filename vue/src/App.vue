<template>
  <div class="app-wrapper">
    <!-- 登录状态显示主布局 -->
    <template v-if="showMainLayout">
      <!-- 头部组件 -->
      <Header />
      
      <!-- 主布局容器 -->
      <div class="main-container">
        <!-- 侧边栏 -->
        <aside class="sidebar">
          <Aside />
        </aside>
        
        <!-- 主内容区 -->
        <main class="main-content">
          <router-view />
        </main>
      </div>
    </template>
    
    <!-- 未登录状态只显示路由视图（登录页） -->
    <template v-else>
      <router-view />
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Aside from './components/Aside.vue'

const route = useRoute()

// 根据当前路由和登录状态决定显示哪种布局
const showMainLayout = computed(() => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  const isLoginPage = route.path === '/login'
  
  // 只有在已登录且不在登录页时才显示主布局
  return isLoggedIn && !isLoginPage
})
</script>

<style scoped>
.app-wrapper {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.main-container {
  display: flex;
  width: 100%;
  height: calc(100% - 106px); /* 减去头部高度 */
}

.sidebar {
  width: 200px;
  background-color: #f5f7fa;
  border-right: 1px solid #e5e6eb;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #ffffff;
  overflow-y: auto;
}
</style>