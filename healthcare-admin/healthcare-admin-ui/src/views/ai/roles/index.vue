<template>
  <div class="ai-roles-container">
    <el-card class="roles-card">
      <template #header>
        <div class="card-header">
          <span>4R角色AI配置</span>
          <el-button type="primary" @click="saveAllConfigs" :loading="saving">
            保存全部配置
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- 家庭医生 -->
        <el-tab-pane label="家庭医生" name="doctor">
          <div class="role-section">
            <h3 class="role-title">
              <el-icon><FirstAidKit /></el-icon>
              家庭医生 (Family Doctor)
            </h3>
            <p class="role-desc">专业医疗核心角色，是用户健康诊疗的第一责任人，提供在线医疗服务与长期健康管理。</p>
            
            <el-form :model="roleConfigs.doctor" label-width="120px" class="role-form">
              <el-form-item label="角色状态">
                <el-switch
                  v-model="roleConfigs.doctor.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>
              
              <el-form-item label="响应模式">
                <el-radio-group v-model="roleConfigs.doctor.responseMode">
                  <el-radio label="ai_first">AI优先</el-radio>
                  <el-radio label="human_first">人工优先</el-radio>
                  <el-radio label="ai_only">仅AI</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="系统提示词">
                <el-input
                  v-model="roleConfigs.doctor.systemPrompt"
                  type="textarea"
                  :rows="10"
                  placeholder="请输入系统提示词，定义AI角色行为"
                />
                <div class="prompt-actions">
                  <el-button link type="primary" @click="resetPrompt('doctor')">
                    恢复默认
                  </el-button>
                  <el-button link type="success" @click="testPrompt('doctor')">
                    测试提示词
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item label="核心能力">
                <el-checkbox-group v-model="roleConfigs.doctor.capabilities">
                  <el-checkbox label="symptom_screening">症状初筛</el-checkbox>
                  <el-checkbox label="medical_record">病历调取</el-checkbox>
                  <el-checkbox label="medication_advice">用药建议</el-checkbox>
                  <el-checkbox label="plan_review">方案审核</el-checkbox>
                  <el-checkbox label="referral">就医引导</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 健康管家 -->
        <el-tab-pane label="健康管家" name="manager">
          <div class="role-section">
            <h3 class="role-title">
              <el-icon><User /></el-icon>
              健康管家 (Health Butler)
            </h3>
            <p class="role-desc">AI智能助手，作为用户与其他角色的调度枢纽，7×24h响应，提供基础健康服务、角色协调及健康管理方案的落地执行。</p>
            
            <el-form :model="roleConfigs.manager" label-width="120px" class="role-form">
              <el-form-item label="角色状态">
                <el-switch
                  v-model="roleConfigs.manager.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>
              
              <el-form-item label="响应模式">
                <el-radio-group v-model="roleConfigs.manager.responseMode">
                  <el-radio label="ai_first">AI优先</el-radio>
                  <el-radio label="human_first">人工优先</el-radio>
                  <el-radio label="ai_only">仅AI</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="系统提示词">
                <el-input
                  v-model="roleConfigs.manager.systemPrompt"
                  type="textarea"
                  :rows="10"
                  placeholder="请输入系统提示词，定义AI角色行为"
                />
                <div class="prompt-actions">
                  <el-button link type="primary" @click="resetPrompt('manager')">
                    恢复默认
                  </el-button>
                  <el-button link type="success" @click="testPrompt('manager')">
                    测试提示词
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item label="核心能力">
                <el-checkbox-group v-model="roleConfigs.manager.capabilities">
                  <el-checkbox label="basic_consult">基础咨询</el-checkbox>
                  <el-checkbox label="task_reminder">任务提醒</el-checkbox>
                  <el-checkbox label="role_dispatch">角色调度</el-checkbox>
                  <el-checkbox label="data_monitoring">数据监测</el-checkbox>
                  <el-checkbox label="plan_execution">方案执行</el-checkbox>
                  <el-checkbox label="plan_update">方案更新</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 权益顾问 -->
        <el-tab-pane label="权益顾问" name="advisor">
          <div class="role-section">
            <h3 class="role-title">
              <el-icon><Gift /></el-icon>
              权益顾问 (Rights Consultant)
            </h3>
            <p class="role-desc">健康权益专家，负责用户健康服务权益的推广、使用指导与价值传递，助力健康管理方案落地。</p>
            
            <el-form :model="roleConfigs.advisor" label-width="120px" class="role-form">
              <el-form-item label="角色状态">
                <el-switch
                  v-model="roleConfigs.advisor.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>
              
              <el-form-item label="响应模式">
                <el-radio-group v-model="roleConfigs.advisor.responseMode">
                  <el-radio label="ai_first">AI优先</el-radio>
                  <el-radio label="human_first">人工优先</el-radio>
                  <el-radio label="ai_only">仅AI</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="系统提示词">
                <el-input
                  v-model="roleConfigs.advisor.systemPrompt"
                  type="textarea"
                  :rows="10"
                  placeholder="请输入系统提示词，定义AI角色行为"
                />
                <div class="prompt-actions">
                  <el-button link type="primary" @click="resetPrompt('advisor')">
                    恢复默认
                  </el-button>
                  <el-button link type="success" @click="testPrompt('advisor')">
                    测试提示词
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item label="核心能力">
                <el-checkbox-group v-model="roleConfigs.advisor.capabilities">
                  <el-checkbox label="rights_inventory">权益盘点</el-checkbox>
                  <el-checkbox label="rights_guidance">权益引导</el-checkbox>
                  <el-checkbox label="activity_push">活动推送</el-checkbox>
                  <el-checkbox label="feedback_collection">反馈收集</el-checkbox>
                  <el-checkbox label="plan_empowerment">方案赋能</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="推送限制">
                <el-input-number
                  v-model="roleConfigs.advisor.maxPushPerMonth"
                  :min="1"
                  :max="10"
                  :step="1"
                />
                <span class="unit">次/月</span>
                <div class="form-tip">每月主动推荐不超过设定次数</div>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 专属保险顾问 -->
        <el-tab-pane label="保险顾问" name="insurance">
          <div class="role-section">
            <h3 class="role-title">
              <el-icon><Shield /></el-icon>
              专属保险顾问 (Insurance Consultant)
            </h3>
            <p class="role-desc">保险规划专家，按需配置，为用户提供个性化保险方案与长期保障管理，作为健康管理方案的风险保障补充。</p>
            
            <el-form :model="roleConfigs.insurance" label-width="120px" class="role-form">
              <el-form-item label="角色状态">
                <el-switch
                  v-model="roleConfigs.insurance.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>
              
              <el-form-item label="响应模式">
                <el-radio-group v-model="roleConfigs.insurance.responseMode">
                  <el-radio label="ai_first">AI优先</el-radio>
                  <el-radio label="human_first">人工优先</el-radio>
                  <el-radio label="ai_only">仅AI</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="系统提示词">
                <el-input
                  v-model="roleConfigs.insurance.systemPrompt"
                  type="textarea"
                  :rows="10"
                  placeholder="请输入系统提示词，定义AI角色行为"
                />
                <div class="prompt-actions">
                  <el-button link type="primary" @click="resetPrompt('insurance')">
                    恢复默认
                  </el-button>
                  <el-button link type="success" @click="testPrompt('insurance')">
                    测试提示词
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item label="核心能力">
                <el-checkbox-group v-model="roleConfigs.insurance.capabilities">
                  <el-checkbox label="needs_assessment">需求评估</el-checkbox>
                  <el-checkbox label="plan_customization">方案定制</el-checkbox>
                  <el-checkbox label="regular_review">定期回顾</el-checkbox>
                  <el-checkbox label="plan_coordination">方案协同</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="入群授权">
                <el-switch
                  v-model="roleConfigs.insurance.requireAuthorization"
                  active-text="需要用户授权"
                  inactive-text="无需授权"
                />
                <div class="form-tip">开启后，保险顾问需经用户同意后才能入群</div>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 提示词测试对话框 -->
    <el-dialog
      v-model="testDialogVisible"
      title="提示词测试"
      width="600px"
    >
      <div class="test-dialog-content">
        <el-input
          v-model="testInput"
          type="textarea"
          :rows="3"
          placeholder="输入测试消息"
        />
        <el-button type="primary" @click="runTest" :loading="testing" style="margin-top: 10px;">
          发送测试
        </el-button>
        <div v-if="testResult" class="test-result">
          <el-divider />
          <h4>AI回复：</h4>
          <div class="result-content">{{ testResult }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('doctor')
const saving = ref(false)
const testDialogVisible = ref(false)
const testing = ref(false)
const testInput = ref('')
const testResult = ref('')
const currentTestRole = ref('')

// 默认提示词配置
const defaultPrompts = {
  doctor: `你是「家庭医生」，是用户健康诊疗的第一责任人。

【角色定位】
- 专业医疗核心角色，提供在线医疗服务与长期健康管理
- 具备执业医生资质，可进行专业医疗诊断和建议

【核心职责】
1. 在线问诊：针对用户常见健康问题提供专业诊疗建议
2. 健康指导：根据用户健康档案，提供慢病管理、康复指导
3. 初步评估：对用户症状进行初步问询，评估病情紧急程度
4. 就医引导：根据评估结果，引导用户至合适医疗机构
5. 定期随访：对慢病患者进行定期健康随访
6. 方案审核：审核AI生成的健康管理方案，确保科学性

【服务边界】
- 不进行线下实体诊疗操作，仅提供线上诊疗建议
- 不处理超出执业范围的医疗行为，复杂病例需转介
- 紧急情况仅做初步处理与引导，不替代专业急救服务

【回复原则】
- 专业、严谨、有同理心
- 必要时建议用户及时就医
- 不给出确定性诊断，仅提供参考建议`,

  manager: `你是「健康管家」，是用户的AI智能助手和角色调度枢纽。

【角色定位】
- 7×24h响应的智能助手
- 用户与其他角色的调度枢纽
- 健康管理方案的落地执行者

【核心职责】
1. 基础咨询：解答用户常见健康疑问（日常保健、疫苗接种等）
2. 任务提醒：推送用药提醒、随访提醒、体检提醒
3. 角色调度：根据用户需求，转介至家庭医生、权益顾问或保险顾问
4. 入群服务：用户入群时发送欢迎语，引导完成健康采集
5. 数据监测：跟踪用户健康数据，异常时及时预警
6. 方案执行：执行健康管理方案中的回访、触达、信息收集
7. 方案更新：根据用户信息自动更新健康管理方案

【服务边界】
- 不提供专业医疗诊断和保险规划
- 不处理复杂医疗或金融决策
- 健康管理方案仅为辅助工具，不替代专业医疗

【回复原则】
- 热情、友好、耐心
- 主动识别用户需求并引导
- 复杂问题及时转介专业角色`,

  advisor: `你是「权益顾问」，是用户健康服务权益的专家。

【角色定位】
- 健康权益专家
- 负责健康服务权益的推广、使用指导与价值传递
- 助力健康管理方案落地

【核心职责】
1. 权益盘点：查询用户账户内的健康权益（问诊券、折扣卡、体检绿通等）
2. 权益引导：在用户有需求时，引导使用对应权益
3. 活动推送：向感兴趣的用户精准推送新权益活动
4. 反馈收集：定期回访，收集对权益服务的满意度
5. 入群介绍：用户入群时推送可享有的健康服务权益概览
6. 方案赋能：将健康权益融入健康管理方案

【服务边界】
- 不提供医疗诊断和保险规划
- 不进行产品销售诱导，推荐以用户需求为导向
- 每月主动推荐不超过2次

【回复原则】
- 清晰、准确地介绍权益内容
- 帮助用户最大化利用已有权益
- 尊重用户选择，不强行推销`,

  insurance: `你是「专属保险顾问」，是用户的保险规划专家。

【角色定位】
- 保险规划专家
- 按需配置，提供个性化保险方案
- 健康管理方案的风险保障补充

【核心职责】
1. 需求评估：了解用户健康状况、已有保障及经济状况
2. 方案定制：基于保障缺口分析，提供个性化保险规划
3. 定期回顾：每季度回顾保险规划，根据变化调整方案
4. 授权管理：需经用户同意后才能提供服务
5. 方案协同：将保险建议融入健康管理方案

【服务边界】
- 不强制推销保险产品，以用户需求为核心
- 不提供超出保险范畴的医疗或金融建议
- 仅在用户授权的社群中提供服务

【回复原则】
- 专业、客观地分析保障需求
- 遵循"保障全面且保费合理"原则
- 尊重用户决策，提供充分信息支持`}

const roleConfigs = reactive({
  doctor: {
    enabled: true,
    responseMode: 'human_first',
    systemPrompt: defaultPrompts.doctor,
    capabilities: ['symptom_screening', 'medical_record', 'medication_advice', 'plan_review', 'referral']
  },
  manager: {
    enabled: true,
    responseMode: 'ai_first',
    systemPrompt: defaultPrompts.manager,
    capabilities: ['basic_consult', 'task_reminder', 'role_dispatch', 'data_monitoring', 'plan_execution', 'plan_update']
  },
  advisor: {
    enabled: true,
    responseMode: 'ai_first',
    systemPrompt: defaultPrompts.advisor,
    capabilities: ['rights_inventory', 'rights_guidance', 'activity_push', 'feedback_collection', 'plan_empowerment'],
    maxPushPerMonth: 2
  },
  insurance: {
    enabled: true,
    responseMode: 'human_first',
    systemPrompt: defaultPrompts.insurance,
    capabilities: ['needs_assessment', 'plan_customization', 'regular_review', 'plan_coordination'],
    requireAuthorization: true
  }
})

// 加载配置
const loadConfigs = async () => {
  try {
    // TODO: 调用API加载配置
    // const res = await aiRoleApi.getRoleConfigs()
    // Object.assign(roleConfigs, res.data)
  } catch (error) {
    console.error('加载配置失败:', error)
  }
}

// 保存全部配置
const saveAllConfigs = async () => {
  saving.value = true
  try {
    // TODO: 调用API保存配置
    // await aiRoleApi.saveRoleConfigs(roleConfigs)
    ElMessage.success('角色配置保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 恢复默认提示词
const resetPrompt = (role: string) => {
  roleConfigs[role as keyof typeof roleConfigs].systemPrompt = defaultPrompts[role as keyof typeof defaultPrompts]
  ElMessage.success('已恢复默认提示词')
}

// 测试提示词
const testPrompt = (role: string) => {
  currentTestRole.value = role
  testInput.value = ''
  testResult.value = ''
  testDialogVisible.value = true
}

// 运行测试
const runTest = async () => {
  if (!testInput.value.trim()) {
    ElMessage.warning('请输入测试消息')
    return
  }
  
  testing.value = true
  try {
    // TODO: 调用API测试提示词
    // const res = await aiRoleApi.testPrompt({
    //   role: currentTestRole.value,
    //   systemPrompt: roleConfigs[currentTestRole.value].systemPrompt,
    //   message: testInput.value
    // })
    
    // 模拟测试结果
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    const roleNames: Record<string, string> = {
      doctor: '家庭医生',
      manager: '健康管家',
      advisor: '权益顾问',
      insurance: '保险顾问'
    }
    
    testResult.value = `【${roleNames[currentTestRole.value]}回复】\n\n收到您的消息："${testInput.value}"\n\n我是您的${roleNames[currentTestRole.value]}，很高兴为您服务。根据您的描述，我会尽力为您提供专业的建议和帮助。请问还有其他需要了解的吗？`
  } catch (error) {
    ElMessage.error('测试失败')
  } finally {
    testing.value = false
  }
}

onMounted(() => {
  loadConfigs()
})
</script>

<style scoped>
.ai-roles-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.role-section {
  padding: 20px;
}

.role-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  color: #303133;
}

.role-title .el-icon {
  font-size: 24px;
  color: #409EFF;
}

.role-desc {
  color: #606266;
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
}

.role-form {
  max-width: 800px;
}

.prompt-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.unit {
  margin-left: 10px;
  color: #606266;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.test-dialog-content {
  padding: 10px;
}

.test-result {
  margin-top: 20px;
}

.result-content {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.8;
  white-space: pre-wrap;
}

:deep(.el-tabs__content) {
  padding: 0;
}
</style>
