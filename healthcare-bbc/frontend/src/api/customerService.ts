import request from '../utils/request'

export interface CustomerMessage {
  messageId: number
  wechatExternalUserId: string
  fromUserId: string
  toUserId: string
  messageType: string
  content: string
  mediaUrl: string
  direction: number
  status: number
  createTime: string
  updateTime: string
}

export interface CustomerServiceSession {
  sessionId: number
  wechatExternalUserId: string
  agentId: number
  agentName: string
  status: number
  startTime: string
  endTime: string
  createTime: string
  updateTime: string
}

export interface AiIntent {
  intentId: number
  intentCode: string
  intentName: string
  intentCategory: string
  targetRole: string
  keywords: string
  confidenceThreshold: number
  priority: number
  status: number
}

// 获取用户最近消息
export function getRecentMessages(externalUserId: string, limit: number = 20) {
  return request({
    url: `/customer-service/messages/${externalUserId}`,
    method: 'get',
    params: { limit }
  })
}

// 发送客服消息
export function sendMessage(externalUserId: string, content: string, agentId: number) {
  return request({
    url: '/customer-service/messages/send',
    method: 'post',
    params: { externalUserId, content, agentId }
  })
}

// 获取活跃会话列表
export function getActiveSessions(agentId: number) {
  return request({
    url: '/customer-service/sessions/active',
    method: 'get',
    params: { agentId }
  })
}

// 分配客服
export function assignAgent(externalUserId: string, agentId: number, agentName: string) {
  return request({
    url: '/customer-service/sessions/assign',
    method: 'post',
    params: { externalUserId, agentId, agentName }
  })
}

// 关闭会话
export function closeSession(sessionId: number) {
  return request({
    url: `/customer-service/sessions/close/${sessionId}`,
    method: 'post'
  })
}

// AI意图识别
export function recognizeIntent(messageContent: string) {
  return request({
    url: '/ai/intent/recognize',
    method: 'post',
    params: { messageContent }
  })
}

// 分析消息
export function analyzeMessage(messageContent: string) {
  return request({
    url: '/ai/analyze',
    method: 'post',
    params: { messageContent }
  })
}

// 生成AI回复
export function generateAiResponse(externalUserId: string, messageContent: string) {
  return request({
    url: '/ai/response/generate',
    method: 'post',
    params: { externalUserId, messageContent }
  })
}
