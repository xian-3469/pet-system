<template>
  <div style="background-color: var(--pet-bg);">
    <!-- 头部导航栏 -->
    <div class="header-container">
      <!-- Logo区域 -->
      <div class="logo-area">
        <img src="../../assets/front-logo.jpg" alt="" class="logo-image">
        <span class="logo-text">智宠平台</span>
      </div>

      <!-- 导航菜单 -->
      <div class="nav-area">
        <el-menu
          :default-active="activeMenu"
          class="pet-menu"
          mode="horizontal"
          router
        >
          <el-menu-item index="/front/home" class="pet-menu-item">首页</el-menu-item>
          <el-menu-item index="/front/petPublic" class="pet-menu-item">宠物广场</el-menu-item>
          <el-menu-item index="/front/petProfile" class="pet-menu-item">我的档案</el-menu-item>
          <el-menu-item index="/front/healthRecord" class="pet-menu-item">健康记录</el-menu-item>
          <el-menu-item index="/front/healthAdvice" class="pet-menu-item">智能建议</el-menu-item>
          <el-menu-item index="/front/adopt" class="pet-menu-item">宠物领养</el-menu-item>
          <el-menu-item index="/front/salvation" class="pet-menu-item">宠物救助</el-menu-item>
          <el-menu-item index="/front/serviceReservation" class="pet-menu-item">服务预约</el-menu-item>
          <el-menu-item index="/front/myServiceOrder" class="pet-menu-item">我的订单</el-menu-item>
          <el-menu-item index="/front/article" class="pet-menu-item">宠物论坛</el-menu-item>
          <el-menu-item index="/front/userNotice" class="pet-menu-item">
            站内信
            <el-badge v-if="unreadNoticeCount > 0" :value="unreadNoticeCount" :max="99" class="notice-badge"></el-badge>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 用户区域 -->
      <div class="user-area">
        <div v-if="!user.username" class="auth-buttons">
          <el-button size="small" @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" size="small" @click="$router.push('/register')">注册</el-button>
        </div>
        <div v-else class="user-dropdown">
          <el-dropdown trigger="click">
            <div class="user-info-wrapper">
              <img :src="$store.state.baseApi + user.avatarUrl" alt="" class="user-avatar">
              <span class="user-name">{{ user.nickname }}</span>
              <i class="el-icon-arrow-down"></i>
            </div>
            <el-dropdown-menu slot="dropdown" class="user-dropdown-menu">
              <el-dropdown-item v-if="user.role === 'ROLE_ADMIN'">
                <router-link to="/home">后台管理</router-link>
              </el-dropdown-item>
              <el-dropdown-item>
                <router-link to="/front/password">修改密码</router-link>
              </el-dropdown-item>
              <el-dropdown-item>
                <router-link to="/front/person">个人信息</router-link>
              </el-dropdown-item>
              <el-dropdown-item divided @click.native="logout">
                <span class="logout-text">退出</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 页面内容 -->
    <div class="content-wrapper">
      <router-view />
    </div>

    <!-- AI 智能助手（全局悬浮） -->
    <AiAssistant />
  </div>
</template>

<script>
import AiAssistant from "@/components/front/AiAssistant.vue"

