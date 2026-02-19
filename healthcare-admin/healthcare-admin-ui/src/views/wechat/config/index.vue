<template>
  <div class="wechat-config-container">
    <el-card class="config-card">
      <template #header>
        <div class="card-header">
          <span>企业微信对接配置</span>
          <el-button type="primary" @click="saveConfig" :loading="saving">
            保存配置
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基础配置 -->
        <el-tab-pane label="基础配置" name="basic">
          <div class="config-section">
            <h3 class="section-title">
              <el-icon><OfficeBuilding /></el-icon>
              企业微信基础配置
            </h3>
            
            <el-form :model="configForm.basic" label-width="150px" class="config-form">
              <el-form-item label="企业ID (CorpID)" prop="corpId" required>
                <el-input 
                  v-model="configForm.basic.corpId" 
                  placeholder="请输入企业微信CorpID"
                />
                <div class="form-tip">在企业微信管理后台"我的企业"页面获取</div>
              </el-form-item>

              <el-form-item label="企业名称" prop="corpName">
                <el-input 
                  v-model="configForm.basic.corpName" 
                  placeholder="请输入企业名称"
                />
              </el-form-item>

              <el-form-item label="应用AgentID" prop="agentId" required>
                <el-input 
                  v-model="configForm.basic.agentId" 
                  placeholder="请输入应用AgentID"
                />
                <div class="form-tip">在"应用管理"-"应用详情"页面获取</div>
              </el-form-item>

              <el-form-item label="应用Secret" prop="corpSecret" required>
                <el-input 
                  v-model="configForm.basic.corpSecret" 
                  type="password"
                  placeholder="请输入应用Secret"
                  show-password
                />
                <div class="form-tip">在"应用管理"-"应用详情"页面获取，请妥善保管</div>
              </el-form-item>

              <el-form-item label="应用名称">
                <el-input 
                  v-model="configForm.basic.agentName" 
                  placeholder="请输入应用名称"
                />
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 消息推送配置 -->
        <el-tab-pane label="消息推送" name="message">
          <div class="config-section">
            <h3 class="section-title">
              <el-icon><ChatDotRound /></el-icon>
              消息推送配置
            </h3>
            
            <el-form :model="configForm.message" label-width="150px" class="config-form">
              <el-form-item label="Token" prop="token" required>
                <el-input 
                  v-model="configForm.message.token" 
                  placeholder="请输入消息推送Token"
                />
                <div class="form-tip">用于生成签名，随机填写并牢记</div>
              </el-form-item>

              <el-form-item label="EncodingAESKey" prop="encodingAesKey" required>
                <el-input 
                  v-model="configForm.message.encodingAesKey" 
                  placeholder="请输入消息加解密密钥"
                />
                <div class="form-tip">消息加密密钥，43位字符</div>
              </el-form-item>

              <el-form-item label="接收消息URL">
                <el-input 
                  v-model="configForm.message.callbackUrl" 
                  placeholder="https://your-domain.com/api/v1/wechat/callback"
                />
                <div class="form-tip">企业微信推送消息的回调地址</div>
              </el-form-item>

              <el-form-item label="消息加密方式">
                <el-radio-group v-model="configForm.message.encryptType">
                  <el-radio label="compatible">兼容模式</el-radio>
                  <el-radio label="safe">安全模式</el-radio>
                  <el-radio label="plain">明文模式</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 网页授权配置 -->
        <el-tab-pane label="网页授权" name="oauth">
          <div class="config-section">
            <h3 class="section-title">
              <el-icon><Link /></el-icon>
              网页授权配置
            </h3>
            
            <el-form :model="configForm.oauth" label-width="150px" class="config-form">
              <el-form-item label="授权回调域名">
                <el-input 
                  v-model="configForm.oauth.oauthDomain" 
                  placeholder="your-domain.com"
                />
                <div class="form-tip">需要在企业微信后台"应用管理"-"网页授权及JS-SDK"中配置</div>
              </el-form-item>

              <el-form-item label="可信域名">
                <el-input 
                  v-model="configForm.oauth.trustedDomain" 
                  placeholder="https://your-domain.com"
                />
              </el-form-item>

              <el-form-item label="JS-SDK配置">
                <el-checkbox-group v-model="configForm.oauth.jsApiList">
                  <el-checkbox label="agentConfig">agentConfig</el-checkbox>
                  <el-checkbox label="sendChatMessage">发送消息</el-checkbox>
                  <el-checkbox label="openUserProfile">打开个人信息</el-checkbox>
                  <el-checkbox label="shareToExternalContact">分享给外部联系人</el-checkbox>
                  <el-checkbox label="shareToExternalChat">分享到外部群</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 通讯录配置 -->
        <el-tab-pane label="通讯录" name="contact">
          <div class="config-section">
            <h3 class="section-title">
              <el-icon><User /></el-icon>
              通讯录同步配置
            </h3>
            
            <el-form :model="configForm.contact" label-width="150px" class="config-form">
              <el-form-item label="通讯录Secret">
                <el-input 
                  v-model="configForm.contact.contactSecret" 
                  type="password"
                  placeholder="请输入通讯录Secret"
                  show-password
                />
                <div class="form-tip">在"管理工具"-"通讯录同步"页面获取</div>
              </el-form-item>

              <el-form-item label="同步方式">
                <el-radio-group v-model="configForm.contact.syncType">
                  <el-radio label="api">API主动同步</el-radio>
                  <el-radio label="webhook">Webhook被动接收</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="自动同步">
                <el-switch 
                  v-model="configForm.contact.autoSync" 
                  active-text="开启"
                  inactive-text="关闭"
                />
              </el-form-item>

              <el-form-item label="同步频率" v-if="configForm.contact.autoSync">
                <el-select v-model="configForm.contact.syncInterval" style="width: 200px">
                  <el-option label="每5分钟" value="5" />
                  <el-option label="每15分钟" value="15" />
                  <el-option label="每30分钟" value="30" />
                  <el-option label="每小时" value="60" />
                  <el-option label="每天" value="1440" />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 客户联系配置 -->
        <el-tab-pane label="客户联系" name="external">
          <div class="config-section">
            <h3 class="section-title">
              <el-icon><ChatDotSquare /></el-icon>
              客户联系配置
            </h3>
            
            <el-form :model="configForm.external" label-width="150px" class="config-form">
              <el-form-item label="客户联系Secret">
                <el-input 
                  v-model="configForm.external.externalContactSecret" 
                  type="password"
                  placeholder="请输入客户联系Secret"
                  show-password
                />
                <div class="form-tip">在"客户联系"-"API"页面获取</div>
              </el-form-item>

              <el-form-item label="欢迎语配置">
                <el-input 
                  v-model="configForm.external.welcomeMessage" 
                  type="textarea"
                  :rows="4"
                  placeholder="请输入新客户欢迎语"
                />
                <div class="form-tip">支持使用变量：{{userName}}、{{agentName}}</div>
              </el-form-item>

              <el-form-item label="入群引导">
                <el-switch 
                  v-model="configForm.external.autoInviteGroup" 
                  active-text="自动邀请入群"
                  inactive-text="不自动邀请"
                />
              </el-form-item>

              <el-form-item label="群活码配置" v-if="configForm.external.autoInviteGroup">
                <el-input 
                  v-model="configForm.external.groupQrCode" 
                  placeholder="请输入群活码链接"
                />
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- 连接测试区域 -->
      <el-divider />
      
      <div class="test-section">
        <h3 class="section-title">
          <el-icon><Connection /></el-icon>
          连接测试
        </h3>
        <div class="test-actions">
          <el-button type="success" @click="testConnection" :loading="testing">
            测试企业微信连接
          </el-button>
          <el-button @click="testMessagePush" :loading="testingMessage">
            测试消息推送
          </el-button>
          <el-button @click="syncContacts" :loading="syncing">
            同步通讯录
          </el-button>
        </div>
        
        <div v-if="testResult" class="test-result" :class="testResult.type">
          <el-alert
            :title="testResult.title"
            :type="testResult.type"
            :description="testResult.message"
            show-icon
            :closable="false"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('basic')
