<template>
  <div class="collection-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户信息采集</span>
        </div>
      </template>
      <el-form :model="form" label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="150" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">提交</el-button>
          <el-button>取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const form = reactive({
  name: '',
  gender: 1,
  age: 18,
  phone: '',
  email: ''
})

const onSubmit = () => {
  console.log('submit!', form)
  ElMessage.success('信息采集成功')
}
</script>

<style scoped>
.collection-container {
  max-width: 600px;
  margin: 20px auto;
}
</style>
