import type { HealthRecord, ApiResponse, PageResult } from '@/types'
import request from '@/utils/request'

export interface HealthRecordCreateDTO {
  userId: number
  height?: number
  weight?: number
  bloodType?: string
  medicalHistory?: string
  allergyHistory?: string
  familyHistory?: string
}

export interface PageQueryDTO {
  pageNum?: number
  pageSize?: number
  keyword?: string
  sortBy?: string
  sortOrder?: string
}

export const healthRecordApi = {
  list(params: PageQueryDTO) {
    return request.get<ApiResponse<PageResult<HealthRecord>>>('/health-records', { params })
  },

  getByUserId(userId: number) {
    return request.get<ApiResponse<HealthRecord>>(`/health-records/user/${userId}`)
  },

  getById(id: number) {
    return request.get<ApiResponse<HealthRecord>>(`/health-records/${id}`)
  },

  create(data: HealthRecordCreateDTO) {
    return request.post<ApiResponse<HealthRecord>>('/health-records', data)
  },

  update(id: number, data: HealthRecordCreateDTO) {
    return request.put<ApiResponse<HealthRecord>>(`/health-records/${id}`, data)
  },

  delete(id: number) {
    return request.delete<ApiResponse<boolean>>(`/health-records/${id}`)
  }
}
