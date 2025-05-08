<template>
  <div class="news-center-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
          v-model="searchQuery"
          placeholder="搜索资讯标题"
          clearable
          prefix-icon="Search"
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <el-select
          v-model="selectedType"
          placeholder="全部类型"
          clearable
          class="type-select"
        >
          <el-option label="全部" value="" />
          <el-option label="公告" value="公告" />
          <el-option label="资讯" value="资讯" />
          <el-option label="更新" value="更新" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </el-card>

    <!-- 资讯列表 -->
    <div class="news-list">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in newsList" :key="item.id">
          <el-card class="news-card" shadow="hover" @click="viewDetail(item.id)">
            <div class="news-type">
              <el-tag :type="getTypeTag(item.type)">{{ item.type }}</el-tag>
            </div>
            <div class="news-title">{{ item.title }}</div>
            <div class="news-description">{{ truncateDescription(item.descr) }}</div>
            <div class="news-meta">
              <div class="news-author">
                <el-avatar :size="24" :src="getAvatarUrl(item.author)">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span>{{ item.author }}</span>
              </div>
              <div class="news-time">{{ formatTime(item.createTime) }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[8, 16, 24, 32]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, User } from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

const router = useRouter()
const searchQuery = ref('')
const selectedType = ref('')
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)
const newsList = ref([])

// 获取资讯列表
const fetchNewsList = async () => {
  try {
    const res = await request.get('/news/selectPage', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        title: searchQuery.value,
        type: selectedType.value
      }
    })
    if (res.code === '200') {
      newsList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取资讯列表失败:', error)
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchNewsList()
}

// 重置搜索
const resetSearch = () => {
  searchQuery.value = ''
  selectedType.value = ''
  handleSearch()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchNewsList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchNewsList()
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/user/news/${id}`)
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
  return dayjs(time).format('YYYY年MM月DD日')
}

// 截取描述文本
const truncateDescription = (text) => {
  if (!text) return ''
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

// 获取头像URL
const getAvatarUrl = (author) => {
  // 这里可以根据实际情况返回头像URL
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${author}`
}

// 初始化
onMounted(() => {
  fetchNewsList()
})
</script>

<style scoped>
.news-center-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.type-select {
  width: 120px;
}

.news-list {
  margin: 20px 0;
}

.news-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  position: relative;
}

.news-card:hover {
  transform: translateY(-5px);
}

.news-type {
  position: absolute;
  top: 10px;
  right: 10px;
}

.news-title {
  font-size: 16px;
  font-weight: bold;
  margin: 10px 0;
  color: #303133;
}

.news-description {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.5;
  height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.news-author {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.news-time {
  color: #909399;
  font-size: 12px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .search-wrapper {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input,
  .type-select {
    width: 100%;
  }
  
  .news-card {
    margin-bottom: 15px;
  }
}
</style>
