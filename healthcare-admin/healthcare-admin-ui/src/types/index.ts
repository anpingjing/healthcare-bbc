// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: string
  requestId: string
}

// 分页响应类型
export interface PageResponse<T = any> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
  hasNextPage: boolean
  hasPreviousPage: boolean
}

// 登录相关类型
export interface LoginForm {
  username: string
  password: string
}

export interface LoginResult {
  accessToken: string
  refreshToken: string
  expiresIn: number
  tokenType: string
  user: {
    userId: number
    username: string
    realName: string
    avatar: string
    roles: string[]
    permissions: string[]
  }
}

// 用户类型
export interface User {
  userId: number
  username: string
  realName: string
  phone: string
  email: string
  avatar: string
  gender: number
  deptId: number
  status: number
  createTime: string
}

// 角色类型
export interface Role {
  roleId: number
  roleCode: string
  roleName: string
  roleType: number
  dataScope: number
  description: string
  status: number
  createTime: string
}

// 社群类型
export interface Group {
  groupId: number
  groupName: string
  groupType: number
  chatId: string
  memberCount: number
  maxMembers: number
  status: number
  createTime: string
}

// API类型
export interface ApiInfo {
  apiId: number
  apiCode: string
  apiName: string
  apiCategory: number
  httpMethod: string
  apiPath: string
  status: number
  createTime: string
}
