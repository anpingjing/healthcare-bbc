import wx from 'weixin-js-sdk'
import sha1 from 'js-sha1'

// 企业微信配置信息
const WECHAT_CONFIG = {
  corpId: 'wwace533e386c63f72',
  agentId: '1000007'
}

/**
 * 生成随机字符串
 */
function generateNonceStr(length: number = 16): string {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

/**
 * 生成时间戳
 */
function generateTimestamp(): number {
  return Math.floor(Date.now() / 1000)
}

/**
 * 生成JS-SDK签名
 */
function generateSignature(jsapiTicket: string, nonceStr: string, timestamp: number, url: string): string {
  const string1 = `jsapi_ticket=${jsapiTicket}&noncestr=${nonceStr}&timestamp=${timestamp}&url=${url}`
  return sha1(string1)
}

/**
 * 初始化企业微信JS-SDK
 */
export async function initWeChatSDK(jsapiTicket: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const nonceStr = generateNonceStr()
    const timestamp = generateTimestamp()
    const url = window.location.href.split('#')[0]
    const signature = generateSignature(jsapiTicket, nonceStr, timestamp, url)

    wx.config({
      beta: true,
      debug: false,
      appId: WECHAT_CONFIG.corpId,
      timestamp: timestamp,
      nonceStr: nonceStr,
      signature: signature,
      jsApiList: [
        'sendChatMessage',
        'openUserProfile',
        'getCurExternalContact',
        'getContext',
        'invoke',
        'agentConfig'
      ]
    })

    wx.ready(() => {
      console.log('企业微信JS-SDK初始化成功')
      resolve()
    })

    wx.error((err: any) => {
      console.error('企业微信JS-SDK初始化失败:', err)
      reject(err)
    })
  })
}

/**
 * 获取当前外部联系人ID
 */
export function getCurExternalContact(): Promise<string> {
  return new Promise((resolve, reject) => {
    wx.invoke('getCurExternalContact', {}, (res: any) => {
      if (res.err_msg === 'getCurExternalContact:ok') {
        resolve(res.userId)
      } else {
        reject(new Error(res.err_msg))
      }
    })
  })
}

/**
 * 获取当前环境
 */
export function getContext(): Promise<string> {
  return new Promise((resolve, reject) => {
    wx.invoke('getContext', {}, (res: any) => {
      if (res.err_msg === 'getContext:ok') {
        resolve(res.entry) // 'single_chat' 或 'group_chat'
      } else {
        reject(new Error(res.err_msg))
      }
    })
  })
}

/**
 * 发送聊天消息
 */
export function sendChatMessage(msgtype: string, content: any): Promise<void> {
  return new Promise((resolve, reject) => {
    wx.invoke('sendChatMessage', {
      msgtype,
      ...content
    }, (res: any) => {
      if (res.err_msg === 'sendChatMessage:ok') {
        resolve()
      } else {
        reject(new Error(res.err_msg))
      }
    })
  })
}

/**
 * 发送文本消息
 */
export function sendTextMessage(text: string): Promise<void> {
  return sendChatMessage('text', { text: { content: text } })
}

/**
 * 打开用户资料页
 */
export function openUserProfile(userId: string): Promise<void> {
  return new Promise((resolve, reject) => {
    wx.invoke('openUserProfile', {
      type: 1, // 1表示企业微信联系人
      userid: userId
    }, (res: any) => {
      if (res.err_msg === 'openUserProfile:ok') {
        resolve()
      } else {
        reject(new Error(res.err_msg))
      }
    })
  })
}

/**
 * 判断是否在企微环境中
 */
export function isWeChatWork(): boolean {
  const ua = navigator.userAgent.toLowerCase()
  return ua.includes('wxwork')
}

/**
 * 获取企业微信配置信息
 */
export function getWeChatConfig() {
  return WECHAT_CONFIG
}
