<template>
  <div class="sidebar-container">
    <div class="sidebar-background"></div>
    <div class="sidebar-menu">
      <el-menu
        :default-active="$route.path" 
        class="el-menu-vertical" 
        router
      >
        <!-- 病人信息管理 -->
        <el-menu-item index="/patient">
          <div class="menu-item-content">
            <div class="menu-icon-wrapper">
              <div class="icon-shine"></div>
              <el-icon class="menu-icon"><User /></el-icon>
            </div>
            <span class="menu-text">病人信息管理</span>
            <div class="menu-arrow">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </el-menu-item>

        <!-- 电子病历管理 -->
        <el-menu-item index="/medical-record">
          <div class="menu-item-content">
            <div class="menu-icon-wrapper">
              <div class="icon-shine"></div>
              <el-icon class="menu-icon"><Document /></el-icon>
            </div>
            <span class="menu-text">电子病历管理</span>
            <div class="menu-arrow">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </el-menu-item>

        <!-- 病历模板管理 -->
        <el-menu-item index="/template">
          <div class="menu-item-content">
            <div class="menu-icon-wrapper">
              <div class="icon-shine"></div>
              <el-icon class="menu-icon"><CopyDocument /></el-icon>
            </div>
            <span class="menu-text">病历模板管理</span>
            <div class="menu-arrow">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </el-menu-item>

        <!-- 检查项目管理 -->
        <el-menu-item index="/inspection">
          <div class="menu-item-content">
            <div class="menu-icon-wrapper">
              <div class="icon-shine"></div>
              <el-icon class="menu-icon"><Search /></el-icon>
            </div>
            <span class="menu-text">检查项目管理</span>
            <div class="menu-arrow">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </el-menu-item>

        <!-- 药品信息管理 -->
        <el-menu-item index="/drugs">
          <div class="menu-item-content">
            <div class="menu-icon-wrapper">
              <div class="icon-shine"></div>
              <el-icon class="menu-icon"><Box /></el-icon>
            </div>
            <span class="menu-text">药品信息管理</span>
            <div class="menu-arrow">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </el-menu-item>

        <!-- 医生排班管理 -->
        <el-menu-item index="/doctor-schedule">
          <div class="menu-item-content">
            <div class="menu-icon-wrapper">
              <div class="icon-shine"></div>
              <el-icon class="menu-icon"><Clock /></el-icon>
            </div>
            <span class="menu-text">医生排班管理</span>
            <div class="menu-arrow">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </el-menu-item>

		<!-- 数据备份 -->
		<el-menu-item index="/backup">
		  <div class="menu-item-content">
		    <div class="menu-icon-wrapper">
		      <div class="icon-shine"></div>
		      <el-icon class="menu-icon"><Setting /></el-icon>
		    </div>
		    <span class="menu-text">数据备份</span>
		    <div class="menu-arrow">
		      <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
		        <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
		      </svg>
		    </div>
		  </div>
		</el-menu-item>


        <!-- 多次就医记录 -->
        <el-menu-item index="/multiple-visit">
          <div class="menu-item-content">
            <div class="menu-icon-wrapper">
              <div class="icon-shine"></div>
              <el-icon class="menu-icon"><Calendar /></el-icon>
            </div>
            <span class="menu-text">多次就医记录</span>
            <div class="menu-arrow">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </el-menu-item>

        <!-- 报表输出 -->
        <el-menu-item index="/report">
          <div class="menu-item-content">
            <div class="menu-icon-wrapper">
              <div class="icon-shine"></div>
              <el-icon class="menu-icon"><DataAnalysis /></el-icon>
            </div>
            <span class="menu-text">报表输出</span>
            <div class="menu-arrow">
              <svg width="10" height="10" viewBox="0 0 24 24" fill="none">
                <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </el-menu-item>


      </el-menu>
    </div>
  </div>
</template>

<script setup>
import { 
  User, 
  Document, 
  CopyDocument, 
  Setting,
  DataAnalysis,
  Search,
  Box,
  Calendar,
  Clock
} from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'

const $route = useRoute()
</script>

<style scoped>
.sidebar-container {
  width: 200px; /* 缩小宽度 */
  height: 100vh;
  background: linear-gradient(180deg, 
    rgba(255, 255, 255, 0.95) 0%, 
    rgba(248, 251, 255, 0.98) 50%, 
    rgba(240, 248, 255, 0.95) 100%);
  border-right: 1px solid rgba(24, 144, 255, 0.08);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.sidebar-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(24, 144, 255, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(105, 192, 255, 0.03) 0%, transparent 50%);
  pointer-events: none;
}

