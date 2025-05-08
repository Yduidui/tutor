<template>
  <div class="not-found">
    <div class="content">
      <img src="@/assets/logo.svg" alt="404" class="error-image" />
      <h1>404</h1>
      <h2>抱歉，您访问的页面不存在</h2>
      <p>请检查您输入的网址是否正确，或者点击下面的按钮返回首页</p>
      <el-button type="primary" size="large" @click="goHome">
        <el-icon><House /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { House } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";

const router = useRouter();

const goHome = () => {
  router.push("/");
};
</script>

<style scoped>
.not-found {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
}

.content {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
}

.error-image {
  width: 300px;
  margin-bottom: 20px;
}

h1 {
  font-size: 72px;
  font-weight: bold;
  color: var(--el-color-primary);
  margin: 0 0 10px;
}

h2 {
  font-size: 24px;
  color: var(--el-text-color-primary);
  margin: 0 0 10px;
}

p {
  font-size: 16px;
  color: var(--el-text-color-secondary);
  margin: 0 0 30px;
}

:deep(.el-button) {
  display: flex;
  align-items: center;
  gap: 5px;
  margin: 0 auto;
}
</style>