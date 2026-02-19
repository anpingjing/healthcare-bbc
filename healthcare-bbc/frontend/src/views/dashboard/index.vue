<template>
  <div class="dashboard-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企微健康社群工作台</span>
        </div>
      </template>
      <div class="welcome-text">
        欢迎使用企微健康社群方案管理系统
      </div>
      <el-row :gutter="20" class="m-t-20">
        <el-col :span="6">
          <el-statistic title="今日新增用户" :value="12" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="待处理咨询" :value="5" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="活跃计划" :value="88" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="转换保单" :value="3" />
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}
.welcome-text {
  font-size: 24px;
  color: #409EFF;
}
.m-t-20 {
  margin-top: 20px;
}
</style>
