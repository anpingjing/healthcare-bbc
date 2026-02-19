<template>
  <div class="group-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-form">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入社群名称"
              style="width: 200px"
              clearable
            />
            <el-select v-model="searchForm.groupType" placeholder="社群类型" clearable style="width: 150px; margin-left: 10px">
              <el-option label="健康交流" :value="1" />
              <el-option label="慢病管理" :value="2" />
              <el-option label="保险服务" :value="3" />
              <el-option label="综合群" :value="4" />
            </el-select>
            <el-button type="primary" style="margin-left: 10px" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
          <el-button type="primary" @click="handleAdd">创建社群</el-button>
        </div>
      </template>

      <el-table :data="groupList" v-loading="loading" border>
        <el-table-column prop="groupName" label="社群名称" min-width="150" />
        <el-table-column prop="groupType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getGroupTypeType(row.groupType)">
              {{ getGroupTypeText(row.groupType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="memberCount" label="成员数" width="100">
          <template #default="{ row }">
            {{ row.memberCount }} / {{ row.maxMembers }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '已解散' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDissolve(row)">解散</el-button>
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

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="社群名称" prop="groupName">
          <el-input v-model="form.groupName" />
        </el-form-item>
        <el-form-item label="社群类型" prop="groupType">
          <el-select v-model="form.groupType" style="width: 100%">
            <el-option label="健康交流" :value="1" />
            <el-option label="慢病管理" :value="2" />
            <el-option label="保险服务" :value="3" />
            <el-option label="综合群" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="社群描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="最大成员数" prop="maxMembers">
          <el-input-number v-model="form.maxMembers" :min="10" :max="500" />
        </el-form-item>
        <el-form-item label="入群方式" prop="joinType">
          <el-radio-group v-model="form.joinType">
            <el-radio :label="1">自动通过</el-radio>
            <el-radio :label="2">需要审核</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="欢迎语" prop="welcomeMsg">
          <el-input v-model="form.welcomeMsg" type="textarea" rows="3" placeholder="用户入群后自动发送的欢迎语" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  groupType: undefined as number | undefined
})

// 数据
const loading = ref(false)
const groupList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  groupId: undefined,
  groupName: '',
  groupType: 1,
  description: '',
  maxMembers: 200,
  joinType: 1,
  welcomeMsg: ''
})

const rules: FormRules = {
  groupName: [{ required: true, message: '请输入社群名称', trigger: 'blur' }],
  groupType: [{ required: true, message: '请选择社群类型', trigger: 'change' }]
}

// 获取社群类型文本
const getGroupTypeText = (type: number) => {
  const map: Record<number, string> = {
    1: '健康交流',
    2: '慢病管理',
    3: '保险服务',
    4: '综合群'
  }
  return map[type] || '未知'
}

// 获取社群类型标签样式
const getGroupTypeType = (type: number) => {
  const map: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'primary',
    4: 'info'
  }
  return map[type] || ''
}

// 获取社群列表
const getList = async () => {
  loading.value = true
  try {
    // TODO: 调用API获取数据
    groupList.value = [
      { groupId: 1, groupName: '高血压管理群', groupType: 2, memberCount: 128, maxMembers: 200, status: 1, createTime: '2026-01-15 10:00:00' },
      { groupId: 2, groupName: '健康交流群', groupType: 1, memberCount: 256, maxMembers: 500, status: 1, createTime: '2026-01-20 14:00:00' },
      { groupId: 3, groupName: '保险服务群', groupType: 3, memberCount: 89, maxMembers: 200, status: 1, createTime: '2026-01-25 09:00:00' }
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  pageNum.value = 1
  getList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.groupType = undefined
  handleSearch()
}

// 创建社群
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '创建社群'
  Object.assign(form, {
    groupId: undefined,
    groupName: '',
    groupType: 1,
    description: '',
    maxMembers: 200,
    joinType: 1,
    welcomeMsg: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑社群'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 查看详情
const handleView = (row: any) => {
  ElMessage.info(`查看社群详情: ${row.groupName}`)
}

// 解散社群
const handleDissolve = (row: any) => {
  ElMessageBox.confirm(`确定要解散社群 "${row.groupName}" 吗？解散后不可恢复！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('社群已解散')
    getList()
  })
}

// 提交
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

// 分页
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
.group-container {
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
