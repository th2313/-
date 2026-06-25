<template>
  <div class="check-item-management">
    <!-- 标题区域 -->
    <h2 class="page-title">
      <el-icon class="title-icon"><Monitor /></el-icon>
      检查项目管理
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
          <span class="btn-text">新增项目</span>
        </div>
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-input
        v-model="searchQuery"
        placeholder="输入项目名称、编码或拼音助记码"
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="handleSearch" class="search-btn">
        <el-icon><Search /></el-icon>
        查询
      </el-button>
      <el-button type="default" @click="handleResetSearch" class="reset-btn">重置</el-button>
    </div>

    <!-- 搜索状态提示 -->
    <div v-if="isSearching" class="search-status">
      <el-alert
        :title="`搜索『${searchQuery}』，共找到 ${pagination.total} 条记录`"
        type="info"
        :closable="false"
        show-icon
      >
        <template #action>
          <el-button type="primary" text @click="handleResetSearch">显示全部</el-button>
        </template>
      </el-alert>
    </div>

    <!-- 检查项目表格和分页容器 -->
    <div class="table-container">
      <div class="check-item-table">
        <el-table 
          :data="tableData" 
          border 
          stripe 
          style="width: 100%"
          v-loading="loading"
          :cell-style="{ padding: '8px 12px', lineHeight: '1.4' }"
          :header-cell-style="{ background: '#f5f7fa', padding: '12px', fontWeight: '600' }"
          size="small"
        >
          <el-table-column type="index" label="序号" width="60" align="center" fixed="left" />
          <el-table-column prop="id" label="ID" width="80" align="center" show-overflow-tooltip />
          <el-table-column prop="itemCode" label="项目编码" min-width="120" show-overflow-tooltip>
            <template #header>
              <el-icon><Document /></el-icon>
              项目编码
            </template>
          </el-table-column>
          <el-table-column prop="itemName" label="项目名称" min-width="150" show-overflow-tooltip>
            <template #header>
              <el-icon><Tickets /></el-icon>
              项目名称
            </template>
            <template #default="scope">
              <span class="item-name">{{ scope.row.itemName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="format" label="规格" min-width="120" show-overflow-tooltip />
          <el-table-column prop="price" label="单价" width="120" align="right">
            <template #header>
              <el-icon><Money /></el-icon>
              单价
            </template>
            <template #default="scope">
              <span class="price-red">¥{{ formatPrice(scope.row.price) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="expClassID" label="费用科目ID" width="120" align="center">
            <template #header>
              <el-icon><Collection /></el-icon>
              费用科目ID
            </template>
          </el-table-column>
          <el-table-column prop="deptID" label="执行科室ID" width="120" align="center">
            <template #header>
              <el-icon><OfficeBuilding /></el-icon>
              执行科室ID
            </template>
          </el-table-column>
          <el-table-column prop="mnemonicCode" label="拼音助记码" min-width="130" show-overflow-tooltip>
            <template #header>
              <el-icon><EditPen /></el-icon>
              拼音助记码
            </template>
          </el-table-column>
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
            <template #header>
              <el-icon><Operation /></el-icon>
              操作
            </template>
            <template #default="scope">
              <div class="operation-btn-group">
                <el-button type="primary" link size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDelete(scope.row.id)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 空状态提示 -->
        <div v-if="tableData.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无检查项目数据" />
        </div>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-area" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageSizeChange"
          @current-change="handleCurrentPageChange"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑检查项目' : '新增检查项目'"
      width="680px"
      :before-close="handleClose"
      class="check-item-form-dialog"
    >
      <div class="form-container">
        <!-- 编辑时显示ID -->
        <div v-if="!isAdd" class="readonly-field">
          <span class="field-label">项目ID</span>
          <span class="field-value">{{ form.id }}</span>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          label-width="100px"
          :rules="formRules"
          class="check-item-form"
        >
          <div class="form-row">
            <el-form-item label="项目编码" prop="itemCode" class="form-item required">
              <el-input 
                v-model="form.itemCode" 
                placeholder="请输入项目编码" 
                class="form-input"
                :class="{ 'error': formErrors.itemCode }"
              />
              <div v-if="formErrors.itemCode" class="error-message">{{ formErrors.itemCode }}</div>
            </el-form-item>
            <el-form-item label="项目名称" prop="itemName" class="form-item required">
              <el-input 
                v-model="form.itemName" 
                placeholder="请输入项目名称" 
                class="form-input"
                :class="{ 'error': formErrors.itemName }"
              />
              <div v-if="formErrors.itemName" class="error-message">{{ formErrors.itemName }}</div>
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="规格" prop="format" class="form-item">
              <el-input 
                v-model="form.format" 
                placeholder="请输入规格" 
                class="form-input"
              />
            </el-form-item>
            <el-form-item label="单价" prop="price" class="form-item required">
              <el-input-number 
                v-model="form.price" 
                :min="0" 
                :precision="2"
                :step="0.01"
                class="form-input"
                controls-position="right"
                placeholder="请输入单价"
                :class="{ 'error': formErrors.price }"
              >
                <template #append>元</template>
              </el-input-number>
              <div v-if="formErrors.price" class="error-message">{{ formErrors.price }}</div>
            </el-form-item>
          </div>

          <div class="form-row">
            <el-form-item label="费用科目ID" prop="expClassID" class="form-item required">
              <el-input-number 
                v-model="form.expClassID" 
                :min="1" 
                class="form-input"
                controls-position="right"
                placeholder="请输入费用科目ID"
                :class="{ 'error': formErrors.expClassID }"
              />
              <div v-if="formErrors.expClassID" class="error-message">{{ formErrors.expClassID }}</div>
            </el-form-item>
            <el-form-item label="执行科室ID" prop="deptID" class="form-item required">
              <el-input-number 
                v-model="form.deptID" 
                :min="1" 
                class="form-input"
                controls-position="right"
                placeholder="请输入执行科室ID"
                :class="{ 'error': formErrors.deptID }"
              />
              <div v-if="formErrors.deptID" class="error-message">{{ formErrors.deptID }}</div>
            </el-form-item>
          </div>

          <el-form-item label="拼音助记码" prop="mnemonicCode" class="full-width">
            <el-input 
              v-model="form.mnemonicCode" 
              placeholder="请输入拼音助记码" 
              class="form-input"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleClose" class="cancel-btn">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleSave" 
            :loading="saveLoading"
            class="submit-btn"
          >
            {{ form.id ? '更新' : '确定' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Plus, Monitor, Search, Document, Tickets, Money, 
  Collection, OfficeBuilding, EditPen, Operation 
} from '@element-plus/icons-vue';
import axios from 'axios';

// 使用完整后端URL
const API_BASE = 'http://localhost:8080';

// 响应式数据
const searchQuery = ref('');
const tableData = ref([]);
const allData = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const formRef = ref(null);
const saveLoading = ref(false);
const isSearching = ref(false);
const isAdd = ref(true);

// 分页相关数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 表单数据
const form = reactive({
  id: '',
  itemCode: '',
  itemName: '',
  format: '',
  price: 0,
  expClassID: 0,
  deptID: 0,
  mnemonicCode: '',
  creationDate: '',
  lastUpdateDate: ''
});

// 表单错误信息
const formErrors = reactive({
  itemCode: '',
  itemName: '',
  price: '',
  expClassID: '',
  deptID: ''
});

// 表单验证规则
const formRules = reactive({
  itemCode: [{ required: true, message: '请输入项目编码', trigger: 'blur' }],
  itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  expClassID: [{ required: true, message: '请输入费用科目ID', trigger: 'blur' }],
  deptID: [{ required: true, message: '请输入执行科室ID', trigger: 'blur' }]
});

// API调用函数
const api = {
  getAll: () => axios.get(`${API_BASE}/api/fmeditem/list`),
  getById: (id) => axios.get(`${API_BASE}/api/fmeditem/${id}`),
  create: (data) => axios.post(`${API_BASE}/api/fmeditem`, data),
  update: (id, data) => axios.put(`${API_BASE}/api/fmeditem/${id}`, data),
  delete: (id) => axios.delete(`${API_BASE}/api/fmeditem/${id}`)
};

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

/** 价格格式化 */
const formatPrice = (price) => {
  if (!price) return '0.00';
  const numPrice = typeof price === 'string' ? parseFloat(price) : price;
  return numPrice.toFixed(2);
};

// 筛选并分页数据
const filterTableData = () => {
  let filteredData = [...allData.value];
  
  // 执行搜索过滤
  if (searchQuery.value.trim()) {
    const keyword = searchQuery.value.trim().toLowerCase();
    filteredData = filteredData.filter(item => 
      (item.itemName && item.itemName.toLowerCase().includes(keyword)) ||
      (item.itemCode && item.itemCode.toLowerCase().includes(keyword)) ||
      (item.mnemonicCode && item.mnemonicCode.toLowerCase().includes(keyword))
    );
    isSearching.value = true;
  } else {
    isSearching.value = false;
  }
  
  // 更新总数据量
  pagination.total = filteredData.length;
  
  // 执行分页计算
  const startIndex = (pagination.currentPage - 1) * pagination.pageSize;
  const endIndex = startIndex + pagination.pageSize;
  tableData.value = filteredData.slice(startIndex, endIndex);
};

// 获取所有数据
const getAllItems = async () => {
  loading.value = true;
  try {
    const response = await api.getAll();
    allData.value = response.data || [];
    filterTableData();
    console.log('获取所有数据:', allData.value.length, '条');
  } catch (error) {
    console.error('获取数据失败:', error);
    ElMessage.error('获取数据失败：' + (error.response?.data?.message || error.message));
    allData.value = [];
    filterTableData();
  } finally {
    loading.value = false;
  }
};

// 搜索功能
const handleSearch = () => {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }
  
  pagination.currentPage = 1;
  filterTableData();
  
  if (tableData.value.length === 0) {
    ElMessage.info('没有找到匹配的项目');
  }
};

// 重置搜索功能
const handleResetSearch = () => {
  searchQuery.value = '';
  pagination.currentPage = 1;
  filterTableData();
  ElMessage.info('已重置搜索条件');
};

// 分页事件处理
const handlePageSizeChange = (val) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
  filterTableData();
};

const handleCurrentPageChange = (val) => {
  pagination.currentPage = val;
  filterTableData();
};

// 表单验证
const validateForm = () => {
  clearFormErrors();
  let isValid = true;

  if (!form.itemCode.trim()) {
    formErrors.itemCode = '请输入项目编码';
    isValid = false;
  }

  if (!form.itemName.trim()) {
    formErrors.itemName = '请输入项目名称';
    isValid = false;
  }

  if (!form.price || form.price < 0) {
    formErrors.price = '请输入有效的单价';
    isValid = false;
  }

  if (!form.expClassID || form.expClassID < 1) {
    formErrors.expClassID = '请输入有效的费用科目ID';
    isValid = false;
  }

  if (!form.deptID || form.deptID < 1) {
    formErrors.deptID = '请输入有效的执行科室ID';
    isValid = false;
  }

  return isValid;
};

// 清除表单错误信息
const clearFormErrors = () => {
  Object.keys(formErrors).forEach(key => {
    formErrors[key] = '';
  });
};

// 新增/编辑
const handleAdd = () => {
  isAdd.value = true;
  resetForm();
  dialogVisible.value = true;
  nextTick(() => {
    clearFormErrors();
    if (formRef.value) {
      formRef.value.clearValidate();
    }
  });
};

const handleEdit = (row) => {
  isAdd.value = false;
  Object.assign(form, {
    ...row,
    price: Number(row.price) || 0,
    expClassID: Number(row.expClassID) || 0,
    deptID: Number(row.deptID) || 0
  });
  dialogVisible.value = true;
  nextTick(() => {
    clearFormErrors();
    if (formRef.value) {
      formRef.value.clearValidate();
    }
  });
};

// 保存数据
const handleSave = async () => {
  if (!validateForm()) {
    ElMessage.warning('请完善表单信息');
    return;
  }
  
  saveLoading.value = true;
  try {
    let response;
    if (form.id) {
      response = await api.update(form.id, form);
    } else {
      response = await api.create(form);
    }
    
    if (response.data) {
      ElMessage.success(form.id ? '编辑成功' : '新增成功');
      dialogVisible.value = false;
      getAllItems();
    } else {
      ElMessage.error(form.id ? '编辑失败' : '新增失败');
    }
  } catch (error) {
    console.error('保存错误:', error);
    const errorMsg = error.response?.data?.message || error.message || '操作失败';
    ElMessage.error(errorMsg);
  } finally {
    saveLoading.value = false;
  }
};

// 删除数据
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该检查项目吗？此操作不可恢复。',
      '删除确认',
      { 
        confirmButtonText: '确定删除', 
        cancelButtonText: '取消', 
        type: 'warning'
      }
    );
    
    const response = await api.delete(id);
    if (response.data) {
      ElMessage.success('删除成功');
      const currentPageDataCount = tableData.value.length;
      await getAllItems();
      
      if (currentPageDataCount === 1 && pagination.currentPage > 1) {
        pagination.currentPage -= 1;
        filterTableData();
      }
    } else {
      ElMessage.error('删除失败');
    }
  } catch (error) {
    if (error === 'cancel') return;
    const errorMsg = error.response?.data?.message || error.message || '删除失败';
    ElMessage.error(errorMsg);
  }
};

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: '',
    itemCode: '',
    itemName: '',
    format: '',
    price: 0,
    expClassID: 0,
    deptID: 0,
    mnemonicCode: '',
    creationDate: '',
    lastUpdateDate: ''
  });
  clearFormErrors();
};

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false;
  resetForm();
  if (formRef.value) {
    formRef.value.clearValidate();
  }
};

// 初始化
onMounted(() => {
  getAllItems();
});
</script>

<style scoped>
/* 页面基础样式 */
.check-item-management {
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
.check-item-table {
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

/* 项目名称样式 */
.item-name {
  font-weight: 500;
  color: #165DFF;
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
.check-item-form-dialog {
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

.check-item-form {
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

.full-width {
  width: 100%;
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

:deep(.form-input.error .el-input__wrapper),
:deep(.form-input.error .el-input-number) {
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
  .check-item-table {
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