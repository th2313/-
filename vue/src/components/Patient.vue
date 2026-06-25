<template>
  <div class="patient-management">
    <!-- 标题区域 -->
    <h2 class="page-title">
      <el-icon class="title-icon"><User /></el-icon>
      病人信息管理
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
          <span class="btn-text">新增病人</span>
        </div>
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索病人姓名、联系电话或住院编号"
        class="search-input"
        clearable
        @keyup.enter="fetchPatients"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="fetchPatients" class="search-btn">
        <el-icon><Search /></el-icon>
        查询
      </el-button>
      <el-button type="default" @click="handleResetSearch" class="reset-btn">重置</el-button>
    </div>

    <!-- 搜索状态提示 -->
    <div v-if="isSearching" class="search-status">
      <el-alert
        :title="`搜索『${searchKeyword}』，共找到 ${filteredPatientList.length} 条记录`"
        type="info"
        :closable="false"
        show-icon
      >
        <template #action>
          <el-button type="primary" text @click="handleResetSearch">显示全部</el-button>
        </template>
      </el-alert>
    </div>

    <!-- 病人表格和分页容器 -->
    <div class="table-container">
      <div class="patient-table">
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
          
          <el-table-column prop="hspID" label="住院编号" min-width="120" show-overflow-tooltip>
            <template #header>
              <el-icon><Document /></el-icon>
              住院编号
            </template>
            <template #default="scope">
              <span class="patient-id">{{ scope.row.hspID }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="name" label="病人姓名" min-width="100" show-overflow-tooltip>
            <template #header>
              <el-icon><User /></el-icon>
              病人姓名
            </template>
            <template #default="scope">
              <span class="patient-name">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="gender" label="性别" width="80" align="center">
            <template #header>
              <el-icon><Male /></el-icon>
              性别
            </template>
            <template #default="scope">
              <el-tag 
                :type="scope.row.gender === '男' ? 'primary' : 'danger'" 
                class="gender-tag"
              >
                {{ scope.row.gender }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="departName" label="科室" min-width="120" show-overflow-tooltip>
            <template #header>
              <el-icon><OfficeBuilding /></el-icon>
              科室
            </template>
            <template #default="scope">
              <el-tag type="info" class="dept-tag">
                {{ scope.row.departName }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="doctorName" label="主治医生" min-width="100" show-overflow-tooltip>
            <template #header>
              <el-icon><Avatar /></el-icon>
              主治医生
            </template>
          </el-table-column>
          
          <el-table-column prop="contactsPhone" label="联系电话" min-width="120" show-overflow-tooltip>
            <template #header>
              <el-icon><Phone /></el-icon>
              联系电话
            </template>
          </el-table-column>
          
          <el-table-column prop="birthday" label="出生日期" width="120" align="center">
            <template #header>
              <el-icon><Calendar /></el-icon>
              出生日期
            </template>
            <template #default="scope">
              <span class="date-text">{{ formatDate(scope.row.birthday) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="bedID" label="病床号" width="100" align="center">
            <template #header>
              <el-icon><House /></el-icon>
              病床号
            </template>
            <template #default="scope">
              <el-tag v-if="scope.row.bedID" type="success" class="bed-tag">
                {{ scope.row.bedID }}
              </el-tag>
              <span v-else class="empty-text">-</span>
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
          
          <el-table-column label="操作" width="150" align="center" fixed="right">
            <template #header>
              <el-icon><Operation /></el-icon>
              操作
            </template>
            <template #default="scope">
              <div class="operation-btn-group">
                <el-button type="primary" link size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDelete(scope.row)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 空状态提示 -->
        <div v-if="currentPageData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无病人数据" />
        </div>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-area" v-if="filteredPatientList.length > 0">
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

    <!-- 病人信息表单弹窗 -->
    <el-dialog
      :title="isAdd ? '新增病人' : '编辑病人'"
      v-model="showForm"
      width="800px"
      :before-close="handleClose"
      class="patient-form-dialog"
    >
      <div class="form-container">
        <!-- 编辑时显示住院编号 -->
        <div v-if="!isAdd" class="readonly-field">
          <span class="field-label">住院编号</span>
          <span class="field-value">{{ patientForm.hspID }}</span>
        </div>

        <div class="form-grid">
          <!-- 第一行：病人姓名 + 性别 -->
          <div class="form-row">
            <div class="form-group required">
              <label class="form-label">病人姓名</label>
              <el-input 
                v-model="patientForm.name" 
                placeholder="请输入病人姓名" 
                class="form-input"
                :class="{ 'error': formErrors.name }"
              />
              <div v-if="formErrors.name" class="error-message">{{ formErrors.name }}</div>
            </div>
            
            <div class="form-group required">
              <label class="form-label">性别</label>
              <div class="gender-options">
                <el-radio-group v-model="patientForm.gender">
                  <el-radio label="男" class="gender-radio">男</el-radio>
                  <el-radio label="女" class="gender-radio">女</el-radio>
                </el-radio-group>
              </div>
              <div v-if="formErrors.gender" class="error-message">{{ formErrors.gender }}</div>
            </div>
          </div>

          <!-- 第二行：科室 + 主治医生 -->
          <div class="form-row">
            <div class="form-group required">
              <label class="form-label">科室</label>
              <el-select 
                v-model="patientForm.departId" 
                placeholder="请选择科室"
                @change="handleDepartChange"
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
              <label class="form-label">主治医生</label>
              <el-select 
                v-model="patientForm.doctorId" 
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

          <!-- 第三行：联系电话 + 出生日期 -->
          <div class="form-row">
            <div class="form-group required">
              <label class="form-label">联系电话</label>
              <el-input 
                v-model="patientForm.contactsPhone" 
                placeholder="请输入联系电话" 
                class="form-input"
                :class="{ 'error': formErrors.contactsPhone }"
              />
              <div v-if="formErrors.contactsPhone" class="error-message">{{ formErrors.contactsPhone }}</div>
            </div>
            
            <div class="form-group required">
              <label class="form-label">出生日期</label>
              <el-date-picker
                v-model="patientForm.birthday"
                type="date"
                placeholder="选择出生日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                class="form-input"
                :class="{ 'error': formErrors.birthday }"
              />
              <div v-if="formErrors.birthday" class="error-message">{{ formErrors.birthday }}</div>
            </div>
          </div>

          <!-- 第四行：病床号 + 身份证号 -->
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">病床号</label>
              <el-input 
                v-model="patientForm.bedID" 
                placeholder="请输入病床号" 
                class="form-input"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">身份证号</label>
              <el-input 
                v-model="patientForm.iDCardNo" 
                placeholder="请输入身份证号（非必填）" 
                class="form-input"
              />
            </div>
          </div>

          <!-- 第五行：入院诊断 -->
          <div class="form-row full-width">
            <div class="form-group">
              <label class="form-label">入院诊断</label>
              <el-input 
                v-model="patientForm.inHspDiagnose" 
                type="textarea" 
                :rows="3" 
                placeholder="请输入入院诊断"
                class="form-textarea"
              />
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
            :loading="saveLoading"
            class="submit-btn"
          >
            {{ isAdd ? '确定' : '更新' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import { 
  Plus, Search, User, Document, Male, OfficeBuilding, 
  Avatar, Phone, Calendar, House, Clock, Operation 
} from "@element-plus/icons-vue";
import axios from "axios";

// 后端接口地址
const baseURL = "http://localhost:8080/patientinfo";

// 表单相关
const formRef = ref(null);
const showForm = ref(false);
const isAdd = ref(true);
const saveLoading = ref(false);

// 搜索相关
const searchKeyword = ref("");
const loading = ref(false);
const isSearching = ref(false);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);

// 科室和医生相关
const departLoading = ref(false);
const doctorLoading = ref(false);
const departments = ref([]);
const doctors = ref([]);
const doctorCache = ref({});

// 病人表单数据
const patientForm = reactive({
  hspID: "", 
  name: "",
  gender: "男",
  departId: "",
  departName: "",
  doctorId: "",
  doctorName: "",
  contactsPhone: "",
  birthday: "",
  bedID: "", 
  inHspDiagnose: "", 
  iDCardNo: "",
  creationDate: ""
});

// 表单错误信息
const formErrors = reactive({
  name: "",
  gender: "",
  departId: "",
  doctorId: "",
  contactsPhone: "",
  birthday: ""
});

// 病人列表数据
const patientList = ref([]);
const allData = ref([]);

// 计算属性
const filteredPatientList = computed(() => {
  if (!Array.isArray(patientList.value)) {
    return [];
  }
  
  if (!searchKeyword.value) {
    return patientList.value;
  }
  
  const keyword = searchKeyword.value.trim().toLowerCase();
  return patientList.value.filter(item => {
    if (!item) return false;
    
    const nameMatch = item.name ? item.name.toLowerCase().includes(keyword) : false;
    const phoneMatch = item.contactsPhone ? item.contactsPhone.includes(keyword) : false;
    const hspIDMatch = item.hspID ? item.hspID.includes(keyword) : false;
    
    return nameMatch || phoneMatch || hspIDMatch;
  });
});

const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredPatientList.value.slice(start, end);
});

const searchTotal = computed(() => {
  return filteredPatientList.value.length;
});

// 辅助方法
/** 日期格式化 */
const formatDate = (dateString) => {
  if (!dateString) return '-';
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    }).replace(/\//g, '-');
  } catch (error) {
    return dateString;
  }
};

// 页面加载时获取病人列表和科室列表
onMounted(() => {
  fetchPatients();
  loadDepartments();
});

// 从后端获取病人列表
const fetchPatients = async () => {
  loading.value = true;
  try {
    const response = await axios.get(baseURL);
    // 适配 Result<T> 统一返回格式: {code: 200, msg: "success", data: [...]}
    if (response.data && response.data.code === 200) {
      patientList.value = Array.isArray(response.data.data) ? response.data.data : [];
    } else if (Array.isArray(response.data)) {
      // 兼容旧格式（直接返回数组）
      patientList.value = response.data;
    } else {
      patientList.value = [];
      ElMessage.error(response.data?.msg || '数据格式异常');
    }
    allData.value = [...patientList.value];
    isSearching.value = false;
    currentPage.value = 1;
  } catch (error) {
    ElMessage.error("获取数据失败：" + (error.response?.data?.msg || error.message));
    patientList.value = [];
    allData.value = [];
  } finally {
    loading.value = false;
  }
};

// 加载科室列表
const loadDepartments = async () => {
  departLoading.value = true;
  try {
    const response = await axios.get('/departinfo/list');
    if (response.data.code === 200) {
      departments.value = response.data.data || [];
    } else {
      ElMessage.error('加载科室失败：' + response.data.msg);
    }
  } catch (error) {
    ElMessage.error('加载科室失败：' + error.message);
  } finally {
    departLoading.value = false;
  }
};

// 根据科室ID加载医生列表
const loadDoctors = async (departId) => {
  if (!departId) {
    doctors.value = [];
    return;
  }
  
  // 如果已经有缓存，直接使用缓存
  if (doctorCache.value[departId]) {
    doctors.value = doctorCache.value[departId];
    return;
  }
  
  doctorLoading.value = true;
  try {
    const response = await axios.get(`/doctorinfo/byDepart/${departId}`);
    if (response.data.code === 200) {
      const doctorList = response.data.data || [];
      doctors.value = doctorList;
      // 缓存医生列表
      doctorCache.value[departId] = doctorList;
    } else {
      ElMessage.error('加载医生失败：' + response.data.msg);
    }
  } catch (error) {
    ElMessage.error('加载医生失败：' + error.message);
  } finally {
    doctorLoading.value = false;
  }
};

// 科室选择变化
const handleDepartChange = async (departId) => {
  patientForm.doctorId = '';
  patientForm.doctorName = '';
  
  if (departId) {
    // 设置科室名称
    const selectedDepart = departments.value.find(d => d.departID === departId);
    if (selectedDepart) {
      patientForm.departName = selectedDepart.departName;
    }
    
    // 加载该科室的医生
    await loadDoctors(departId);
  } else {
    doctors.value = [];
    patientForm.departName = '';
  }
};

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
};

// 搜索功能
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    fetchPatients();
    return;
  }
  
  currentPage.value = 1;
  isSearching.value = true;
  
  if (filteredPatientList.value.length === 0) {
    ElMessage.info('没有找到匹配的病人信息');
  }
};

