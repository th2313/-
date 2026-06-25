<template>
  <div class="header">
    <div class="header-glow"></div>
    <div class="header-container">
      <!-- 系统图标 -->
      <div class="logo-section">
        <div class="logo-orb">
          <div class="logo-shine"></div>
          <svg class="logo-icon" width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M19 3H5C3.89543 3 3 3.89543 3 5V19C3 20.1046 3.89543 21 5 21H19C20.1046 21 21 20.1046 21 19V5C21 3.89543 20.1046 3 19 3Z" stroke="currentColor" stroke-width="1.5"/>
            <path d="M8 10H16" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            <path d="M8 14H13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            <path d="M16 7V17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <div class="title-group">
          <h1 class="system-title">医学信息管理系统</h1>
          <div class="title-accent"></div>
        </div>
      </div>
      
      <!-- 右侧信息区域：时间 + 用户信息 -->
      <div class="right-section">
        <!-- 当前时间显示 -->
        <div class="time-display">
          <div class="time-badge">
            <div class="time-dot"></div>
            <div class="current-time">{{ currentTime }}</div>
          </div>
        </div>
        
        <!-- 用户信息 -->
        <div class="user-section">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-card">
              <div class="user-orb">
                <div class="user-shine"></div>
                <el-icon class="user-icon"><User /></el-icon>
              </div>
              <span class="user-name">{{ userName }}</span>
              <div class="dropdown-chevron">
                <el-icon><ArrowDown /></el-icon>
              </div>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="elegant-dropdown">
                <el-dropdown-item command="logout" class="elegant-item">
                  <div class="item-elegant">
                    <div class="item-icon">
                      <el-icon><SwitchButton /></el-icon>
                    </div>
                    <span>退出登录</span>
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { User, ArrowDown, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const currentTime = ref('')
let timer = null

// 格式化时间显示
const formatTime = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekday = weekdays[date.getDay()]
  
  return `${year}-${month}-${day} ${weekday} ${hours}:${minutes}:${seconds}`
}

// 初始化时间并设置定时器更新
onMounted(() => {
  currentTime.value = formatTime(new Date())
  timer = setInterval(() => {
    currentTime.value = formatTime(new Date())
  }, 1000)
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

// 计算用户名
const userName = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  return userInfo ? JSON.parse(userInfo).uname : '用户'
})

// 处理下拉菜单命令
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
      
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userInfo')
      
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch {
      // 用户取消操作
    }
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.95) 0%, 
    rgba(248, 251, 255, 0.98) 50%, 
    rgba(240, 248, 255, 0.95) 100%);
  padding: 0;
  position: relative;
  z-index: 1000;
  border-bottom: 1px solid rgba(24, 144, 255, 0.08);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.header-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    rgba(24, 144, 255, 0.3) 50%, 
    transparent 100%);
  animation: headerGlow 3s ease-in-out infinite;
}

@keyframes headerGlow {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.8; }
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 40px;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo-orb {
  position: relative;
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, #1890ff 0%, #69c0ff 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 
    0 8px 32px rgba(24, 144, 255, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.3),
    inset 0 -1px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.logo-shine {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.2) 0%, 
    transparent 50%);
  border-radius: 16px;
}

.logo-orb:hover {
  transform: translateY(-4px) rotate(5deg);
  box-shadow: 
    0 16px 40px rgba(24, 144, 255, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.4),
    inset 0 -1px 0 rgba(0, 0, 0, 0.1);
}

.logo-icon {
  color: white;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
  position: relative;
  z-index: 1;
}

.title-group {
  position: relative;
}

