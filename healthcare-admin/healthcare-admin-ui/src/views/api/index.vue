<template>
  <div class="api-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-form">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入API名称/路径"
              style="width: 200px"
              clearable
            />
            <el-select v-model="searchForm.apiCategory" placeholder="分类" clearable style="width: 150px; margin-left: 10px">
              <el-option label="企微API" :value="1" />
              <el-option label="业务API" :value="2" />
              <el-option label="第三方API" :value="3" />
            </el-select>
            <el-button type="primary" style="margin-left: 10px" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
          <el-button type="primary" @click="handleAdd">新增API</el-button>
        </div>
      </template>

      <el-table :data="apiList" v-loading="loading" border>
        <el-table-column prop="apiName" label="API名称" min-width="150" />
        <el-table-column prop="httpMethod" label="方法" width="80">
          <template #default="{ row }">
            <el-tag :type="getMethodType(row.httpMethod)" size="small">
              {{ row.httpMethod }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="apiPath" label="路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="apiCategory" label="分类" width="100">
          <template #default="{ row }">
            {{ getCategoryText(row.apiCategory) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleTest(row)">测试</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="API编码" prop="apiCode">
              <el-input v-model="form.apiCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="API名称" prop="apiName">
              <el-input v-model="form.apiName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="apiCategory">
              <el-select v-model="form.apiCategory" style="width: 100%">
                <el-option label="企微API" :value="1" />
                <el-option label="业务API" :value="2" />
                <el-option label="第三方API" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="HTTP方法" prop="httpMethod">
              <el-select v-model="form.httpMethod" style="width: 100%">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
                <el-option label="PATCH" value="PATCH" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="API路径" prop="apiPath">
          <el-input v-model="form.apiPath" placeholder="/api/v1/xxx" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" />
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
  apiCategory: undefined as number | undefined
})

// 数据
const loading = ref(false)
const apiList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  apiId: undefined,
  apiCode: '',
  apiName: '',
  apiCategory: 1,
  httpMethod: 'GET',
  apiPath: '',
  description: ''
})

const rules: FormRules = {
  apiCode: [{ required: true, message: '请输入API编码', trigger: 'blur' }],
  apiName: [{ required: true, message: '请输入API名称', trigger: 'blur' }],
  apiCategory: [{ required: true, message: '请选择分类', trigger: 'change' }],
  httpMethod: [{ required: true, message: '请选择HTTP方法', trigger: 'change' }],
  apiPath: [{ required: true, message: '请输入API路径', trigger: 'blur' }]
}

// 获取HTTP方法标签样式
const getMethodType = (method: string) => {
  const map: Record<string, string> = {
    'GET': 'success',
    'POST': 'primary',
    'PUT': 'warning',
    'DELETE': 'danger',
    'PATCH': 'info'
  }
  return map[method] || ''
}

// 获取分类文本
const getCategoryText = (category: number) => {
  const map: Record<number, string> = {
    1: '企微API',
    2: '业务API',
    3: '第三方API'
  }
  return map[category] || '未知'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    1: '开发中',
    2: '测试中',
    3: '已发布',
    4: '已废弃'
  }
  return map[status] || '未知'
}

// 获取状态标签样式
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'danger'
  }
  return map[status] || ''
}

// 获取API列表
const getList = async () => {
  loading.value = true
  try {
    // TODO: 调用API获取数据
    apiList.value = [
      { apiId: 1, apiName: '获取用户列表', httpMethod: 'GET', apiPath: '/api/v1/users', apiCategory: 2, status: 3, createTime: '2026-01-15 10:00:00' },
      { apiId: 2, apiName: '创建用户', httpMethod: 'POST', apiPath: '/api/v1/users', apiCategory: 2, status: 3, createTime: '2026-01-15 10:00:00' },
      { apiId: 3, apiName: '获取客户列表', httpMethod: 'GET', apiPath: '/cgi-bin/externalcontact/list', apiCategory: 1, status: 3, createTime: '2026-01-20 14:00:00' }
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
  searchForm.apiCategory = undefined
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增API'
  Object.assign(form, {
    apiId: undefined,
    apiCode: '',
    apiName: '',
    apiCategory: 1,
    httpMethod: 'GET',
    apiPath: '',
    description: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑API'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 测试
const handleTest = (row: any) => {
  ElMessage.info(`测试API: ${row.apiName}`)
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除API "${row.apiName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
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
.api-container {
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
