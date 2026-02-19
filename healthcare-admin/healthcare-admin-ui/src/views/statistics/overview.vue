<template>
  <div class="statistics-overview-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon user-icon"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalUsers }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon profile-icon"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalProfiles }}</div>
            <div class="stat-label">健康档案</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon group-icon"><el-icon><ChatDotRound /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalGroups }}</div>
            <div class="stat-label">企微群数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon service-icon"><el-icon><Service /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalServices }}</div>
            <div class="stat-label">服务次数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header><span>健康等级分布</span></template>
          <div class="chart-container">
            <div class="level-item" v-for="(count, level) in statistics.levelDistribution" :key="level">
              <span class="level-label">{{ getLevelText(level as string) }}</span>
              <el-progress :percentage="getPercentage(count as number)" :color="getLevelColor(level as string)" :stroke-width="20" />
              <span class="level-count">{{ count }}人</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>服务类型统计</span></template>
          <div class="chart-container">
            <div class="service-item" v-for="item in statistics.serviceTypeStats" :key="item.type">
              <span class="service-label">{{ getServiceTypeText(item.type) }}</span>
              <el-progress :percentage="item.percentage" :stroke-width="20" />
              <span class="service-count">{{ item.count }}次</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>近期服务记录</span>
              <el-radio-group v-model="timeRange" size="small">
                <el-radio-button label="week">近7天</el-radio-button>
                <el-radio-button label="month">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <el-table :data="recentServices" border>
            <el-table-column prop="userName" label="用户" width="100" />
            <el-table-column prop="serviceType" label="服务类型" width="120">
              <template #default="{ row }">{{ getServiceTypeText(row.serviceType) }}</template>
            </el-table-column>
            <el-table-column prop="staffName" label="服务人员" width="100" />
            <el-table-column prop="content" label="服务内容" show-overflow-tooltip />
            <el-table-column prop="satisfaction" label="满意度" width="150">
              <template #default="{ row }"><el-rate v-model="row.satisfaction" disabled /></template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

const timeRange = ref('week')

const statistics = reactive({
  totalUsers: 1234,
  totalProfiles: 856,
  totalGroups: 56,
  totalServices: 3289,
  levelDistribution: { low: 450, medium: 280, high: 126 },
  serviceTypeStats: [
    { type: 1, count: 1200, percentage: 36 },
    { type: 2, count: 800, percentage: 24 },
    { type: 3, count: 600, percentage: 18 },
    { type: 4, count: 689, percentage: 22 }
  ]
})

const recentServices = ref([
  { userName: '张三', serviceType: 1, staffName: '李医生', content: '高血压用药咨询', satisfaction: 5, createTime: '2026-02-05 10:30:00' },
  { userName: '李四', serviceType: 2, staffName: '王顾问', content: '健康权益咨询', satisfaction: 4, createTime: '2026-02-05 14:00:00' },
  { userName: '王五', serviceType: 3, staffName: '张顾问', content: '保险产品介绍', satisfaction: 5, createTime: '2026-02-05 15:30:00' }
])

const getLevelText = (level: string) => ({ low: '低风险', medium: '中风险', high: '高风险' }[level] || level)
const getLevelColor = (level: string) => ({ low: '#67C23A', medium: '#E6A23C', high: '#F56C6C' }[level] || '#409EFF')
const getServiceTypeText = (type: number) => ({ 1: '健康咨询', 2: '权益服务', 3: '保险咨询', 4: '回访' }[type] || '未知')
const getPercentage = (count: number) => Math.round((count / 856) * 100)

onMounted(() => {})
</script>

<style scoped>
.statistics-overview-container { padding: 0; }

.stat-card { display: flex; align-items: center; padding: 10px; }
.stat-icon { width: 60px; height: 60px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; color: #fff; margin-right: 15px; }
.user-icon { background-color: #409EFF; }
.profile-icon { background-color: #67C23A; }
.group-icon { background-color: #E6A23C; }
.service-icon { background-color: #F56C6C; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 5px; }

.chart-container { padding: 20px; }
.level-item, .service-item { display: flex; align-items: center; margin-bottom: 20px; }
.level-label, .service-label { width: 80px; font-size: 14px; color: #606266; }
.level-count, .service-count { width: 80px; text-align: right; font-size: 14px; color: #909399; }
</style>