.system-title {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  background: linear-gradient(135deg, #1890ff 0%, #0050b3 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1px;
  text-shadow: 0 4px 8px rgba(24, 144, 255, 0.15);
  position: relative;
}

.title-accent {
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #1890ff, rgba(24, 144, 255, 0.3));
  border-radius: 2px;
  opacity: 0.8;
  animation: titleFlow 4s ease-in-out infinite;
}

@keyframes titleFlow {
  0%, 100% { transform: translateX(0) scaleX(1); }
  50% { transform: translateX(20px) scaleX(1.2); }
}

/* 右侧信息区域样式 */
.right-section {
  display: flex;
  align-items: center;
  gap: 32px;
}

/* 时间显示样式 */
.time-display {
  position: relative;
}

.time-badge {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 24px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(24, 144, 255, 0.12);
  border-radius: 14px;
  backdrop-filter: blur(20px);
  box-shadow: 
    0 6px 24px rgba(24, 144, 255, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

.time-badge::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(24, 144, 255, 0.06), 
    transparent);
  transition: left 0.8s ease;
}

.time-badge:hover::before {
  left: 100%;
}

.time-badge:hover {
  transform: translateY(-3px);
  box-shadow: 
    0 12px 32px rgba(24, 144, 255, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.95);
  border-color: rgba(24, 144, 255, 0.2);
}

.time-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #52c41a, #73d13d);
  box-shadow: 0 0 12px rgba(82, 196, 26, 0.5);
  animation: timeBeat 2s ease-in-out infinite;
}

@keyframes timeBeat {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

.current-time {
  color: #1890ff;
  font-size: 15px;
  font-weight: 600;
  font-family: 'SF Mono', 'Courier New', monospace;
  letter-spacing: 0.8px;
  text-shadow: 0 1px 2px rgba(24, 144, 255, 0.1);
}

/* 用户信息区域 */
.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(24, 144, 255, 0.12);
  border-radius: 14px;
  backdrop-filter: blur(20px);
  box-shadow: 
    0 6px 24px rgba(24, 144, 255, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: pointer;
  min-width: 140px;
}

.user-card:hover {
  transform: translateY(-3px);
  box-shadow: 
    0 12px 32px rgba(24, 144, 255, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.95);
  border-color: rgba(24, 144, 255, 0.2);
  background: rgba(255, 255, 255, 0.85);
}

.user-orb {
  position: relative;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #1890ff 0%, #69c0ff 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(24, 144, 255, 0.3);
}

.user-shine {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.3) 0%, 
    transparent 50%);
  border-radius: 10px;
}

.user-icon {
  color: white;
  font-size: 16px;
}

.user-name {
  font-weight: 600;
  color: #1890ff;
  font-size: 14px;
  letter-spacing: 0.5px;
  flex: 1;
}

.dropdown-chevron {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  color: #69c0ff;
}

.user-card:hover .dropdown-chevron {
  color: #1890ff;
  transform: rotate(180deg) scale(1.1);
}

/* 下拉菜单样式 */
:deep(.elegant-dropdown) {
  border-radius: 14px;
  box-shadow: 
    0 16px 40px rgba(24, 144, 255, 0.18),
    0 8px 20px rgba(24, 144, 255, 0.12);
  border: 1px solid rgba(24, 144, 255, 0.1);
  padding: 8px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
}

:deep(.elegant-item) {
  border-radius: 10px;
  margin: 2px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  padding: 0;
}

/* 修改后的退出登录悬停样式 */
:deep(.elegant-item:hover) {
  background: #1890ff; /* 主蓝色，与logo主色一致 */
  color: white;
  box-shadow: 0 4px 16px rgba(24, 144, 255, 0.3); /* 蓝色阴影 */
}

:deep(.item-elegant) {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
}

:deep(.item-icon) {
  width: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 16px 24px;
  }
  
  .system-title {
    font-size: 22px;
  }
  
  .logo-orb {
    width: 44px;
    height: 44px;
  }
  
  .time-badge,
  .user-card {
    padding: 10px 16px;
  }
  
  .right-section {
    gap: 20px;
  }
  
  .user-name {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .header-container {
    padding: 12px 16px;
  }
  
  .system-title {
    font-size: 18px;
  }
  
  .title-accent {
    display: none;
  }
  
  .user-name {
    display: none;
  }
  
  .user-card {
    min-width: auto;
    padding: 8px 12px;
  }
  
  .time-badge {
    padding: 8px 12px;
  }
  
  .current-time {
    font-size: 12px;
  }
  
  .user-orb {
    width: 32px;
    height: 32px;
  }
  
  .user-icon {
    font-size: 14px;
  }
}
</style>