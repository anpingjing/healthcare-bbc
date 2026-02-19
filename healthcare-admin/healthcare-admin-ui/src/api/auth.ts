import request from '@/utils/request'
import type { ApiResponse, LoginForm, LoginResult } from '@/types'

export const login = (data: LoginForm): Promise<ApiResponse<LoginResult>> => {
  return request({
    url: '/api/v1/auth/login',
    method: 'post',
    data
  })
}

export const logout = (): Promise<ApiResponse<void>> => {
  return request({
    url: '/api/v1/auth/logout',
    method: 'post'
  })
}

export const refreshToken = (refreshToken: string): Promise<ApiResponse<LoginResult>> => {
  return request({
    url: '/api/v1/auth/refresh',
    method: 'post',
    params: { refreshToken }
  })
}
