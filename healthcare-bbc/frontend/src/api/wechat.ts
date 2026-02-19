import request from '../utils/request'

export interface JsSdkConfig {
  appId: string
  timestamp: string
  nonceStr: string
  signature: string
}

export interface GroupChatItem {
  chatId: string
  status: number
  name: string
}

export interface GroupChatListResult {
  errcode: number
  errmsg: string
  groupChatList: GroupChatItem[]
  nextCursor: string
}

export interface GroupMember {
  userId: string
  type: number
  joinTime: number
  joinScene: number
}

export interface GroupChatInfo {
  chatId: string
  name: string
  owner: string
  memberList: GroupMember[]
  createTime: number
  notice: string
  adminList: string[]
}

export interface ExternalContact {
  externalUserid: string
  name: string
  avatar: string
  corpName: string
  type: number
  gender: number
}

export interface FollowUser {
  userid: string
  remark: string
  description: string
  createTime: number
  state: string
  remarkCorpName: string
  remarkMobiles: string[]
}

export interface ExternalContactInfo {
  errcode: number
  errmsg: string
  externalContact: ExternalContact
  followUser: FollowUser[]
}

export interface MessageSendResult {
  errcode: number
  errmsg: string
  msgId: string
  responseCode: string
}

export function getJsSdkConfig(url: string) {
  return request({
    url: '/wechat/js-sdk/config',
    method: 'get',
    params: { url }
  })
}

export function sendTextMessage(userId: string, content: string) {
  return request<MessageSendResult>({
    url: '/wechat/message/text',
    method: 'post',
    params: { userId, content }
  })
}

export function sendMarkdownMessage(userId: string, content: string) {
  return request<MessageSendResult>({
    url: '/wechat/message/markdown',
    method: 'post',
    params: { userId, content }
  })
}

export function sendTemplateCardMessage(userId: string, templateCard: Record<string, any>) {
  return request<MessageSendResult>({
    url: '/wechat/message/template-card',
    method: 'post',
    params: { userId },
    data: templateCard
  })
}

export function sendChatMessage(chatId: string, content: string) {
  return request<MessageSendResult>({
    url: '/wechat/message/chat',
    method: 'post',
    params: { chatId, content }
  })
}

export function getGroupChatList(limit: number = 100, cursor?: string, statusFilter: number = 0) {
  return request<GroupChatListResult>({
    url: '/wechat/groupchat/list',
    method: 'get',
    params: { limit, cursor, statusFilter }
  })
}

export function getGroupChatInfo(chatId: string) {
  return request<GroupChatInfo>({
    url: `/wechat/groupchat/${chatId}`,
    method: 'get'
  })
}

export function createGroupChat(name: string, owner: string, userIdList: string[], chatId?: string) {
  return request<string>({
    url: '/wechat/groupchat/create',
    method: 'post',
    params: { name, owner, chatId },
    data: userIdList
  })
}

export function getExternalContactInfo(externalUserId: string) {
  return request<ExternalContactInfo>({
    url: `/wechat/external-contact/${externalUserId}`,
    method: 'get'
  })
}

export function getExternalContactList(userId: string) {
  return request<ExternalContactInfo[]>({
    url: '/wechat/external-contact/list',
    method: 'get',
    params: { userId }
  })
}

export function refreshAccessToken() {
  return request<void>({
    url: '/wechat/token/refresh',
    method: 'post'
  })
}
