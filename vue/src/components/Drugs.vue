<template>
  <div class="drugs-management">
    <!-- 标题区域 -->
    <h2 class="page-title">
      <el-icon class="title-icon"><FirstAidKit /></el-icon>
      药品信息管理
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
          <span class="btn-text">新增药品</span>
        </div>
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索药品名称、药品编码或拼音助记码"
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="handleSearch" class="search-btn">查询</el-button>
      <el-button type="default" @click="handleResetSearch" style="margin-left: 10px;">重置</el-button>
    </div>

    <!-- 搜索状态提示 -->
    <div v-if="isSearching" class="search-status">
      <el-alert
        :title="`搜索『${searchKeyword}』，共找到 ${searchTotal} 条记录`"
        type="info"
        :closable="false"
        show-icon
      >
        <template #action>
          <el-button type="primary" text @click="handleResetSearch">显示全部</el-button>
        </template>
      </el-alert>
    </div>

    <!-- 药品表格和分页容器 -->
    <div class="table-container">
      <div class="drugs-table">
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
          <el-table-column prop="drugsCode" label="药品编码" width="120" align="center" show-overflow-tooltip />
          <el-table-column prop="drugsName" label="药品名称" min-width="150" show-overflow-tooltip>
            <template #default="scope">
              <span class="drug-name">{{ scope.row.drugsName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="drugsFormat" label="规格" width="120" show-overflow-tooltip />
          <el-table-column prop="drugsUnit" label="单位" width="80" align="center" />
          <el-table-column prop="manufacturer" label="生产厂家" width="180" show-overflow-tooltip>
            <template #default="scope">
              <span class="manufacturer">{{ scope.row.manufacturer }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="drugsPrice" label="单价" width="100" align="right">
            <template #default="scope">
              <span class="price-red">¥{{ formatPrice(scope.row.drugsPrice) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="mnemonicCode" label="拼音码" width="120" show-overflow-tooltip />
          <el-table-column prop="creationDate" label="创建时间" width="140" align="center">
            <template #default="scope">
              <span class="date-text">{{ formatDate(scope.row.creationDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="lastUpdateDate" label="更新时间" width="140" align="center">
            <template #default="scope">
              <span class="date-text">{{ formatDate(scope.row.lastUpdateDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center" fixed="right">
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
          <el-empty description="暂无药品数据" />
        </div>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-area" v-if="filteredDrugsList.length > 0">
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

    <!-- 药品信息表单弹窗 -->
    <el-dialog
      :title="isAdd ? '新增药品' : '编辑药品'"
      v-model="showForm"
      width="800px"
      :before-close="handleClose"
      class="drugs-form-dialog"
    >
      <div class="form-container">
        <!-- 编辑时显示ID -->
        <div v-if="!isAdd" class="readonly-field">
          <span class="field-label">药品ID</span>
          <span class="field-value">{{ drugsForm.id }}</span>
        </div>

        <div class="form-grid">
          <!-- 第一行：药品编码 + 药品名称 -->
          <div class="form-row">
            <div class="form-group required">
              <label class="form-label">药品编码</label>
              <el-input 
                v-model="drugsForm.drugsCode" 
                placeholder="请输入药品编码" 
                class="form-input"
                :class="{ 'error': formErrors.drugsCode }"
              />
              <div v-if="formErrors.drugsCode" class="error-message">{{ formErrors.drugsCode }}</div>
            </div>
            
            <div class="form-group required">
              <label class="form-label">药品名称</label>
              <el-input 
                v-model="drugsForm.drugsName" 
                placeholder="请输入药品名称" 
                class="form-input"
                :class="{ 'error': formErrors.drugsName }"
              />
              <div v-if="formErrors.drugsName" class="error-message">{{ formErrors.drugsName }}</div>
            </div>
          </div>

          <!-- 第二行：药品规格 + 包装单位 -->
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">药品规格</label>
              <el-input 
                v-model="drugsForm.drugsFormat" 
                placeholder="请输入药品规格" 
                class="form-input"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">包装单位</label>
              <el-input 
                v-model="drugsForm.drugsUnit" 
                placeholder="请输入包装单位" 
                class="form-input"
              />
            </div>
          </div>

          <!-- 第三行：生产厂家 + 药品单价 -->
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">生产厂家</label>
              <el-input 
                v-model="drugsForm.manufacturer" 
                placeholder="请输入生产厂家" 
                class="form-input"
              />
            </div>
            
            <div class="form-group required">
              <label class="form-label">药品单价</label>
              <el-input 
                v-model="drugsForm.drugsPrice" 
                placeholder="请输入药品单价" 
                class="form-input"
                type="number"
                min="0"
                step="0.01"
                :class="{ 'error': formErrors.drugsPrice }"
              >
                <template #append>元</template>
              </el-input>
              <div v-if="formErrors.drugsPrice" class="error-message">{{ formErrors.drugsPrice }}</div>
            </div>
          </div>

          <!-- 第四行：药品剂型 + 药品类型 -->
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">药品剂型ID</label>
              <el-input 
                v-model="drugsForm.drugsDosageID" 
                placeholder="请输入药品剂型ID" 
                class="form-input"
                type="number"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">药品类型ID</label>
              <el-input 
                v-model="drugsForm.drugsTypeID" 
                placeholder="请输入药品类型ID" 
                class="form-input"
                type="number"
              />
            </div>
          </div>

          <!-- 第五行：拼音助记码 -->
          <div class="form-row full-width">
            <div class="form-group">
              <label class="form-label">拼音助记码</label>
              <el-input 
                v-model="drugsForm.mnemonicCode" 
                placeholder="请输入拼音助记码" 
                class="form-input"
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
import { Plus, Search, FirstAidKit } from "@element-plus/icons-vue";
import axios from "axios";

// 创建 axios 实例
const request = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 10000,
});

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
const total = ref(0);

// 所有数据（从后端获取的完整数据）
const allDrugsData = ref([]);

// 药品表单数据
const drugsForm = reactive({
  id: "",
  drugsCode: "",
  drugsName: "",
  drugsFormat: "",
  drugsUnit: "",
  manufacturer: "",
  drugsDosageID: "",
  drugsTypeID: "",
  drugsPrice: "",
  mnemonicCode: "",
  creationDate: "",
  lastUpdateDate: "",
  delMark: 0
});

// 表单错误信息
const formErrors = reactive({
  drugsCode: "",
  drugsName: "",
  drugsPrice: ""
});

// 日期格式化方法
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

// 价格格式化方法
const formatPrice = (price) => {
  if (!price) return '0.00';
  
  // 如果是字符串，转换为数字
  const numPrice = typeof price === 'string' ? parseFloat(price) : price;
  
  // 格式化为两位小数
  return numPrice.toFixed(2);
};

// 筛选后的列表（用于搜索）
const filteredDrugsList = computed(() => {
  if (!Array.isArray(allDrugsData.value)) {
    return [];
  }
  
  if (!searchKeyword.value) {
    return allDrugsData.value;
  }
  
  const keyword = searchKeyword.value.trim().toLowerCase();
  return allDrugsData.value.filter(item => {
    if (!item) return false;
    
    const nameMatch = item.drugsName ? item.drugsName.toLowerCase().includes(keyword) : false;
    const codeMatch = item.drugsCode ? item.drugsCode.toLowerCase().includes(keyword) : false;
    const mnemonicMatch = item.mnemonicCode ? item.mnemonicCode.toLowerCase().includes(keyword) : false;
    
    return nameMatch || codeMatch || mnemonicMatch;
  });
});

// 当前页显示的数据（前端分页）
const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredDrugsList.value.slice(start, end);
});

// 搜索后的总数
const searchTotal = computed(() => {
  return filteredDrugsList.value.length;
});

// 分页大小改变
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1; // 重置到第一页
};

// 当前页改变
const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
};

// 页面加载时获取药品列表
onMounted(() => {
  fetchDrugs();
});

// 从后端获取药品列表
const fetchDrugs = async () => {
  loading.value = true;
  try {
    const response = await request.get("/drugs");
    allDrugsData.value = Array.isArray(response.data) ? response.data : [];
    total.value = allDrugsData.value.length;
    isSearching.value = false;
    currentPage.value = 1; // 重置到第一页
  } catch (error) {
    console.error("API Error:", error);
    ElMessage.error("获取药品数据失败：" + (error.response?.data?.message || error.message));
    allDrugsData.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 搜索功能
const handleSearch = () => {
  currentPage.value = 1; // 搜索时回到第一页
  isSearching.value = !!searchKeyword.value.trim();
};

// 重置搜索
const handleResetSearch = () => {
  searchKeyword.value = '';
  isSearching.value = false;
  currentPage.value = 1;
  ElMessage.info('已重置搜索条件');
};

// 新增药品
const handleAdd = () => {
  isAdd.value = true;
  resetForm();
  showForm.value = true;
  nextTick(() => {
    clearFormErrors();
  });
};

// 编辑药品
const handleEdit = (row) => {
  isAdd.value = false;
  Object.assign(drugsForm, {
    id: row.id || "",
    drugsCode: row.drugsCode || "",
    drugsName: row.drugsName || "",
    drugsFormat: row.drugsFormat || "",
    drugsUnit: row.drugsUnit || "",
    manufacturer: row.manufacturer || "",
    drugsDosageID: row.drugsDosageID || "",
    drugsTypeID: row.drugsTypeID || "",
    drugsPrice: row.drugsPrice || "",
    mnemonicCode: row.mnemonicCode || "",
    creationDate: row.creationDate || "",
    lastUpdateDate: row.lastUpdateDate || "",
    delMark: row.delMark || 0
  });
  showForm.value = true;
  nextTick(() => {
    clearFormErrors();
  });
};

// 删除药品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm("确定删除该药品信息吗？", "确认删除", {
      confirmButtonText: "确定删除",
      cancelButtonText: "取消",
      type: "warning"
    });

    await request.delete(`/drugs/${row.id}`);
    ElMessage.success("删除成功");
    fetchDrugs(); // 刷新列表
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败：" + (error.response?.data?.message || error.message));
    }
  }
};

// 表单验证
const validateForm = () => {
  clearFormErrors();
  let isValid = true;

  // 验证药品编码
  if (!drugsForm.drugsCode.trim()) {
    formErrors.drugsCode = "请输入药品编码";
    isValid = false;
  }

  // 验证药品名称
  if (!drugsForm.drugsName.trim()) {
    formErrors.drugsName = "请输入药品名称";
    isValid = false;
  }

  // 验证药品单价
  if (!drugsForm.drugsPrice) {
    formErrors.drugsPrice = "请输入药品单价";
    isValid = false;
  } else if (parseFloat(drugsForm.drugsPrice) < 0) {
    formErrors.drugsPrice = "药品单价不能为负数";
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

// 提交表单
const submitForm = async () => {
  if (!validateForm()) {
    ElMessage.warning("请完善表单信息");
    return;
  }
  
  try {
    saveLoading.value = true;
    
    let response;
    if (isAdd.value) {
      // 新增药品
      response = await request.post("/drugs", drugsForm);
    } else {
      // 编辑药品
      response = await request.put(`/drugs/${drugsForm.id}`, drugsForm);
    }
    
    if (response.status >= 200 && response.status < 300) {
      ElMessage.success(isAdd.value ? "新增成功" : "编辑成功");
      showForm.value = false;
      resetForm();
      fetchDrugs(); // 刷新列表
    } else {
      ElMessage.error(`${isAdd.value ? "新增" : "编辑"}失败：${response.data?.message || "未知错误"}`);
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
  Object.assign(drugsForm, {
    id: "",
    drugsCode: "",
    drugsName: "",
    drugsFormat: "",
    drugsUnit: "",
    manufacturer: "",
    drugsDosageID: "",
    drugsTypeID: "",
    drugsPrice: "",
    mnemonicCode: "",
    creationDate: "",
    lastUpdateDate: "",
    delMark: 0
  });
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
.drugs-management {
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
.drugs-table {
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

/* 药品名称样式 */
.drug-name {
  font-weight: 500;
  color: #165DFF;
}

/* 生产厂家样式 */
.manufacturer {
  color: #606266;
  font-size: 12px;
}

/* 价格样式 - 红色 */
.price-red {
  font-weight: 600;
  color: #f56c6c !important;
  font-size: 13px;
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
.drugs-form-dialog {
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
  .drugs-table {
    overflow-x: auto;
  }
  
  :deep(.el-table) {
    min-width: 1000px;
  }
}
</style>