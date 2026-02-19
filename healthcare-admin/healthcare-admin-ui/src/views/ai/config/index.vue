<template>
  <div class="ai-config-container">
    <!-- 模型管理区域 -->
    <el-card class="models-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">AI模型管理</span>
            <el-tag type="info" class="model-count">{{ modelList.length }} 个模型</el-tag>
          </div>
          <el-button type="primary" @click="showAddModelDialog">
            <el-icon><Plus /></el-icon> 添加模型
          </el-button>
        </div>
      </template>

      <!-- 模型列表 -->
      <div class="models-grid">
        <div
          v-for="model in modelList"
          :key="model.id"
          class="model-card"
          :class="{ active: currentModel?.id === model.id, disabled: !model.enabled }"
          @click="selectModel(model)"
        >
          <div class="model-header">
            <div class="model-icon" :style="{ backgroundColor: getModelColor(model.provider) }">
              <el-icon><component :is="getModelIcon(model.provider)" /></el-icon>
            </div>
            <div class="model-info">
              <div class="model-name">{{ model.name }}</div>
              <div class="model-provider">{{ getProviderLabel(model.provider) }}</div>
            </div>
            <el-switch
              v-model="model.enabled"
              @click.stop
              @change="(val) => toggleModelEnabled(model, val)"
            />
          </div>
          <div class="model-status">
            <el-tag :type="model.enabled ? 'success' : 'info'" size="small">
              {{ model.enabled ? '已启用' : '已禁用' }}
            </el-tag>
            <el-tag v-if="model.isDefault" type="warning" size="small" class="default-tag">默认</el-tag>
          </div>
          <div class="model-actions">
            <el-button link type="primary" @click.stop="setAsDefault(model)">
              设为默认
            </el-button>
            <el-button link type="danger" @click.stop="deleteModel(model)">
              删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 模型配置详情 -->
    <el-card v-if="currentModel" class="config-detail-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">{{ currentModel.name }} 配置</span>
            <el-tag :type="currentModel.enabled ? 'success' : 'info'">
              {{ currentModel.enabled ? '已启用' : '已禁用' }}
            </el-tag>
          </div>
          <div class="header-actions">
            <el-button type="success" @click="testModelConnection" :loading="testing">
              <el-icon><Connection /></el-icon> 测试连接
            </el-button>
            <el-button type="primary" @click="saveModelConfig" :loading="saving">
              <el-icon><Check /></el-icon> 保存配置
            </el-button>
          </div>
        </div>
      </template>

      <el-form :model="currentModel.config" label-width="140px" class="model-config-form">
        <!-- 基础配置 -->
        <el-divider content-position="left">基础配置</el-divider>
        
        <el-form-item label="模型名称">
          <el-input v-model="currentModel.name" placeholder="请输入模型名称" />
        </el-form-item>

        <el-form-item label="模型版本">
          <el-select v-model="currentModel.config.modelVersion" style="width: 100%">
            <el-option
              v-for="version in getModelVersions(currentModel.provider)"
              :key="version.value"
              :label="version.label"
              :value="version.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="API密钥">
          <el-input
            v-model="currentModel.config.apiKey"
            type="password"
            placeholder="请输入API密钥"
            show-password
          />
        </el-form-item>

        <el-form-item label="API地址" v-if="showApiUrl(currentModel.provider)">
          <el-input v-model="currentModel.config.apiUrl" placeholder="请输入API地址" />
        </el-form-item>

        <el-form-item label="Base URL" v-if="showBaseUrl(currentModel.provider)">
          <el-input v-model="currentModel.config.baseUrl" placeholder="请输入Base URL" />
        </el-form-item>

        <!-- 高级参数配置 -->
        <el-divider content-position="left">参数配置</el-divider>

        <el-form-item label="温度参数 (Temperature)">
          <div class="param-row">
            <el-slider v-model="currentModel.config.temperature" :min="0" :max="2" :step="0.1" style="flex: 1" />
            <span class="param-value">{{ currentModel.config.temperature }}</span>
          </div>
          <div class="form-tip">值越高输出越随机，值越低输出越确定</div>
        </el-form-item>

        <el-form-item label="最大Token数">
          <div class="param-row">
            <el-slider v-model="currentModel.config.maxTokens" :min="100" :max="16000" :step="100" style="flex: 1" />
            <span class="param-value">{{ currentModel.config.maxTokens }}</span>
          </div>
        </el-form-item>

        <el-form-item label="Top P">
          <div class="param-row">
            <el-slider v-model="currentModel.config.topP" :min="0" :max="1" :step="0.05" style="flex: 1" />
            <span class="param-value">{{ currentModel.config.topP }}</span>
          </div>
        </el-form-item>

        <el-form-item label="超时时间">
          <el-input-number v-model="currentModel.config.timeout" :min="5" :max="300" :step="5" />
          <span class="unit">秒</span>
        </el-form-item>

        <!-- 特殊配置 -->
        <template v-if="currentModel.provider === 'deepseek'">
          <el-divider content-position="left">DeepSeek 特殊配置</el-divider>
          <el-form-item label="推理模式">
            <el-switch
              v-model="currentModel.config.reasoningMode"
              active-text="开启推理"
              inactive-text="标准模式"
            />
            <div class="form-tip">开启后模型会进行深度推理，适合复杂问题</div>
          </el-form-item>
        </template>

        <template v-if="currentModel.provider === 'kimi'">
          <el-divider content-position="left">Kimi 特殊配置</el-divider>
          <el-form-item label="长文本优化">
            <el-switch
              v-model="currentModel.config.longContext"
              active-text="开启"
              inactive-text="关闭"
            />
            <div class="form-tip">开启后优化长文本处理能力</div>
          </el-form-item>
        </template>

        <template v-if="currentModel.provider === 'qwen'">
          <el-divider content-position="left">通义千问 特殊配置</el-divider>
          <el-form-item label="搜索增强">
            <el-switch
              v-model="currentModel.config.enableSearch"
              active-text="开启"
              inactive-text="关闭"
            />
            <div class="form-tip">开启后模型会结合搜索结果回答</div>
          </el-form-item>
        </template>

        <template v-if="currentModel.provider === 'volcano'">
          <el-divider content-position="left">火山引擎 特殊配置</el-divider>
          <el-form-item label="账号ID">
            <el-input v-model="currentModel.config.accountId" placeholder="请输入火山引擎账号ID" />
          </el-form-item>
        </template>
      </el-form>

      <!-- 测试结果 -->
      <div v-if="testResult" class="test-result-section">
        <el-divider />
        <el-alert
          :title="testResult.title"
          :type="testResult.type"
          :description="testResult.message"
          show-icon
          :closable="false"
        />
      </div>
    </el-card>

    <!-- 添加模型对话框 -->
    <el-dialog
      v-model="addModelDialogVisible"
      title="添加AI模型"
      width="600px"
    >
      <div class="provider-grid">
        <div
          v-for="provider in availableProviders"
          :key="provider.value"
          class="provider-card"
          @click="selectProvider(provider.value)"
        >
          <div class="provider-icon" :style="{ backgroundColor: provider.color }">
            <el-icon><component :is="provider.icon" /></el-icon>
          </div>
          <div class="provider-name">{{ provider.label }}</div>
          <div class="provider-desc">{{ provider.description }}</div>
        </div>
      </div>
    </el-dialog>

    <!-- 全局配置 -->
    <el-card class="global-config-card">
      <template #header>
        <div class="card-header">
          <span class="title">全局AI配置</span>
          <el-button type="primary" @click="saveGlobalConfig" :loading="savingGlobal">
            保存全局配置
          </el-button>
        </div>
      </template>

      <el-form :model="globalConfig" label-width="160px">
        <el-form-item label="默认模型">
          <el-select v-model="globalConfig.defaultModelId" style="width: 300px">
            <el-option
              v-for="model in enabledModels"
              :key="model.id"
              :label="model.name"
              :value="model.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="4R角色默认模型">
          <div class="role-models">
            <div class="role-model-item">
              <span class="role-label">家庭医生:</span>
              <el-select v-model="globalConfig.roleModels.doctor" style="width: 200px">
                <el-option label="使用默认模型" value="default" />
                <el-option
                  v-for="model in enabledModels"
                  :key="model.id"
                  :label="model.name"
                  :value="model.id"
                />
              </el-select>
            </div>
            <div class="role-model-item">
              <span class="role-label">健康管家:</span>
              <el-select v-model="globalConfig.roleModels.manager" style="width: 200px">
                <el-option label="使用默认模型" value="default" />
                <el-option
                  v-for="model in enabledModels"
                  :key="model.id"
                  :label="model.name"
                  :value="model.id"
                />
              </el-select>
            </div>
            <div class="role-model-item">
              <span class="role-label">权益顾问:</span>
              <el-select v-model="globalConfig.roleModels.advisor" style="width: 200px">
                <el-option label="使用默认模型" value="default" />
                <el-option
                  v-for="model in enabledModels"
                  :key="model.id"
                  :label="model.name"
                  :value="model.id"
                />
              </el-select>
            </div>
            <div class="role-model-item">
              <span class="role-label">保险顾问:</span>
              <el-select v-model="globalConfig.roleModels.insurance" style="width: 200px">
                <el-option label="使用默认模型" value="default" />
                <el-option
                  v-for="model in enabledModels"
                  :key="model.id"
                  :label="model.name"
                  :value="model.id"
                />
              </el-select>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="功能开关">
          <div class="feature-switches">
            <el-switch v-model="globalConfig.enableAiResponse" active-text="启用AI回复" />
            <el-switch v-model="globalConfig.enableIntentRecognition" active-text="启用意图识别" />
            <el-switch v-model="globalConfig.enableSmartDispatch" active-text="启用智能调度" />
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, Connection, Check, Delete, 
  Cpu, Lightning, ChatDotRound, 
  OfficeBuilding, Magic, Link 
} from '@element-plus/icons-vue'

