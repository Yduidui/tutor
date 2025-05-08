<template>
  <div class="homepage-container">
    <!-- 轮播图区域 -->
    <el-carousel :interval="4000" type="card" height="300px" class="banner">
      <el-carousel-item v-for="(item, index) in banners" :key="index">
        <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }">
          <div class="banner-content">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
            <el-button type="primary" @click="handleBannerClick(item)">了解更多</el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 平台特色 -->
    <div class="features-section">
      <h2 class="section-title">平台特色</h2>
      <el-row :gutter="20">
        <el-col :span="6" v-for="(feature, index) in features" :key="index">
          <el-card class="feature-card" shadow="hover">
            <div class="feature-icon">
              <el-icon><component :is="feature.icon" /></el-icon>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 热门课程 -->
    <div class="courses-section">
      <div class="section-header">
        <h2 class="section-title">热门课程</h2>
        <el-button type="primary" link @click="goToCourseCenter">查看更多</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="(course, index) in hotCourses" :key="index">
          <el-card class="course-card" shadow="hover" @click="viewCourseDetail(course.id)">
            <img :src="course.cover" class="course-image" />
            <div class="course-info">
              <h3>{{ course.name }}</h3>
              <p class="course-teacher">{{ course.teacher }}</p>
              <div class="course-meta">
                <span class="course-price">¥{{ course.price }}</span>
                <span class="course-students">{{ course.studentCount }}人学习</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 优秀教师 -->
    <div class="teachers-section">
      <div class="section-header">
        <h2 class="section-title">优秀教师</h2>
        <el-button type="primary" link @click="goToTeacherShowcase">查看更多</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="4" v-for="(teacher, index) in featuredTeachers" :key="index">
          <el-card class="teacher-card" shadow="hover" @click="viewTeacherDetail(teacher.id)">
            <el-avatar :size="80" :src="teacher.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <h3>{{ teacher.name }}</h3>
            <p class="teacher-subject">{{ teacher.subject }}</p>
            <div class="teacher-rating">
              <el-rate v-model="teacher.rating" disabled show-score text-color="#ff9900" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 学习路径 -->
    <div class="learning-path-section">
      <h2 class="section-title">学习路径</h2>
      <el-steps :active="1" finish-status="success" align-center>
        <el-step title="选择课程" description="浏览并选择适合您的课程" />
        <el-step title="报名学习" description="完成报名并支付课程费用" />
        <el-step title="开始学习" description="按照课程安排开始学习" />
        <el-step title="完成课程" description="完成课程学习并获得证书" />
      </el-steps>
    </div>

    <!-- 学员反馈 -->
    <div class="testimonials-section">
      <h2 class="section-title">学员反馈</h2>
      <el-carousel :interval="5000" height="200px">
        <el-carousel-item v-for="(testimonial, index) in testimonials" :key="index">
          <div class="testimonial-card">
            <div class="testimonial-content">
              <p class="testimonial-text">"{{ testimonial.content }}"</p>
              <div class="testimonial-author">
                <el-avatar :size="40" :src="testimonial.avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <div class="author-info">
                  <h4>{{ testimonial.name }}</h4>
                  <p>{{ testimonial.course }}</p>
                </div>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from "@/utils/request.js"
import {
  User,
  Reading,
  VideoCamera,
  ChatDotRound,
  Star,
  Trophy,
  Medal,
  Connection
} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";

const router = useRouter()

// 轮播图数据
const banners = ref([
  {
    id: 1,
    title: '优质在线辅导课程',
    description: '名师授课，一对一辅导，助您学习更高效',
    image: '',
    link: '/user/courseCenter'
  },
  {
    id: 2,
    title: '专业教师团队',
    description: '经验丰富的教师团队，为您提供专业指导',
    image: '',
    link: '/user/teacherShowcase'
  },
  {
    id: 3,
    title: '灵活的学习时间',
    description: '随时随地，按照自己的节奏学习',
    image: '',
    link: '/user/myCourses'
  },
  {
    id: 4,
    title: '丰富的课程资源',
    description: '覆盖多个学科，满足不同学习需求',
    image: '',
    link: '/user/courseCenter'
  }
])
// 平台特色
const features = ref([
  {
    icon: 'Reading',
    title: '优质课程',
    description: '精心设计的课程内容，确保学习效果'
  },
  {
    icon: 'User',
    title: '专业教师',
    description: '经验丰富的教师团队，提供专业指导'
  },
  {
    icon: 'VideoCamera',
    title: '在线直播',
    description: '实时互动，解答疑惑，提高学习效率'
  },
  {
    icon: 'ChatDotRound',
    title: '一对一辅导',
    description: '个性化辅导，针对性解决问题'
  }
])
// 热门课程
const hotCourses = ref([])
// 优秀教师
const featuredTeachers = ref([])
// 学员反馈
const testimonials = ref([
  {
    id: 1,
    name: '小明',
    avatar: '',
    course: '高中数学提高班',
    content: '张老师的课程非常生动有趣，让我对数学产生了浓厚的兴趣，成绩也有了明显提高。'
  },
  {
    id: 2,
    name: '小红',
    avatar: '',
    course: '初中英语口语训练',
    content: '李老师的口语训练课程非常实用，我的英语口语水平有了很大提升，感谢老师的耐心指导。'
  },
  {
    id: 3,
    name: '小华',
    avatar: '',
    course: '小学语文阅读理解',
    content: '王老师的阅读理解课程让我学会了如何快速抓住文章重点，我的语文成绩有了显著提高。'
  }
])

