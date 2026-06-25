<template>
  <div class="visit-record-page">
    <el-page-header content="患者多次就医记录管理" />

    <el-card shadow="hover" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="患者住院编号（hspID）">
          <el-input 
            v-model="searchForm.hspID" 
            placeholder="请输入纯字符串hspID（如0011，无特殊符号）" 
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="getVisitRecordsByHspID"
            :loading="isLoading"
          >
            {{ isLoading ? '查询中...' : '查询记录' }}
          </el-button>
          <el-button type="success" @click="showAddDialog = true" style="margin-left: 10px;">
            新增就医记录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格渲染前先判空，避免undefined调用length -->
    <el-card shadow="hover" class="table-card" v-if="visitRecords && visitRecords.length > 0">
      <el-table :data="visitRecords" border stripe>
        <el-table-column label="ID" prop="id" width="80" align="center" />
        <el-table-column label="患者hspID" prop="hspID" width="120" align="center" />
        <el-table-column label="接诊医生ID" prop="doctorID" width="120" align="center" />
        <el-table-column label="就诊科室" prop="departName" width="150" align="center" />
        <el-table-column label="就诊类型" prop="visitType" width="120" align="center" />
        <el-table-column label="就诊时间" prop="visitTime" width="200" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.visitTime) }}
          </template>
        </el-table-column>
        <el-table-column label="患者主诉" prop="mainComplaint" align="center" />
        <el-table-column label="就诊状态" prop="visitStatus" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.visitStatus)">
              {{ scope.row.visitStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-empty description="暂无就医记录，请先查询或新增" v-else class="empty-tip" />

    <el-dialog 
      v-model="showAddDialog" 
      :title="isEdit ? '编辑就医记录' : '新增就医记录'"
      width="600px"
    >
      <el-form 
        :model="form" 
        ref="formRef" 
        :rules="formRules" 
        label-width="120px"
      >
        <el-form-item label="患者hspID" prop="hspID">
          <el-input 
            v-model="form.hspID" 
            :disabled="isEdit" 
            placeholder="请输入纯字符串hspID（如0011）"
          />
        </el-form-item>
        <el-form-item label="接诊医生ID" prop="doctorID">
          <el-input v-model="form.doctorID" placeholder="请输入医生ID" />
        </el-form-item>
        <el-form-item label="关联病历ID（可选）" prop="recordID">
          <el-input v-model="form.recordID" placeholder="若有病历，输入病历ID" />
        </el-form-item>
        <el-form-item label="就诊类型" prop="visitType">
          <el-select v-model="form.visitType" placeholder="请选择就诊类型">
            <el-option label="门诊" value="门诊" />
            <el-option label="住院" value="住院" />
            <el-option label="复诊" value="复诊" />
          </el-select>
        </el-form-item>
        <el-form-item label="就诊时间" prop="visitTime">
          <el-date-picker 
            v-model="form.visitTime" 
            type="datetime" 
            placeholder="选择就诊时间"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="患者主诉" prop="mainComplaint">
          <el-input 
            v-model="form.mainComplaint" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入患者主要症状"
          />
        </el-form-item>
        <el-form-item label="就诊状态" prop="visitStatus">
          <el-select v-model="form.visitStatus" placeholder="请选择就诊状态">
            <el-option label="已完成" value="已完成" />
            <el-option label="治疗中" value="治疗中" />
            <el-option label="已出院" value="已出院" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="2" 
            placeholder="可选，输入额外说明"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="showAddDialog = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox, ElTag, ElEmpty, ElPageHeader, ElButton } from 'element-plus';
import axios from 'axios';

const searchForm = reactive({ hspID: '' });
const visitRecords = ref([]); // 初始化为空数组，避免undefined
const showAddDialog = ref(false);
const isEdit = ref(false);
const formRef = ref(null);
const form = reactive({
  id: '',
  hspID: '',
  doctorID: '',
  recordID: '',
  departName: '',
  visitType: '',
  visitTime: '',
  mainComplaint: '',
  visitStatus: '',
  remark: ''
});
const isLoading = ref(false); // 新增加载状态

const formRules = {
  hspID: [
    { required: true, message: '患者hspID不能为空', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: 'hspID仅支持字母和数字，无特殊符号', trigger: 'blur' }
  ],
  doctorID: [{ required: true, message: '医生ID不能为空', trigger: 'blur' }],
  visitType: [{ required: true, message: '就诊类型不能为空', trigger: 'change' }],
  visitTime: [{ required: true, message: '就诊时间不能为空', trigger: 'change' }],
  mainComplaint: [{ required: true, message: '患者主诉不能为空', trigger: 'blur' }],
  visitStatus: [{ required: true, message: '就诊状态不能为空', trigger: 'change' }]
};

const getVisitRecordsByHspID = async () => {
  if (!searchForm.hspID.trim()) {
    ElMessage.warning('请输入患者hspID');
    return;
  }
  isLoading.value = true;
  try {
    const res = await axios.get(`/visitRecord/hsp/${searchForm.hspID.trim()}`);
    // 假设接口返回格式为{ code: 200, data: [] }，请根据实际后端格式调整
    if (res.data && res.data.code === 200) {
      visitRecords.value = res.data.data || [];
    } else {
      ElMessage.error(res.data?.msg || '查询失败，请检查接口返回格式');
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || '查询失败，请稍后再试');
  } finally {
    isLoading.value = false;
  }
};

const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleString();
};

const getStatusTagType = (status) => {
  switch (status) {
    case '已完成':
      return 'success';
    case '治疗中':
      return 'warning';
    case '已出院':
      return 'info';
    default:
      return 'default';
  }
};

const handleEdit = (row) => {
  Object.assign(form, row);
  isEdit.value = true;
  showAddDialog.value = true;
};

const handleDelete = async (id) => {
  await ElMessageBox.confirm(
    '确认删除该就医记录吗？删除后不可恢复',
    '提示',
    { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
  );
  try {
    await axios.delete(`/visitRecord/${id}`);
    ElMessage.success('删除成功');
    getVisitRecordsByHspID();
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || '删除失败');
  }
};

const submitForm = async () => {
  await formRef.value.validate();
  try {
    if (isEdit.value) {
      await axios.put(`/visitRecord/${form.id}`, form);
      ElMessage.success('编辑成功');
    } else {
      await axios.post('/visitRecord', form);
      ElMessage.success('新增成功');
    }
    showAddDialog.value = false;
    getVisitRecordsByHspID();
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || '操作失败');
  }
};
</script>

<style scoped>
.visit-record-page {
  padding: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
.empty-tip {
  margin-top: 20px;
}
</style>