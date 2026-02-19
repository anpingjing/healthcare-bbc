export interface User {
  userId: number
  wechatId: string
  name: string
  gender: number
  genderText: string
  age: number
  phone: string
  email: string
  createTime: string
  updateTime: string
  status: number
  statusText: string
}

export interface HealthRecord {
  recordId: number
  userId: number
  height: number
  weight: number
  bloodType: string
  medicalHistory: string
  allergyHistory: string
  familyHistory: string
  createTime: string
  updateTime: string
}

export interface HealthMonitorData {
  dataId: number
  userId: number
  dataType: string
  dataValue: number
  dataUnit: string
  measureTime: string
  deviceType: string
  deviceId: string
  createTime: string
}

export interface HealthPlan {
  planId: number
  userId: number
  planType: string
  planContent: string
  startDate: string
  endDate: string
  status: number
  createTime: string
  updateTime: string
}

export interface InsuranceProduct {
  productId: number
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
  healthRequirements: string
  createTime: string
  updateTime: string
  status: number
}

export interface InsurancePolicy {
  policyId: number
  policyNo: string
  userId: number
  productId: number
  premiumAmount: number
  coverageAmount: number
  startDate: string
  endDate: string
  paymentPeriod: string
  status: number
  createTime: string
  updateTime: string
}

export interface UserTag {
  tagId: number
  userId: number
  tagCategory: string
  tagName: string
  tagSource: string
  confidenceScore: number
  createTime: string
}

export interface HealthBenefit {
  benefitId: number
  benefitName: string
  benefitType: string
  description: string
  validPeriod: number
  status: number
  createTime: string
}

export interface UserBenefitRel {
  relId: number
  userId: number
  benefitId: number
  sourceType: string
  startTime: string
  endTime: string
  status: number
  useTime: string
}

export interface PageResult<T> {
  total: number
  records: T[]
  pageNum: number
  pageSize: number
  pages: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}
