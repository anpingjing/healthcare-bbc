<template>
  <div class="health-profile-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-form">
            <el-input
              v-model="searchForm.keyword"
              placeholder="姓名/手机号"
              style="width: 180px"
              clearable
            />
            <el-select v-model="searchForm.healthLevel" placeholder="健康等级" clearable style="width: 120px; margin-left: 10px">
              <el-option label="低风险" value="low" />
              <el-option label="中风险" value="medium" />
              <el-option label="高风险" value="high" />
            </el-select>
            <el-button type="primary" style="margin-left: 10px" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
          <el-button type="primary" @click="handleAdd">新建档案</el-button>
        </div>
      </template>

      <el-table :data="profileList" v-loading="loading" border>
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="60">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="age" label="年龄" width="60">
          <template #default="{ row }">
            {{ calculateAge(row.birthDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="healthScore" label="健康评分" width="100">
          <template #default="{ row }">
            <el-progress 
              :percentage="row.healthScore || 0" 
              :color="getScoreColor(row.healthScore)"
              :stroke-width="10"
              :show-text="false"
            />
            <span style="margin-left: 5px">{{ row.healthScore || 0 }}分</span>
          </template>
        </el-table-column>
        <el-table-column prop="healthLevel" label="健康等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getHealthLevelType(row.healthLevel)">
              {{ getHealthLevelText(row.healthLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskTags" label="风险标签" min-width="150">
          <template #default="{ row }">
            <el-tag 
              v-for="tag in (row.riskTags || '').split(',').filter(Boolean)" 
              :key="tag" 
              size="small" 
              style="margin-right: 5px"
            >
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="doctorName" label="家庭医生" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleAssignRoles(row)">分配</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="form.realName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="form.birthDate" type="date" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身高(cm)" prop="height">
              <el-input-number v-model="form.height" :min="50" :max="250" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)" prop="weight">
              <el-input-number v-model="form.weight" :min="10" :max="300" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血型" prop="bloodType">
              <el-select v-model="form.bloodType" style="width: 100%">
                <el-option label="A型" value="A" />
                <el-option label="B型" value="B" />
                <el-option label="AB型" value="AB" />
                <el-option label="O型" value="O" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="过敏史" prop="allergies">
          <el-input v-model="form.allergies" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="既往病史" prop="medicalHistory">
          <el-input v-model="form.medicalHistory" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="慢性病" prop="chronicDiseases">
          <el-input v-model="form.chronicDiseases" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配服务角色对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配服务角色" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="家庭医生">
          <el-select v-model="assignForm.doctorId" placeholder="请选择" style="width: 100%">
            <el-option v-for="doctor in doctorList" :key="doctor.userId" :label="doctor.realName" :value="doctor.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="健康管家">
          <el-select v-model="assignForm.healthManagerId" placeholder="请选择" style="width: 100%">
            <el-option v-for="manager in managerList" :key="manager.userId" :label="manager.realName" :value="manager.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="权益顾问">
          <el-select v-model="assignForm.benefitAdvisorId" placeholder="请选择" style="width: 100%">
            <el-option v-for="advisor in benefitAdvisorList" :key="advisor.userId" :label="advisor.realName" :value="advisor.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="保险顾问">
          <el-select v-model="assignForm.insuranceAdvisorId" placeholder="请选择" style="width: 100%">
            <el-option v-for="advisor in insuranceAdvisorList" :key="advisor.userId" :label="advisor.realName" :value="advisor.userId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const profileList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  healthLevel: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  profileId: undefined,
  realName: '',
  gender: 1,
  birthDate: '',
  phone: '',
  height: 170,
  weight: 65,
  bloodType: '',
  emergencyContact: '',
  emergencyPhone: '',
  allergies: '',
  medicalHistory: '',
  chronicDiseases: ''
})

const rules: FormRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
}

// 分配角色
const assignDialogVisible = ref(false)
const assignForm = reactive({
  profileId: undefined,
  doctorId: undefined,
  healthManagerId: undefined,
  benefitAdvisorId: undefined,
  insuranceAdvisorId: undefined
})

const doctorList = ref([])
const managerList = ref([])
const benefitAdvisorList = ref([])
const insuranceAdvisorList = ref([])

// 计算年龄
const calculateAge = (birthDate: string) => {
  if (!birthDate) return '-'
  const birth = new Date(birthDate)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const monthDiff = today.getMonth() - birth.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--
  }
  return age
}

// 获取评分颜色
const getScoreColor = (score: number) => {
  if (score >= 80) return '#67C23A'
  if (score >= 60) return '#E6A23C'
  return '#F56C6C'
}

// 获取健康等级
const getHealthLevelText = (level: string) => {
  const map: Record<string, string> = {
    low: '低风险',
    medium: '中风险',
    high: '高风险'
  }
  return map[level] || '未知'
}

const getHealthLevelType = (level: string) => {
  const map: Record<string, string> = {
    low: 'success',
    medium: 'warning',
    high: 'danger'
  }
  return map[level] || 'info'
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    profileList.value = [
      { profileId: 1, realName: '张三', gender: 1, phone: '13800138001', birthDate: '1980-05-15', healthScore: 85, healthLevel: 'low', riskTags: '高血压', doctorName: '李医生', status: 1 },
      { profileId: 2, realName: '李四', gender: 2, phone: '13800138002', birthDate: '1975-08-20', healthScore: 72, healthLevel: 'medium', riskTags: '糖尿病,高血脂', doctorName: '王医生', status: 1 },
      { profileId: 3, realName: '王五', gender: 1, phone: '13800138003', birthDate: '1990-03-10', healthScore: 92, healthLevel: 'low', riskTags: '', doctorName: '李医生', status: 1 }
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  getList()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.healthLevel = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新建健康档案'
  Object.assign(form, {
    profileId: undefined,
    realName: '',
    gender: 1,
    birthDate: '',
    phone: '',
    height: 170,
    weight: 65,
    bloodType: '',
    emergencyContact: '',
    emergencyPhone: '',
    allergies: '',
    medicalHistory: '',
    chronicDiseases: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑健康档案'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = (row: any) => {
  ElMessage.info(`查看档案详情: ${row.realName}`)
}

const handleAssignRoles = (row: any) => {
  assignForm.profileId = row.profileId
  assignDialogVisible.value = true
}

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

const handleAssignSubmit = () => {
  ElMessage.success('分配成功')
  assignDialogVisible.value = false
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val: number) => {
  pageNum.value = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.health-profile-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
