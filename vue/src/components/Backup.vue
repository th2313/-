<template>
  <div class="backup-container">
    <!-- 顶部标题区域 -->
    <div class="backup-header">
      <div class="header-content">
        <h1>数据备份管理</h1>
        <p>系统数据导出与备份，保障您的数据安全</p>
      </div>
      <div class="header-icon">
        <div class="icon-wrapper">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M19 11H5M5 11L9 7M5 11L9 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M5 21H19C20.1046 21 21 20.1046 21 19V5C21 3.89543 20.1046 3 19 3H5C3.89543 3 3 3.89543 3 5V19C3 20.1046 3.89543 21 5 21Z" stroke="currentColor" stroke-width="2"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- 备份选项卡片 -->
    <div class="backup-cards">
      <!-- 全量备份 -->
      <div class="backup-card card-primary">
        <div class="card-decoration"></div>
        <div class="card-header">
          <div class="card-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 15V3M12 3L8 7M12 3L16 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L2 21C2 21.5523 2.44772 22 3 22H21C21.5523 22 22 21.5523 22 21V17" stroke="currentColor" stroke-width="2"/>
              <path d="M18 15C18 12.7909 16.2091 11 14 11C13.4835 11 12.9946 11.1208 12.56 11.3373C11.7277 11.7589 11.1429 12.5812 11.0376 13.5441C10.0344 13.8299 9.25 14.7491 9.25 15.875C9.25 17.1857 10.3143 18.25 11.625 18.25H18C19.3807 18.25 20.5 17.1307 20.5 15.75C20.5 14.3693 19.3807 13.25 18 13.25C17.8619 13.25 17.7262 13.2603 17.5938 13.2802C17.2307 11.7369 15.8278 10.5833 14.125 10.5833C12.067 10.5833 10.3958 12.2545 10.3958 14.3125" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h3>全量数据备份</h3>
          <el-tag type="success" size="small">推荐</el-tag>
        </div>
        <p class="card-description">导出系统中所有模块的完整数据，确保数据完整性</p>
        <div class="card-actions">
          <el-button type="primary" @click="handleFullBackup" :loading="loading.full" class="action-button">
            <el-icon><Download /></el-icon>
            导出全量数据
          </el-button>
        </div>
      </div>

      <!-- 模块选择备份 -->
      <div class="backup-card card-warning">
        <div class="card-decoration"></div>
        <div class="card-header">
          <div class="card-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 11L12 14L22 4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M21 12V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H16" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h3>模块选择备份</h3>
          <el-tag type="warning" size="small">灵活</el-tag>
        </div>
        <p class="card-description">选择特定模块进行数据导出，节省存储空间</p>
        
        <div class="module-selection">
          <div class="selection-title">选择备份模块：</div>
          <el-checkbox-group v-model="selectedModules" class="module-checkboxes">
            <div class="checkbox-row">
              <el-checkbox label="user">用户数据</el-checkbox>
              <el-checkbox label="patient">患者信息</el-checkbox>
              <el-checkbox label="doctor">医生信息</el-checkbox>
            </div>
            <div class="checkbox-row">
              <el-checkbox label="department">科室信息</el-checkbox>
              <el-checkbox label="schedule">排班信息</el-checkbox>
              <el-checkbox label="drug">药品信息</el-checkbox>
            </div>
            <div class="checkbox-row">
              <el-checkbox label="medical">检查项目</el-checkbox>
              <el-checkbox label="record">电子病历</el-checkbox>
              <el-checkbox label="template">病历模板</el-checkbox>
            </div>
          </el-checkbox-group>
        </div>

        <div class="card-actions">
          <el-button type="warning" @click="handleSelectiveBackup" :disabled="selectedModules.length === 0" :loading="loading.selective" class="action-button">
            <el-icon><Select /></el-icon>
            导出选中模块
          </el-button>
        </div>
      </div>

      <!-- 按时间备份 -->
      <div class="backup-card card-info">
        <div class="card-decoration"></div>
        <div class="card-header">
          <div class="card-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
              <path d="M12 7V12L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <h3>时间范围备份</h3>
          <el-tag type="info" size="small">高级</el-tag>
        </div>
        <p class="card-description">按时间范围导出数据，便于数据追溯</p>
        
        <div class="time-range">
          <div class="time-title">选择时间范围：</div>
          <el-date-picker
            v-model="timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
            class="custom-date-picker"
          />
        </div>

        <div class="card-actions">
          <el-button type="success" @click="handleTimeBackup" :disabled="!timeRange" :loading="loading.time" class="action-button">
            <el-icon><Calendar /></el-icon>
            按时间导出
          </el-button>
        </div>
      </div>
    </div>

    <!-- 备份历史记录 -->
    <div class="backup-history">
      <div class="history-header">
        <div class="history-title">
          <h3>备份历史记录</h3>
          <p>查看和管理历史备份文件</p>
        </div>
        <el-button type="primary" text @click="refreshHistory" class="refresh-button">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
      
      <el-table :data="backupHistory" v-loading="loading.history" class="history-table" empty-text="暂无备份记录">
        <el-table-column prop="fileName" label="文件名" min-width="200">
          <template #default="{ row }">
            <div class="file-info">
              <div class="file-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M14 2V8H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M16 13H8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <path d="M16 17H8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <path d="M10 9H9H8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </div>
              <span class="file-name">{{ row.fileName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="backupTime" label="备份时间" width="180" />
        <el-table-column prop="dataSize" label="数据大小" width="120">
          <template #default="{ row }">
            <span class="data-size">{{ row.dataSize }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="moduleCount" label="模块数量" width="100">
          <template #default="{ row }">
            <span class="module-count">{{ row.moduleCount }} 个</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button size="small" @click="downloadBackup(row)" class="action-download">
                <el-icon><Download /></el-icon>
                下载
              </el-button>
              <el-button size="small" @click="deleteBackup(row)" class="action-delete">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Download,
  Select,
  Calendar,
  Refresh,
  Document,
  Delete
} from '@element-plus/icons-vue'

// 响应式数据
const selectedModules = ref([])
const timeRange = ref('')
const loading = reactive({
  full: false,
  selective: false,
  time: false,
  history: false
})

const backupHistory = ref([
  {
    fileName: 'backup_full_20241201.json',
    backupTime: '2024-12-01 10:30:00',
    dataSize: '2.3 MB',
    moduleCount: 9
  },
  {
    fileName: 'backup_selective_20241128.json',
    backupTime: '2024-11-28 15:20:00',
    dataSize: '1.1 MB',
    moduleCount: 4
  }
])

// 备份功能实现
const handleFullBackup = async () => {
  loading.full = true
  try {
    const backupData = await exportAllData()
    downloadJSON(backupData, `hospital_backup_full_${getCurrentTime()}.json`)
    ElMessage.success('全量数据备份成功')
    addToHistory('全量备份', backupData)
  } catch (error) {
    ElMessage.error('备份失败: ' + error.message)
  } finally {
    loading.full = false
  }
}

const handleSelectiveBackup = async () => {
  loading.selective = true
  try {
    const backupData = await exportSelectedModules(selectedModules.value)
    downloadJSON(backupData, `hospital_backup_selective_${getCurrentTime()}.json`)
    ElMessage.success('选择性备份成功')
    addToHistory('选择性备份', backupData, selectedModules.value.length)
  } catch (error) {
    ElMessage.error('备份失败: ' + error.message)
  } finally {
    loading.selective = false
  }
}

const handleTimeBackup = async () => {
  loading.time = true
  try {
    const [start, end] = timeRange.value
    const backupData = await exportByTimeRange(start, end)
    downloadJSON(backupData, `hospital_backup_time_${getCurrentTime()}.json`)
    ElMessage.success('时间范围备份成功')
    addToHistory('时间范围备份', backupData)
  } catch (error) {
    ElMessage.error('备份失败: ' + error.message)
  } finally {
    loading.time = false
  }
}

// 数据导出函数
const exportAllData = async () => {
  const [
    users,
    patients,
    doctors,
    departments,
    schedules,
    drugs,
    medicalItems,
    records,
    templates
  ] = await Promise.all([
    fetchData('/tUser/list'),
    fetchData('/patientinfo'),
    fetchData('/doctorinfo/all'),
    fetchData('/departinfo/list'),
    fetchData('/doctorSchedule/list'),
    fetchData('/drugs'),
    fetchData('/api/fmeditem/list'),
    fetchData('/medrecordinfo/list'),
    fetchData('/templateinfo/list')
  ])

  return {
    metadata: {
      exportTime: new Date().toISOString(),
      system: '医院信息管理系统',
      version: '1.0',
      moduleCount: 9
    },
    users: users.data || users,
    patients: patients.data || patients,
    doctors: doctors.data || doctors,
    departments: departments.data || departments,
    schedules: schedules.data || schedules,
    drugs: drugs.data || drugs,
    medicalItems: medicalItems.data || medicalItems,
    records: records.data || records,
    templates: templates.data || templates
  }
}

const exportSelectedModules = async (modules) => {
  const requests = {}
  const moduleMap = {
    user: '/tUser/list',
    patient: '/patientinfo',
    doctor: '/doctorinfo/all',
    department: '/departinfo/list',
    schedule: '/doctorSchedule/list',
    drug: '/drugs',
    medical: '/api/fmeditem/list',
    record: '/medrecordinfo/list',
    template: '/templateinfo/list'
  }

  for (const module of modules) {
    if (moduleMap[module]) {
      requests[module] = fetchData(moduleMap[module])
    }
  }

  const results = await Promise.all(Object.values(requests))
  const backupData = {
    metadata: {
      exportTime: new Date().toISOString(),
      system: '医院信息管理系统',
      version: '1.0',
      moduleCount: modules.length,
      modules: modules
    }
  }

  let index = 0
  for (const module of modules) {
    backupData[module + 's'] = results[index].data || results[index]
    index++
  }

  return backupData
}

const exportByTimeRange = async (startDate, endDate) => {
  // 这里可以根据时间范围过滤数据
  // 由于现有接口不支持时间筛选，这里先导出全量数据
  // 实际项目中可以在后端添加时间筛选接口
  ElMessage.warning('时间范围筛选功能需要后端支持，当前导出全量数据')
  return await exportAllData()
}

// 工具函数
const fetchData = async (url) => {
  const response = await fetch(`http://localhost:8080${url}`)
  if (!response.ok) {
    throw new Error(`请求失败: ${response.status}`)
  }
  return await response.json()
}

const downloadJSON = (data, filename) => {
  const blob = new Blob([JSON.stringify(data, null, 2)], { 
    type: 'application/json' 
  })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

const getCurrentTime = () => {
  return new Date().toISOString().replace(/[:.]/g, '-').split('T')[0]
}

const addToHistory = (type, data, moduleCount = 9) => {
  const size = new Blob([JSON.stringify(data)]).size
  const sizeStr = (size / 1024 / 1024).toFixed(2) + ' MB'
  
  backupHistory.value.unshift({
    fileName: `backup_${type.toLowerCase()}_${getCurrentTime()}.json`,
    backupTime: new Date().toLocaleString(),
    dataSize: sizeStr,
    moduleCount: moduleCount
  })
}

const refreshHistory = () => {
  loading.history = true
  setTimeout(() => {
    // 这里可以调用API获取真实的备份历史
    loading.history = false
    ElMessage.success('已刷新')
  }, 1000)
}

const downloadBackup = (record) => {
  ElMessage.info(`下载备份: ${record.fileName}`)
}

const deleteBackup = async (record) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除备份文件 "${record.fileName}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    backupHistory.value = backupHistory.value.filter(item => item.fileName !== record.fileName)
    ElMessage.success('删除成功')
  } catch {
    // 用户取消删除
  }
}

onMounted(() => {
  // 初始化时可以加载备份历史
})
</script>

<style scoped>
.backup-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #f8fafc;
  min-height: 100vh;
}

