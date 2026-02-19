<template>
  <div class="health-data-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-form">
            <el-input v-model="searchForm.keyword" placeholder="用户姓名" style="width: 150px" clearable />
            <el-select v-model="searchForm.dataType" placeholder="数据类型" clearable style="width: 120px; margin-left: 10px">
              <el-option label="血压" :value="1" />
              <el-option label="血糖" :value="2" />
              <el-option label="心率" :value="3" />
              <el-option label="体重" :value="4" />
              <el-option label="步数" :value="5" />
            </el-select>
            <el-date-picker v-model="searchForm.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" style="margin-left: 10px" />
            <el-button type="primary" style="margin-left: 10px" @click="handleSearch">查询</el-button>
          </div>
          <el-button type="primary" @click="handleExport">导出数据</el-button>
        </div>
      </template>

      <el-table :data="dataList" v-loading="loading" border>
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="dataType" label="数据类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getDataTypeText(row.dataType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dataValue" label="数值" width="120">
          <template #default="{ row }">
            <span :class="{ 'abnormal-value': row.isAbnormal }">{{ row.dataValue }} {{ row.dataUnit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="measureTime" label="测量时间" width="180" />
        <el-table-column prop="deviceType" label="设备类型" width="120" />
        <el-table-column prop="isAbnormal" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isAbnormal ? 'danger' : 'success'">
              {{ row.isAbnormal ? '异常' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewTrend(row)">趋势</el-button>
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
import { ElMessage } from 'element-plus'

const loading = ref(false)
const dataList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({ keyword: '', dataType: undefined as number | undefined, dateRange: [] as any[] })

const getDataTypeText = (type: number) => ({ 1: '血压', 2: '血糖', 3: '心率', 4: '体重', 5: '步数' }[type] || '未知')

const getList = async () => {
  loading.value = true
  try {
    dataList.value = [
      { dataId: 1, userName: '张三', dataType: 1, dataValue: '135/85', dataUnit: 'mmHg', measureTime: '2026-02-05 08:30:00', deviceType: '血压计', isAbnormal: 1, remark: '晨起测量' },
      { dataId: 2, userName: '张三', dataType: 2, dataValue: '6.2', dataUnit: 'mmol/L', measureTime: '2026-02-05 07:00:00', deviceType: '血糖仪', isAbnormal: 0, remark: '空腹血糖' },
      { dataId: 3, userName: '李四', dataType: 3, dataValue: '72', dataUnit: 'bpm', measureTime: '2026-02-05 09:00:00', deviceType: '手环', isAbnormal: 0, remark: '' }
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pageNum.value = 1; getList() }
const handleExport = () => ElMessage.success('数据导出中...')
const handleViewTrend = (row: any) => ElMessage.info(`查看${row.userName}的${getDataTypeText(row.dataType)}趋势`)

onMounted(() => { getList() })
</script>

<style scoped>
.health-data-container { padding: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { display: flex; align-items: center; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.abnormal-value { color: #F56C6C; font-weight: bold; }
</style>
