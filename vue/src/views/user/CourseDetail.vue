<template>
  <div class="course-detail-container">
    <el-row :gutter="20">
      <!-- 左侧课程信息 -->
      <el-col :span="16">
        <el-card class="course-info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h2 class="course-title">{{ data.course.title }}</h2>
              <el-button @click="goBack" class="back-button">
                <el-icon><ArrowLeft /></el-icon>
                返回
              </el-button>
            </div>
          </template>

          <div class="course-cover">
            <el-image :src="data.course.cover" fit="cover" class="cover-image">
              <template #error>
                <div class="image-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>

          <div class="course-meta">
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>授课教师：{{ data.course.teacherName }}</span>
              <el-button type="primary" link @click="router.push(`/user/teacher/${data.course.teacherId}`)">
                查看教师详情
              </el-button>
            </div>
            <div class="meta-item">
              <el-icon><Collection /></el-icon>
              <span>所属学科：{{ data.course.subjectName }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>课程持续：{{ data.course.duration }}小时</span>
            </div>
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>开课时间：{{ formatTime(data.course.startTime) }} - {{ formatTime(data.course.endTime) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>报名人数：{{ data.course.studentNames ? data.course.studentNames.split(',').length : 0 }}人</span>
            </div>
            <div class="meta-item">
              <el-icon><PriceTag /></el-icon>
              <span>课程价格：{{ data.course.price > 0 ? `¥${data.course.price}` : '免费' }}</span>
            </div>
            <div class="meta-item">
              <el-icon><InfoFilled /></el-icon>
              <span>课程状态：{{ data.course.status }}</span>
            </div>
          </div>

          <el-divider />

          <div class="course-description">
            <h3>课程简介</h3>
            <p>{{ data.course.description }}</p>
          </div>

          <div class="course-students" v-if="data.course.studentNames">
            <h3>已报名学生</h3>
            <div class="student-list">
              <el-tag v-for="student in data.course.studentNames.split(',')" :key="student" class="student-tag">
                {{ student }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧操作区域 -->
      <el-col :span="8">
        <el-card class="action-card" shadow="hover">
          <div class="action-content">
            <div class="price-section">
              <span class="price-label">课程价格</span>
              <span class="price-value">{{ data.course.price > 0 ? `¥${data.course.price}` : '免费' }}</span>
            </div>

            <div class="status-section">
              <el-tag :type="getStatusType(data.course.status)">{{ data.course.status }}</el-tag>
            </div>

            <div class="action-buttons">
              <template v-if="data.isStudent">
<!--                <template v-if="!data.isEnrolled">-->
                  <el-button
                      type="primary"
                      size="large"
                      @click="handleEnroll"
                  >
                    点击预约
                  </el-button>
<!--                </template>-->
<!--                <template v-else>-->
<!--                  <el-button-->
<!--                      v-if="data.orderStatus === '未支付'"-->
<!--                      type="warning"-->
<!--                      size="large"-->
<!--                      @click="handlePay"-->
<!--                  >-->
<!--                    去支付-->
<!--                  </el-button>-->
<!--                  <el-button-->
<!--                      v-else-->
<!--                      type="success"-->
<!--                      size="large"-->
<!--                      :disabled="data.course.status !== '进行中'"-->
<!--                      @click="handleStartLearning"-->
<!--                  >-->
<!--                    开始学习-->
<!--                  </el-button>-->
<!--                </template>-->
              </template>
              <template v-else-if="data.isTeacher">
                <el-button
                    type="primary"
                    size="large"
                    @click="router.push('/user/courseCenter')"
                >
                  返回课程中心
                </el-button>
              </template>
              <template v-else>
                <el-button
                    type="primary"
                    size="large"
                    @click="router.push('/login')"
                >
                  请先登录
                </el-button>
              </template>
            </div>

            <div class="contact-info">
              <h4>联系方式</h4>
              <p>如有疑问，请联系授课教师：{{ data.course.teacherName }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft,
  User,
  Collection,
  Clock,
  Calendar,
  Picture,
  PriceTag,
  InfoFilled
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 使用reactive管理状态
const data = reactive({
  course: {},
  isLoggedIn: false,
  isTeacher: false,
  isStudent: false,
  isEnrolled: false,
  orderStatus: '未支付' // 新增订单状态
})

// 检查登录状态和角色
const checkLoginStatus = () => {
  const userInfo = JSON.parse(localStorage.getItem('tutor-user') || 'null')
  data.isLoggedIn = userInfo !== null
  if (data.isLoggedIn) {
    data.isTeacher = userInfo.role === 'TEA'
    data.isStudent = userInfo.role === 'STU'
    // 检查是否已报名
    if (data.course.studentIds && userInfo) {
      data.isEnrolled = data.course.studentIds.split(',').includes(userInfo.id.toString())
      // 如果已报名，检查订单状态
      if (data.isEnrolled) {
        checkOrderStatus(userInfo.id)
      }
    }
  }
}

// 检查订单状态
const checkOrderStatus = async (studentId) => {
  try {
    const res = await request.get('/order/selectAll', {
      params: {
        studentId: studentId,
        courseId: data.course.id
      }
    })
    if (res.code === '200' && res.data && res.data.length > 0) {
      // 假设只取第一条订单的状态
      data.orderStatus = res.data[0].status
    } else {
      data.orderStatus = null // 没有订单
    }
  } catch (error) {
    console.error('获取订单状态失败:', error)
  }
}


// 获取课程详情
const fetchCourseDetail = async () => {
  try {
    const res = await request.get(`/course/selectById/${route.params.id}`)
    if (res.code === '200') {
      data.course = res.data
      checkLoginStatus()
    }
  } catch (error) {
    console.error('获取课程详情失败:', error)
  }
}

// 返回列表
const goBack = () => {
  router.push('/user/courseCenter')
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
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

const handleEnroll = async () => {
  if (!data.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!data.isStudent) {
    ElMessage.warning('只有学生可以预约课程')
    return
  }

  router.push({
    path: '/user/mySchedule',
    query: {
      teacherId: data.course.teacherId,
      teacherName: data.course.teacherName
    }
  })
}
// // 处理报名
// const handleEnroll = async () => {
//   if (!data.isLoggedIn) {
//     ElMessage.warning('请先登录')
//     router.push('/login')
//     return
//   }
//
//   if (!data.isStudent) {
//     ElMessage.warning('只有学生可以预约课程')
//     return
//   }
//
//   const userInfo = JSON.parse(localStorage.getItem('tutor-user'))
//   console.log('userInfo:', userInfo);
//   console.log('course:', data.course);
//   try {
//     // 创建订单
//     const orderData = {
//       studentId: userInfo.id,
//       courseId: data.course.id,
//       teacherId: data.course.teacherId,
//       totalPrice: data.course.price,
//       status: '未支付',
//       paymentMethod: '暂无'
//     }
//
//     const res = await request.post('/order/add', orderData)
//     if (res.code === '200') {
//       ElMessage.success('报名成功，请完成支付')
//       // 更新课程信息
//       fetchCourseDetail()
//     } else {
//       ElMessage.error(res.msg || '报名失败')
//     }
//   } catch (error) {
//     console.error('报名失败:', error)
//     ElMessage.error('报名失败，请稍后重试')
//   }
// }

const handlePay = async () => {
  if (!data.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const res = await request.get('/order/selectByTeacherId/' + data.course.teacherId)
    const order = res.data

    if (order && order.orderNo) {
      window.open('http://localhost:8080/alipay/pay?orderNo=' + order.orderNo)
    } else {
      ElMessage.error('未找到订单号')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('获取订单失败')
  }
}


// // 处理开始学习
// const handleStartLearning = () => {
//   if (!data.isLoggedIn) {
//     ElMessage.warning('请先登录')
//     router.push('/login')
//     return
//   }
//   if (!data.isStudent) {
//     ElMessage.warning('只有学生可以学习课程')
//     return
//   }
//
//   // TODO: 实现开始学习逻辑
//   console.log('开始学习课程:', data.course.id)
// }

// 初始化
onMounted(() => {
  fetchCourseDetail()
})
</script>

<style scoped>
.course-detail-container {
  padding: 20px;
}

.course-info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-title {
  margin: 0;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.back-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

.course-cover {
  margin: 20px 0;
}

.cover-image {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
}

.course-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  margin: 20px 0;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-regular);
}

.meta-item .el-icon {
  font-size: 16px;
  color: var(--el-text-color-secondary);
}

.course-description {
  margin: 20px 0;
}

.course-description h3 {
  margin-bottom: 15px;
  color: var(--el-text-color-primary);
}

.course-description p {
  color: var(--el-text-color-regular);
  line-height: 1.8;
  text-align: justify;
}

.course-students {
  margin-top: 20px;
}

.course-students h3 {
  margin-bottom: 15px;
  color: var(--el-text-color-primary);
}

.student-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.student-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.action-card {
  position: sticky;
  top: 20px;
}

.action-content {
  padding: 20px;
}

.price-section {
  text-align: center;
  margin-bottom: 20px;
}

.price-label {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 5px;
}

.price-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--el-color-primary);
}

.status-section {
  text-align: center;
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.contact-info {
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color-light);
}

.contact-info h4 {
  margin: 0 0 10px;
  color: var(--el-text-color-primary);
}

.contact-info p {
  margin: 0;
  color: var(--el-text-color-regular);
  font-size: 14px;
}

@media (max-width: 768px) {
  .course-meta {
    grid-template-columns: 1fr;
  }
}
</style> 