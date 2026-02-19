<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>企业微信健康社群</span>
        </div>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="企业微信ID" prop="wechatId">
          <el-input v-model="loginForm.wechatId" placeholder="请输入企业微信ID" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/store/user'
import { authApi } from '@/api/auth'
import type { LoginDTO } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive<LoginDTO>({
  wechatId: ''
})

const rules: FormRules = {
  wechatId: [
    { required: true, message: '请输入企业微信ID', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await authApi.login(loginForm)
        userStore.setToken(response.token)
        userStore.setUserInfo({
          userId: response.userId,
          wechatId: response.wechatId,
          name: response.name,
          gender: 0,
          genderText: '未知',
          age: 0,
          phone: '',
          email: '',
          createTime: '',
          updateTime: '',
          status: 1,
          statusText: '正常'
        })
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error('Login error:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}
</style>
