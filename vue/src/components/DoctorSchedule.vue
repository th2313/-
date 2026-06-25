<template>
  <div class="doctor-schedule-management">
    <!-- 标题区域 -->
    <h2 class="page-title">
      <el-icon class="title-icon"><Calendar /></el-icon>
      医生排班管理
    </h2>

    <!-- 操作按钮区域 -->
    <div class="operation-area">
      <el-button 
        type="primary" 
        @click="showAddDialog" 
        class="add-btn"
      >
        <div class="btn-content">
          <el-icon class="btn-icon"><Plus /></el-icon>
          <span class="btn-text">新增排班</span>
        </div>
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-radio-group v-model="searchType" @change="handleSearchTypeChange" class="search-type">
        <el-radio-button label="depart">
          <el-icon><OfficeBuilding /></el-icon>
          按科室查询
        </el-radio-button>
        <el-radio-button label="doctor">
          <el-icon><User /></el-icon>
          按医生查询
        </el-radio-button>
      </el-radio-group>

      <div class="search-inputs">
        <!-- 按科室查询 -->
        <el-select 
          v-if="searchType === 'depart'"
          v-model="searchForm.departId" 
          placeholder="请选择科室"
          @change="handleDepartChange"
          :loading="departLoading"
          clearable
          class="search-select"
        >
          <el-option 
            v-for="depart in departments" 
            :key="depart.departID" 
            :label="depart.departName" 
            :value="depart.departID"
          />
        </el-select>

        <!-- 按医生查询 -->
        <el-select 
          v-if="searchType === 'doctor'"
          v-model="searchForm.doctorId" 
          placeholder="请选择医生"
          @change="handleDoctorChange"
          :loading="doctorLoading"
          clearable
          filterable
          class="search-select"
        >
          <el-option 
            v-for="doctor in allDoctors" 
            :key="doctor.doctorID" 
            :label="`${doctor.name} - ${getDepartName(doctor.departID)}`" 
            :value="doctor.doctorID"
          />
        </el-select>

        <el-date-picker 
          v-model="searchForm.date" 
          type="date" 
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          class="date-picker"
        />

        <el-button type="primary" @click="loadScheduleList" class="search-btn">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button type="default" @click="resetSearch" class="reset-btn">重置</el-button>
      </div>
    </div>

    <!-- 搜索状态提示 -->
    <div v-if="isSearching" class="search-status">
      <el-alert
        :title="searchStatusText"
        type="info"
        :closable="false"
        show-icon
      >
        <template #action>
          <el-button type="primary" text @click="resetSearch">显示全部</el-button>
        </template>
      </el-alert>
    </div>

    <!-- 排班表格和分页容器 -->
    <div class="table-container">
      <div class="schedule-table">
        <el-table 
          :data="currentPageData" 
          border 
          stripe 
          style="width: 100%"
          v-loading="listLoading"
          :cell-style="{ padding: '8px 12px', lineHeight: '1.4' }"
          :header-cell-style="{ background: '#f5f7fa', padding: '12px', fontWeight: '600' }"
          size="small"
        >
          <el-table-column type="index" label="序号" width="60" align="center" fixed="left" />
          <el-table-column prop="id" label="排班ID" width="80" align="center" show-overflow-tooltip />
          
          <el-table-column label="科室" width="150" show-overflow-tooltip>
            <template #default="scope">
              <div class="cell-with-icon">
                <el-icon><OfficeBuilding /></el-icon>
                <span>{{ getDepartName(scope.row.departId) }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="医生" width="150" show-overflow-tooltip>
            <template #default="scope">
              <div class="cell-with-icon">
                <el-icon><User /></el-icon>
                <span>{{ getDoctorName(scope.row.doctorId) }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="scheduleDate" label="排班日期" width="140" align="center">
            <template #default="scope">
              <el-tag type="info" class="date-tag">
                {{ formatDate(scope.row.scheduleDate) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="workTime" label="工作时间" width="100" align="center">
            <template #default="scope">
              <el-tag 
                :type="getWorkTimeType(scope.row.workTime)" 
                class="time-tag"
              >
                {{ scope.row.workTime }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="creationDate" label="创建时间" width="160" align="center">
            <template #default="scope">
              <span class="date-text">{{ formatDate(scope.row.creationDate) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="lastUpdateDate" label="更新时间" width="160" align="center">
            <template #default="scope">
              <span class="date-text">{{ formatDate(scope.row.lastUpdateDate) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="150" align="center" fixed="right">
            <template #default="scope">
              <div class="operation-btn-group">
                <el-button type="primary" link size="small" @click="showEditDialog(scope.row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="deleteSchedule(scope.row.id)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 空状态提示 -->
        <div v-if="currentPageData.length === 0 && !listLoading" class="empty-state">
          <el-empty description="暂无排班数据" />
        </div>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-area" v-if="filteredScheduleList.length > 0">
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

    <!-- 排班信息表单弹窗 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      :before-close="handleClose"
      class="schedule-form-dialog"
    >
      <div class="form-container">
        <!-- 编辑时显示ID -->
        <div v-if="!isAdd" class="readonly-field">
          <span class="field-label">排班ID</span>
          <span class="field-value">{{ form.id }}</span>
        </div>

        <div class="form-grid">
          <!-- 第一行：科室 + 医生 -->
          <div class="form-row">
            <div class="form-group required">
              <label class="form-label">科室</label>
              <el-select 
                v-model="form.departId" 
                placeholder="请选择科室"
                @change="handleFormDepartChange"
                :loading="departLoading"
                class="form-input"
                :class="{ 'error': formErrors.departId }"
              >
                <el-option 
                  v-for="depart in departments" 
                  :key="depart.departID" 
                  :label="depart.departName" 
                  :value="depart.departID"
                />
              </el-select>
              <div v-if="formErrors.departId" class="error-message">{{ formErrors.departId }}</div>
            </div>
            
            <div class="form-group required">
              <label class="form-label">医生</label>
              <el-select 
                v-model="form.doctorId" 
                placeholder="请选择医生"
                :loading="doctorLoading"
                class="form-input"
                :class="{ 'error': formErrors.doctorId }"
              >
                <el-option 
                  v-for="doctor in doctors" 
                  :key="doctor.doctorID" 
                  :label="doctor.name" 
                  :value="doctor.doctorID"
                />
              </el-select>
              <div v-if="formErrors.doctorId" class="error-message">{{ formErrors.doctorId }}</div>
            </div>
          </div>

          <!-- 第二行：排班日期 + 工作时间 -->
          <div class="form-row">
            <div class="form-group required">
              <label class="form-label">排班日期</label>
              <el-date-picker 
                v-model="form.scheduleDate" 
                type="date" 
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledPastDate"
                class="form-input"
                :class="{ 'error': formErrors.scheduleDate }"
              />
              <div v-if="formErrors.scheduleDate" class="error-message">{{ formErrors.scheduleDate }}</div>
            </div>
            
            <div class="form-group required">
              <label class="form-label">工作时间</label>
              <el-select 
                v-model="form.workTime" 
                placeholder="请选择工作时间" 
                class="form-input"
                :class="{ 'error': formErrors.workTime }"
              >
                <el-option label="上午" value="上午" />
                <el-option label="下午" value="下午" />
                <el-option label="全天" value="全天" />
              </el-select>
              <div v-if="formErrors.workTime" class="error-message">{{ formErrors.workTime }}</div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleClose" class="cancel-btn">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitForm" 
            :loading="btnLoading"
            class="submit-btn"
          >
            {{ isAdd.value ? '确定' : '更新' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Plus, Search, Calendar, OfficeBuilding, User 
} from '@element-plus/icons-vue';
import axios from 'axios';

// ========================= 基础配置 =========================
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.timeout = 5000;

// ========================= 状态管理 =========================
// 加载状态
const departLoading = ref(false);
const doctorLoading = ref(false);
const listLoading = ref(false);
const btnLoading = ref(false);

// 查询方式
const searchType = ref('depart');
const isSearching = ref(false);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);

// 筛选表单
const searchForm = reactive({
  departId: '',
  doctorId: '',
  date: ''
});

// 核心数据列表
const allScheduleData = ref([]);
const departments = ref([]);
const doctors = ref([]);
const allDoctors = ref([]);
const doctorCache = ref({});

// 弹窗相关
const dialogVisible = ref(false);
const dialogTitle = ref('新增排班');
const isAdd = ref(true);

// 表单数据
const form = reactive({
  id: '',
  departId: '',
  doctorId: '',
  scheduleDate: '',
  workTime: ''
});

// 表单错误信息
const formErrors = reactive({
  departId: '',
  doctorId: '',
  scheduleDate: '',
  workTime: ''
});

// ========================= 计算属性 =========================
// 筛选后的列表
const filteredScheduleList = computed(() => {
  if (!Array.isArray(allScheduleData.value)) {
    return [];
  }
  
  let filtered = allScheduleData.value;
  
  // 按查询类型筛选
  if (searchType.value === 'depart' && searchForm.departId) {
    filtered = filtered.filter(item => item.departId === searchForm.departId);
  } else if (searchType.value === 'doctor' && searchForm.doctorId) {
    filtered = filtered.filter(item => item.doctorId === searchForm.doctorId);
  }
  
  // 按日期筛选
  if (searchForm.date) {
    filtered = filtered.filter(item => item.scheduleDate === searchForm.date);
  }
  
  return filtered;
});

// 当前页显示的数据
const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredScheduleList.value.slice(start, end);
});

// 搜索后的总数
const searchTotal = computed(() => {
  return filteredScheduleList.value.length;
});

// 搜索状态文本
const searchStatusText = computed(() => {
  let text = '搜索';
  
  if (searchType.value === 'depart' && searchForm.departId) {
    const departName = getCurrentDepartName();
    text += `科室「${departName}」`;
  } else if (searchType.value === 'doctor' && searchForm.doctorId) {
    const doctorName = getCurrentDoctorName();
    text += `医生「${doctorName}」`;
  }
  
  if (searchForm.date) {
    text += `日期「${searchForm.date}」`;
  }
  
  text += `，共找到 ${searchTotal.value} 条记录`;
  return text;
});

// ========================= 页面初始化 =========================
onMounted(() => {
  loadDepartments();
  loadAllDoctors();
  loadScheduleList();
});

// ========================= 辅助方法 =========================
/** 
 * 日期格式化（解决时区问题，强制按本地时区解析）
 * 处理场景：后端返回UTC时间（如2023-11-08T00:00:00Z）时，本地时区可能显示为11-07
 */
const formatDate = (dateString) => {
  if (!dateString) return '-';
  try {
    // 处理纯日期格式（如2023-11-08）
    if (!dateString.includes('T')) {
      return dateString;
    }
    
    // 处理带时区的日期（如2023-11-08T00:00:00Z 或 2023-11-08T08:00:00+08:00）
    const date = new Date(dateString);
    
    // 关键：获取本地时区的年月日（不受UTC影响）
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从0开始
    const day = String(date.getDate()).padStart(2, '0'); // 日期（本地时区）
    
    return `${year}-${month}-${day}`;
  } catch (error) {
    // 异常情况直接截取日期部分
    return dateString.split('T')[0] || dateString;
  }
};

/** 科室ID转名称 */
const getDepartName = (departId) => {
  if (!departId) return '-';
  const depart = departments.value.find(d => d.departID === departId);
  return depart?.departName || '-';
};

/** 医生ID转名称 */
const getDoctorName = (doctorId) => {
  if (!doctorId) return '-';
  const doctor = allDoctors.value.find(d => d.doctorID === doctorId);
  return doctor?.name || '-';
};

/** 获取当前选中的科室名称 */
const getCurrentDepartName = () => {
  if (!searchForm.departId) return '';
  const depart = departments.value.find(d => d.departID === searchForm.departId);
  return depart?.departName || '';
};

/** 获取当前选中的医生名称 */
const getCurrentDoctorName = () => {
  if (!searchForm.doctorId) return '';
  const doctor = allDoctors.value.find(d => d.doctorID === searchForm.doctorId);
  return doctor?.name || '';
};

/** 获取工作时间标签类型 */
const getWorkTimeType = (workTime) => {
  const typeMap = {
    '上午': 'success',
    '下午': 'warning', 
    '全天': 'danger'
  };
  return typeMap[workTime] || 'info';
};

/** 禁止选择过去日期 */
const disabledPastDate = (time) => {
  return time.getTime() < new Date().setHours(0, 0, 0, 0);
};

// ========================= 接口请求方法 =========================
/** 加载所有科室 */
const loadDepartments = async () => {
  departLoading.value = true;
  try {
    const res = await axios.get('/departinfo/list');
    if (res.data.code !== 200) {
      ElMessage.error('加载科室失败：' + res.data.msg);
      return;
    }
    departments.value = res.data.data || [];
  } catch (error) {
    ElMessage.error('加载科室失败：' + error.message);
  } finally {
    departLoading.value = false;
  }
};

/** 加载所有医生 */
const loadAllDoctors = async () => {
  doctorLoading.value = true;
  try {
    const res = await axios.get('/doctorinfo/all');
    if (res.data.code !== 200) {
      ElMessage.error('加载医生列表失败：' + res.data.msg);
      return;
    }
    allDoctors.value = res.data.data || [];
  } catch (error) {
    ElMessage.error('加载医生列表失败：' + error.message);
  } finally {
    doctorLoading.value = false;
  }
};

/** 根据科室ID加载医生 */
const loadDoctors = async (departId) => {
  if (!departId) {
    doctors.value = [];
    return;
  }
  if (doctorCache.value[departId]) {
    doctors.value = doctorCache.value[departId];
    return;
  }
  doctorLoading.value = true;
  try {
    const res = await axios.get(`/doctorinfo/byDepart/${departId}`);
    if (res.data.code !== 200) {
      ElMessage.error('加载医生失败：' + res.data.msg);
      return;
    }
    const doctorList = res.data.data || [];
    doctors.value = doctorList;
    doctorCache.value[departId] = doctorList;
  } catch (error) {
    handleError(error, '加载医生失败');
  } finally {
    doctorLoading.value = false;
  }
};

/** 加载排班列表 */
const loadScheduleList = async () => {
  listLoading.value = true;
  try {
    let res;
    
    if (searchType.value === 'doctor' && searchForm.doctorId) {
      res = await axios.get(`/doctorSchedule/doctor/${searchForm.doctorId}`);
    } else if (searchType.value === 'depart' && searchForm.departId) {
      res = await axios.get(`/doctorSchedule/depart/${searchForm.departId}`);
    } else {
      const params = {};
      if (searchForm.departId) params.departId = searchForm.departId;
      if (searchForm.doctorId) params.doctorId = searchForm.doctorId;
      if (searchForm.date) params.date = searchForm.date;
      
      res = await axios.get('/doctorSchedule/list', { params });
    }
    
    if (res.data.code !== 200) {
      ElMessage.error('加载排班列表失败：' + res.data.msg);
      return;
    }
    
    allScheduleData.value = res.data.data || [];
    isSearching.value = searchForm.departId || searchForm.doctorId || searchForm.date;
    
  } catch (error) {
    handleError(error, '加载排班列表失败');
  } finally {
    listLoading.value = false;
  }
};

/** 提交表单 */
const submitForm = async () => {
  if (!validateForm()) {
    ElMessage.warning('请完善表单信息');
    return;
  }
  
  btnLoading.value = true;
  try {
    let res;
    if (isAdd.value) {
      res = await axios.post('/doctorSchedule', form);
    } else {
      res = await axios.put('/doctorSchedule', form);
    }
    
    if (res.data.code !== 200) {
      ElMessage.error(res.data.msg || '操作失败');
      return;
    }
    
    ElMessage.success(isAdd.value ? '新增成功' : '编辑成功');
    dialogVisible.value = false;
    resetForm();
    loadScheduleList();
    
  } catch (error) {
    handleError(error, isAdd.value ? '新增失败' : '编辑失败');
  } finally {
    btnLoading.value = false;
  }
};

/** 删除排班 */
const deleteSchedule = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该排班信息吗？', '确认删除', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    });

    btnLoading.value = true;
    const res = await axios.delete(`/doctorSchedule/${id}`);
    if (res.data.code !== 200) {
      ElMessage.error(res.data.msg || '删除失败');
      return;
    }
    
    ElMessage.success('删除成功');
    loadScheduleList();
    
  } catch (error) {
    if (error !== 'cancel') {
      handleError(error, '删除失败');
    }
  } finally {
    btnLoading.value = false;
  }
};

// ========================= 表单处理 =========================
/** 表单验证 */
const validateForm = () => {
  clearFormErrors();
  let isValid = true;

  if (!form.departId) {
    formErrors.departId = '请选择科室';
    isValid = false;
  }

  if (!form.doctorId) {
    formErrors.doctorId = '请选择医生';
    isValid = false;
  }

  if (!form.scheduleDate) {
    formErrors.scheduleDate = '请选择排班日期';
    isValid = false;
  }

  if (!form.workTime) {
    formErrors.workTime = '请选择工作时间';
    isValid = false;
  }

  return isValid;
};

/** 清除表单错误信息 */
const clearFormErrors = () => {
  Object.keys(formErrors).forEach(key => {
    formErrors[key] = '';
  });
};

/** 重置表单 */
const resetForm = () => {
  Object.assign(form, {
    id: '',
    departId: '',
    doctorId: '',
    scheduleDate: '',
    workTime: ''
  });
  clearFormErrors();
};

// ========================= 事件处理 =========================
/** 分页大小改变 */
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
};

/** 当前页改变 */
const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
};

/** 查询方式变化 */
const handleSearchTypeChange = (type) => {
  if (type === 'depart') {
    searchForm.doctorId = '';
  } else {
    searchForm.departId = '';
  }
  loadScheduleList();
};

/** 筛选条件科室变化 */
const handleDepartChange = (departId) => {
  loadDoctors(departId);
  loadScheduleList();
};

/** 筛选条件医生变化 */
const handleDoctorChange = (doctorId) => {
  loadScheduleList();
};

/** 弹窗表单科室变化 */
const handleFormDepartChange = (departId) => {
  form.doctorId = '';
  loadDoctors(departId);
};

/** 重置搜索 */
const resetSearch = () => {
  searchForm.departId = '';
  searchForm.doctorId = '';
  searchForm.date = '';
  searchType.value = 'depart';
  isSearching.value = false;
  currentPage.value = 1;
  loadScheduleList();
  ElMessage.info('已重置搜索条件');
};

/** 显示新增弹窗 */
const showAddDialog = () => {
  isAdd.value = true;
  dialogTitle.value = '新增排班';
  resetForm();
  dialogVisible.value = true;
  nextTick(() => {
    clearFormErrors();
    form.scheduleDate = '';
  });
};

/** 显示编辑弹窗 */
const showEditDialog = (row) => {
  isAdd.value = false;
  dialogTitle.value = '编辑排班';
  Object.assign(form, {
    id: row.id,
    departId: row.departId,
    doctorId: row.doctorId,
    scheduleDate: formatDate(row.scheduleDate), // 编辑时也用本地时区格式化
    workTime: row.workTime
  });
  loadDoctors(row.departId);
  dialogVisible.value = true;
  nextTick(() => {
    clearFormErrors();
  });
};

/** 关闭弹窗 */
const handleClose = () => {
  dialogVisible.value = false;
  resetForm();
};

/** 统一错误处理 */
const handleError = (error, defaultMsg) => {
  let errorMsg = defaultMsg;
  if (error.response) {
    errorMsg += '：' + (error.response.data?.msg || error.response.statusText);
  } else if (error.request) {
    errorMsg += '：网络请求失败，请检查后端服务是否正常';
  } else {
    errorMsg += '：' + error.message;
  }
  ElMessage.error(errorMsg);
};
</script>

<style scoped>
/* 样式保持不变 */
.doctor-schedule-management {
  padding: 20px;
  box-sizing: border-box;
  background: #f5f7fa;
  min-height: 100vh;
}

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

.search-area {
  margin-bottom: 16px;
}

.search-type {
  margin-bottom: 12px;
}

.search-inputs {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.search-select,
.date-picker {
  width: 200px;
}

.search-status {
  margin-bottom: 16px;
}

.table-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.schedule-table {
  padding: 0;
}

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table .cell) {
  line-height: 1.4;
  word-break: normal;
}

:deep(.el-table-column) {
  .cell {
    overflow: visible !important;
    text-overflow: clip !important;
    white-space: nowrap !important;
  }
}

:deep(.el-table--small) {
  font-size: 12px;
}

.cell-with-icon {
  display: flex;
  align-items: center;
  gap: 6px;
}

.cell-with-icon .el-icon {
  color: #909399;
  font-size: 14px;
}

.date-tag {
  border: none;
  font-weight: 500;
  font-size: 12px;
  padding: 4px 8px;
  min-width: 90px;
  white-space: nowrap;
  overflow: visible;
  text-overflow: clip;
  color: chocolate;;
}

.time-tag {
  border: none;
  font-weight: 500;
  font-size: 12px;
}

.date-text {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.operation-btn-group {
  display: flex;
  justify-content: center;
  gap: 8px;
}

:deep(.el-button--small) {
  padding: 5px 8px;
  font-size: 12px;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #909399;
}

.pagination-area {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background: #fafafa;
}

.schedule-form-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
  }
}

.form-container {
  padding: 10px 0;
}

.readonly-field {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.field-label {
  font-weight: 600;
  color: #606266;
  margin-right: 12px;
  min-width: 80px;
}

.field-value {
  color: #303133;
  font-weight: 500;
}

.form-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-group.required .form-label::after {
  content: " *";
  color: #f56c6c;
}

.form-label {
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

:deep(.form-input.error .el-input__wrapper),
:deep(.form-input.error .el-date-editor) {
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

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-inputs {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-select,
  .date-picker {
    width: 100%;
  }
  
  .pagination-area {
    justify-content: center;
  }
  
  :deep(.el-pagination) {
    flex-wrap: wrap;
  }
}

@media (max-width: 1200px) {
  .schedule-table {
    overflow-x: auto;
  }
  
  :deep(.el-table) {
    min-width: 1100px;
  }
}
</style>