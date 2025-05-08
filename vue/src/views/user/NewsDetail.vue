<template>
  <div class="news-detail-container">
    <el-card class="news-detail-card" shadow="hover">
      <div class="news-header">
        <div class="news-title">{{ newsData.title }}</div>
        <div class="news-meta">
          <el-tag :type="getTypeTag(newsData.type)">{{ newsData.type }}</el-tag>
          <div class="news-author">
            <el-avatar :size="32" :src="getAvatarUrl(newsData.author)">
              <el-icon><User /></el-icon>
            </el-avatar>
            <span>{{ newsData.author }}</span>
          </div>
          <div class="news-time">{{ formatTime(newsData.createTime) }}</div>
        </div>
      </div>
      
      <div class="news-content" v-html="newsData.content"></div>
      
      <div class="news-footer">
        <el-button @click="goBack">返回列表</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User } from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const newsData = ref({})

// 获取资讯详情
const fetchNewsDetail = async () => {
  try {
    const res = await request.get(`/news/selectById/${route.params.id}`)
    if (res.code === '200') {
      newsData.value = res.data
    }
  } catch (error) {
    console.error('获取资讯详情失败:', error)
  }
}

// 获取类型标签样式
const getTypeTag = (type) => {
  const types = {
    '公告': 'warning',
    '资讯': 'success',
    '更新': 'info'
  }
  return types[type] || 'info'
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日 HH:mm')
}

// 获取头像URL
const getAvatarUrl = (author) => {
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${author}`
}

// 返回列表
const goBack = () => {
  router.push('/user/newsCenter')
}

// 初始化
onMounted(() => {
  fetchNewsDetail()
})
</script>

<style scoped>
.news-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.news-detail-card {
  border-radius: 8px;
}

.news-header {
  margin-bottom: 30px;
}

.news-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
  line-height: 1.4;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #606266;
}

.news-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.news-time {
  color: #909399;
}

.news-content {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  margin-bottom: 30px;
}

.news-content :deep(p) {
  margin-bottom: 1em;
}

.news-content :deep(img) {
  max-width: 100%;
  height: auto;
  margin: 1em 0;
  border-radius: 4px;
}

.news-footer {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

@media (max-width: 768px) {
  .news-detail-container {
    padding: 15px;
  }
  
  .news-title {
    font-size: 20px;
  }
  
  .news-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style> 