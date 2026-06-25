<template>
  <div class="login-container">
    <div class="login-form">
      <h2>医学信息管理系统</h2>
      
      <!-- 登录/注册切换 -->
      <div class="form-tabs">
        <button 
          :class="['tab-btn', { active: isLogin }]" 
          @click="switchToLogin"
        >
          登录
        </button>
        <button 
          :class="['tab-btn', { active: !isLogin }]" 
          @click="switchToRegister"
        >
          注册
        </button>
      </div>

      <!-- 登录表单 -->
      <form @submit.prevent="handleSubmit" v-if="isLogin">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="loginForm.uname" 
            type="text" 
            placeholder="请输入用户名"
            required
          >
        </div>
        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="loginForm.upassword" 
            type="password" 
            placeholder="请输入密码"
            required
          >
        </div>
        <button type="submit" :disabled="loading" class="submit-btn">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <!-- 注册表单 -->
      <form @submit.prevent="handleSubmit" v-else>
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="registerForm.uname" 
            type="text" 
            placeholder="请输入用户名"
            required
          >
        </div>
        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="registerForm.upassword" 
            type="password" 
            placeholder="请输入密码"
            required
          >
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            required
          >
        </div>
        <div class="form-group">
          <label>用户类型</label>
          <select v-model="registerForm.utype" class="form-select">
            <option value="普通用户">普通用户</option>
            <option value="管理员">管理员</option>
          </select>
        </div>
        <button type="submit" :disabled="loading" class="submit-btn">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>

      <div v-if="error" class="error-message">{{ error }}</div>
      <div v-if="success" class="success-message">{{ success }}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      isLogin: true, // 默认显示登录表单
      loading: false,
      error: '',
      success: '',
      loginForm: {
        uname: '',
        upassword: ''
      },
      registerForm: {
        uname: '',
        upassword: '',
        confirmPassword: '',
        utype: '普通用户'
      }
    }
  },
  mounted() {
    // 关键：删除组件内的自动跳转逻辑，登录状态由路由守卫统一处理
  },
  methods: {
    switchToLogin() {
      this.isLogin = true
      this.error = ''
      this.success = ''
    },
    
    switchToRegister() {
      this.isLogin = false
      this.error = ''
      this.success = ''
    },
    
    async handleSubmit() {
      if (this.isLogin) {
        await this.handleLogin()
      } else {
        await this.handleRegister()
      }
    },
    
    async handleLogin() {
      // 登录表单验证
      if (!this.loginForm.uname.trim()) {
        this.error = '请输入用户名'
        return
      }
      if (!this.loginForm.upassword) {
        this.error = '请输入密码'
        return
      }

      this.loading = true
      this.error = ''
      
      try {
        console.log('发送登录请求:', {
          uname: this.loginForm.uname,
          upassword: this.loginForm.upassword
        })

        const response = await fetch('http://localhost:8080/tUser/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            uname: this.loginForm.uname,
            upassword: this.loginForm.upassword
          })
        })
        
        const result = await response.json()
        console.log('登录响应:', result)
        
        if (result.code === 200) {
          // 登录成功：存储登录态
          localStorage.setItem('isLoggedIn', 'true')
          localStorage.setItem('userInfo', JSON.stringify(result.data))
          
          // 支持回跳（用户原本想访问的页面）
          const redirectPath = this.$route.query.redirect || '/patient'
          this.$router.push(redirectPath)
        } else {
          this.error = result.msg || '登录失败'
        }
      } catch (error) {
        console.error('登录错误:', error)
        this.error = '登录失败，请检查网络连接或后端服务'
      } finally {
        this.loading = false
      }
    },
    
    async handleRegister() {
      // 注册表单验证
      if (!this.registerForm.uname.trim()) {
        this.error = '请输入用户名'
        return
      }
      if (!this.registerForm.upassword) {
        this.error = '请输入密码'
        return
      }
      if (this.registerForm.upassword !== this.registerForm.confirmPassword) {
        this.error = '两次输入的密码不一致'
        return
      }
      if (this.registerForm.upassword.length < 6) {
        this.error = '密码长度至少6位'
        return
      }

      this.loading = true
      this.error = ''
      this.success = ''
      
      try {
        console.log('发送注册请求:', {
          uname: this.registerForm.uname,
          upassword: this.registerForm.upassword,
          utype: this.registerForm.utype
        })

        const response = await fetch('http://localhost:8080/tUser/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            uname: this.registerForm.uname,
            upassword: this.registerForm.upassword,
            utype: this.registerForm.utype
          })
        })
        
        const result = await response.json()
        console.log('注册响应:', result)
        
        if (result.code === 200) {
          this.success = '注册成功！请登录'
          // 清空注册表单
          this.registerForm = {
            uname: '',
            upassword: '',
            confirmPassword: '',
            utype: '普通用户'
          }
          // 自动切换到登录标签
          setTimeout(() => {
            this.isLogin = true
          }, 1500)
        } else {
          this.error = result.msg || '注册失败'
        }
      } catch (error) {
        console.error('注册错误:', error)
        this.error = '注册失败，请检查网络连接'
      } finally {
        this.loading = false
      }
    },

    // 新增：退出登录方法（可在其他页面调用，这里先定义）
    handleLogout() {
      // 清除登录状态
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('userInfo');
      // 跳转到登录页
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('src/assets/da96e6b8a09f88d9ee135cbc471e12b7.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(2px);
}

.login-form {
  background: rgba(255, 255, 255, 0.95);
  padding: 2.5rem;
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
  width: 400px;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.5rem;
}

.form-tabs {
  display: flex;
  margin-bottom: 2rem;
  border-bottom: 1px solid #e1e5e9;
}

.tab-btn {
  flex: 1;
  padding: 12px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: 600;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #555;
}

input, .form-select {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e1e5e9;
  border-radius: 6px;
  box-sizing: border-box;
  font-size: 1rem;
  transition: border-color 0.3s;
  background: rgba(255, 255, 255, 0.9);
}

input:focus, .form-select:focus {
  outline: none;
  border-color: #667eea;
  background: white;
}

.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.3s;
}

.submit-btn:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #e74c3c;
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9rem;
  background: rgba(231, 76, 60, 0.1);
  padding: 8px;
  border-radius: 4px;
}

.success-message {
  color: #52c41a;
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9rem;
  background: rgba(82, 196, 26, 0.1);
  padding: 8px;
  border-radius: 4px;
}
</style>	