export default {
  name: "Front",
  components: { AiAssistant },
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      unreadNoticeCount: 0
    }
  },
  computed: {
    activeMenu() {
      const path = this.$route.path
      const menuPaths = [
        '/front/home',
        '/front/petPublic',
        '/front/petProfile',
        '/front/healthRecord',
        '/front/healthAdvice',
        '/front/adopt',
        '/front/salvation',
        '/front/myServiceOrder',
        '/front/serviceReservation',
        '/front/article',
        '/front/userNotice'
      ]
      if (menuPaths.includes(path)) {
        return path
      }
      for (const menuPath of menuPaths) {
        if (path.startsWith(menuPath + '/') || path === menuPath) {
          return menuPath
        }
      }
      return '/front/home'
    }
  },
  created() {
    if (this.user && this.user.id) {
      this.loadUnreadNoticeCount()
    }
  },
  methods: {
    logout() {
      this.$store.commit("logout")
      this.$message.success("退出成功")
    },
    loadUnreadNoticeCount() {
      this.request.get("/health-record/notice/unread-count").then(res => {
        if (res.code === '200') {
          this.unreadNoticeCount = res.data || 0
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
/* 头部容器 - 水平排列 */
.header-container {
  display: flex;
  align-items: center;
  height: 70px;
  padding: 0 20px;
  background: linear-gradient(135deg, #FFFFFF 0%, #FFF9F5 100%);
  box-shadow: 0 2px 12px rgba(255, 154, 86, 0.1);
}

/* Logo区域 */
.logo-area {
  display: flex;
  align-items: center;
  width: 200px;
  flex-shrink: 0;
}

.logo-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid var(--pet-border);
  object-fit: cover;
}

.logo-text {
  margin-left: 10px;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 1px;
  background: linear-gradient(135deg, var(--pet-primary) 0%, var(--pet-primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
}

/* 导航区域 - 均匀分布 */
.nav-area {
  flex: 1;
  display: flex;
  justify-content: space-between;
}

.nav-area .el-menu {
  width: 100%;
  background: transparent !important;
  border: none !important;
}

/* 菜单项统一样式 */
.pet-menu-item {
  padding: 0 14px !important;
  font-size: 15px !important;
  height: 70px !important;
  line-height: 70px !important;
  color: var(--pet-text) !important;
}

/* 用户区域 */
.user-area {
  width: 150px;
  flex-shrink: 0;
  text-align: right;
}

.auth-buttons {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.auth-buttons .el-button {
  border-radius: 20px;
  padding: 8px 16px;
}

/* 用户下拉框 */
.user-dropdown {
  display: flex;
  justify-content: flex-end;
}

.user-info-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-info-wrapper:hover {
  background-color: rgba(255, 154, 86, 0.1);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 2px solid var(--pet-border);
  object-fit: cover;
  margin-right: 8px;
}

.user-name {
  max-width: 60px;
  font-size: 14px;
  color: var(--pet-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-badge {
  margin-left: 6px;
}

/* 内容区域 */
.content-wrapper {
  width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>

<style>
/* 全局导航栏选中样式 */
.el-menu--horizontal > .el-menu-item.is-active {
  background-color: transparent !important;
  color: var(--pet-primary) !important;
  font-weight: 600 !important;
  border-bottom: 3px solid var(--pet-primary) !important;
}

.el-menu--horizontal > .el-submenu.is-active > .el-submenu__title {
  background-color: transparent !important;
  color: var(--pet-primary) !important;
  border-bottom: 3px solid var(--pet-primary) !important;
}

/* 菜单项悬停样式 */
.el-menu--horizontal > .el-menu-item:hover,
.el-menu--horizontal > .el-submenu .el-submenu__title:hover {
  background-color: rgba(255, 154, 86, 0.08) !important;
  color: var(--pet-primary) !important;
}

/* 移除按钮点击时的蓝色边框 */
.el-menu-item:focus,
.el-menu-item:hover,
.el-submenu__title:hover {
  outline: none !important;
  box-shadow: none !important;
}

/* 下拉菜单样式 */
.user-dropdown-menu {
  border-radius: 12px !important;
  padding: 8px 0 !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.user-dropdown-menu a {
  display: block;
  color: var(--pet-text);
  text-decoration: none;
  font-size: 14px;
}

.user-dropdown-menu a:hover {
  color: var(--pet-primary);
}

.logout-text {
  color: var(--pet-accent);
}

/* 下拉菜单悬停 */
.el-dropdown-menu__item:hover {
  background-color: rgba(255, 154, 86, 0.1) !important;
}

/* 响应式适配 */
@media screen and (max-width: 1200px) {
  .logo-area {
    width: auto;
  }
  
  .logo-text {
    font-size: 16px;
  }
  
  .pet-menu-item {
    padding: 0 10px !important;
    font-size: 14px !important;
  }
  
  .user-area {
    width: auto;
  }
  
  .user-name {
    display: none;
  }
}

@media screen and (max-width: 992px) {
  .logo-text {
    display: none;
  }
  
  .pet-menu-item {
    padding: 0 8px !important;
    font-size: 13px !important;
  }
}

@media screen and (max-width: 768px) {
  .pet-menu-item span {
    display: none;
  }
}
</style>
