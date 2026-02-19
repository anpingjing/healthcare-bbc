import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('@/views/layout/index.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '首页', icon: 'HomeFilled' }
        },
        {
          path: 'users',
          name: 'Users',
          component: () => import('@/views/user/index.vue'),
          meta: { title: '用户管理', icon: 'UserFilled' }
        },
        {
          path: 'roles',
          name: 'Roles',
          component: () => import('@/views/role/index.vue'),
          meta: { title: '角色管理', icon: 'Collection' }
        },
        {
          path: 'groups',
          name: 'Groups',
          component: () => import('@/views/group/index.vue'),
          meta: { title: '社群管理', icon: 'ChatDotRound' }
        },
        {
          path: 'groups/:id/keyword-reply',
          name: 'KeywordReply',
          component: () => import('@/views/group/keyword-reply.vue'),
          meta: { title: '关键词回复', icon: 'ChatLineSquare' }
        },
        {
          path: 'apis',
          name: 'Apis',
          component: () => import('@/views/api/index.vue'),
          meta: { title: 'API管理', icon: 'Connection' }
        },
        {
          path: 'health/profile',
          name: 'HealthProfile',
          component: () => import('@/views/health/profile/index.vue'),
          meta: { title: '健康档案', icon: 'Document' }
        },
        {
          path: 'health/plan',
          name: 'HealthPlan',
          component: () => import('@/views/health/plan/index.vue'),
          meta: { title: '健康计划', icon: 'Calendar' }
        },
        {
          path: 'health/data',
          name: 'HealthData',
          component: () => import('@/views/health/data/index.vue'),
          meta: { title: '健康数据', icon: 'DataLine' }
        },
        {
          path: 'service/record',
          name: 'ServiceRecord',
          component: () => import('@/views/service/record/index.vue'),
          meta: { title: '服务记录', icon: 'Service' }
        },
        {
          path: 'wechat/groups',
          name: 'WechatGroups',
          component: () => import('@/views/wechat/groups.vue'),
          meta: { title: '企微群管理', icon: 'ChatDotSquare' }
        },
        {
          path: 'wechat/contacts',
          name: 'WechatContacts',
          component: () => import('@/views/wechat/contacts.vue'),
          meta: { title: '企微联系人', icon: 'User' }
        },
        {
          path: 'wechat/config',
          name: 'WechatConfig',
          component: () => import('@/views/wechat/config/index.vue'),
          meta: { title: '企微对接配置', icon: 'Setting' }
        },
        {
          path: 'statistics/overview',
          name: 'StatisticsOverview',
          component: () => import('@/views/statistics/overview.vue'),
          meta: { title: '数据概览', icon: 'DataAnalysis' }
        },
        {
          path: 'ai/config',
          name: 'AiConfig',
          component: () => import('@/views/ai/config/index.vue'),
          meta: { title: 'AI大模型配置', icon: 'Cpu' }
        },
        {
          path: 'ai/roles',
          name: 'AiRoles',
          component: () => import('@/views/ai/roles/index.vue'),
          meta: { title: '4R角色配置', icon: 'UserFilled' }
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.public) {
    next()
  } else if (!userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
