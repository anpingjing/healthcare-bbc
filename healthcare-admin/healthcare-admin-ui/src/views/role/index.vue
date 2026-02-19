<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>

      <el-table :data="roleList" v-loading="loading" border>
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleType" label="角色类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.roleType === 1" type="success">系统预设</el-tag>
            <el-tag v-else>自定义</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dataScope" label="数据范围" width="120">
          <template #default="{ row }">
            {{ getDataScopeText(row.dataScope) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handlePermission(row)">权限</el-button>
            <el-button link type="danger" @click="handleDelete(row)" :disabled="row.roleType === 1">删除</el-button>
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
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="isEdit && form.roleType === 1" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="角色类型" prop="roleType">
          <el-radio-group v-model="form.roleType" :disabled="isEdit">
            <el-radio :label="1">系统预设</el-radio>
            <el-radio :label="2">自定义</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="form.dataScope" style="width: 100%">
            <el-option label="全部数据" :value="1" />
            <el-option label="部门数据" :value="2" />
            <el-option label="个人数据" :value="3" />
            <el-option label="自定义" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

// 数据
const loading = ref(false)
const roleList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  roleId: undefined,
  roleCode: '',
  roleName: '',
  roleType: 2,
  dataScope: 1,
  description: '',
  status: 1
})

const rules: FormRules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleType: [{ required: true, message: '请选择角色类型', trigger: 'change' }]
}

// 获取数据范围文本
const getDataScopeText = (scope: number) => {
  const map: Record<number, string> = {
    1: '全部数据',
    2: '部门数据',
    3: '个人数据',
    4: '自定义'
  }
  return map[scope] || '未知'
}

// 获取角色列表
const getList = async () => {
  loading.value = true
  try {
    // TODO: 调用API获取数据
    // 模拟数据
    roleList.value = [
      { roleId: 1, roleCode: 'ROLE_SUPER_ADMIN', roleName: '超级管理员', roleType: 1, dataScope: 1, description: '系统最高权限', status: 1, createTime: '2026-01-01 00:00:00' },
      { roleId: 2, roleCode: 'ROLE_ADMIN', roleName: '运营管理员', roleType: 1, dataScope: 1, description: '负责社群运营', status: 1, createTime: '2026-01-01 00:00:00' },
      { roleId: 3, roleCode: 'ROLE_DOCTOR', roleName: '家庭医生', roleType: 1, dataScope: 3, description: '提供医疗服务', status: 1, createTime: '2026-01-01 00:00:00' }
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  Object.assign(form, {
    roleId: undefined,
    roleCode: '',
    roleName: '',
    roleType: 2,
    dataScope: 1,
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除角色 "${row.roleName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    getList()
  })
}

// 权限配置
const handlePermission = (row: any) => {
  // TODO: 打开权限配置对话框
  ElMessage.info(`配置角色权限: ${row.roleName}`)
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
.role-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