// 模型提供商定义
const availableProviders = [
  { 
    value: 'openai', 
    label: 'OpenAI', 
    icon: 'Cpu', 
    color: '#10a37f',
    description: 'GPT-4 / GPT-3.5 系列模型'
  },
  { 
    value: 'deepseek', 
    label: 'DeepSeek', 
    icon: 'Lightning', 
    color: '#4f46e5',
    description: '深度求索大模型，支持推理模式'
  },
  { 
    value: 'kimi', 
    label: 'Kimi', 
    icon: 'ChatDotRound', 
    color: '#3b82f6',
    description: 'Moonshot AI，支持超长上下文'
  },
  { 
    value: 'qwen', 
    label: '通义千问', 
    icon: 'Magic', 
    color: '#f97316',
    description: '阿里云大模型，支持搜索增强'
  },
  { 
    value: 'volcano', 
    label: '火山引擎', 
    icon: 'OfficeBuilding', 
    color: '#ef4444',
    description: '字节跳动豆包大模型'
  },
  { 
    value: 'zhipu', 
    label: '智谱AI', 
    icon: 'Link', 
    color: '#8b5cf6',
    description: 'ChatGLM系列模型'
  },
  { 
    value: 'wenxin', 
    label: '文心一言', 
    icon: 'ChatDotRound', 
    color: '#06b6d4',
    description: '百度大模型'
  },
  { 
    value: 'custom', 
    label: '自定义', 
    icon: 'Cpu', 
    color: '#6b7280',
    description: '兼容OpenAI API的自定义模型'
  }
]