.sidebar-menu {
  padding: 16px 0; /* 减小内边距 */
  height: calc(100vh - 32px);
  overflow: hidden;
}

.el-menu-vertical {
  width: 100%;
  height: 100%;
  border: none;
  background: transparent;
  overflow: hidden;
}

:deep(.el-menu-item) {
  position: relative;
  height: 48px; /* 减小高度 */
  margin: 4px 12px; /* 减小边距 */
  border-radius: 10px; /* 减小圆角 */
  transition: all 0.3s ease; /* 简化动画 */
  overflow: hidden;
  background: transparent;
}

:deep(.el-menu-item)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 3px; /* 减小宽度 */
  background: linear-gradient(180deg, #1890ff 0%, #69c0ff 100%);
  transform: scaleY(0);
  transition: transform 0.3s ease;
  border-radius: 0 3px 3px 0;
}

:deep(.el-menu-item)::after {
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
  transition: left 0.4s ease;
}

:deep(.el-menu-item:hover)::after {
  left: 100%;
}

:deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 
    0 4px 12px rgba(24, 144, 255, 0.12),
    0 2px 6px rgba(24, 144, 255, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transform: translateX(3px);
}

:deep(.el-menu-item.is-active)::before {
  transform: scaleY(1);
}

:deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.7);
  transform: translateX(2px);
  box-shadow: 
    0 2px 8px rgba(24, 144, 255, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.menu-item-content {
  display: flex;
  align-items: center;
  gap: 12px; /* 减小间距 */
  width: 100%;
  padding: 0 8px;
  position: relative;
  z-index: 1;
}

.menu-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px; /* 减小尺寸 */
  height: 32px;
  border-radius: 8px; /* 减小圆角 */
  background: rgba(24, 144, 255, 0.1);
  transition: all 0.3s ease;
  box-shadow: 0 1px 4px rgba(24, 144, 255, 0.1);
}

.icon-shine {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.3) 0%, 
    transparent 50%);
  border-radius: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

:deep(.el-menu-item:hover) .menu-icon-wrapper,
:deep(.el-menu-item.is-active) .menu-icon-wrapper {
  background: rgba(24, 144, 255, 0.15);
  transform: scale(1.05); /* 减小缩放 */
  box-shadow: 
    0 2px 8px rgba(24, 144, 255, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

:deep(.el-menu-item:hover) .icon-shine,
:deep(.el-menu-item.is-active) .icon-shine {
  opacity: 1;
}

.menu-icon {
  font-size: 16px; /* 减小图标 */
  color: #1890ff;
  transition: all 0.3s ease;
  filter: drop-shadow(0 1px 1px rgba(24, 144, 255, 0.2));
}

:deep(.el-menu-item.is-active) .menu-icon {
  color: #1890ff;
  transform: scale(1.05); /* 减小缩放 */
}

:deep(.el-menu-item:hover) .menu-icon {
  transform: scale(1.02);
}

.menu-text {
  font-weight: 500;
  font-size: 14px; /* 减小字体 */
  color: #1a2b50;
  transition: all 0.3s ease;
  flex: 1;
  letter-spacing: 0.2px;
}

:deep(.el-menu-item.is-active) .menu-text {
  color: #1890ff;
  font-weight: 600;
  font-size: 16.5px; /* 放大字体 */
  text-shadow: 0 1px 1px rgba(24, 144, 255, 0.1);
}

:deep(.el-menu-item:hover) .menu-text {
  color: #1890ff;
}

.menu-arrow {
  opacity: 0;
  transform: translateX(-6px);
  transition: all 0.3s ease;
  color: #69c0ff;
}

:deep(.el-menu-item:hover) .menu-arrow,
:deep(.el-menu-item.is-active) .menu-arrow {
  opacity: 1;
  transform: translateX(0);
}

:deep(.el-menu-item.is-active) .menu-arrow {
  color: #1890ff;
}

:deep(.el-menu-item:not(:last-child)) {
  border-bottom: 1px solid rgba(238, 242, 248, 0.6);
}

.sidebar-menu::-webkit-scrollbar {
  display: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-container {
    width: 180px;
  }
  
  .menu-item-content {
    gap: 10px;
  }
  
  .menu-text {
    font-size: 13px;
  }
  
  .menu-icon-wrapper {
    width: 30px;
    height: 30px;
  }
  
  .menu-icon {
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .sidebar-container {
    width: 160px;
  }
  
  .menu-text {
    font-size: 12px;
  }
  
  .menu-arrow {
    display: none;
  }
  
  .menu-item-content {
    gap: 8px;
    padding: 0 6px;
  }
}
</style>