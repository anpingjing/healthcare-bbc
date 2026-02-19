import type { InsuranceProduct, InsurancePolicy, ApiResponse } from '@/types'
import request from '@/utils/request'

export interface InsuranceProductCreateDTO {
  productCode: string
  productName: string
  productType: string
  minAge: number
  maxAge: number
  minPremium: number
  maxPremium: number
  coverageAmount: number
  coveragePeriod: string
  paymentPeriod: string
  healthRequirements?: string
  status?: number
}

export interface InsurancePolicyCreateDTO {
  policyNo: string
  userId: number
  productId: number
  premiumAmount: number
  coverageAmount: number
  startDate: string
  endDate: string
  paymentPeriod: string
  status?: number
}

export const insuranceApi = {
  getProducts(params: any) {
    return request.get<ApiResponse<any>>('/insurance-products', { params })
  },

  getAvailableProducts() {
    return request.get<ApiResponse<InsuranceProduct[]>>('/insurance-products/available')
  },

  getProductByType(productType: string) {
    return request.get<ApiResponse<InsuranceProduct[]>>(`/insurance-products/type/${productType}`)
  },

  getProductById(id: number) {
    return request.get<ApiResponse<InsuranceProduct>>(`/insurance-products/${id}`)
  },

  createProduct(data: InsuranceProductCreateDTO) {
    return request.post<ApiResponse<InsuranceProduct>>('/insurance-products', data)
  },

  updateProduct(id: number, data: InsuranceProductCreateDTO) {
    return request.put<ApiResponse<InsuranceProduct>>(`/insurance-products/${id}`, data)
  },

  deleteProduct(id: number) {
    return request.delete<ApiResponse<boolean>>(`/insurance-products/${id}`)
  },

  getPolicies(params: any) {
    return request.get<ApiResponse<any>>('/insurance-policies', { params })
  },

  getPoliciesByUserId(userId: number) {
    return request.get<ApiResponse<InsurancePolicy[]>>(`/insurance-policies/user/${userId}`)
  },

  getPoliciesByUserIdAndStatus(userId: number, status: number) {
    return request.get<ApiResponse<InsurancePolicy[]>>(`/insurance-policies/user/${userId}/status/${status}`)
  },

  getPolicyById(id: number) {
    return request.get<ApiResponse<InsurancePolicy>>(`/insurance-policies/${id}`)
  },

  createPolicy(data: InsurancePolicyCreateDTO) {
    return request.post<ApiResponse<InsurancePolicy>>('/insurance-policies', data)
  },

  updatePolicy(id: number, data: InsurancePolicyCreateDTO) {
    return request.put<ApiResponse<InsurancePolicy>>(`/insurance-policies/${id}`, data)
  },

  deletePolicy(id: number) {
    return request.delete<ApiResponse<boolean>>(`/insurance-policies/${id}`)
  }
}
