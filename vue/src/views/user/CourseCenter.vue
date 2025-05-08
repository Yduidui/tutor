<template>
  <div class="course-center-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索课程名称"
          clearable
          prefix-icon="Search"
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <el-select v-model="searchForm.subjectId" placeholder="选择科目" class="type-select" clearable>
          <el-option
            v-for="item in subjectList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="课程状态" class="type-select" clearable>
          <el-option label="未开始" value="未开始" />
          <el-option label="进行中" value="进行中" />
          <el-option label="已结束" value="已结束" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </el-card>

    <!-- 课程列表 -->
    <div class="course-list">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in courseList" :key="course.id">
          <el-card class="course-card" shadow="hover" @click="viewDetail(course.id)">
            <div class="course-cover-wrapper">
              <el-image :src="course.cover" fit="cover" class="course-cover">
                <template #error>
                  <div class="image-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="course-status">
                <el-tag :type="getStatusType(course.status)">{{ course.status }}</el-tag>
              </div>
            </div>
            <div class="course-info">
              <h3 class="course-title">{{ course.title }}</h3>
              <div class="course-meta">
                <div class="course-teacher">
                  <el-icon><User /></el-icon>
                  {{ course.teacherName }}
                </div>
                <div class="course-subject">
                  <el-icon><Collection /></el-icon>
                  {{ course.subjectName }}
                </div>
              </div>
              <div class="course-price">
                {{ course.price > 0 ? `¥${course.price}` : '免费' }}
              </div>
              <div class="course-desc">{{ truncateDescription(course.description) }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[8, 12, 16, 20]"
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
import { Search, User, Collection, Picture } from '@element-plus/icons-vue'
import request from '@/utils/request'
import {ElMessage} from "element-plus";

const router = useRouter()
const searchForm = ref({
  keyword: '',
  subjectId: '',
  status: ''
})
const subjectList = ref([])
const courseList = ref([])
const pageNum = ref(1)
const pageSize = ref(8)
const total = ref(0)

// 获取科目列表
const fetchSubjects = async () => {
  try {
    const res = await request.get('/subject/selectAll')
    if (res.code === '200') {
      subjectList.value = res.data
    }
  } catch (error) {
    console.error('获取科目列表失败:', error)
  }
}

// 获取课程列表
const fetchCourses = async () => {
  try {
    const res = await request.get('/course/selectPage', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        title: searchForm.value.keyword,
        subjectId: searchForm.value.subjectId,
        status: searchForm.value.status
      }
    })
    if (res.code === '200') {
      courseList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  fetchCourses()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    keyword: '',
    subjectId: '',
    status: ''
  }
  handleSearch()
}

// 查看详情
const viewDetail = (id) => {
  // 1. 点赞数 +1
  try {
    request.put(`/course/addClickCount/${id}`)
  } catch (e) {
    ElMessage.error('点击数更新失败')
  }
  router.push(`/user/course/${id}`)
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchCourses()
}

// 页码改变
const handleCurrentChange = (val) => {
  pageNum.value = val
  fetchCourses()
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case '未开始':
      return 'info'
    case '进行中':
      return 'success'
    case '已结束':
      return 'danger'
    default:
      return 'info'
  }
}

// 截取描述文本
const truncateDescription = (text) => {
  if (!text) return ''
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

// 初始化
onMounted(() => {
  fetchSubjects()
  fetchCourses()
})
</script>

<style scoped>
.course-center-container {
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

.course-list {
  margin: 20px 0;
}

.course-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  position: relative;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-cover-wrapper {
  position: relative;
  margin-bottom: 15px;
}

.course-cover {
  width: 100%;
  height: 160px;
  border-radius: 4px;
}

.image-placeholder {
  width: 100%;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
}

.course-status {
  position: absolute;
  top: 10px;
  right: 10px;
}

.course-info {
  padding: 0 15px;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 10px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
}

.course-teacher,
.course-subject {
  display: flex;
  align-items: center;
  gap: 5px;
}

.course-price {
  color: var(--el-color-danger);
  font-weight: bold;
  margin-bottom: 10px;
}

.course-desc {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
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
  
  .course-card {
    margin-bottom: 15px;
  }
}
</style> 