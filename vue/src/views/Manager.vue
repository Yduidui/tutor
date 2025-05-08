<template>
  <div class="manager-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo">
<!--          <img src="@/assets/logo.png" alt="Logo">-->
          <span class="title">辅导平台管理系统</span>
        </div>
      </div>
      <div class="header-right">
        <div class="user-info">
          <el-dropdown trigger="click">
            <div class="user-avatar">
              <el-avatar :size="40" :src="data.user.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ data.user ? data.user.name : '未知用户' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown-menu">
                <el-dropdown-item class="dropdown-item" @click="$router.push('/manager/personalinfo')">
                  <el-icon class="dropdown-icon"><User /></el-icon>
                  <span class="dropdown-text">个人信息</span>
                </el-dropdown-item>
                <el-dropdown-item class="dropdown-item" @click="$router.push('/manager/changePassword')">
                  <el-icon class="dropdown-icon"><Lock /></el-icon>
                  <span class="dropdown-text">修改密码</span>
                </el-dropdown-item>
                <el-dropdown-item class="dropdown-item" divided @click="logout">
                  <el-icon class="dropdown-icon"><SwitchButton /></el-icon>
                  <span class="dropdown-text">退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 主体内容区 -->
    <div class="main-container">
      <!-- 左侧菜单 -->
      <div class="sidebar">
        <el-menu
          router
          :default-active="currentRoutePath"
          class="sidebar-menu"
          :collapse="isCollapse"
        >
          <el-menu-item index="/manager/home">
            <el-icon><HomeFilled /></el-icon>
            <template #title>系统首页</template>
          </el-menu-item>

          <el-menu-item index="/manager/help">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>工单管理</template>
          </el-menu-item>

          <el-menu-item index="/manager/subject">
            <el-icon><Collection /></el-icon>
            <template #title>科目管理</template>
          </el-menu-item>

          <el-menu-item index="/manager/course">
            <el-icon><Notebook /></el-icon>
            <template #title>课程管理</template>
          </el-menu-item>

          <el-menu-item index="/manager/news">
            <el-icon><Document /></el-icon>
            <template #title>新闻管理</template>
          </el-menu-item>

          <el-menu-item index="/manager/article">
            <el-icon><Document /></el-icon>
            <span>论坛管理</span>
          </el-menu-item>

          <el-menu-item index="/manager/order">
            <el-icon><Files /></el-icon>
            <template #title>订单管理</template>
          </el-menu-item>

          <el-menu-item index="/manager/appointment">
            <el-icon><Coin /></el-icon>
            <template #title>预约管理</template>
          </el-menu-item>

          <el-menu-item index="/manager/comment">
            <el-icon><ChatLineRound /></el-icon>
            <template #title>评论管理</template>
          </el-menu-item>

          <el-menu-item index="/manager/visit">
            <el-icon><MapLocation /></el-icon>
            <template #title>访问管理</template>
          </el-menu-item>


          <el-sub-menu index="user">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/manager/student">
              <el-icon><User /></el-icon>
              <span>学生信息</span>
            </el-menu-item>
            <el-menu-item index="/manager/teacher">
              <el-icon><User /></el-icon>
              <span>老师信息</span>
            </el-menu-item>
            <el-menu-item index="/manager/admin">
              <el-icon><User /></el-icon>
              <span>管理员信息</span>
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item @click="logout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容区 -->
      <div class="content">
        <div class="content-header">
          <el-button
            type="text"
            :icon="isCollapse ? 'Expand' : 'Fold'"
            @click="toggleSidebar"
          />
        </div>
        <div class="content-body">
          <router-view @updateUser="updateUser" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import {
  HomeFilled,
  DataAnalysis,
  Document,
  User,
  Lock,
  SwitchButton,
  Expand,
  Fold
} from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

const route = useRoute();
const currentRoutePath = ref(route.path);
const isCollapse = ref(false);

// 解析 localStorage 中的用户信息
let user = null;
try {
  user = JSON.parse(localStorage.getItem('tutor-user'));
} catch (error) {
  console.error('解析用户信息失败:', error);
}

const data = reactive({
  user: user || { name: '未知用户' }
});

// 切换侧边栏的展开和收起状态
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value;
};

// 退出登录
const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('tutor-user');
    location.href = '/login';   //退出到登录页面
  });
};

// 更新用户信息 为了更新头像时，不需要刷新就可以直接更新右上角头像。
const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('tutor-user'));
};

onMounted(() => {
  currentRoutePath.value = route.path;
});
</script>

<style scoped>
.custom-dropdown-menu {
  width: 180px;
  border: none;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  background-color: #fff;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  transition: background-color 0.2s ease;
  position: relative;
}

.dropdown-item:hover {
  background-color: #f0f5ff;
}

.dropdown-item::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background-color: #fff; /* 可以根据主题色调整 */
  border-radius: 0 4px 4px 0;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.dropdown-item:hover::before {
  opacity: 1;
}

.dropdown-icon {
  margin-right: 14px;
  font-size: 20px;
  color: #666;
  transition: color 0.2s ease;
}

.dropdown-item:hover .dropdown-icon {
  color: #fff;
}

.dropdown-text {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}
.manager-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  height: 60px;
  background: deepskyblue;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo img {
  height: 40px;
  width: auto;
}

.title {
  font-size: 20px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  cursor: pointer;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-avatar:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.username {
  font-size: 16px;
  color:black;
  font-weight: bold;
  font-style: italic;
}

.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
  overflow-y: auto;
}

.sidebar-menu {
  height: 100%;
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
  overflow: hidden;
}

.content-header {
  height: 50px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.content-body {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}


</style>