const getTopCourse = async () => {
  try {
    const res = await request.get('/course/top')
    if (res.code === '200') {
      hotCourses.value = res.data
    } else {
      ElMessage.error(res.msg || '获取优秀教师失败')
    }
  } catch (error) {
    ElMessage.error('网络错误或服务器异常')
  }
}

const getTopTeacher = async () => {
  try {
    const res = await request.get('/teacher/top')
    if (res.code === '200') {
      featuredTeachers.value = res.data
    } else {
      ElMessage.error(res.msg || '获取优秀教师失败')
    }
  } catch (error) {
    ElMessage.error('网络错误或服务器异常')
  }
}


// 处理轮播图点击
const handleBannerClick = (item) => {
  router.push(item.link)
}

// 查看课程详情
const viewCourseDetail = (courseId) => {
  router.push(`/user/courseDetail/${courseId}`)
}

// 查看教师详情
const viewTeacherDetail = (teacherId) => {
  router.push(`/user/teacherDetail/${teacherId}`)
}

// 跳转到课程中心
const goToCourseCenter = () => {
  router.push('/user/courseCenter')
}

// 跳转到教师风采
const goToTeacherShowcase = () => {
  router.push('/user/teacherShowcase')
}
onMounted(async () => {

  getTopCourse()
  getTopTeacher()
})
</script>

<style scoped>
.homepage-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.banner {
  margin-bottom: 30px;
}

.banner-item {
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
}

.banner-content {
  background-color: rgba(0, 0, 0, 0.5);
  padding: 20px;
  border-radius: 8px;
}

.banner-content h2 {
  font-size: 24px;
  margin-bottom: 10px;
}

.banner-content p {
  font-size: 16px;
  margin-bottom: 15px;
}

.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
  position: relative;
  padding-left: 15px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background-color: #409EFF;
  border-radius: 2px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.features-section,
.courses-section,
.teachers-section,
.learning-path-section,
.testimonials-section {
  margin-bottom: 40px;
}

.feature-card {
  text-align: center;
  padding: 20px;
  height: 100%;
}

.feature-icon {
  font-size: 40px;
  color: #409EFF;
  margin-bottom: 15px;
}

.feature-card h3 {
  font-size: 18px;
  margin-bottom: 10px;
}

.feature-card p {
  color: #666;
  font-size: 14px;
}

.course-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.course-info {
  padding: 10px;
}

.course-info h3 {
  font-size: 16px;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-teacher {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-price {
  color: #F56C6C;
  font-weight: bold;
  font-size: 16px;
}

.course-students {
  color: #999;
  font-size: 12px;
}

.teacher-card {
  text-align: center;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.teacher-card:hover {
  transform: translateY(-5px);
}

.teacher-card h3 {
  margin: 10px 0 5px;
  font-size: 16px;
}

.teacher-subject {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.teacher-rating {
  margin-top: 10px;
}

.learning-path-section {
  padding: 30px 0;
}

.testimonial-card {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.testimonial-content {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  max-width: 600px;
}

.testimonial-text {
  font-style: italic;
  color: #666;
  margin-bottom: 15px;
  font-size: 16px;
}

.testimonial-author {
  display: flex;
  align-items: center;
}

.author-info {
  margin-left: 10px;
}

.author-info h4 {
  margin: 0 0 5px;
  font-size: 16px;
}

.author-info p {
  margin: 0;
  color: #999;
  font-size: 14px;
}
</style>