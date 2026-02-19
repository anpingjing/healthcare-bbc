import type { User, ApiResponse, PageResult } from '@/types'
import request from '@/utils/request'

export interface UserCreateDTO {
  wechatId: string
  name: string
  gender?: number
  age?: number
  phone?: string
  email?: string
}

export interface UserUpdateDTO {
  name?: string
  gender?: number
  age?: number
  phone?: string
  email?: string
  status?: number
}

export interface PageQueryDTO {
  pageNum?: number
  pageSize?: number
  keyword?: string
  sortBy?: string
  sortOrder?: string
}

export const userApi = {
  list(params: PageQueryDTO) {
    return request.get<ApiResponse<PageResult<User>>>('/users', { params })
  },

  getById(id: number) {
    return request.get<ApiResponse<User>>(`/users/${id}`)
  },

  create(data: UserCreateDTO) {
    return request.post<ApiResponse<User>>('/users', data)
  },

  update(id: number, data: UserUpdateDTO) {
    return request.put<ApiResponse<User>>(`/users/${id}`, data)
  },

  delete(id: number) {
    return request.delete<ApiResponse<boolean>>(`/users/${id}`)
  }
}
