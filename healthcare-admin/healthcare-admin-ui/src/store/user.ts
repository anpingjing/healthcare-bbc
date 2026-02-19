import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout } from '@/api/auth'
import type { LoginForm, LoginResult } from '@/types'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<LoginResult | null>(null)

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.name || '')
  const userId = computed(() => userInfo.value?.userId || 0)

  // Actions
  const setToken = (accessToken: string) => {
    token.value = accessToken
    localStorage.setItem('token', accessToken)
  }

  const clearToken = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  const loginAction = async (loginForm: LoginForm) => {
    const res = await login(loginForm)
    if (res.code === 200) {
      const data = res.data
      setToken(data.token)
      userInfo.value = data
      return true
    }
    return false
  }

  const logoutAction = async () => {
    try {
      await logout()
    } finally {
      clearToken()
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    userId,
    setToken,
    clearToken,
    loginAction,
    logoutAction
  }
})
