<template>
  <div class="template-management">
    <!-- 标题区域 -->
    <h2 class="page-title">
      <el-icon class="title-icon"><Files /></el-icon>
      病历模板管理
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
          <span class="btn-text">新增模板</span>
        </div>
      </el-button>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索模板ID、主诉或初步诊断"
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
      <el-button type="default" @click="resetSearch" class="reset-btn">重置</el-button>
      
      <!-- 导入导出按钮组 -->
      <div class="import-export-group">
        <el-upload
          class="import-upload"
          action="http://localhost:8080/templateinfo/import" 
          :on-success="handleImportSuccess"
          :on-error="handleImportError"
          :file-list="importFileList"
          accept=".xlsx,.xls"
          :auto-upload="true"
          :show-file-list="false"
        >
          <el-button type="default" class="import-btn">
            <el-icon><Upload /></el-icon> 导入模板
          </el-button>
        </el-upload>
        
        <el-button type="default" @click="handleExport" class="export-btn">
          <el-icon><Download /></el-icon> 导出选中
        </el-button>
      </div>
    </div>

    <!-- 搜索状态提示 -->
    <div v-if="isSearching" class="search-status">
      <el-alert
        :title="`搜索『${searchKeyword}』，共找到 ${filteredTemplateList.length} 条记录`"
        type="info"
        :closable="false"
        show-icon
      >
        <template #action>
          <el-button type="primary" text @click="resetSearch">显示全部</el-button>
        </template>
      </el-alert>
    </div>

    <!-- 模板表格和分页容器 -->
    <div class="table-container">
      <div class="template-table">
        <el-table 
          :data="currentPageData" 
          border 
          stripe 
          style="width: 100%"
          v-loading="loading"
          :cell-style="{ padding: '8px 12px', lineHeight: '1.4' }"
          :header-cell-style="{ background: '#f5f7fa', padding: '12px', fontWeight: '600' }"
          size="small"
          @selection-change="handleSelectionChange"
          ref="tableRef"
        >
          <!-- 多选列 -->
          <el-table-column type="selection" width="55" align="center" fixed="left" />
          <el-table-column type="index" label="序号" width="60" align="center" fixed="left" />
          
          <el-table-column prop="templateID" label="模板ID" width="140" show-overflow-tooltip>
            <template #header>
              <el-icon><Document /></el-icon>
              模板ID
            </template>
            <template #default="scope">
              <span class="template-id">{{ scope.row.templateID }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="主诉" label="主诉" min-width="220" show-overflow-tooltip>
            <template #header>
              <el-icon><ChatDotRound /></el-icon>
              主诉
            </template>
            <template #default="scope">
              <span class="complaint-text">{{ scope.row.主诉 }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="初步诊断" label="初步诊断" min-width="220" show-overflow-tooltip>
            <template #header>
              <el-icon><FirstAidKit /></el-icon>
              初步诊断
            </template>
            <template #default="scope">
              <span class="diagnosis-text">{{ scope.row.初步诊断 }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="创建人" label="创建人" width="140" show-overflow-tooltip>
            <template #header>
              <el-icon><User /></el-icon>
              创建人
            </template>
          </el-table-column>
          
          <el-table-column prop="创建时间" label="创建时间" width="180" align="center">
            <template #header>
              <el-icon><Clock /></el-icon>
              创建时间
            </template>
            <template #default="scope">
              <span class="date-text">{{ formatDate(scope.row.创建时间) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="180" align="center" fixed="right">
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
          <el-empty description="暂无病历模板数据" />
        </div>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-area" v-if="filteredTemplateList.length > 0">
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

    <!-- 模板表单弹窗（已确保闭合） -->
    <el-dialog
      :title="isAdd ? '新增病历模板' : '编辑病历模板'"
      v-model="showForm"
      width="700px"
      :before-close="handleClose"
      class="template-form-dialog"
      destroy-on-close
    >
      <div class="form-container">
        <!-- 编辑时显示ID -->
        <div v-if="!isAdd" class="readonly-field">
          <span class="field-label">模板ID</span>
          <span class="field-value">{{ templateForm.templateID }}</span>
        </div>

        <el-form
          :model="templateForm"
          :rules="rules"
          ref="formRef"
          label-width="100px"
          class="template-form"
          label-position="left"
        >
          <!-- 模板ID：新增可输，编辑禁用 -->
          <el-form-item label="模板ID" prop="templateID" class="required">
            <el-input 
              v-model="templateForm.templateID" 
              placeholder="请输入模板ID" 
              :disabled="!isAdd"
              class="form-input"
              :class="{ 'error': formErrors.templateID }"
            />
            <div v-if="formErrors.templateID" class="error-message">{{ formErrors.templateID }}</div>
          </el-form-item>
          
          <!-- 核心必填字段 -->
          <el-form-item label="主诉" prop="主诉" class="required">
            <el-input 
              v-model="templateForm.主诉" 
              placeholder="请输入患者就诊主要症状" 
              class="form-input"
              :class="{ 'error': formErrors.主诉 }"
            />
            <div v-if="formErrors.主诉" class="error-message">{{ formErrors.主诉 }}</div>
          </el-form-item>
          
          <el-form-item label="初步诊断" prop="初步诊断" class="required">
            <el-input 
              v-model="templateForm.初步诊断" 
              placeholder="请输入初步诊断结果" 
              class="form-input"
              :class="{ 'error': formErrors.初步诊断 }"
            />
            <div v-if="formErrors.初步诊断" class="error-message">{{ formErrors.初步诊断 }}</div>
          </el-form-item>
          
          <el-form-item label="诊疗计划" prop="诊疗计划" class="required">
            <el-input
              v-model="templateForm.诊疗计划"
              placeholder="请输入诊疗计划（如：抗感染治疗+门诊随访）"
              type="textarea"
              :rows="3"
              class="form-input"
              :class="{ 'error': formErrors.诊疗计划 }"
            />
            <div v-if="formErrors.诊疗计划" class="error-message">{{ formErrors.诊疗计划 }}</div>
          </el-form-item>
          
          <!-- 非必填扩展字段 -->
          <el-form-item label="现病史">
            <el-input
              v-model="templateForm.现病史"
              placeholder="请输入现病史（如：发热3天，伴咳嗽咽痛）"
              type="textarea"
              :rows="3"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="既往史">
            <el-input
              v-model="templateForm.既往史"
              placeholder="请输入既往史（如：高血压5年，糖尿病2年）"
              type="textarea"
              :rows="2"
              class="form-input"
            />
          </el-form-item>
          
          <!-- 两列布局：婚育史+月经史 -->
          <div class="form-row">
            <el-form-item label="婚育史" class="form-item">
              <el-input
                v-model="templateForm.婚育史"
                placeholder="请输入婚育史（如：已婚，育1子）"
                type="textarea"
                :rows="2"
                class="form-input"
              />
            </el-form-item>
            <el-form-item label="月经史" class="form-item">
              <el-input
                v-model="templateForm.月经史"
                placeholder="女性患者专属（如：周期28天，末次2025-01-01）"
                type="textarea"
                :rows="2"
                class="form-input"
              />
            </el-form-item>
          </div>
          
          <el-form-item label="家族史">
            <el-input
              v-model="templateForm.家族史"
              placeholder="请输入家族史（如：父亲有冠心病）"
              type="textarea"
              :rows="2"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="体格检查">
            <el-input
              v-model="templateForm.体格检查"
              placeholder="请输入体格检查结果（如：体温36.8℃，心率72次/分）"
              type="textarea"
              :rows="3"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="专科检查">
            <el-input
              v-model="templateForm.专科检查"
              placeholder="请输入专科检查结果（如：肺部听诊无异常）"
              type="textarea"
              :rows="3"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="辅助检查">
            <el-input
              v-model="templateForm.辅助检查"
              placeholder="请输入辅助检查结果（如：血常规：白细胞10.5×10⁹/L）"
              type="textarea"
              :rows="3"
              class="form-input"
            />
          </el-form-item>
          
          <el-form-item label="鉴别诊断">
            <el-input
              v-model="templateForm.鉴别诊断"
              placeholder="请输入鉴别诊断要点（如：与流感鉴别：无肌肉酸痛）"
              type="textarea"
              :rows="2"
              class="form-input"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleClose" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="saveLoading" class="submit-btn">
            {{ isAdd ? '确定' : '更新' }}
          </el-button>
        </div>
      </template>
    </el-dialog> <!-- 已添加闭合标签 -->
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import { 
  Plus, Search, Files, Upload, Download, Document, 
  ChatDotRound, FirstAidKit, User, Clock, Operation 
} from "@element-plus/icons-vue";
import axios from "axios";

// 基础配置
const baseURL = "http://localhost:8080/templateinfo";
const formRef = ref(null);
const tableRef = ref(null);
const showForm = ref(false);
const isAdd = ref(true);
const loading = ref(false);
const saveLoading = ref(false);
const searchKeyword = ref("");
const importFileList = ref([]);
const isSearching = ref(false);
const selectedTemplateIds = ref([]);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);

// 模板列表数据
const templateList = ref([]);
const allData = ref([]);

// 表单数据
const templateForm = reactive({
  templateID: "",
  主诉: "",
  现病史: "",
  既往史: "",
  婚育史: "",
  月经史: "",
  家族史: "",
  体格检查: "",
  专科检查: "",
  辅助检查: "",
  鉴别诊断: "",
  初步诊断: "",
  诊疗计划: "",
  创建人: "",
  创建时间: ""
});

// 表单错误信息
const formErrors = reactive({
  templateID: "",
  主诉: "",
  初步诊断: "",
  诊疗计划: ""
});

// 筛选后的列表
const filteredTemplateList = computed(() => {
  if (!Array.isArray(templateList.value)) return [];
  if (!searchKeyword.value.trim()) return templateList.value;

  const keyword = searchKeyword.value.trim();
  return templateList.value.filter(item => {
    const idMatch = item.templateID?.includes(keyword) || false;
    const complaintMatch = item.主诉?.includes(keyword) || false;
    const diagnosisMatch = item.初步诊断?.includes(keyword) || false;
    return idMatch || complaintMatch || diagnosisMatch;
  });
});

// 当前页显示的数据
const currentPageData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredTemplateList.value.slice(start, end);
});

// 搜索后的总数
const searchTotal = computed(() => {
  return filteredTemplateList.value.length;
});

// 表单校验规则
const rules = {
  templateID: [
    { required: true, message: "请输入模板ID", trigger: "blur" },
    { min: 2, max: 50, message: "模板ID长度应在2-50个字符之间", trigger: "blur" },
    { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_()-]+$/, 
      message: "模板ID仅支持中文、字母、数字及_()-.符号", 
      trigger: "blur" }
  ],
  主诉: [
    { required: true, message: "请输入主诉", trigger: "blur" },
    { min: 2, max: 200, message: "主诉长度应在2-200个字符之间", trigger: "blur" }
  ],
  初步诊断: [
    { required: true, message: "请输入初步诊断", trigger: "blur" },
    { min: 2, max: 200, message: "初步诊断长度应在2-200个字符之间", trigger: "blur" }
  ],
  诊疗计划: [
    { required: true, message: "请输入诊疗计划", trigger: "blur" },
    { min: 2, max: 500, message: "诊疗计划长度应在2-500个字符之间", trigger: "blur" }
  ]
};

// 辅助方法：日期格式化
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

// 生命周期钩子：初始化加载数据
onMounted(() => {
  fetchTemplates();
  templateForm.创建人 = "张医生"; // 模拟当前登录用户
});

// 获取模板列表
const fetchTemplates = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`${baseURL}/list`);
    if (response.data.code === 200) {
      templateList.value = response.data.data || [];
      allData.value = [...templateList.value];
      isSearching.value = false;
      currentPage.value = 1;
    } else {
      templateList.value = [];
      allData.value = [];
      ElMessage.warning("暂无模板数据：" + response.data.msg);
    }
  } catch (error) {
    templateList.value = [];
    allData.value = [];
    ElMessage.error("获取失败：" + (error.response?.data?.msg || error.message));
  } finally {
    loading.value = false;
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
    fetchTemplates();
    return;
  }
  
  currentPage.value = 1;
  isSearching.value = true;
  
  if (tableRef.value) {
    tableRef.value.clearSelection();
    selectedTemplateIds.value = [];
  }
  
  if (filteredTemplateList.value.length === 0) {
    ElMessage.info('没有找到匹配的模板');
  }
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = '';
  templateList.value = allData.value;
  isSearching.value = false;
  currentPage.value = 1;
  if (tableRef.value) {
    tableRef.value.clearSelection();
    selectedTemplateIds.value = [];
  }
  ElMessage.info('已重置搜索条件');
};