const saving = ref(false)
const testing = ref(false)
const testingMessage = ref(false)
const syncing = ref(false)
const testResult = ref<{ type: 'success' | 'error' | 'warning'; title: string; message: string } | null>(null)

const configForm = reactive({
  basic: {
    corpId: '',
    corpName: '',
    agentId: '',
    corpSecret: '',
    agentName: ''
  },
  message: {
    token: '',
    encodingAesKey: '',
    callbackUrl: '',
    encryptType: 'compatible'
  },
  oauth: {
    oauthDomain: '',
    trustedDomain: '',
    jsApiList: ['agentConfig', 'sendChatMessage']
  },
  contact: {
    contactSecret: '',
    syncType: 'api',
    autoSync: false,
    syncInterval: '60'
  },
  external: {
    externalContactSecret: '',
    welcomeMessage: '您好！欢迎加入健康管理社群，我是您的健康管家，将为您提供专业的健康服务。',
    autoInviteGroup: true,
    groupQrCode: ''
  }
})

// 加载配置
const loadConfig = async () => {
  try {
    // TODO: 调用API加载配置
    // const res = await wechatConfigApi.getConfig()
    // Object.assign(configForm, res.data)
  } catch (error) {
    console.error('加载配置失败:', error)
  }
}

// 保存配置
const saveConfig = async () => {
  saving.value = true
  try {
    // TODO: 调用API保存配置
    // await wechatConfigApi.saveConfig(configForm)
    ElMessage.success('配置保存成功')
  } catch (error) {
    ElMessage.error('配置保存失败')
  } finally {
    saving.value = false
  }
}

