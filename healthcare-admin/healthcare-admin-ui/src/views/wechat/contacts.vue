<template>
  <div class="wechat-contacts-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-form">
            <el-input v-model="searchForm.keyword" placeholder="姓名/手机号" style="width: 180px" clearable />
            <el-select v-model="searchForm.staffId" placeholder="跟进人" clearable style="width: 120px; margin-left: 10px">
              <el-option v-for="staff in staffList" :key="staff.userId" :label="staff.realName" :value="staff.userId" />
            </el-select>
            <el-button type="primary" style="margin-left: 10px" @click="handleSearch">查询</el-button>
          </div>
        </div>
      </template>

      <el-table :data="contactList" v-loading="loading" border>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">{{ row.name?.charAt(0) }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="corpName" label="企业" width="150" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="staffName" label="跟进人" width="100" />
        <el-table-column prop="tagNames" label="标签" min-width="150">
          <template #default="{ row }">
            <el-tag v-for="tag in (row.tagNames || '').split(',').filter(Boolean)" :key="tag" size="small" style="margin-right: 5px">{{ tag }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="添加时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button link type="primary" @click="handleSendMessage(row)">发消息</el-button>
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
const contactList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({ keyword: '', staffId: undefined as number | undefined })
const staffList = ref([
  { userId: 1, realName: '李医生' },
  { userId: 2, realName: '王管家' },
  { userId: 3, realName: '张顾问' }
])

const getList = async () => {
  loading.value = true
  try {
    contactList.value = [
      { externalUserId: 'ext001', name: '张三', avatar: '', phone: '138****8001', corpName: 'ABC公司', position: '经理', staffName: '李医生', tagNames: '高血压,高血脂', createTime: '2026-01-10 10:00:00' },
      { externalUserId: 'ext002', name: '李四', avatar: '', phone: '138****8002', corpName: '', position: '', staffName: '王管家', tagNames: '糖尿病', createTime: '2026-01-15 14:00:00' }
    ]
    total.value = 2
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pageNum.value = 1; getList() }
const handleView = (row: any) => ElMessage.info(`查看${row.name}详情`)
const handleSendMessage = (row: any) => ElMessage.info(`发送消息给${row.name}`)

onMounted(() => { getList() })
</script>

<style scoped>
.wechat-contacts-container { padding: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { display: flex; align-items: center; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
