<template>
  <div class="keyword-reply-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-form">
            <el-select v-model="selectedGroupId" placeholder="选择社群" style="width: 200px">
              <el-option v-for="group in groupList" :key="group.groupId" :label="group.groupName" :value="group.groupId" />
            </el-select>
            <el-button type="primary" style="margin-left: 10px" @click="getList">查询</el-button>
          </div>
          <el-button type="primary" @click="handleAdd">新增关键词</el-button>
        </div>
      </template>

      <el-table :data="replyList" v-loading="loading" border>
        <el-table-column prop="keyword" label="关键词" width="150" />
        <el-table-column prop="matchType" label="匹配类型" width="100">
          <template #default="{ row }">
            {{ getMatchTypeText(row.matchType) }}
          </template>
        </el-table-column>
        <el-table-column prop="replyContent" label="回复内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="contentType" label="内容类型" width="100">
          <template #default="{ row }">
            {{ getContentTypeText(row.contentType) }}
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80" sortable />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="关键词" prop="keyword">
          <el-input v-model="form.keyword" placeholder="触发关键词" />
        </el-form-item>
        <el-form-item label="匹配类型" prop="matchType">
          <el-select v-model="form.matchType" style="width: 100%">
            <el-option label="精确匹配" :value="1" />
            <el-option label="包含匹配" :value="2" />
            <el-option label="开头匹配" :value="3" />
            <el-option label="结尾匹配" :value="4" />
            <el-option label="正则匹配" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容类型" prop="contentType">
          <el-select v-model="form.contentType" style="width: 100%">
            <el-option label="文本" :value="1" />
            <el-option label="图片" :value="2" />
            <el-option label="链接" :value="3" />
            <el-option label="小程序" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复内容" prop="replyContent">
          <el-input
            v-model="form.replyContent"
            type="textarea"
            :rows="5"
            placeholder="自动回复的内容"
          />
        </el-form-item>
        <el-form-item label="媒体URL" prop="mediaUrl" v-if="form.contentType !== 1">
          <el-input v-model="form.mediaUrl" placeholder="图片/链接地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-input-number v-model="form.priority" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="触发限制" prop="triggerLimit">
              <el-input-number v-model="form.triggerLimit" :min="0" style="width: 100%" />
              <div class="form-tip">每人每天触发次数，0表示不限制</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="生效时间">
          <el-time-picker v-model="form.startTime" placeholder="开始时间" format="HH:mm" />
          <span style="margin: 0 10px">至</span>
          <el-time-picker v-model="form.endTime" placeholder="结束时间" format="HH:mm" />
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

const loading = ref(false)
const replyList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const selectedGroupId = ref<number>()
const groupList = ref([
  { groupId: 1, groupName: '高血压管理群' },
  { groupId: 2, groupName: '健康交流群' },
  { groupId: 3, groupName: '保险服务群' }
])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  replyId: undefined,
  groupId: undefined,
  keyword: '',
  matchType: 2,
  replyContent: '',
  contentType: 1,
  mediaUrl: '',
  priority: 0,
  startTime: null,
  endTime: null,
  triggerLimit: 0,
  status: 1
})

const rules: FormRules = {
  keyword: [{ required: true, message: '请输入关键词', trigger: 'blur' }],
  matchType: [{ required: true, message: '请选择匹配类型', trigger: 'change' }],
  replyContent: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
}

const getMatchTypeText = (type: number) => {
  const map: Record<number, string> = {
    1: '精确匹配',
    2: '包含匹配',
    3: '开头匹配',
    4: '结尾匹配',
    5: '正则匹配'
  }
  return map[type] || '未知'
}

const getContentTypeText = (type: number) => {
  const map: Record<number, string> = {
    1: '文本',
    2: '图片',
    3: '链接',
    4: '小程序'
  }
  return map[type] || '未知'
}

const getList = async () => {
  if (!selectedGroupId.value) {
    ElMessage.warning('请先选择社群')
    return
  }
  loading.value = true
  try {
    replyList.value = [
      { replyId: 1, groupId: 1, keyword: '血压', matchType: 2, replyContent: '正常血压范围：收缩压90-140mmHg，舒张压60-90mmHg', contentType: 1, priority: 10, status: 1 },
      { replyId: 2, groupId: 1, keyword: '用药', matchType: 2, replyContent: '请按医嘱规律服药，如有不适请及时联系医生', contentType: 1, priority: 8, status: 1 },
      { replyId: 3, groupId: 1, keyword: '预约', matchType: 2, replyContent: '点击链接预约医生：https://example.com/booking', contentType: 3, priority: 5, status: 1 }
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  if (!selectedGroupId.value) {
    ElMessage.warning('请先选择社群')
    return
  }
  isEdit.value = false
  dialogTitle.value = '新增关键词回复'
  Object.assign(form, {
    replyId: undefined,
    groupId: selectedGroupId.value,
    keyword: '',
    matchType: 2,
    replyContent: '',
    contentType: 1,
    mediaUrl: '',
    priority: 0,
    startTime: null,
    endTime: null,
    triggerLimit: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑关键词回复'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除关键词 "${row.keyword}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    getList()
  })
}

const handleStatusChange = (row: any) => {
  ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
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

const handleSizeChange = (val: number) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val: number) => {
  pageNum.value = val
  getList()
}

onMounted(() => {
  // 默认选择第一个社群
  if (groupList.value.length > 0) {
    selectedGroupId.value = groupList.value[0].groupId
    getList()
  }
})
</script>

<style scoped>
.keyword-reply-container {
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

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
