<template>
  <div class="navbar-container">
    <!-- 顶部导航栏 -->
    <el-header class="navbar-header">
      <div class="navbar-left">
        <div class="logo-container">
          <img src="@/assets/logo.png" alt="Logo" class="logo" />
          <h1 class="site-title">在线辅导平台</h1>
        </div>
      </div>

      <div class="navbar-center">
        <el-menu
            :default-active="activeIndex"
            class="navbar-menu"
            mode="horizontal"
            router
            @select="handleSelect"
            :ellipsis="false"
        >
          <el-menu-item index="/user/homepage">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/user/courseCenter">
            <el-icon><Reading /></el-icon>
            <span>课程中心</span>
          </el-menu-item>
          <el-menu-item index="/user/teacherShowcase">
            <el-icon><User /></el-icon>
            <span>老师风采</span>
          </el-menu-item>
          <el-menu-item index="/user/newsCenter">
            <el-icon><Bell /></el-icon>
            <span>资讯/公告</span>
          </el-menu-item>
          <el-menu-item index="/user/myCourses">
            <el-icon><Collection /></el-icon>
            <span>我的课程</span>
          </el-menu-item>
          <el-menu-item index="/user/mySchedule">
            <el-icon><Calendar /></el-icon>
            <span>我的排课</span>
          </el-menu-item>
          <el-menu-item index="/user/forum">
            <el-icon><Document /></el-icon>
            <span>论坛</span>
          </el-menu-item>
          <el-menu-item index="/user/messageCenter">
            <el-icon><ChatDotRound /></el-icon>
            <span>消息中心</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="navbar-right">
        <div v-if="!isLoggedIn" class="auth-buttons">
          <el-button type="primary" @click="goToLogin">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
        </div>
        <div v-else class="user-info">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="avatar-container">
              <el-avatar :size="40" :src="userAvatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="personalInfo">
                  <el-icon><User /></el-icon>
                  <span style="margin-left: 5px;">个人信息</span>
                </el-dropdown-item>
                <el-dropdown-item command="helpCenter">
                  <el-icon><DataAnalysis /></el-icon>
                  <span style="margin-left: 5px;">帮助中心</span>
                </el-dropdown-item>
                <div v-if="userInfo.role ==='TEA'">
                  <el-dropdown-item command="teacherDataCenter">
                    <el-icon><DataAnalysis /></el-icon>
                    <span style="margin-left: 5px;">数据中心</span>
                  </el-dropdown-item>
                </div>
                <el-dropdown-item command="changePassword">
                  <el-icon><Lock /></el-icon>
                  <span style="margin-left: 5px;">修改密码</span>
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  <span style="margin-left: 5px;">退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <router-view @updateUser="updateUser" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  House,
  Reading,
  User,
  Bell,
  Collection,
  Calendar,
  ChatDotRound,
  SwitchButton,
  Lock
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const activeIndex = ref('/user/homepage')
const isLoggedIn = ref(false)
const username = ref('')
const userAvatar = ref('')
const userInfo = JSON.parse(localStorage.getItem('tutor-user'))
// 检查用户是否已登录
onMounted(() => {
  const userInfo = localStorage.getItem('tutor-user')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    isLoggedIn.value = true
    username.value = user.name || user.username
    userAvatar.value = user.avatar || ''
  }

  // 设置当前激活的菜单项
  activeIndex.value = route.path
})

// 处理菜单选择
const handleSelect = (index) => {
  activeIndex.value = index
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'personalInfo') {
    router.push('/user/personalInfo')
  } else if (command === 'teacherDataCenter') {
    router.push('/user/teacherDataCenter')
  }else if (command === 'helpCenter') {
    router.push('/user/helpCenter')
  }else if (command === 'changePassword') {
    router.push('/user/changePassword')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('tutor-user')
      isLoggedIn.value = false
      ElMessage.success('已成功退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}
// 更新用户信息 为了更新头像时，不需要刷新就可以直接更新右上角头像。
const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('tutor-user'));
};
// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.navbar-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.navbar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 70px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-left {
  display: flex;
  align-items: center;
}

.logo-container {
  display: flex;
  align-items: center;
}

.logo {
  height: 40px;
  margin-right: 10px;
}

.site-title {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  margin: 0;
}

.navbar-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.navbar-menu {
  border-bottom: none;

}

.navbar-right {
  display: flex;
  align-items: center;
}

.auth-buttons {
  display: flex;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar-container {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: #333;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
}
::v-deep(.el-dropdown-menu__item) {
  padding: 10px 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
}

::v-deep(.el-dropdown-menu__item:hover) {
  background-color: #f0f7ff;
}
</style> 