// 重置搜索
const handleResetSearch = () => {
  searchKeyword.value = '';
  patientList.value = allData.value;
  isSearching.value = false;
  currentPage.value = 1;
  ElMessage.info('已重置搜索条件');
};

// 表单验证
const validateForm = () => {
  clearFormErrors();
  let isValid = true;

  // 验证病人姓名
  if (!patientForm.name.trim()) {
    formErrors.name = "请输入病人姓名";
    isValid = false;
  } else if (patientForm.name.trim().length > 50) {
    formErrors.name = "姓名长度不能超过50个字符";
    isValid = false;
  }

  // 验证性别
  if (!patientForm.gender) {
    formErrors.gender = "请选择性别";
    isValid = false;
  }

  // 验证科室
  if (!patientForm.departId) {
    formErrors.departId = "请选择科室";
    isValid = false;
  }



  // 验证联系电话
  if (!patientForm.contactsPhone.trim()) {
    formErrors.contactsPhone = "请输入联系电话";
    isValid = false;
  } else if (!/^1[3-9]\d{9}$/.test(patientForm.contactsPhone)) {
    formErrors.contactsPhone = "手机号格式错误";
    isValid = false;
  }

  // 验证出生日期
  if (!patientForm.birthday) {
    formErrors.birthday = "请选择出生日期";
    isValid = false;
  }

  return isValid;
};

