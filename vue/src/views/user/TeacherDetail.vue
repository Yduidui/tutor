<template>
  <div class="teacher-detail-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :span="8">
        <el-card class="info-card" shadow="hover">
          <div class="avatar-container">
            <el-avatar :size="120" :src="teacherData.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <h2 class="teacher-name">{{ teacherData.name }}</h2>
            <p class="teacher-subject">{{ teacherData.subject }}</p>
            <div class="teacher-rating">
              <el-rate v-model="teacherData.rating" disabled show-score text-color="#ff9900" />
            </div>
          </div>
          <el-divider />
          <div class="info-list">
            <div class="info-item">
              <el-icon><School /></el-icon>
              <span>所在学校：{{ teacherData.school }}</span>
            </div>
            <div class="info-item">
              <el-icon><Collection /></el-icon>
              <span>教学课程：{{ teacherData.totalClasses }}门</span>
            </div>
            <div class="info-item">
              <el-icon><Clock /></el-icon>
              <span>授课时长：{{ teacherData.totalHours }}小时</span>
            </div>
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>联系电话：{{ teacherData.phone }}</span>
            </div>
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <span>电子邮箱：{{ teacherData.email }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧详细信息区域 -->
      <el-col :span="16">
        <el-card class="detail-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>个人简介</span>
              <el-button @click="goBack" class="back-button">
                <el-icon><ArrowLeft /></el-icon>
                返回
              </el-button>
            </div>
          </template>

          <div class="teacher-description">
            {{ teacherData.descr }}
          </div>

          <div class="qualification-section">
            <h3>资质证明</h3>
            <div class="qualification-photos" v-if="teacherData.qualificationPhotos">
              <el-image
                v-for="(photo, index) in teacherData.qualificationPhotos.split(',')"
                :key="index"
                :src="photo"
                :preview-src-list="teacherData.qualificationPhotos.split(',')"
                fit="contain"
                class="qualification-photo"
              />
            </div>
            <div v-else class="no-qualification">暂无资质证明</div>
          </div>

          <div class="action-section">
            <el-button 
              type="primary" 
              @click="handleViewSchedule"
              :disabled="!data.isLoggedIn"
            >
              {{ data.isLoggedIn ? '查看教师预约日历' : '登录后查看预约' }}
            </el-button>
          </div>
        </el-card>

        <!-- 评论区 -->
        <el-card class="comment-section" shadow="hover">
          <template #header>
            <div class="comment-header">
              <span>学生评价</span>
              <div class="rating-summary">
                <span class="average-rating">平均评分：{{ averageRating.toFixed(1) }}</span>
                <el-rate v-model="averageRating" disabled show-score text-color="#ff9900" />
              </div>
            </div>
          </template>

          <!-- 评论列表 -->
          <div class="comment-list" v-loading="loading">
            <div v-if="comments.length === 0" class="no-comment">
              暂无评价
            </div>
            <div v-else v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-user">
                <el-avatar :size="40" :src="comment.studentAvatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <div class="comment-info">
                  <span class="student-name">{{ comment.studentName }}</span>
                  <el-rate v-model="comment.rating" disabled show-score text-color="#ff9900" />
                </div>
                <span class="comment-time">{{ formatTime(comment.createdTime) }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
            </div>
          </div>

          <!-- 评论提交表单 -->
          <div class="comment-form" v-if="data.isLoggedIn && data.userRole === 'STU'">
            <el-divider>发表评价</el-divider>
            <div v-if="teacherData && teacherData.id">
              <CommentSubmit :teacher-id="teacherData.id" @comment-submitted="handleCommentSubmitted" />

            </div>
          </div>
          <div v-else-if="!data.isLoggedIn" class="login-tip">
            请<el-button type="text" @click="goToLogin">登录</el-button>后发表评价
          </div>
          <div v-else class="role-tip">
            只有学生可以发表评价
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, User, School, Collection, Clock, Phone, Message } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import CommentSubmit from './CommentSubmit.vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const teacherData = ref({})

// 使用reactive管理状态
const data = reactive({
  isLoggedIn: false,
  userRole: null
})

const comments = ref([])
const loading = ref(false)
const averageRating = ref(0)

// 检查登录状态
const checkLoginStatus = () => {
  const userInfo = JSON.parse(localStorage.getItem('tutor-user') || 'null')
  data.isLoggedIn = userInfo !== null
  if (data.isLoggedIn) {
    data.userRole = userInfo.role
  }
}

// 处理查看预约日历
const handleViewSchedule = () => {
  if (!data.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (data.userRole !== 'STU') {
    ElMessage.warning('只有学生可以预约课程')
    return
  }

  router.push({
    path: '/user/mySchedule',
    query: {
      teacherId: teacherData.value.id,
      teacherName: teacherData.value.name
    }
  })
}
console.log('父组件传递的 teacherId:', teacherData.value.id);
// 获取教师详情
const fetchTeacherDetail = async () => {
  try {
    const res = await request.get(`/teacher/selectById/${route.params.id}`)
    if (res.code === '200') {
      teacherData.value = res.data
    }
  } catch (error) {
    console.error('获取教师详情失败:', error)
  }
}

// 获取评论列表
const fetchComments = async () => {
  loading.value = true
  try {
    const res = await request.get(`/comment/teacher/${route.params.id}`)
    if (res.code === '200') {
      comments.value = res.data
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取平均评分
const fetchAverageRating = async () => {
  try {
    const res = await request.get(`/comment/average/${route.params.id}`)
    if (res.code === '200') {
      averageRating.value = res.data || 0
    }
  } catch (error) {
    console.error('获取平均评分失败:', error)
  }
}

// 处理评论提交
const handleCommentSubmitted = () => {
  fetchComments()
  fetchAverageRating()
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 返回列表
const goBack = () => {
  router.push('/user/teacherShowcase')
}

// 初始化
onMounted(() => {
  checkLoginStatus()
  fetchTeacherDetail()
  fetchComments()
  fetchAverageRating()
})
</script>

<style scoped>
.teacher-detail-container {
  padding: 20px;
}

.info-card {
  height: 100%;
}

.avatar-container {
  text-align: center;
  padding: 20px 0;
}

.teacher-name {
  margin: 15px 0 5px;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.teacher-subject {
  margin: 0 0 10px;
  font-size: 16px;
  color: var(--el-text-color-secondary);
}

.teacher-rating {
  margin-top: 10px;
}

.info-list {
  padding: 0 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  color: var(--el-text-color-regular);
}

.info-item .el-icon {
  font-size: 18px;
  color: var(--el-text-color-secondary);
}

.detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

.teacher-description {
  color: var(--el-text-color-regular);
  line-height: 1.8;
  text-align: justify;
  margin-bottom: 30px;
}

.qualification-section {
  margin-top: 30px;
}

.qualification-section h3 {
  margin-bottom: 15px;
  color: var(--el-text-color-primary);
}

.qualification-photos {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  padding: 16px;
}

.qualification-photo {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.qualification-photo:hover {
  transform: scale(1.02);
}

.no-qualification {
  color: var(--el-text-color-secondary);
  text-align: center;
  padding: 20px;
}

.action-section {
  margin-top: 20px;
  text-align: center;
  padding: 20px;
  background-color: var(--el-fill-color-light);
  border-radius: 8px;
}

.action-section .el-button {
  width: 100%;
  max-width: 300px;
}

.comment-section {
  margin-top: 20px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rating-summary {
  display: flex;
  align-items: center;
  gap: 10px;
}

.average-rating {
  font-size: 16px;
  color: var(--el-text-color-regular);
}

.comment-list {
  min-height: 200px;
}

.no-comment {
  text-align: center;
  color: var(--el-text-color-secondary);
  padding: 40px 0;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.comment-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.student-name {
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.comment-time {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.comment-content {
  color: var(--el-text-color-regular);
  line-height: 1.6;
  margin-left: 52px;
}

.comment-form {
  margin-top: 20px;
}

.login-tip, .role-tip {
  text-align: center;
  color: var(--el-text-color-secondary);
  padding: 20px 0;
}

:deep(.el-rate) {
  margin-top: 4px;
}

@media (max-width: 768px) {
  .info-card {
    margin-bottom: 20px;
  }
  
  .qualification-photos {
    grid-template-columns: 1fr;
  }
}
</style> 