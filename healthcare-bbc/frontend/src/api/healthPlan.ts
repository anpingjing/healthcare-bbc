import type { HealthPlan, ApiResponse } from '@/types'
import request from '@/utils/request'

export interface HealthPlanCreateDTO {
  userId: number
  planType: string
  planContent: string
  startDate: string
  endDate: string
  status?: number
}

export const healthPlanApi = {
  list(params: any) {
    return request.get<ApiResponse<any>>('/health-plans', { params })
  },

  getByUserId(userId: number) {
    return request.get<ApiResponse<HealthPlan[]>>(`/health-plans/user/${userId}`)
  },

  getByUserIdAndStatus(userId: number, status: number) {
    return request.get<ApiResponse<HealthPlan[]>>(`/health-plans/user/${userId}/status/${status}`)
  },

  create(data: HealthPlanCreateDTO) {
    return request.post<ApiResponse<HealthPlan>>('/health-plans', data)
  },

  update(id: number, data: HealthPlanCreateDTO) {
    return request.put<ApiResponse<HealthPlan>>(`/health-plans/${id}`, data)
  },

  delete(id: number) {
    return request.delete<ApiResponse<boolean>>(`/health-plans/${id}`)
  }
}
