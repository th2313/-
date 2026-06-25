<template>
  <div class="page-container">
    <h1 class="page-title">报表输出</h1>
    <div class="page-content">
      <p>该页面用于生成各类统计报表，包括：</p>
      <ul class="feature-list">
        <li>病人统计报表</li>
        <li>诊疗情况报表</li>
        <li>工作量统计报表</li>
        <li>报表导出与打印</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
// 报表输出组件逻辑将在这里实现
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #fff;
  height: 100%;
}

.page-title {
  color: #165DFF;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.page-content {
  line-height: 1.6;
}

.feature-list {
  margin-top: 10px;
  padding-left: 20px;
}

.feature-list li {
  margin: 8px 0;
}
</style>
