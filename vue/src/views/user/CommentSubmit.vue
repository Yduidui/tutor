<template>
  <div class="comment-submit">
    <el-form :model="commentForm" :rules="rules" ref="formRef">
      <el-form-item label="评分" prop="rating">
        <el-rate
          v-model="commentForm.rating"
          :max="5"
          :step="0.5"
          show-score
          text-color="#ff9900"
          score-template="{value}分"
        />
      </el-form-item>
      
      <el-form-item label="评价内容" prop="content">
        <el-input
          v-model="commentForm.content"
          type="textarea"
          :rows="4"
          placeholder="请输入您的评价内容（500字以内）"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitComment">提交评价</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  teacherId: {
    type: Number,
    required: true
  }
})

const formRef = ref(null)
const commentForm = reactive({
  teacherId: props.teacherId,
  studentId: JSON.parse(localStorage.getItem('tutor-user')).id,
  rating: 0,
  content: ''
})
console.log("这里是传来的teacherID:" + commentForm.teacherId)

const rules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  content: [
    { max: 500, message: '评价内容不能超过500字', trigger: 'blur' }
  ]
}

const submitComment = async () => {
  console.log('提交的评论数据:', commentForm); // 打印评论数据
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    const res = await request.post('/comment/submit', commentForm)
    if (res.code === '200') {
      ElMessage.success('评价提交成功')
      // 清空表单
      commentForm.rating = 0
      commentForm.content = ''
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  }
}
</script>

<style scoped>
.comment-submit {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-rate) {
  margin-top: 8px;
}

:deep(.el-textarea__inner) {
  resize: none;
}
</style> 