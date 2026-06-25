import { createRouter, createWebHistory } from 'vue-router'
import Patient from './components/Patient.vue'
import MedicalRecord from './components/MedicalRecord.vue'
import Template from './components/Template.vue'
import Backup from './components/Backup.vue'
import Report from './components/Report.vue'
import Inspection from './components/Inspection.vue'
import Drugs from './components/Drugs.vue'
import MultipleVisit from './components/MultipleVisit.vue'
import Login from './views/Login.vue'
import DoctorSchedule from './components/DoctorSchedule.vue' // 导入 Doctor 组件

// 登录态管理工具函数
const auth = {
  // 获取登录状态
  isLoggedIn() {
    try {
      return localStorage.getItem('isLoggedIn') === 'true'
    } catch (error) {
      console.error('获取登录状态失败:', error)
      return false
    }
  },
  
  // 清除登录状态
  clearLoginState() {
    try {
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userInfo')
    } catch (error) {
      console.error('清除登录状态失败:', error)
    }
  }
}

const routes = [
  { path: '/patient', component: Patient, meta: { requiresAuth: true } },
  { path: '/medical-record', component: MedicalRecord, meta: { requiresAuth: true } },
  { path: '/template', component: Template, meta: { requiresAuth: true } },
  { path: '/backup', component: Backup, meta: { requiresAuth: true } },
  { path: '/report', component: Report, meta: { requiresAuth: true } },
  { path: '/inspection', component: Inspection, meta: { requiresAuth: true } },
  { path: '/drugs', component: Drugs, meta: { requiresAuth: true } },
  { path: '/multiple-visit', component: MultipleVisit, meta: { requiresAuth: true } },
  { path: '/doctor-schedule', component: DoctorSchedule, meta: { requiresAuth: true } },
  { path: '/login', component: Login, meta: { requiresAuth: false } },
  { path: '/', redirect: '/login' },
  { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 优化后的路由守卫
router.beforeEach((to, from, next) => {
  const isLoggedIn = auth.isLoggedIn()
  
  // 调试信息，方便开发时排查问题
  console.log('路由跳转检查:', {
    from: from.path,
    to: to.path,
    isLoggedIn: isLoggedIn,
    requiresAuth: to.meta.requiresAuth
  })
  
  // 核心逻辑
  if (to.meta.requiresAuth) {
    // 需要登录的页面
    if (isLoggedIn) {
      // 已登录，正常访问
      next()
    } else {
      // 未登录，跳转到登录页，并记录想要访问的页面
      next({ 
        path: '/login',
        query: { redirect: to.fullPath } // 记录原本要访问的路径，登录后可跳转回去
      })
    }
  } else {
    // 不需要登录的页面（目前只有登录页）
    if (to.path === '/login') {
      if (isLoggedIn) {
        // 已登录用户访问登录页，自动跳转到患者管理页
        next('/patient')
      } else {
        // 未登录用户访问登录页，正常显示
        next()
      }
    } else {
      // 其他不需要登录的页面（如果有）
      next()
    }
  }
})

export default router