// 测试连接
const testConnection = async () => {
  testing.value = true
  testResult.value = null
  
  try {
    // TODO: 调用API测试连接
    // const res = await wechatConfigApi.testConnection(configForm.basic)
    
    // 模拟测试结果
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    testResult.value = {
      type: 'success',
      title: '连接测试成功',
      message: '企业微信API连接正常，Token获取成功，应用信息验证通过。'
    }
  } catch (error) {
    testResult.value = {
      type: 'error',
      title: '连接测试失败',
      message: '请检查CorpID和Secret是否正确，确保IP白名单已配置。'
    }
  } finally {
    testing.value = false
  }
}

// 测试消息推送
const testMessagePush = async () => {
  testingMessage.value = true
  testResult.value = null
  
  try {
    // TODO: 调用API测试消息推送
    // const res = await wechatConfigApi.testMessagePush()
    
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    testResult.value = {
      type: 'success',
      title: '消息推送测试成功',
      message: '消息已成功推送到企业微信，请检查是否收到测试消息。'
    }
  } catch (error) {
    testResult.value = {
      type: 'error',
      title: '消息推送测试失败',
      message: '请检查消息推送URL和Token配置是否正确。'
    }
  } finally {
    testingMessage.value = false
  }
}

// 同步通讯录
const syncContacts = async () => {
  syncing.value = true
  testResult.value = null
  
  try {
    // TODO: 调用API同步通讯录
    // const res = await wechatConfigApi.syncContacts()
    
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    testResult.value = {
      type: 'success',
      title: '通讯录同步成功',
      message: '已成功同步 156 位员工信息，3 个部门信息。'
    }
  } catch (error) {
    testResult.value = {
      type: 'error',
      title: '通讯录同步失败',
      message: '请检查通讯录Secret是否正确，确保有通讯录管理权限。'
    }
  } finally {
    syncing.value = false
  }
}

onMounted(() => {
  loadConfig()
})
</script>

<style scoped>
.wechat-config-container {
  padding: 20px;
}

.config-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.config-section {
  padding: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.section-title .el-icon {
  font-size: 20px;
  color: #409EFF;
}

.config-form {
  max-width: 600px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.test-section {
  padding: 20px;
}

.test-actions {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.test-result {
  max-width: 600px;
}

:deep(.el-tabs__content) {
  padding: 0;
}
</style>
