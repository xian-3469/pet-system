import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store";

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue')
  },
  {
    path: '/front',
    name: 'Front',
    component: () => import('../views/front/Front'),
    children: [
      { path: 'person', name: 'FrontPerson', component: () => import('../views/front/Person.vue')},
      { path: 'password', name: 'FrontPassword', component: () => import('../views/front/Password.vue')},
      {
        path: 'home',
        name: 'FrontHome',
        component: () => import('../views/front/Home.vue')
      },
      {
        path: 'homeDetail',
        name: 'HomeDetail',
        component: () => import('../views/front/HomeDetail')
      },
      {
        path: 'adopt',
        name: 'Adopt',
        component: () => import('../views/front/Adopt.vue')
      },
      {
        path: 'myAdopt',
        name: 'MyAdopt',
        component: () => import('../views/front/MyAdopt.vue')
      },
      {
        path: 'salvation',
        name: 'Salvation',
        component: () => import('../views/front/Salvation.vue')
      },
      {
        path: 'article',
        name: 'Article',
        component: () => import('../views/front/Article.vue')
      },
      {
        path: 'petPublic',
        name: 'PetPublic',
        component: () => import('../views/front/PetPublic.vue')
      },
      {
        path: 'petProfile',
        name: 'PetProfile',
        component: () => import('../views/front/PetProfile.vue')
      },
      {
        path: 'healthRecord',
        name: 'HealthRecord',
        component: () => import('../views/front/HealthRecord.vue')
      },
      {
        path: 'userNotice',
        name: 'UserNotice',
        component: () => import('../views/front/Notice.vue')
      },
      {
        path: 'serviceReservation',
        name: 'ServiceReservation',
        component: () => import('../views/front/ServiceReservation.vue')
      },
      {
        path: 'myServiceOrder',
        name: 'MyServiceOrder',
        component: () => import('../views/front/MyServiceOrder.vue')
      },
      {
        path: 'healthAdvice',
        name: 'HealthAdvice',
        component: () => import('../views/front/HealthAdvice.vue')
      },
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

// 注意：刷新页面会导致页面路由重置
let routesAdded = false
export const resetRouter = () => {
  routesAdded = false  // 重置标志位
  router.matcher = new VueRouter({
    mode: 'history',
    routes
  })
}

export const setRoutes = () => {
  if (routesAdded) return
  const storeMenus = localStorage.getItem("menus");
  if (storeMenus) {
    // 拼装动态路由
    const manageRoute = { path: '/', name: 'Manage', component: () => import('../views/Manage.vue'), redirect: "/login", children: [
        { path: 'person', name: 'ManagePerson', component: () => import('../views/Person.vue')},
        { path: 'password', name: 'ManagePassword', component: () => import('../views/Password.vue')},
        { path: 'petProfileAdmin', name: '档案审核管理', component: () => import('../views/PetProfileAdmin.vue')},
        { path: 'serviceItemAdmin', name: '服务预约管理', component: () => import('../views/ServiceItemAdmin.vue')},
      ] }
    const menus = JSON.parse(storeMenus)
    // 已有的子路由路径，用于去重
    const existingPaths = new Set(manageRoute.children.map(c => c.path));
    menus.forEach(item => {
      if (item.path) {  // 当且仅当path不为空的时候才去设置路由
        const pathKey = item.path.replace("/", "");
        // 跳过已存在的路由，防止重复注册
        if (existingPaths.has(pathKey)) return;
        let itemMenu = { path: pathKey, name: item.name, component: () => import('../views/' + item.pagePath + '.vue')}
        manageRoute.children.push(itemMenu)
        existingPaths.add(pathKey)
      } else if(item.children && item.children.length) {
        item.children.forEach(item => {
          if (item.path) {
            const pathKey = item.path.replace("/", "");
            if (existingPaths.has(pathKey)) return;
            let itemMenu = { path: pathKey, name: item.name, component: () => import('../views/' + item.pagePath + '.vue')}
            manageRoute.children.push(itemMenu)
            existingPaths.add(pathKey)
          }
        })
      }
    })

    // 获取当前的路由对象名称数组
    const currentRouteNames = router.getRoutes().map(v => v.name)
    if (!currentRouteNames.includes('Manage')) {
      // 动态添加到现在的路由对象中去
      router.addRoute(manageRoute)
      routesAdded = true
    }
  }
}

// 每次刷新页面都要重新设置路由，否则路由就会被重置
setRoutes()

router.beforeEach((to, from, next) => {
  localStorage.setItem("currentPathName", to.name)  // 设置当前的路由名称
  store.commit("setPath")
  if (!to.matched.length) {
    const menus = localStorage.getItem("menus")
    if (!menus) {
      next("/login")
    } else {
      next("/404")
    }
  } else {
    next()
  }
})

export default router