.backup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px 32px;
  color: white;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
}

.header-content h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
}

.header-content p {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.header-icon .icon-wrapper {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon svg {
  color: white;
}

.backup-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.backup-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.backup-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.card-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
}

.card-primary .card-decoration {
  background: linear-gradient(90deg, #3498db, #2ecc71);
}

.card-warning .card-decoration {
  background: linear-gradient(90deg, #f39c12, #e74c3c);
}

.card-info .card-decoration {
  background: linear-gradient(90deg, #9b59b6, #3498db);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.card-primary .card-icon {
  background: rgba(52, 152, 219, 0.1);
  color: #3498db;
}

.card-warning .card-icon {
  background: rgba(243, 156, 18, 0.1);
  color: #f39c12;
}

.card-info .card-icon {
  background: rgba(155, 89, 182, 0.1);
  color: #9b59b6;
}

.card-header h3 {
  margin: 0;
  flex: 1;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.card-description {
  color: #7f8c8d;
  margin-bottom: 20px;
  line-height: 1.5;
}

.module-selection, .time-range {
  margin-bottom: 20px;
}

.selection-title, .time-title {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 12px;
  font-weight: 500;
}

.module-checkboxes {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.checkbox-row {
  display: flex;
  gap: 16px;
}

.custom-date-picker {
  border-radius: 8px;
}

.card-actions {
  text-align: center;
}

.action-button {
  width: 100%;
  border-radius: 8px;
  padding: 12px;
  font-weight: 500;
}

.backup-history {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.history-title h3 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.history-title p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.refresh-button {
  border-radius: 8px;
  padding: 8px 16px;
}

.history-table {
  border-radius: 8px;
  overflow: hidden;
}

.file-info {
  display: flex;
  align-items: center;
}

.file-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: rgba(52, 152, 219, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: #3498db;
}

.file-name {
  font-weight: 500;
}

.data-size {
  font-weight: 500;
  color: #2c3e50;
}

.module-count {
  font-weight: 500;
  color: #2c3e50;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.action-download, .action-delete {
  border-radius: 6px;
}

.action-download {
  color: #3498db;
  border-color: #3498db;
}

.action-download:hover {
  background: #3498db;
  color: white;
}

.action-delete {
  color: #e74c3c;
  border-color: #e74c3c;
}

.action-delete:hover {
  background: #e74c3c;
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .backup-container {
    padding: 16px;
  }
  
  .backup-header {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }
  
  .header-icon {
    margin-top: 16px;
  }
  
  .backup-cards {
    grid-template-columns: 1fr;
  }
  
  .checkbox-row {
    flex-direction: column;
    gap: 8px;
  }
  
  .history-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .refresh-button {
    margin-top: 12px;
  }
}
</style>