// 处理表格勾选变化
const handleSelectionChange = (selectedItems) => {
  selectedTemplateIds.value = selectedItems.map(item => item.templateID);
};

// 表单验证
const validateForm = () => {
  clearFormErrors();
  let isValid = true;

  if (!templateForm.templateID.trim()) {
    formErrors.templateID = '请输入模板ID';
    isValid = false;
  }

  if (!templateForm.主诉.trim()) {
    formErrors.主诉 = '请输入主诉';
    isValid = false;
  }

  if (!templateForm.初步诊断.trim()) {
    formErrors.初步诊断 = '请输入初步诊断';
    isValid = false;
  }

  if (!templateForm.诊疗计划.trim()) {
    formErrors.诊疗计划 = '请输入诊疗计划';
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

// 新增模板
const handleAdd = () => {
  isAdd.value = true;
  resetForm();
  showForm.value = true;
  nextTick(() => {
    clearFormErrors();
    if (formRef.value) {
      formRef.value.clearValidate();
    }
  });
};

// 编辑模板
const handleEdit = (row) => {
  isAdd.value = false;
  Object.assign(templateForm, {
    templateID: row.templateID || "",
    主诉: row.主诉 || "",
    现病史: row.现病史 || "",
    既往史: row.既往史 || "",
    婚育史: row.婚育史 || "",
    月经史: row.月经史 || "",
    家族史: row.家族史 || "",
    体格检查: row.体格检查 || "",
    专科检查: row.专科检查 || "",
    辅助检查: row.辅助检查 || "",
    鉴别诊断: row.鉴别诊断 || "",
    初步诊断: row.初步诊断 || "",
    诊疗计划: row.诊疗计划 || "",
    创建人: row.创建人 || "",
    创建时间: row.创建时间 || ""
  });
  showForm.value = true;
  nextTick(() => {
    clearFormErrors();
    if (formRef.value) {
      formRef.value.clearValidate();
    }
  });
};

// 删除模板
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定删除模板【${row.templateID} - ${row.主诉 || '未命名'}】？删除后不可恢复！`,
      "确认删除",
      { type: "warning", confirmButtonText: "确定删除", cancelButtonText: "取消" }
    );

    const response = await axios.delete(`${baseURL}/${row.templateID}`);
    if (response.data.code === 200) {
      ElMessage.success("删除成功");
      fetchTemplates();
      if (tableRef.value) {
        tableRef.value.clearSelection();
        selectedTemplateIds.value = [];
      }
    } else {
      ElMessage.error("删除失败：" + response.data.msg);
    }
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败：" + (error.response?.data?.msg || error.message));
    }
  }
};

// 提交表单
const submitForm = async () => {
  if (!validateForm()) {
    ElMessage.warning('请完善表单信息');
    return;
  }
  
  saveLoading.value = true;
  
  try {
    let response;
    if (isAdd.value) {
      response = await axios.post(`${baseURL}/add`, templateForm);
    } else {
      response = await axios.put(`${baseURL}/update`, templateForm);
    }

    if (response.data.code === 200) {
      ElMessage.success(isAdd.value ? "新增成功" : "编辑成功");
      showForm.value = false;
      resetForm();
      fetchTemplates();
      if (tableRef.value) {
        tableRef.value.clearSelection();
        selectedTemplateIds.value = [];
      }
    } else {
      ElMessage.error(`${isAdd.value ? "新增" : "编辑"}失败：${response.data.msg}`);
    }
  } catch (error) {
    const errorMsg = error.response?.data?.msg || error.message || "网络错误";
    const statusCode = error.response?.status || "未知状态";
    ElMessage.error(`${isAdd.value ? "新增" : "编辑"}失败(${statusCode})：${errorMsg}`);
  } finally {
    saveLoading.value = false;
  }
};

// 重置表单
const resetForm = () => {
  Object.assign(templateForm, {
    templateID: "",
    主诉: "",
    现病史: "",
    既往史: "",
    婚育史: "",
    月经史: "",
    家族史: "",
    体格检查: "",
    专科检查: "",
    辅助检查: "",
    鉴别诊断: "",
    初步诊断: "",
    诊疗计划: "",
    创建时间: ""
  });
  clearFormErrors();
  if (formRef.value) {
    formRef.value.clearValidate();
  }
};

// 关闭弹窗
const handleClose = () => {
  showForm.value = false;
  resetForm();
};

// 导入成功回调
const handleImportSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success(`导入成功，共导入${response.data}条模板`);
    fetchTemplates();
    if (tableRef.value) {
      tableRef.value.clearSelection();
      selectedTemplateIds.value = [];
    }
  } else {
    ElMessage.error("导入失败：" + (response.msg || "未知错误"));
  }
  importFileList.value = [];
};

// 导入失败回调
const handleImportError = (error) => {
  ElMessage.error("导入失败：" + (error.message || "文件上传错误"));
  importFileList.value = [];
};

// 导出选中的模板
const handleExport = async () => {
  if (selectedTemplateIds.value.length === 0) {
    ElMessage.warning("请先勾选需要导出的模板");
    return;
  }

  try {
    const params = new URLSearchParams();
    selectedTemplateIds.value.forEach(id => {
      params.append("templateIds", id);
    });

    const url = `${baseURL}/exportByIds?${params.toString()}`;
    const response = await axios({
      url,
      method: "GET",
      responseType: "blob"
    });

    const blob = new Blob([response.data], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    });
    const downloadUrl = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = downloadUrl;
    a.download = `选中病历模板_${new Date().getTime()}.xlsx`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(downloadUrl);

    if (tableRef.value) {
      tableRef.value.clearSelection();
      selectedTemplateIds.value = [];
    }
    ElMessage.success("导出成功");
  } catch (error) {
    ElMessage.error("导出失败：" + (error.response?.data?.msg || error.message));
  }
};
</script>

<style scoped>
/* 样式内容与之前一致 */
.template-management {
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
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  gap: 12px;
  flex-wrap: wrap;
}

.search-input {
  width: 400px;
}

.import-export-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

:deep(.import-upload .el-upload) {
  display: block;
}

:deep(.import-btn),
.export-btn {
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 15px;
}

:deep(.import-upload .el-button) {
  height: 32px;
  padding: 8px 15px;
}

.export-btn {
  height: 32px;
  padding: 8px 15px;
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

.template-table {
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

.template-id {
  font-weight: 500;
  color: #165DFF;
}

.complaint-text,
.diagnosis-text {
  color: #606266;
}

.date-text {
  font-size: 12px;
  color: #909399;
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

.template-form-dialog {
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

.template-form {
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

@media (max-width: 768px) {
  .search-area {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input {
    width: 100%;
  }
  
  .import-export-group {
    margin-left: 0;
    justify-content: flex-start;
    width: 100%;
  }
  
  .import-export-group .import-upload,
  .import-export-group .export-btn {
    flex: 1;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .pagination-area {
    justify-content: center;
  }
  
  :deep(.el-pagination) {
    flex-wrap: wrap;
  }
}

@media (max-width: 1200px) {
  .template-table {
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