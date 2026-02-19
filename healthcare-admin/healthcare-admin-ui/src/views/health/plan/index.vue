<template>
  <div class="health-plan-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-form">
            <el-input v-model="searchForm.keyword" placeholder="用户姓名" style="width: 180px" clearable />
            <el-select v-model="searchForm.planType" placeholder="计划类型" clearable style="width: 120px; margin-left: 10px">
              <el-option label="运动计划" :value="1" />
              <el-option label="饮食计划" :value="2" />
              <el-option label="慢病管理" :value="3" />
              <el-option label="康复计划" :value="4" />
            </el-select>
            <el-button type="primary" style="margin-left: 10px" @click="handleSearch">查询</el-button>
          </div>
          <el-button type="primary" @click="handleAdd">创建计划</el-button>
        </div>
      </template>

      <el-table :data="planList" v-loading="loading" border>
        <el-table-column prop="planName" label="计划名称" min-width="150" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="planType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getPlanTypeTag(row.planType)">{{ getPlanTypeText(row.planType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelType(row.riskLevel)">{{ getRiskLevelText(row.riskLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="doctorApproved" label="医生审核" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.doctorApproved === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="row.doctorApproved === 2" type="danger">已拒绝</el-tag>
            <el-tag v-else type="info">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="completionRate" label="完成率" width="120">
          <template #default="{ row }">
            <el-progress :percentage="row.completionRate || 0" :stroke-width="10" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleApprove(row)" v-if="row.doctorApproved === 0">审核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next"
        />
      </div>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="form.planName" />
        </el-form-item>
        <el-form-item label="计划类型" prop="planType">
          <el-select v-model="form.planType" style="width: 100%">
            <el-option label="运动计划" :value="1" />
            <el-option label="饮食计划" :value="2" />
            <el-option label="慢病管理" :value="3" />
            <el-option label="康复计划" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="健康目标" prop="healthGoals">
          <el-input v-model="form.healthGoals" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="干预措施" prop="interventions">
          <el-input v-model="form.interventions" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="饮食建议" prop="dietAdvice">
          <el-input v-model="form.dietAdvice" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="运动计划" prop="exercisePlan">
          <el-input v-model="form.exercisePlan" type="textarea" rows="2" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="form.endDate" type="date" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-radio-group v-model="form.riskLevel">
            <el-radio :label="1">低风险</el-radio>
            <el-radio :label="2">中风险</el-radio>
            <el-radio :label="3">高风险</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const planList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({ keyword: '', planType: undefined as number | undefined })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  planId: undefined,
  planName: '',
  planType: 1,
  healthGoals: '',
  interventions: '',
  dietAdvice: '',
  exercisePlan: '',
  startDate: '',
  endDate: '',
  riskLevel: 1
})

const rules: FormRules = {
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  planType: [{ required: true, message: '请选择计划类型', trigger: 'change' }]
}

const getPlanTypeText = (type: number) => ({ 1: '运动计划', 2: '饮食计划', 3: '慢病管理', 4: '康复计划' }[type] || '未知')
const getPlanTypeTag = (type: number) => ({ 1: 'success', 2: 'warning', 3: 'danger', 4: 'info' }[type] || '')
const getRiskLevelText = (level: number) => ({ 1: '低', 2: '中', 3: '高' }[level] || '未知')
const getRiskLevelType = (level: number) => ({ 1: 'success', 2: 'warning', 3: 'danger' }[level] || 'info')
const getStatusText = (status: number) => ({ 0: '禁用', 1: '进行中', 2: '已完成', 3: '已暂停' }[status] || '未知')
const getStatusType = (status: number) => ({ 0: 'danger', 1: 'success', 2: 'info', 3: 'warning' }[status] || '')

const getList = async () => {
  loading.value = true
  try {
    planList.value = [
      { planId: 1, planName: '高血压管理计划', userName: '张三', planType: 3, riskLevel: 2, doctorApproved: 1, completionRate: 65, status: 1 },
      { planId: 2, planName: '减重运动计划', userName: '李四', planType: 1, riskLevel: 1, doctorApproved: 1, completionRate: 80, status: 1 }
    ]
    total.value = 2
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pageNum.value = 1; getList() }
const handleAdd = () => { isEdit.value = false; dialogTitle.value = '创建健康计划'; dialogVisible.value = true }
const handleEdit = (row: any) => { isEdit.value = true; dialogTitle.value = '编辑健康计划'; Object.assign(form, row); dialogVisible.value = true }
const handleView = (row: any) => ElMessage.info(`查看计划: ${row.planName}`)
const handleApprove = (row: any) => ElMessage.success('审核通过')

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      getList()
    }
  })
}

onMounted(() => { getList() })
</script>

<style scoped>
.health-plan-container { padding: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { display: flex; align-items: center; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