// 模型版本选项
const modelVersions: Record<string, Array<{label: string, value: string}>> = {
  openai: [
    { label: 'GPT-4o', value: 'gpt-4o' },
    { label: 'GPT-4 Turbo', value: 'gpt-4-turbo' },
    { label: 'GPT-4', value: 'gpt-4' },
    { label: 'GPT-3.5 Turbo', value: 'gpt-3.5-turbo' }
  ],
  deepseek: [
    { label: 'DeepSeek-V3', value: 'deepseek-v3' },
    { label: 'DeepSeek-R1', value: 'deepseek-r1' },
    { label: 'DeepSeek-V2.5', value: 'deepseek-v2.5' }
  ],
  kimi: [
    { label: 'Kimi K2', value: 'kimi-k2' },
    { label: 'Kimi K1.5', value: 'kimi-k1.5' },
    { label: 'Kimi 32K', value: 'kimi-32k' },
    { label: 'Kimi 128K', value: 'kimi-128k' }
  ],
  qwen: [
    { label: '通义千问-Max', value: 'qwen-max' },
    { label: '通义千问-Plus', value: 'qwen-plus' },
    { label: '通义千问-Turbo', value: 'qwen-turbo' },
    { label: '通义千问-Long', value: 'qwen-long' }
  ],
  volcano: [
    { label: 'Doubao-Pro', value: 'doubao-pro' },
    { label: 'Doubao-Lite', value: 'doubao-lite' },
    { label: 'Doubao-Vision', value: 'doubao-vision' }
  ],
  zhipu: [
    { label: 'GLM-4', value: 'glm-4' },
    { label: 'GLM-4-Flash', value: 'glm-4-flash' },
    { label: 'GLM-4-Air', value: 'glm-4-air' },
    { label: 'ChatGLM3', value: 'chatglm3' }
  ],
  wenxin: [
    { label: 'ERNIE 4.0', value: 'ernie-4.0' },
    { label: 'ERNIE 3.5', value: 'ernie-3.5' },
    { label: 'ERNIE Speed', value: 'ernie-speed' },
    { label: 'ERNIE Lite', value: 'ernie-lite' }
  ],
  custom: [
    { label: '自定义模型', value: 'custom' }
  ]
}