// 清除表单错误信息
const clearFormErrors = () => {
  Object.keys(formErrors).forEach(key => {
    formErrors[key] = "";
  });
};

// 新增病人
const handleAdd = () => {
  isAdd.value = true;
  resetForm();
  showForm.value = true;
  nextTick(() => {
    clearFormErrors();
  });
};

// 编辑病人
const handleEdit = async (row) => {
  isAdd.value = false;
  
  // 先设置基本数据
  Object.assign(patientForm, {
    hspID: row.hspID || "",
    name: row.name || "",
    gender: row.gender || "男",
    contactsPhone: row.contactsPhone || "",
    birthday: row.birthday || "",
    bedID: row.bedID || "",
    inHspDiagnose: row.inHspDiagnose || "",
    iDCardNo: row.iDCardNo || "",
    creationDate: row.creationDate || ""
  });
  
  // 设置科室和医生
  if (row.departId) {
    patientForm.departId = row.departId;
    patientForm.departName = row.departName || '';
    
    // 加载该科室的医生
    await loadDoctors(row.departId);
    
    if (row.doctorId) {
      patientForm.doctorId = row.doctorId;
      patientForm.doctorName = row.doctorName || '';
    }
  }
  
  showForm.value = true;
  nextTick(() => {
    clearFormErrors();
  });
};

