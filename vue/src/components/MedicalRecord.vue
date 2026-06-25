<template>
  <div class="medical-record-management">
    <!-- 标题区域 -->
    <h2 class="page-title">
      <el-icon class="title-icon"><Document /></el-icon>
      电子病历管理
    </h2>

    <!-- 操作按钮区域 -->
    <div class="operation-area">
      <el-button 
        type="primary" 
        @click="handleAdd" 
        class="add-btn"
      >
        <div class="btn-content">
          <el-icon class="btn-icon"><Plus /></el-icon>
          <span class="btn-text">新增病历</span>
        </div>
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-input
        v-model="searchParams.keyword"
        placeholder="搜索患者ID或姓名"
        class="search-input"
        clearable
        @keyup.enter="getMedicalRecords"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="getMedicalRecords" class="search-btn">
        <el-icon><Search /></el-icon>
        查询
      </el-button>
      <el-button type="default" @click="handleReset" class="reset-btn">重置</el-button>
    </div>

    <!-- 搜索状态提示 -->
    <div v-if="isSearching" class="search-status">
      <el-alert
        :title="`搜索『${searchParams.keyword}』，共找到 ${medicalRecords.length} 条记录`"
        type="info"
        :closable="false"
        show-icon
      >
        <template #action>
          <el-button type="primary" text @click="handleReset">显示全部</el-button>
        </template>
      </el-alert>
    </div>

    <!-- 病历表格和分页容器 -->
    <div class="table-container">
      <div class="medical-record-table">
        <el-table 
          :data="currentPageData" 
          border 
          stripe 
          style="width: 100%"
          v-loading="loading"
          :cell-style="{ padding: '8px 12px', lineHeight: '1.4' }"
          :header-cell-style="{ background: '#f5f7fa', padding: '12px', fontWeight: '600' }"
          size="small"
        >
          <el-table-column type="index" label="序号" width="60" align="center" fixed="left" />
          
          <el-table-column prop="hspID" label="患者ID" align="center" min-width="120" show-overflow-tooltip>
            <template #header>
              <el-icon><User /></el-icon>
              患者ID
            </template>
            <template #default="scope">
              <span class="patient-id">{{ scope.row.hspID }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="患者姓名" align="center" min-width="120" show-overflow-tooltip>
            <template #header>
              <el-icon><Avatar /></el-icon>
              患者姓名
            </template>
            <template #default="scope">
              <span class="patient-name">{{ getPatientName(scope.row.hspID) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="主诉" label="主诉" min-width="180" show-overflow-tooltip>
            <template #header>
              <el-icon><ChatDotRound /></el-icon>
              主诉
            </template>
            <template #default="scope">
              <span class="complaint-text">{{ scope.row.主诉 }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="现病史" label="现病史" min-width="250" show-overflow-tooltip>
            <template #header>
              <el-icon><Notebook /></el-icon>
              现病史
            </template>
          </el-table-column>
          
          <el-table-column prop="初步诊断" label="初步诊断" min-width="180" show-overflow-tooltip>
            <template #header>
              <el-icon><FirstAidKit /></el-icon>
              初步诊断
            </template>
            <template #default="scope">
              <el-tag type="warning" class="diagnosis-tag">
                {{ scope.row.初步诊断 }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="creationDate" label="创建时间" width="140" align="center">
            <template #header>
              <el-icon><Clock /></el-icon>
              创建时间
            </template>
            <template #default="scope">
              <span class="date-text">{{ formatDate(scope.row.creationDate) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="240" align="center" fixed="right">
            <template #header>
              <el-icon><Operation /></el-icon>
              操作
            </template>
            <template #default="scope">
              <div class="operation-btn-group">
                <el-button type="primary" link size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="info" link size="small" @click="handleView(scope.row)">查看</el-button>
                <el-button type="success" link size="small" @click="handleSaveAsTemplate(scope.row)">存为模板</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 空状态提示 -->
        <div v-if="currentPageData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无电子病历数据" />
        </div>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-area" v-if="medicalRecords.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="searchTotal"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑病历弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="formType === 'add' ? '新增电子病历' : '编辑电子病历'"
      width="850px"
      :before-close="handleClose"
      class="medical-record-form-dialog"
    >
      <div class="form-container">
        <!-- 患者信息查询区（仅新增时显示） -->
        <div v-if="formType === 'add'" class="patient-query-section">
          <div class="section-title">患者信息查询</div>
          <div class="query-form">
            <el-input
              v-model="queryKeyword"
              placeholder="输入患者ID或姓名查询"
              clearable
              class="query-input"
              @keyup.enter="queryPatientInfo"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button 
              type="primary" 
              @click="queryPatientInfo"
              :loading="queryLoading"
              class="query-btn"
            >
              查询
            </el-button>
          </div>

          <!-- 患者信息回显区 -->
          <div 
            v-if="showPatientInfo" 
            class="patient-info-display"
          >
            <el-descriptions :column="4" border :size="'small'">
              <el-descriptions-item label="患者ID">
                <span class="info-value">{{ patientInfo.hspID || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="姓名">
                <span class="info-value">{{ patientInfo.name || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="性别">
                <el-tag type="info">{{ patientInfo.gender || '-' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="出生日期">
                <span class="info-value">{{ formatDate(patientInfo.birthday) || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="科室">
                <el-tag type="success">{{ patientInfo.departName || '-' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="主治医生">
                <span class="info-value">{{ patientInfo.doctorName || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="联系电话">
                <span class="info-value">{{ patientInfo.contactsPhone || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="入院时间">
                <span class="info-value">{{ formatDate(patientInfo.hspTime) || '-' }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>

        <!-- 模板选择区 -->
        <div v-if="formType === 'add'" class="template-section">
          <div class="section-title">模板选择</div>
          <el-select 
            v-model="selectedTemplateId" 
            placeholder="选择模板快速创建"
            @change="loadTemplate"
            clearable
            class="template-select"
          >
            <el-option 
              v-for="tpl in templates" 
              :key="tpl.templateID" 
              :label="tpl.templateID" 
              :value="tpl.templateID"
            />
          </el-select>
        </div>

        <!-- 病历表单 -->
        <el-form
          ref="medicalRecordForm"
          :model="formData"
          label-width="100px"
          :rules="formRules"
          class="medical-record-form"
          label-position="left"
        >
          <!-- 患者姓名字段（不可编辑） -->
          <el-form-item label="患者姓名" class="required">
            <el-input 
              v-model="formData.name" 
              placeholder="请先查询患者信息"
              :disabled="true" 
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="主诉" prop="主诉" class="required">
            <el-input 
              v-model="formData.主诉" 
              placeholder="请输入主诉" 
              class="form-input"
              :class="{ 'error': formErrors.主诉 }"
            />
            <div v-if="formErrors.主诉" class="error-message">{{ formErrors.主诉 }}</div>
          </el-form-item>
          
          <el-form-item label="现病史">
            <el-input 
              v-model="formData.现病史" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入现病史"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="既往史">
            <el-input 
              v-model="formData.既往史" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入既往史"
              class="form-input"
            />
          </el-form-item>
          
          <div class="form-row">
            <el-form-item label="婚育史" class="form-item">
              <el-input 
                v-model="formData.婚育史" 
                placeholder="请输入婚育史" 
                class="form-input"
              />
            </el-form-item>
            
            <el-form-item label="月经史" class="form-item">
              <el-input 
                v-model="formData.月经史" 
                placeholder="请输入月经史" 
                class="form-input"
              />
            </el-form-item>
          </div>

          <el-form-item label="家族史">
            <el-input 
              v-model="formData.家族史" 
              placeholder="请输入家族史" 
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="体格检查">
            <el-input 
              v-model="formData.体格检查" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入体格检查结果"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="专科检查">
            <el-input 
              v-model="formData.专科检查" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入专科检查结果"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="辅助检查">
            <el-input 
              v-model="formData.辅助检查" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入辅助检查结果"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="鉴别诊断">
            <el-input 
              v-model="formData.鉴别诊断" 
              type="textarea" 
              :rows="2" 
              placeholder="请输入鉴别诊断"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="初步诊断" class="required">
            <el-input 
              v-model="formData.初步诊断" 
              type="textarea" 
              :rows="2" 
              placeholder="请输入初步诊断"
              class="form-input"
              :class="{ 'error': formErrors.初步诊断 }"
            />
            <div v-if="formErrors.初步诊断" class="error-message">{{ formErrors.初步诊断 }}</div>
          </el-form-item>
          
          <el-form-item label="诊疗计划" class="required">
            <el-input 
              v-model="formData.诊疗计划" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入诊疗计划"
              class="form-input"
              :class="{ 'error': formErrors.诊疗计划 }"
            />
            <div v-if="formErrors.诊疗计划" class="error-message">{{ formErrors.诊疗计划 }}</div>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleClose" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="saveLoading" class="submit-btn">
            {{ formType === 'add' ? '确定' : '更新' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 存为模板弹窗 -->
    <el-dialog
      v-model="saveAsTemplateVisible"
      title="保存为模板"
      width="450px"
      :before-close="() => saveAsTemplateVisible = false"
      class="template-save-dialog"
    >
      <div class="form-container">
        <el-form-item label="模板ID" prop="templateID" class="required">
          <el-input 
            v-model="templateForm.templateID" 
            placeholder="输入模板ID"
            class="form-input"
            :class="{ 'error': formErrors.templateID }"
          />
          <div v-if="formErrors.templateID" class="error-message">{{ formErrors.templateID }}</div>
        </el-form-item>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="saveAsTemplateVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="confirmSaveAsTemplate" class="submit-btn">确认保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看病历弹窗 -->
    <el-dialog
      v-model="viewVisible"
      title="查看电子病历"
      width="850px"
      :before-close="() => viewVisible = false"
      class="medical-record-view-dialog"
    >
      <div class="view-container">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="患者ID" span="1">
            <span class="view-value">{{ viewData.hspID || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="患者姓名" span="1">
            <span class="view-value">{{ getPatientName(viewData.hspID) || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="主诉" span="2">
            <span class="view-value">{{ viewData.主诉 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="现病史" span="2">
            <span class="view-value">{{ viewData.现病史 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="既往史" span="2">
            <span class="view-value">{{ viewData.既往史 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="婚育史" span="1">
            <span class="view-value">{{ viewData.婚育史 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="月经史" span="1">
            <span class="view-value">{{ viewData.月经史 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="家族史" span="2">
            <span class="view-value">{{ viewData.家族史 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="体格检查" span="2">
            <span class="view-value">{{ viewData.体格检查 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="专科检查" span="2">
            <span class="view-value">{{ viewData.专科检查 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="辅助检查" span="2">
            <span class="view-value">{{ viewData.辅助检查 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="鉴别诊断" span="2">
            <span class="view-value">{{ viewData.鉴别诊断 || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="初步诊断" span="2">
            <el-tag type="warning" class="view-diagnosis">{{ viewData.初步诊断 || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="诊疗计划" span="2">
            <span class="view-value">{{ viewData.诊疗计划 || '-' }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Plus, Search, Document, User, Avatar, ChatDotRound,
  Notebook, FirstAidKit, Clock, Operation
} from '@element-plus/icons-vue'
import axios from 'axios'

// 配置后端接口基础路径
axios.defaults.baseURL = 'http://localhost:8080'

// 统一从 Result<T> 响应中提取数据的辅助函数
const unwrapResult = (response) => {
  if (response.data && response.data.code === 200) {
    return response.data.data
  }
  // 兼容旧格式（直接返回数据）
  return response.data
}

// 状态管理
const medicalRecords = ref([])
const templates = ref([])
const searchParams = reactive({
  keyword: ''
})
const isSearching = ref(false)
const loading = ref(false)
const saveLoading = ref(false)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

// 患者信息查询相关
const queryKeyword = ref('')
const patientInfo = ref({})
const showPatientInfo = ref(false)
const queryLoading = ref(false)
const patientNameMap = ref({})
const patientNameCache = ref({})

// 表单控制
const dialogVisible = ref(false)
const viewVisible = ref(false)
const saveAsTemplateVisible = ref(false)
const formType = ref('add')
const formData = ref({
  hspID: '',
  name: '',
  主诉: '',
  现病史: '',
  既往史: '',
  婚育史: '',
  月经史: '',
  家族史: '',
  体格检查: '',
  专科检查: '',
  辅助检查: '',
  鉴别诊断: '',
  初步诊断: '',
  诊疗计划: ''
})
const viewData = ref({})
const selectedTemplateId = ref('')
const templateForm = ref({
  templateID: ''
})
const medicalRecordForm = ref(null)

// 表单错误信息
const formErrors = reactive({
  主诉: '',
  初步诊断: '',
  诊疗计划: '',
  templateID: ''
})

// 表单验证规则
const formRules = reactive({
  主诉: [
    { required: true, message: '主诉不能为空', trigger: 'blur' }
  ],
  初步诊断: [
    { required: true, message: '初步诊断不能为空', trigger: 'blur' }
  ],
  诊疗计划: [
    { required: true, message: '诊疗计划不能为空', trigger: 'blur' }
  ]
})

// 计算属性
const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return medicalRecords.value.slice(start, end)
})

const searchTotal = computed(() => {
  return medicalRecords.value.length
})

// 初始化加载数据
onMounted(() => {
  getMedicalRecords()
  getTemplates()
})

// 获取病历列表
const getMedicalRecords = async () => {
  loading.value = true
  try {
    const keyword = searchParams.keyword.trim()
    let records = []

    if (!keyword) {
      const res = await axios.get('/medrecordinfo/list')
      records = res.data?.code === 200 ? res.data.data || [] : []
    } else {
      const idRes = await axios.get('/medrecordinfo/list', {
        params: { hspID: keyword }
      })
      records = idRes.data?.code === 200 ? idRes.data.data || [] : []

      if (records.length === 0) {
        const nameRes = await axios.get('/patientinfo/name', {
          params: { name: keyword }
        })
        const patients = unwrapResult(nameRes) || []

        if (!Array.isArray(patients) || patients.length === 0) {
          ElMessage.info(`未找到姓名包含「${keyword}」的患者`)
          records = []
        } else {
          const hspIds = patients.map(p => p.hspID).join(',')
          const recordRes = await axios.get('/medrecordinfo/listByHspIDs', {
            params: { hspIDs: hspIds }
          })
          records = recordRes.data?.code === 200 ? recordRes.data.data || [] : []

          if (records.length === 0) {
            ElMessage.info(`找到 ${patients.length} 名患者，但未查询到他们的病历`)
          }
        }
      }
    }

    if (records.length > 0) {
      const hspIds = [...new Set(records.map(r => r.hspID))]
      patientNameMap.value = await getPatientNamesBatch(hspIds)
    }

    medicalRecords.value = records
    isSearching.value = !!keyword
    currentPage.value = 1
  } catch (err) {
    console.error('获取病历列表错误:', err)
    ElMessage.error('服务器异常：' + (err.response?.data?.message || err.message))
  } finally {
    loading.value = false
  }
}

// 批量获取患者姓名
const getPatientNamesBatch = async (hspIds) => {
  const nameMap = {}
  if (!hspIds || hspIds.length === 0) return nameMap

  for (const id of hspIds) {
    if (patientNameCache.value[id]) {
      nameMap[id] = patientNameCache.value[id]
      continue
    }
    try {
      const res = await axios.get(`/patientinfo/${id}`)
      const patient = unwrapResult(res)
      if (patient && patient.name) {
        nameMap[id] = patient.name
        patientNameCache.value[id] = patient.name
      } else {
        nameMap[id] = '未知姓名'
      }
    } catch (err) {
      console.error(`查询患者${id}姓名失败:`, err)
      nameMap[id] = '未知姓名'
    }
  }
  return nameMap
}

// 获取模板列表
const getTemplates = async () => {
  try {
    const res = await axios.get('/medrecordinfo/templates')
    if (res.data?.code === 200) {
      templates.value = res.data.data || []
    } else {
      ElMessage.error(res.data?.msg || '获取模板失败')
    }
  } catch (err) {
    console.error('获取模板列表错误:', err)
    ElMessage.error('服务器异常：' + (err.response?.data?.msg || err.message))
  }
}

// 查询患者信息
const queryPatientInfo = async () => {
  const keyword = queryKeyword.value.trim()
  if (!keyword) {
    ElMessage.warning('请输入患者ID或姓名')
    return
  }

  queryLoading.value = true
  try {
    let patient = null

    // 先尝试按住院编号精确查询
    try {
      const idRes = await axios.get(`/patientinfo/${keyword}`)
      const data = unwrapResult(idRes)
      if (data && data.hspID) {
        patient = data
      }
    } catch (idErr) {
      // 404 或其他错误，继续尝试姓名查询
    }

    // 如果精确查询没找到，尝试按姓名模糊查询
    if (!patient) {
      const nameRes = await axios.get('/patientinfo/name', {
        params: { name: keyword }
      })
      const patients = unwrapResult(nameRes)

      if (!patients || !Array.isArray(patients) || patients.length === 0) {
        ElMessage.warning(`未找到姓名包含「${keyword}」的患者`)
        resetDialog()
        return
      } else if (patients.length === 1) {
        patient = patients[0]
      } else {
        ElMessage.info(`找到 ${patients.length} 名匹配患者，默认选择第一个`)
        patient = patients[0]
      }
    }

    patientInfo.value = patient
    showPatientInfo.value = true
    formData.value.hspID = patient.hspID
    formData.value.name = patient.name
    patientNameCache.value[patient.hspID] = patient.name

  } catch (err) {
    console.error('查询患者信息错误:', err)
    ElMessage.error('查询失败：' + (err.response?.data?.msg || err.message))
    resetDialog()
  } finally {
    queryLoading.value = false
  }
}

// 表单验证
const validateForm = () => {
  clearFormErrors()
  let isValid = true

  if (!formData.value.主诉.trim()) {
    formErrors.主诉 = '请输入主诉'
    isValid = false
  }

  if (!formData.value.初步诊断.trim()) {
    formErrors.初步诊断 = '请输入初步诊断'
    isValid = false
  }

  if (!formData.value.诊疗计划.trim()) {
    formErrors.诊疗计划 = '请输入诊疗计划'
    isValid = false
  }

  return isValid
}

const validateTemplateForm = () => {
  clearFormErrors()
  let isValid = true

  if (!templateForm.value.templateID.trim()) {
    formErrors.templateID = '请输入模板ID'
    isValid = false
  }

  return isValid
}

// 清除表单错误信息
const clearFormErrors = () => {
  Object.keys(formErrors).forEach(key => {
    formErrors[key] = ''
  })
}

// 重置弹窗状态
const resetDialog = () => {
  queryKeyword.value = ''
  patientInfo.value = {}
  showPatientInfo.value = false
  formData.value = {
    hspID: '',
    name: '',
    主诉: '',
    现病史: '',
    既往史: '',
    婚育史: '',
    月经史: '',
    家族史: '',
    体格检查: '',
    专科检查: '',
    辅助检查: '',
    鉴别诊断: '',
    初步诊断: '',
    诊疗计划: ''
  }
  selectedTemplateId.value = ''
  clearFormErrors()
  if (medicalRecordForm.value && typeof medicalRecordForm.value.clearValidate === 'function') {
    medicalRecordForm.value.clearValidate()
  }
}

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 加载模板
const loadTemplate = async () => {
  if (!selectedTemplateId.value) {
    ElMessage.warning('请选择模板')
    return
  }
  if (!formData.value.hspID) {
    ElMessage.warning('请先查询患者信息')
    return
  }
  
  try {
    const res = await axios.post('/medrecordinfo/createFromTemplate', {}, {
      params: {
        hspID: formData.value.hspID,
        templateID: selectedTemplateId.value
      }
    })
    
    if (res.data?.code === 200) {
      const templateData = res.data.data || {}
      formData.value = { ...formData.value, ...templateData }
      ElMessage.success('模板加载成功')
    } else {
      ElMessage.error(res.data?.msg || '加载模板失败')
    }
  } catch (err) {
    console.error('加载模板错误:', err)
    ElMessage.error('服务器异常：' + (err.response?.data?.msg || err.message))
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!medicalRecordForm.value) {
    ElMessage.error('表单初始化失败，请重试')
    return
  }
  
  if (!validateForm()) {
    ElMessage.warning('请完善表单信息')
    return
  }
  
  saveLoading.value = true
  
  try {
    const submitData = { ...formData.value }
    delete submitData.name
    
    const res = await axios.post('/medrecordinfo/save', submitData)
    
    if (res.data?.code === 200) {
      ElMessage.success(`${formType === 'add' ? '新增' : '编辑'}成功`)
      dialogVisible.value = false
      getMedicalRecords()
      if (formType.value === 'add') {
        resetDialog()
      }
    } else {
      ElMessage.error(res.data?.msg || '操作失败')
    }
  } catch (err) {
    console.error('保存病历错误:', err)
    ElMessage.error('服务器异常：' + (err.response?.data?.msg || err.message))
  } finally {
    saveLoading.value = false
  }
}

// 确认保存为模板
const confirmSaveAsTemplate = async () => {
  if (!validateTemplateForm()) {
    ElMessage.warning('请完善表单信息')
    return
  }
  
  try {
    const templateData = { ...formData.value }
    delete templateData.name
    
    const res = await axios.post('/medrecordinfo/saveAsTemplate', templateData, {
      params: { templateID: templateForm.value.templateID }
    })
    
    if (res.data?.code === 200) {
      ElMessage.success('保存为模板成功')
      saveAsTemplateVisible.value = false
      getTemplates()
    } else {
      ElMessage.error(res.data?.msg || '保存模板失败')
    }
  } catch (err) {
    console.error('保存为模板错误:', err)
    ElMessage.error('服务器异常：' + (err.response?.data?.msg || err.message))
  }
}

// 重置搜索
const handleReset = () => {
  searchParams.keyword = ''
  isSearching.value = false
  currentPage.value = 1
  getMedicalRecords()
}

// 新增病历
const handleAdd = () => {
  formType.value = 'add'
  dialogVisible.value = true
  nextTick(() => {
    resetDialog()
  })
}

// 编辑病历
const handleEdit = async (row) => {
  formType.value = 'edit'
  formData.value = { ...row }

  try {
    const res = await axios.get(`/patientinfo/${row.hspID}`)
    const patient = unwrapResult(res)
    if (patient && patient.name) {
      formData.value.name = patient.name
      patientNameCache.value[row.hspID] = patient.name
    } else {
      formData.value.name = '患者信息不存在'
      ElMessage.warning('患者信息已不存在，建议检查')
    }
  } catch (err) {
    formData.value.name = '未知姓名'
    console.error('编辑时查询患者信息失败:', err)
  }

  dialogVisible.value = true
  nextTick(() => {
    if (medicalRecordForm.value && typeof medicalRecordForm.value.clearValidate === 'function') {
      medicalRecordForm.value.clearValidate()
    }
  })
}

// 查看病历
const handleView = (row) => {
  viewData.value = { ...row }
  viewVisible.value = true
}

// 保存为模板
const handleSaveAsTemplate = (row) => {
  formData.value = { ...row }
  templateForm.value = { templateID: '' }
  saveAsTemplateVisible.value = true
}

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false
  if (medicalRecordForm.value && typeof medicalRecordForm.value.clearValidate === 'function') {
    medicalRecordForm.value.clearValidate()
  }
  if (formType.value === 'add') {
    resetDialog()
  }
}

// 工具函数：获取患者姓名
const getPatientName = (hspID) => {
  return patientNameMap.value[hspID] || '未知姓名'
}

// 工具函数：日期格式化
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString()
}
</script>

<style scoped>
/* 页面基础样式 */
.medical-record-management {
  padding: 20px;
  box-sizing: border-box;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 标题区域 */
.page-title {
  display: flex;
  align-items: center;
  font-size: 22px;
  color: #165DFF;
  margin: 0 0 16px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #e5e7eb;
}

.title-icon {
  margin-right: 12px;
  font-size: 24px;
}

/* 操作按钮区域 */
.operation-area {
  margin-bottom: 16px;
}

.add-btn {
  width: 120px;
  height: 36px;
  padding: 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  height: 100%;
}

.btn-icon {
  font-size: 14px;
  line-height: 1;
}

.btn-text {
  font-size: 14px;
  line-height: 1;
}

/* 搜索区域样式 */
.search-area {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.search-input {
  width: 400px;
  margin-right: 10px;
}

/* 搜索状态提示 */
.search-status {
  margin-bottom: 16px;
}

/* 表格容器 */
.table-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 表格样式优化 */
.medical-record-table {
  padding: 0;
}

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table .cell) {
  line-height: 1.4;
  word-break: break-word;
}

:deep(.el-table--small) {
  font-size: 12px;
}

/* 患者ID样式 */
.patient-id {
  font-weight: 500;
  color: #165DFF;
}

/* 患者姓名样式 */
.patient-name {
  font-weight: 500;
  color: #000000;
}

/* 主诉文本样式 */
.complaint-text {
  color: #61c3c8;
  box-shadow:#61c3c8 ;
  box-shadow: ;
}

/* 诊断标签样式 */
.diagnosis-tag {
  border: none;
  font-weight: 500;
  font-size: 12px;
}

/* 日期样式 */
.date-text {
  font-size: 12px;
  color: #909399;
}

/* 操作按钮组优化 */
.operation-btn-group {
  display: flex;
  justify-content: center;
  gap: 8px;
}

:deep(.el-button--small) {
  padding: 5px 8px;
  font-size: 12px;
}

/* 空状态样式 */
.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #909399;
}

/* 分页区域优化 */
.pagination-area {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background: #fafafa;
}

/* 弹窗表单样式 */
.medical-record-form-dialog,
.template-save-dialog,
.medical-record-view-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
  }
}

.form-container {
  padding: 10px 0;
}

/* 患者查询区域 */
.patient-query-section,
.template-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.section-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  font-size: 14px;
}

.query-form {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}

.query-input {
  flex: 1;
}

/* 患者信息显示 */
.patient-info-display {
  margin-top: 12px;
}

.info-value {
  font-weight: 500;
  color: #303133;
}

/* 模板选择 */
.template-select {
  width: 240px;
}

/* 病历表单 */
.medical-record-form {
  padding: 0;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 0;
}

.form-item {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-item.required .form-label::after {
  content: " *";
  color: #f56c6c;
}

:deep(.el-form-item) {
  margin-bottom: 15px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-input {
  width: 100%;
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  height: 18px;
}

:deep(.form-input.error .el-input__wrapper) {
  box-shadow: 0 0 0 1px #f56c6c inset;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.cancel-btn,
.submit-btn {
  min-width: 80px;
}

/* 查看病历样式 */
.view-container {
  padding: 10px 0;
}

.view-value {
  color: #303133;
  line-height: 1.6;
}

.view-diagnosis {
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-area {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    width: 100%;
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 15px;
  }
  
  .query-form {
    flex-direction: column;
  }
  
  .query-input {
    width: 100%;
  }
  
  .pagination-area {
    justify-content: center;
  }
  
  :deep(.el-pagination) {
    flex-wrap: wrap;
  }
}

/* 表格响应式优化 */
@media (max-width: 1200px) {
  .medical-record-table {
    overflow-x: auto;
  }
  
  :deep(.el-table) {
    min-width: 1000px;
  }
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-table__header) {
  font-weight: 600;
}

:deep(.el-table__empty-block) {
  background: white;
}
</style>