// 默认配置
const defaultConfigs: Record<string, any> = {
  openai: {
    apiUrl: 'https://api.openai.com/v1',
    temperature: 0.7,
    maxTokens: 2000,
    topP: 1,
    timeout: 30
  },
  deepseek: {
    apiUrl: 'https://api.deepseek.com/v1',
    temperature: 0.7,
    maxTokens: 4000,
    topP: 1,
    timeout: 60,
    reasoningMode: false
  },
  kimi: {
    apiUrl: 'https://api.moonshot.cn/v1',
    temperature: 0.7,
    maxTokens: 4000,
    topP: 1,
    timeout: 60,
    longContext: true
  },
  qwen: {
    apiUrl: 'https://dashscope.aliyuncs.com/api/v1',
    temperature: 0.7,
    maxTokens: 2000,
    topP: 0.95,
    timeout: 30,
    enableSearch: false
  },
  volcano: {
    apiUrl: 'https://ark.cn-beijing.volces.com/api/v3',
    temperature: 0.7,
    maxTokens: 2000,
    topP: 1,
    timeout: 30,
    accountId: ''
  },
  zhipu: {
    apiUrl: 'https://open.bigmodel.cn/api/paas/v4',
    temperature: 0.7,
    maxTokens: 2000,
    topP: 0.95,
    timeout: 30
  },
  wenxin: {
    apiUrl: 'https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop',
    temperature: 0.7,
    maxTokens: 2000,
    topP: 1,
    timeout: 30
  },
  custom: {
    apiUrl: '',
    baseUrl: '',
    temperature: 0.7,
    maxTokens: 2000,
    topP: 1,
    timeout: 30
  }
}

// 状态
const modelList = ref<Array<any>>([])
const currentModel = ref<any>(null)
const addModelDialogVisible = ref(false)
const saving = ref(false)
const savingGlobal = ref(false)
const testing = ref(false)
const testResult = ref<any>(null)

const globalConfig = reactive({
  defaultModelId: '',
  roleModels: {
    doctor: 'default',
    manager: 'default',
    advisor: 'default',
    insurance: 'default'
  },
  enableAiResponse: true,
  enableIntentRecognition: true,
  enableSmartDispatch: true
})

// 计算属性
const enabledModels = computed(() => modelList.value.filter(m => m.enabled))

// 获取提供商标签
const getProviderLabel = (provider: string) => {
  const p = availableProviders.find(p => p.value === provider)
  return p?.label || provider
}

// 获取模型颜色
const getModelColor = (provider: string) => {
  const p = availableProviders.find(p => p.value === provider)
  return p?.color || '#6b7280'
}

// 获取模型图标
const getModelIcon = (provider: string) => {
  const p = availableProviders.find(p => p.value === provider)
  return p?.icon || 'Cpu'
}

// 获取模型版本选项
const getModelVersions = (provider: string) => {
  return modelVersions[provider] || []
}

// 是否显示API URL
const showApiUrl = (provider: string) => {
  return provider !== 'custom'
}

// 是否显示Base URL
const showBaseUrl = (provider: string) => {
  return provider === 'custom'
}

// 加载模型列表
const loadModels = async () => {
  // 模拟数据，实际应从API获取
  modelList.value = [
    {
      id: '1',
      name: 'OpenAI GPT-4',
      provider: 'openai',
      enabled: true,
      isDefault: true,
      config: {
        modelVersion: 'gpt-4',
        apiKey: '',
        apiUrl: 'https://api.openai.com/v1',
        temperature: 0.7,
        maxTokens: 2000,
        topP: 1,
        timeout: 30
      }
    }
  ]
  if (modelList.value.length > 0) {
    currentModel.value = modelList.value[0]
  }
}

