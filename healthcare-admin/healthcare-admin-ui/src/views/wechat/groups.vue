<template>
  <div class="wechat-groups-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-form">
            <el-input v-model="searchForm.keyword" placeholder="群名称" style="width: 180px" clearable />
            <el-button type="primary" style="margin-left: 10px" @click="handleSearch">查询</el-button>
          </div>
          <el-button type="primary" @click="handleCreateGroup">创建企微群</el-button>
        </div>
      </template>

      <el-table :data="groupList" v-loading="loading" border>
        <el-table-column prop="groupName" label="群名称" min-width="150" />
        <el-table-column prop="chatId" label="群ID" width="200" />
        <el-table-column prop="memberCount" label="成员数" width="100" />
        <el-table-column prop="ownerName" label="群主" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '已解散' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewMembers(row)">成员</el-button>
            <el-button link type="primary" @click="handleInvite(row)">邀请</el-button>
            <el-button link type="primary" @click="handleSendMessage(row)">发消息</el-button>
            <el-button link type="danger" @click="handleDismiss(row)">解散</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, sizes, prev, pager, next" />
      </div>
    </el-card>

    <!-- 创建群对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建企微群" width="500px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="群名称">
          <el-input v-model="createForm.groupName" />
        </el-form-item>
        <el-form-item label="群主">
          <el-select v-model="createForm.ownerId" style="width: 100%">
            <el-option v-for="user in staffList" :key="user.userId" :label="user.realName" :value="user.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="初始成员">
          <el-select v-model="createForm.userIds" multiple style="width: 100%">
            <el-option v-for="user in staffList" :key="user.userId" :label="user.realName" :value="user.userId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSubmit">创建</el-button>
      </template>
    </el-dialog>

    <!-- 发送消息对话框 -->
    <el-dialog v-model="messageDialogVisible" title="发送群消息" width="500px">
      <el-form :model="messageForm" label-width="100px">
        <el-form-item label="消息类型">
          <el-radio-group v-model="messageForm.msgType">
            <el-radio label="text">文本</el-radio>
            <el-radio label="image">图片</el-radio>
            <el-radio label="link">链接</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="消息内容">
          <el-input v-model="messageForm.content" type="textarea" rows="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="messageDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSendSubmit">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const groupList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({ keyword: '' })
const createDialogVisible = ref(false)
const messageDialogVisible = ref(false)

const createForm = reactive({ groupName: '', ownerId: undefined as number | undefined, userIds: [] as number[] })
const messageForm = reactive({ chatId: '', msgType: 'text', content: '' })

const staffList = ref([
  { userId: 1, realName: '李医生' },
  { userId: 2, realName: '王管家' },
  { userId: 3, realName: '张顾问' }
])

const getList = async () => {
  loading.value = true
  try {
    groupList.value = [
      { groupId: 1, groupName: '高血压管理群', chatId: 'wrOgQhDgAAzzz', memberCount: 128, ownerName: '李医生', createTime: '2026-01-15 10:00:00', status: 1 },
      { groupId: 2, groupName: '健康交流群', chatId: 'wrOgQhDgAayyy', memberCount: 256, ownerName: '王管家', createTime: '2026-01-20 14:00:00', status: 1 }
    ]
    total.value = 2
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pageNum.value = 1; getList() }
const handleCreateGroup = () => { createDialogVisible.value = true }
const handleViewMembers = (row: any) => ElMessage.info(`查看${row.groupName}成员列表`)
const handleInvite = (row: any) => ElMessage.info(`邀请成员加入${row.groupName}`)
const handleSendMessage = (row: any) => { messageForm.chatId = row.chatId; messageDialogVisible.value = true }
const handleDismiss = (row: any) => {
  ElMessageBox.confirm(`确定要解散群"${row.groupName}"吗？`, '警告', { type: 'warning' }).then(() => {
    ElMessage.success('群已解散')
    getList()
  })
}

const handleCreateSubmit = () => { ElMessage.success('创建成功'); createDialogVisible.value = false; getList() }
const handleSendSubmit = () => { ElMessage.success('消息发送成功'); messageDialogVisible.value = false }

onMounted(() => { getList() })
</script>

<style scoped>
.wechat-groups-container { padding: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { display: flex; align-items: center; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
