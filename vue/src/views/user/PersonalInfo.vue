<template>
  <!-- 教师端：评价展示 -->
  <div v-if="user.role === 'TEA'" class="comment-section">
    <div class="section-header">
      <h3>收到的评价</h3>
      <el-button type="primary" @click="refreshComments">刷新</el-button>
    </div>
    
    <div class="comment-overview">
      <div class="average-rating">
        <span class="label">平均评分：</span>
        <el-rate v-model="averageRating" disabled show-score text-color="#ff9900" />
      </div>
    </div>

    <div class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <div class="student-info">
            <el-avatar :size="40" :src="comment.studentAvatar || '/logo.png'" />
            <span class="student-name">{{ comment.studentName }}</span>
          </div>
          <div class="comment-rating">
            <el-rate v-model="comment.rating" disabled show-score text-color="#ff9900" />
          </div>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-time">{{ formatTime(comment.createdTime) }}</div>
      </div>
      
      <el-empty v-if="comments.length === 0" description="暂无评价" />
    </div>
  </div>

  <!-- 学生端：我的评价 -->
  <div v-else class="comment-section">
    <div class="section-header">
      <h3>我的评价</h3>
      <el-button type="primary" @click="refreshComments">刷新</el-button>
    </div>

    <div class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <div class="teacher-info">
            <el-avatar :size="40" :src="comment.teacherAvatar || '/logo.png'" />
            <span class="teacher-name">{{ comment.teacherName }}</span>
          </div>
          <div class="comment-actions">
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteComment(comment)"
            >
              撤回
            </el-button>
          </div>
        </div>
        <div class="comment-rating">
          <el-rate v-model="comment.rating" disabled show-score text-color="#ff9900" />
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-time">{{ formatTime(comment.createdTime) }}</div>
      </div>
      
      <el-empty v-if="comments.length === 0" description="暂无评价" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

// 评价相关
const comments = ref([])
const averageRating = ref(0)

const loadComments = async () => {
  try {
    if (user.value.role === 'TEA') {
      // 教师端：获取收到的评价
      const res = await request.get(`/comment/teacher/${user.value.userNo}`)
      if (res.code === '200') {
        comments.value = res.data
        // 计算平均分
        if (comments.value.length > 0) {
          const sum = comments.value.reduce((acc, cur) => acc + cur.rating, 0)
          averageRating.value = (sum / comments.value.length).toFixed(1)
        }
      }
    } else {
      // 学生端：获取自己发表的评价
      const res = await request.get(`/comment/student/${user.value.userNo}`)
      if (res.code === '200') {
        comments.value = res.data
      }
    }
  } catch (error) {
    ElMessage.error('加载评价失败')
  }
}

const handleDeleteComment = async (comment) => {
  try {
    await ElMessageBox.confirm('确定要撤回该评价吗？', '提示', {
      type: 'warning'
    })
    const res = await request.delete(`/comment/${comment.id}`)
    if (res.code === '200') {
      ElMessage.success('撤回成功')
      loadComments()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('撤回失败')
    }
  }
}

const refreshComments = () => {
  loadComments()
}

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-section {
  margin-top: 20px;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.comment-overview {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.average-rating {
  display: flex;
  align-items: center;
  gap: 10px;
}

.label {
  font-size: 16px;
  color: #606266;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fafafa;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.student-info, .teacher-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.student-name, .teacher-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.comment-rating {
  margin: 10px 0;
}

.comment-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 10px 0;
}

.comment-time {
  font-size: 12px;
  color: #909399;
  text-align: right;
}

.comment-actions {
  display: flex;
  gap: 10px;
}
</style> 