// 删除病人
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm("确定删除该病人信息吗？此操作不可恢复。", "确认删除", {
      confirmButtonText: "确定删除",
      cancelButtonText: "取消",
      type: "warning"
    });

    const response = await axios.delete(`${baseURL}/${row.hspID}`);
    if (response.data && response.data.code === 200) {
      ElMessage.success(response.data.msg || "删除成功");
    } else {
      ElMessage.success("删除成功");
    }
    fetchPatients();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败：" + (error.response?.data?.msg || error.message));
    }
  }
};

// 提交表单
const submitForm = async () => {
  if (!validateForm()) {
    ElMessage.warning("请完善表单信息");
    return;
  }
  
  try {
    saveLoading.value = true;
    
    // 设置医生姓名
    if (patientForm.doctorId) {
      const selectedDoctor = doctors.value.find(d => d.doctorID === patientForm.doctorId);
      if (selectedDoctor) {
        patientForm.doctorName = selectedDoctor.name;
      }
    }
    
    let response;
    if (isAdd.value) {
      // 新增：不传递hspID，由后端自动生成
      const { hspID, ...formData } = patientForm;
      response = await axios.post(baseURL, formData);
    } else {
      // 编辑：需要传递hspID用于标识
      response = await axios.put(`${baseURL}/${patientForm.hspID}`, patientForm);
    }

    // 适配 Result<T> 统一返回格式
    const isSuccess = response.data && response.data.code === 200;
    const msg = response.data?.msg || '';

    if (isSuccess) {
      ElMessage.success(msg || (isAdd.value ? "新增成功" : "编辑成功"));
      showForm.value = false;
      resetForm();
      fetchPatients();
    } else if (response.status >= 200 && response.status < 300) {
      // 兼容旧格式
      ElMessage.success(isAdd.value ? "新增成功" : "编辑成功");
      showForm.value = false;
      resetForm();
      fetchPatients();
    } else {
      ElMessage.error(msg || `${isAdd.value ? "新增" : "编辑"}失败：未知错误`);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.message || 
                    error.message || 
                    "网络请求错误";
    const statusCode = error.response?.status || "未知状态码";
    ElMessage.error(`${isAdd.value ? "新增" : "编辑"}失败(${statusCode})：${errorMsg}`);
    console.error("请求错误详情:", error);
  } finally {
    saveLoading.value = false;
  }
};

// 重置表单
const resetForm = () => {
  Object.assign(patientForm, {
    hspID: "",
    name: "",
    gender: "男",
    departId: "",
    departName: "",
    doctorId: "",
    doctorName: "",
    contactsPhone: "",
    birthday: "",
    bedID: "",
    inHspDiagnose: "",
    iDCardNo: "",
    creationDate: ""
  });
  doctors.value = [];
  clearFormErrors();
};

// 关闭弹窗
const handleClose = () => {
  showForm.value = false;
  resetForm();
};
</script>

<style scoped>
/* 页面基础样式 */
.patient-management {
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
.patient-table {
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

/* 病人ID样式 */
.patient-id {
  font-weight: 500;
  color: #165DFF;
}

/* 病人姓名样式 */
.patient-name {
  font-weight: 500;
  color: #303133;
}

/* 性别标签样式 */
.gender-tag {
  border: none;
  font-weight: 500;
  font-size: 12px;
}

/* 科室标签样式 */
.dept-tag {
  border: none;
  font-weight: 500;
  font-size: 12px;
  color: #f56c6c;
}

/* 病床号标签样式 */
.bed-tag {
  border: none;
  font-weight: 500;
  font-size: 12px;
}

/* 日期样式 */
.date-text {
  font-size: 12px;
  color: #909399;
}

/* 空文本样式 */
.empty-text {
  color: #c0c4cc;
  font-style: italic;
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
.patient-form-dialog {
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

.form-row.full-width {
  flex-direction: column;
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

.form-input,
.form-textarea {
  width: 100%;
}

.form-textarea {
  :deep(.el-textarea__inner) {
    resize: vertical;
    min-height: 80px;
  }
}

.gender-options {
  display: flex;
  align-items: center;
  height: 40px;
}

.gender-radio {
  margin-right: 30px;
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  height: 18px;
}

:deep(.form-input.error .el-input__wrapper),
:deep(.form-input.error .el-date-editor),
:deep(.form-input.error .el-select) {
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

/* 响应式设计 */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-area {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    width: 100%;
    margin-right: 0;
    margin-bottom: 10px;
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
  .patient-table {
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