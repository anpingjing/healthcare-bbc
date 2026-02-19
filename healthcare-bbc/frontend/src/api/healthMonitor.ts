import type { HealthMonitorData, ApiResponse } from '@/types'
import request from '@/utils/request'

export interface HealthMonitorDataCreateDTO {
  userId: number
  dataType: string
  dataValue: number
  dataUnit: string
  measureTime: string
  deviceType?: string
  deviceId?: string
}

export const healthMonitorApi = {
  list(params: any) {
    return request.get<ApiResponse<any>>('/health-monitor-data', { params })
  },

  getByUserId(userId: number) {
    return request.get<ApiResponse<HealthMonitorData[]>>(`/health-monitor-data/user/${userId}`)
  },

  getByUserIdAndDataType(userId: number, dataType: string) {
    return request.get<ApiResponse<HealthMonitorData[]>>(`/health-monitor-data/user/${userId}/type/${dataType}`)
  },

  getByUserIdAndTimeRange(userId: number, startTime: string, endTime: string) {
    return request.get<ApiResponse<HealthMonitorData[]>>(`/health-monitor-data/user/${userId}/time-range`, {
      params: { startTime, endTime }
    })
  },

  create(data: HealthMonitorDataCreateDTO) {
    return request.post<ApiResponse<HealthMonitorData>>('/health-monitor-data', data)
  },

  batchCreate(data: HealthMonitorDataCreateDTO[]) {
    return request.post<ApiResponse<boolean>>('/health-monitor-data/batch', data)
  },

  delete(id: number) {
    return request.delete<ApiResponse<boolean>>(`/health-monitor-data/${id}`)
  }
}
