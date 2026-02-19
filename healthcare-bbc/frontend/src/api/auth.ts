import type { ApiResponse } from '@/types'
import request from '@/utils/request'

export interface LoginDTO {
  wechatId: string
}

export interface LoginVO {
  token: string
  userId: number
  wechatId: string
  name: string
}

export const authApi = {
  login(data: LoginDTO) {
    return request.post<ApiResponse<LoginVO>>('/auth/login', data)
  },

  logout() {
    return request.post<ApiResponse<void>>('/auth/logout')
  }
}
