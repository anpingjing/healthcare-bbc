<template>
  <div class="sidebar-container">
    <el-card class="user-info-card">
      <template #header>
        <div class="card-header">
          <span>客户信息</span>
          <el-tag v-if="externalUserId" type="success" size="small">已识别</el-tag>
          <el-tag v-else type="warning" size="small">未识别</el-tag>
        </div>
      </template>

      <div v-if="externalUserId" class="user-info">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="客户ID">{{ externalUserId }}</el-descriptions-item>
          <el-descriptions-item label="聊天类型">{{ chatType === 'single_chat' ? '单聊' : '群聊' }}</el-descriptions-item>
        </el-descriptions>

        <div class="action-buttons">
          <el-button type="primary" @click="viewUserProfile" :disabled="!externalUserId">
            查看客户资料
          </el-button>
          <el-button type="success" @click="sendHealthTips">
            发送健康建议
          </el-button>
        </div>
      </div>

      <el-empty v-else description="请在企业微信中打开此页面" />
    </el-card>

    <el-card class="quick-actions-card">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>

      <div class="quick-actions">
        <el-button-group class="action-group">
          <el-button type="primary" @click="sendQuickReply('health')">
            <el-icon><FirstAidKit /></el-icon>
            健康咨询
          </el-button>
          <el-button type="warning" @click="sendQuickReply('insurance')">
            <el-icon><Document /></el-icon>
            保险服务
          </el-button>
        </el-button-group>

        <el-button-group class="action-group">
          <el-button type="success" @click="sendQuickReply('benefit')">
            <el-icon><Present /></el-icon>
            权益查询
          </el-button>
          <el-button type="info" @click="sendQuickReply('contact')">
            <el-icon><Phone /></el-icon>
            联系管家
          </el-button>
        </el-button-group>
      </div>
    </el-card>

    <el-card class="ai-assistant-card">
      <template #header>
        <div class="card-header">
          <span>AI智能助手</span>
          <el-tag type="primary" size="small" effect="dark">AI</el-tag>
        </div>
      </template>

      <div class="ai-chat">
        <div class="chat-messages" ref="chatMessagesRef">
          <div
            v-for="(msg, index) in chatMessages"
            :key="index"
            :class="['message', msg.type]"
          >
            <div class="message-content">{{ msg.content }}</div>
          </div>
        </div>

        <div class="chat-input">
          <el-input
            v-model="aiInput"
            placeholder="输入问题，AI为您解答..."
            @keyup.enter="sendToAi"
          >
            <template #append>
              <el-button @click="sendToAi">
                <el-icon><Promotion /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { FirstAidKit, Document, Present, Phone, Promotion } from '@element-plus/icons-vue'
import {
  initWeChatSDK,
  getCurExternalContact,
  getContext,
  openUserProfile,
  sendTextMessage,
  isWeChatWork
} from '../../utils/wechat'
import { getJsSdkConfig, type JsSdkConfig } from '../../api/wechat'
import { generateAiResponse } from '../../api/customerService'

const externalUserId = ref('')
const chatType = ref('')
const aiInput = ref('')
const chatMessages = ref<Array<{ type: string; content: string }>>([
  { type: 'ai', content: '您好！我是AI健康助手，可以帮您解答健康、保险、权益相关问题。' }
])
const chatMessagesRef = ref<HTMLElement>()

// 初始化企微JS-SDK
const initWeChat = async () => {
  if (!isWeChatWork()) {
    ElMessage.warning('请在企业微信中打开此页面')
    return
  }

  try {
    const url = window.location.href.split('#')[0]
    const config: JsSdkConfig = await getJsSdkConfig(url) as JsSdkConfig
    await initWeChatSDK(config.signature)

    // 获取当前外部联系人
    externalUserId.value = await getCurExternalContact()

    // 获取聊天类型
    chatType.value = await getContext()

    ElMessage.success('企业微信初始化成功')
  } catch (error) {
    console.error('初始化失败:', error)
    ElMessage.error('企业微信初始化失败')
  }
}

