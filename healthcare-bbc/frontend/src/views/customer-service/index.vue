<template>
  <div class="customer-service-container">
    <el-row :gutter="20">
      <!-- 左侧会话列表 -->
      <el-col :span="6">
        <el-card class="session-list-card">
          <template #header>
            <div class="card-header">
              <span>会话列表</span>
              <el-button type="primary" size="small" @click="refreshSessions">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <el-input
            v-model="searchQuery"
            placeholder="搜索用户"
            prefix-icon="Search"
            clearable
            class="search-input"
          />
          <div class="session-list">
            <div
              v-for="session in filteredSessions"
              :key="session.sessionId"
              :class="['session-item', { active: currentSession?.sessionId === session.sessionId }]"
              @click="selectSession(session)"
            >
              <div class="session-info">
                <div class="user-name">{{ session.wechatExternalUserId }}</div>
                <div class="session-status">
                  <el-tag size="small" :type="session.status === 1 ? 'success' : 'info'">
                    {{ session.status === 1 ? '进行中' : '已结束' }}
                  </el-tag>
                </div>
              </div>
              <div class="agent-name">客服: {{ session.agentName || '未分配' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 中间聊天区域 -->
      <el-col :span="12">
        <el-card class="chat-card">
          <template #header>
            <div class="chat-header">
              <span v-if="currentSession">{{ currentSession.wechatExternalUserId }}</span>
              <span v-else>请选择会话</span>
              <div v-if="currentSession" class="chat-actions">
                <el-button type="danger" size="small" @click="closeCurrentSession">
                  结束会话
                </el-button>
              </div>
            </div>
          </template>

          <div v-if="currentSession" class="chat-container">
            <div class="messages-container" ref="messagesContainer">
              <div
                v-for="message in messages"
                :key="message.messageId"
                :class="['message-item', message.direction === 1 ? 'incoming' : 'outgoing']"
              >
                <div class="message-content">
                  <div class="message-text">{{ message.content }}</div>
                  <div class="message-time">{{ formatTime(message.createTime) }}</div>
                </div>
              </div>
            </div>

            <div class="input-container">
              <el-input
                v-model="newMessage"
                type="textarea"
                :rows="3"
                placeholder="输入消息..."
                @keyup.enter.ctrl="sendMessage"
              />
              <div class="input-actions">
                <el-button type="primary" @click="sendMessage" :disabled="!newMessage.trim()">
                  发送
                </el-button>
              </div>
            </div>
          </div>

          <el-empty v-else description="请从左侧选择一个会话" />
        </el-card>
      </el-col>

      <!-- 右侧AI分析区域 -->
      <el-col :span="6">
        <el-card class="ai-analysis-card">
          <template #header>
            <div class="card-header">
              <span>AI智能分析</span>
            </div>
          </template>

          <div v-if="aiAnalysis" class="analysis-content">
            <div class="analysis-section">
              <h4>意图识别</h4>
              <el-descriptions :column="1" border>
                <el-descriptions-item label="意图">
                  {{ aiAnalysis.intent?.intentName }}
                </el-descriptions-item>
                <el-descriptions-item label="分类">
                  {{ aiAnalysis.intent?.intentCategory }}
                </el-descriptions-item>
                <el-descriptions-item label="目标角色">
                  <el-tag :type="getRoleTagType(aiAnalysis.intent?.targetRole)">
                    {{ getRoleName(aiAnalysis.intent?.targetRole) }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="analysis-section">
              <h4>情感分析</h4>
              <el-tag :type="getSentimentType(aiAnalysis.sentiment)">
                {{ getSentimentName(aiAnalysis.sentiment) }}
              </el-tag>
            </div>

            <div class="analysis-section">
              <h4>紧急程度</h4>
              <el-tag :type="getUrgencyType(aiAnalysis.urgency)">
                {{ getUrgencyName(aiAnalysis.urgency) }}
              </el-tag>
            </div>

            <div class="analysis-section">
              <h4>关键词</h4>
              <el-tag
                v-for="keyword in aiAnalysis.keywords"
                :key="keyword"
                size="small"
                class="keyword-tag"
              >
                {{ keyword }}
              </el-tag>
            </div>

            <div class="analysis-section">
              <h4>AI建议回复</h4>
              <el-input
                v-model="aiSuggestion"
                type="textarea"
                :rows="4"
                readonly
              />
              <el-button type="primary" size="small" @click="useAiSuggestion" class="use-suggestion-btn">
                使用此回复
              </el-button>
            </div>
          </div>

          <el-empty v-else description="选择会话查看AI分析" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import {
  getActiveSessions,
  getRecentMessages,
  sendMessage as apiSendMessage,
  closeSession as apiCloseSession,
  analyzeMessage,
  generateAiResponse,
  type CustomerServiceSession,
  type CustomerMessage
} from '../../api/customerService'

const sessions = ref<CustomerServiceSession[]>([])
const currentSession = ref<CustomerServiceSession | null>(null)
const messages = ref<CustomerMessage[]>([])
const newMessage = ref('')
const searchQuery = ref('')
const aiAnalysis = ref<any>(null)
const aiSuggestion = ref('')
const messagesContainer = ref<HTMLElement>()

const currentAgentId = 1 // 当前客服ID，实际应从登录信息获取

const filteredSessions = computed(() => {
  if (!searchQuery.value) return sessions.value
  return sessions.value.filter(s =>
    s.wechatExternalUserId.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const refreshSessions = async () => {
  try {
    sessions.value = await getActiveSessions(currentAgentId)
  } catch (error) {
    ElMessage.error('获取会话列表失败')
  }
}

const selectSession = async (session: CustomerServiceSession) => {
  currentSession.value = session
  await loadMessages()
  await analyzeCurrentSession()
}

const loadMessages = async () => {
  if (!currentSession.value) return
  try {
    messages.value = await getRecentMessages(currentSession.value.wechatExternalUserId, 50)
    scrollToBottom()
  } catch (error) {
    ElMessage.error('获取消息失败')
  }
}

const analyzeCurrentSession = async () => {
  if (!currentSession.value || messages.value.length === 0) return
  
  const lastMessage = messages.value[messages.value.length - 1]
  if (lastMessage.direction === 2) return // 如果是客服发送的消息，不分析

  try {
    aiAnalysis.value = await analyzeMessage(lastMessage.content)
    aiSuggestion.value = await generateAiResponse(
      currentSession.value.wechatExternalUserId,
      lastMessage.content
    )
  } catch (error) {
    console.error('AI分析失败', error)
  }
}

const sendMessage = async () => {
  if (!currentSession.value || !newMessage.value.trim()) return
  
  try {
    await apiSendMessage(
      currentSession.value.wechatExternalUserId,
      newMessage.value,
      currentAgentId
    )
    newMessage.value = ''
    await loadMessages()
  } catch (error) {
    ElMessage.error('发送消息失败')
  }
}

const closeCurrentSession = async () => {
  if (!currentSession.value) return
  
  try {
    await apiCloseSession(currentSession.value.sessionId)
    ElMessage.success('会话已结束')
    currentSession.value = null
    aiAnalysis.value = null
    aiSuggestion.value = ''
    await refreshSessions()
  } catch (error) {
    ElMessage.error('结束会话失败')
  }
}

const useAiSuggestion = () => {
  newMessage.value = aiSuggestion.value
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString()
}

const getRoleTagType = (role: string) => {
  const types: Record<string, any> = {
    'DOCTOR': 'success',
    'ADVISOR': 'warning',
    'MANAGER': 'info',
    'AI': 'primary'
  }
  return types[role] || 'info'
}

const getRoleName = (role: string) => {
  const names: Record<string, string> = {
    'DOCTOR': '医生',
    'ADVISOR': '保险顾问',
    'MANAGER': '健康管家',
    'AI': 'AI助手'
  }
  return names[role] || role
}

const getSentimentType = (sentiment: string) => {
  const types: Record<string, any> = {
    'POSITIVE': 'success',
    'NEGATIVE': 'danger',
    'NEUTRAL': 'info'
  }
  return types[sentiment] || 'info'
}

const getSentimentName = (sentiment: string) => {
  const names: Record<string, string> = {
    'POSITIVE': '积极',
    'NEGATIVE': '消极',
    'NEUTRAL': '中性'
  }
  return names[sentiment] || sentiment
}

const getUrgencyType = (urgency: string) => {
  const types: Record<string, any> = {
    'HIGH': 'danger',
    'MEDIUM': 'warning',
    'LOW': 'success'
  }
  return types[urgency] || 'info'
}

const getUrgencyName = (urgency: string) => {
  const names: Record<string, string> = {
    'HIGH': '高',
    'MEDIUM': '中',
    'LOW': '低'
  }
  return names[urgency] || urgency
}

onMounted(() => {
  refreshSessions()
})
</script>

<style scoped lang="scss">
.customer-service-container {
  padding: 20px;
  height: calc(100vh - 100px);

  .el-row {
    height: 100%;
  }

  .el-col {
    height: 100%;
  }

  .el-card {
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  :deep(.el-card__body) {
    flex: 1;
    overflow: hidden;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.session-list-card {
  .search-input {
    margin-bottom: 15px;
  }

  .session-list {
    overflow-y: auto;
    height: calc(100% - 80px);
  }

  .session-item {
    padding: 12px;
    border-bottom: 1px solid #ebeef5;
    cursor: pointer;
    transition: background-color 0.3s;

    &:hover {
      background-color: #f5f7fa;
    }

    &.active {
      background-color: #ecf5ff;
    }

    .session-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 5px;

      .user-name {
        font-weight: bold;
        color: #303133;
      }
    }

    .agent-name {
      font-size: 12px;
      color: #909399;
    }
  }
}

.chat-card {
  .chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .chat-container {
    display: flex;
    flex-direction: column;
    height: 100%;
  }

  .messages-container {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    background-color: #f5f5f5;
    border-radius: 4px;
    margin-bottom: 15px;
  }

  .message-item {
    margin-bottom: 15px;
    display: flex;

    &.incoming {
      justify-content: flex-start;

      .message-content {
        background-color: #fff;
        border: 1px solid #ebeef5;
      }
    }

    &.outgoing {
      justify-content: flex-end;

      .message-content {
        background-color: #409eff;
        color: #fff;
      }
    }

    .message-content {
      max-width: 70%;
      padding: 10px 15px;
      border-radius: 8px;

      .message-text {
        word-wrap: break-word;
        line-height: 1.5;
      }

      .message-time {
        font-size: 12px;
        margin-top: 5px;
        opacity: 0.7;
      }
    }
  }

  .input-container {
    .input-actions {
      margin-top: 10px;
      text-align: right;
    }
  }
}

.ai-analysis-card {
  .analysis-content {
    overflow-y: auto;
    height: 100%;
  }

  .analysis-section {
    margin-bottom: 20px;

    h4 {
      margin-bottom: 10px;
      color: #303133;
      font-size: 14px;
    }

    .keyword-tag {
      margin-right: 5px;
      margin-bottom: 5px;
    }

    .use-suggestion-btn {
      margin-top: 10px;
      width: 100%;
    }
  }
}
</style>
