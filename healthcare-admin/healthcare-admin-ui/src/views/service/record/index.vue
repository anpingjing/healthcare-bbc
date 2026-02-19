<template>
  <div class="service-record-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-form">
            <el-input v-model="searchForm.keyword" placeholder="用户姓名" style="width: 150px" clearable />
            <el-select v-model="searchForm.serviceType" placeholder="服务类型" clearable style="width: 120px; margin-left: 10px">
              <el-option label="健康咨询" :value="1" />
              <el-option label="权益服务" :value="2" />
              <el-option label="保险咨询" :value="3" />
              <el-option label="回访" :value="4" />
            </el-select>
            <el-button type="primary" style="margin-left: 10px" @click="handleSearch">查询</el-button>
          </div>
        </div>
      </template>

      <el-table :data="recordList" v-loading="loading" border>
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="serviceType" label="服务类型" width="100">
          <template #default="{ row }">{{ getServiceTypeText(row.serviceType) }}</template>
        </el-table-column>
        <el-table-column prop="serviceRole" label="服务角色" width="100">
          <template #default="{ row }">{{ getRoleText(row.serviceRole) }}</template>
        </el-table-column>
        <el-table-column prop="staffName" label="服务人员" width="100" />
        <el-table-column prop="content" label="服务内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="satisfaction" label="满意度" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.satisfaction" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="服务时间" width="180" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, sizes, prev, pager, next" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

const loading = ref(false)
const recordList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({ keyword: '', serviceType: undefined as number | undefined })

const getServiceTypeText = (type: number) => ({ 1: '健康咨询', 2: '权益服务', 3: '保险咨询', 4: '回访' }[type] || '未知')
const getRoleText = (role: number) => ({ 1: '家庭医生', 2: '健康管家', 3: '权益顾问', 4: '保险顾问' }[role] || '未知')
const getStatusText = (status: number) => ({ 0: '已取消', 1: '进行中', 2: '已完成' }[status] || '未知')
const getStatusType = (status: number) => ({ 0: 'info', 1: 'warning', 2: 'success' }[status] || '')

const getList = async () => {
  loading.value = true
  try {
    recordList.value = [
      { recordId: 1, userName: '张三', serviceType: 1, serviceRole: 1, staffName: '李医生', content: '高血压用药咨询，建议调整用药方案', satisfaction: 5, createTime: '2026-02-05 10:30:00', status: 2 },
      { recordId: 2, userName: '李四', serviceType: 2, serviceRole: 3, staffName: '王顾问', content: '健康权益咨询，介绍体检套餐', satisfaction: 4, createTime: '2026-02-05 14:00:00', status: 2 }
    ]
    total.value = 2
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pageNum.value = 1; getList() }

onMounted(() => { getList() })
</script>

<style scoped>
.service-record-container { padding: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { display: flex; align-items: center; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