// 查看客户资料
const viewUserProfile = async () => {
  if (!externalUserId.value) return
  try {
    await openUserProfile(externalUserId.value)
  } catch (error) {
    ElMessage.error('打开客户资料失败')
  }
}

// 发送健康建议
const sendHealthTips = async () => {
  const tips = '【健康小贴士】\n1. 定期监测血压血糖\n2. 保持适量运动\n3. 饮食清淡少盐\n4. 定期体检\n如有健康问题，随时咨询我！'
  await sendMessage(tips)
}

// 发送快捷回复
const sendQuickReply = async (type: string) => {
  const replies: Record<string, string> = {
    health: '您好！我是您的健康顾问。请问有什么健康问题需要咨询？我可以帮您分析症状、提供健康建议。',
    insurance: '您好！我是保险顾问。我可以帮您：\n1. 分析保障缺口\n2. 推荐合适产品\n3. 解答理赔问题\n请问有什么可以帮您？',
    benefit: '您好！以下是您的健康权益：\n1. 免费体检服务\n2. 专家绿通服务\n3. 在线问诊服务\n4. 购药优惠\n如需使用，请告诉我！',
    contact: '已为您转接健康管家，稍后将为您服务。紧急事项请拨打客服热线：400-xxx-xxxx'
  }
  await sendMessage(replies[type])
}

// 发送消息到聊天窗口
const sendMessage = async (content: string) => {
  try {
    await sendTextMessage(content)
    ElMessage.success('消息已发送')
  } catch (error) {
    ElMessage.error('发送消息失败')
  }
}

// 发送消息给AI
const sendToAi = async () => {
  if (!aiInput.value.trim()) return

  const userMessage = aiInput.value.trim()
  chatMessages.value.push({ type: 'user', content: userMessage })
  aiInput.value = ''
  scrollToBottom()

  try {
    // 调用AI接口
    const response = await generateAiResponse(externalUserId.value || 'unknown', userMessage) as string
    chatMessages.value.push({ type: 'ai', content: response })
    scrollToBottom()
  } catch (error) {
    chatMessages.value.push({ type: 'ai', content: '抱歉，AI服务暂时不可用，请稍后再试。' })
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
    }
  })
}

onMounted(() => {
  initWeChat()
})
</script>

<style scoped lang="scss">
.sidebar-container {
  padding: 10px;
  max-width: 100%;

  .el-card {
    margin-bottom: 10px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
  }
}

.user-info-card {
  .user-info {
    .action-buttons {
      margin-top: 15px;
      display: flex;
      gap: 10px;
      flex-wrap: wrap;

      .el-button {
        flex: 1;
        min-width: 120px;
      }
    }
  }
}

.quick-actions-card {
  .quick-actions {
    display: flex;
    flex-direction: column;
    gap: 10px;

    .action-group {
      display: flex;
      width: 100%;

      .el-button {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 5px;
      }
    }
  }
}

.ai-assistant-card {
  .ai-chat {
    .chat-messages {
      height: 200px;
      overflow-y: auto;
      border: 1px solid #ebeef5;
      border-radius: 4px;
      padding: 10px;
      margin-bottom: 10px;
      background-color: #f5f7fa;

      .message {
        margin-bottom: 10px;
        max-width: 85%;

        &.user {
          margin-left: auto;
          text-align: right;

          .message-content {
            background-color: #409eff;
            color: #fff;
            display: inline-block;
            padding: 8px 12px;
            border-radius: 12px 12px 0 12px;
          }
        }

        &.ai {
          margin-right: auto;

          .message-content {
            background-color: #fff;
            border: 1px solid #ebeef5;
            display: inline-block;
            padding: 8px 12px;
            border-radius: 12px 12px 12px 0;
            text-align: left;
          }
        }
      }
    }
  }
}
</style>