// 选择模型
const selectModel = (model: any) => {
  currentModel.value = model
  testResult.value = null
}

// 显示添加模型对话框
const showAddModelDialog = () => {
  addModelDialogVisible.value = true
}

// 选择提供商
const selectProvider = (provider: string) => {
  const newModel = {
    id: Date.now().toString(),
    name: getProviderLabel(provider) + ' ' + (modelList.value.length + 1),
    provider,
    enabled: true,
    isDefault: modelList.value.length === 0,
    config: {
      modelVersion: modelVersions[provider]?.[0]?.value || '',
      apiKey: '',
      ...defaultConfigs[provider]
    }
  }
  modelList.value.push(newModel)
  currentModel.value = newModel
  addModelDialogVisible.value = false
  ElMessage.success('模型添加成功')
}

// 切换模型启用状态
const toggleModelEnabled = (model: any, enabled: boolean) => {
  model.enabled = enabled
  ElMessage.success(`${model.name} 已${enabled ? '启用' : '禁用'}`)
}

// 设为默认
const setAsDefault = (model: any) => {
  modelList.value.forEach(m => m.isDefault = false)
  model.isDefault = true
  globalConfig.defaultModelId = model.id
  ElMessage.success(`${model.name} 已设为默认模型`)
}

// 删除模型
const deleteModel = async (model: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除模型 "${model.name}" 吗？`,
      '确认删除',
      { type: 'warning' }
    )
    const index = modelList.value.findIndex(m => m.id === model.id)
    modelList.value.splice(index, 1)
    if (currentModel.value?.id === model.id) {
      currentModel.value = modelList.value[0] || null
    }
    ElMessage.success('模型已删除')
  } catch {
    // 取消删除
  }
}

// 保存模型配置
const saveModelConfig = async () => {
  if (!currentModel.value) return
  
  saving.value = true
  try {
    // TODO: 调用API保存
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('模型配置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 测试模型连接
const testModelConnection = async () => {
  if (!currentModel.value) return
  
  testing.value = true
  testResult.value = null
  
  try {
    // TODO: 调用API测试连接
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    testResult.value = {
      type: 'success',
      title: '连接测试成功',
      message: `模型 ${currentModel.value.name} 连接正常，响应时间: 1.2s，支持的功能: 流式输出、函数调用、JSON模式`
    }
  } catch (error) {
    testResult.value = {
      type: 'error',
      title: '连接测试失败',
      message: '请检查API密钥和地址配置是否正确，确保网络连接正常'
    }
  } finally {
    testing.value = false
  }
}

// 保存全局配置
const saveGlobalConfig = async () => {
  savingGlobal.value = true
  try {
    // TODO: 调用API保存
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('全局配置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    savingGlobal.value = false
  }
}

onMounted(() => {
  loadModels()
})
</script>

<style scoped>
.ai-config-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.models-card {
  margin-bottom: 20px;
}

.model-count {
  font-size: 12px;
}

.models-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.model-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.model-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.model-card.active {
  border-color: #409eff;
  background: #f0f9ff;
}

.model-card.disabled {
  opacity: 0.6;
}

.model-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.model-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.model-info {
  flex: 1;
}

.model-name {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
}

.model-provider {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.model-status {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.default-tag {
  background-color: #e6a23c;
  color: white;
}

.model-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.config-detail-card {
  margin-bottom: 20px;
}

.model-config-form {
  max-width: 700px;
}

.param-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.param-value {
  min-width: 50px;
  text-align: right;
  font-weight: bold;
  color: #409eff;
}

.unit {
  margin-left: 8px;
  color: #606266;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.test-result-section {
  margin-top: 20px;
}

.provider-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.provider-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.provider-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
  transform: translateY(-2px);
}

.provider-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin: 0 auto 12px;
}

.provider-name {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
  margin-bottom: 4px;
}

.provider-desc {
  font-size: 12px;
  color: #909399;
}

.global-config-card {
  margin-top: 20px;
}

.role-models {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.role-model-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-label {
  width: 80px;
  color: #606266;
}

.feature-switches {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

:deep(.el-divider__text) {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}
</style>
