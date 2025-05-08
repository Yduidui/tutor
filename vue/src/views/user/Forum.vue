<template>
  <div class="forum-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <h1 class="page-title">📣 论坛社区</h1>
      <p class="page-description">欢迎在此提问、分享与交流</p>
    </div>

    <!-- 顶部工具栏区域 -->
    <el-card class="toolbar-card" shadow="hover">
      <div class="toolbar-wrapper">
        <div class="toolbar-left">
          <el-input
              v-model="data.searchQuery"
              placeholder="搜索文章标题或作者..."
              clearable
              prefix-icon="Search"
              class="search-input"
              @keyup.enter="loadArticles"
          />
          <el-radio-group v-model="data.sortBy" @change="loadArticles" class="sort-group">
            <el-radio-button label="time">最新发布</el-radio-button>
            <el-radio-button label="likes">最多点赞</el-radio-button>
          </el-radio-group>
        </div>
        <div class="toolbar-right">
          <el-button type="primary" icon="Plus" @click="handleAddPost">发布文章</el-button>
          <el-button 
            :type="data.showMyArticles ? 'success' : 'default'" 
            icon="Document" 
            @click="toggleMyArticles"
          >
            {{ data.showMyArticles ? '全部文章' : '我的文章' }}
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 文章列表 -->
    <div class="article-list">
      <el-card v-for="article in data.articles" :key="article.id" class="article-card" shadow="hover">
        <div class="article-header">
          <div class="article-info">
            <h3 class="article-title" @click="viewArticle(article)">{{ article.title }}</h3>
            <div class="article-meta">
              <span class="author">作者：{{ article.userNo }}</span>
              <span class="time">{{ formatTime(article.time) }}</span>
            </div>
          </div>
          <div class="article-actions">
            <el-button
                :type="article.isLiked ? 'danger' : 'default'"
                :icon="article.isLiked ? 'Star' : 'StarFilled'"
                circle
                @click="handleLike(article)"
            />
            <el-button
                type="primary"
                :icon="article.showComments ? 'ChatDotRound' : 'ChatLineRound'"
                circle
                @click="toggleComments(article)"
            />
            <el-button
                v-if="data.showMyArticles && isMyArticle(article)"
                type="danger"
                icon="Delete"
                circle
                @click="handleDeleteArticle(article)"
            />
          </div>
        </div>

        <div class="article-content">
          <p class="article-description">{{ article.descr }}</p>
          <el-image
              v-if="article.cover"
              :src="article.cover"
              :preview-src-list="[article.cover]"
              class="article-cover"
              fit="cover"
          />
        </div>

        <div class="article-stats">
          <span class="likes-count">
            <el-icon><Star /></el-icon>
            {{ article.likes || 0 }} 点赞
          </span>
          <span class="comments-count">
            <el-icon><ChatDotRound /></el-icon>
            {{ article.commentCount || 0 }} 评论
          </span>
        </div>

        <!-- 评论区 -->
        <div v-if="article.showComments" class="comments-section">
          <div class="comments-list">
            <div v-if="!article.comments?.length" class="no-comments">
              暂无评论，快来发表第一条评论吧！
            </div>
            <div v-else v-for="comment in article.comments" :key="comment.id" class="comment-item">
              <div class="comment-user">
                <el-avatar :size="32" :src="comment.userAvatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{ comment.username }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
            </div>
          </div>

          <!-- 评论输入框 -->
          <div v-if="data.isLoggedIn" class="comment-input">
            <el-input
                v-model="article.newComment"
                type="textarea"
                :rows="2"
                placeholder="写下你的评论..."
                @keyup.enter.ctrl="submitComment(article)"
            />
            <div class="comment-tip">按 Ctrl + Enter 发送</div>
            <el-button type="primary" @click="submitComment(article)">发送评论</el-button>
          </div>
          <div v-else class="login-tip">
            请<el-button type="text" @click="goToLogin">登录</el-button>后发表评论
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
          v-model:current-page="data.pageNum"
          v-model:page-size="data.pageSize"
          :page-sizes="[5, 10, 15, 20]"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="data.total"
          @size-change="loadArticles"
          @current-change="loadArticles"
      />
    </div>

    <!-- 文章详情对话框 -->
    <el-dialog
        v-model="data.articleVisible"
        :title="data.currentArticle?.title"
        width="800px"
        destroy-on-close
    >
      <div class="article-detail" v-if="data.currentArticle">
        <div class="article-meta">
          <span class="author">作者：{{ data.currentArticle.userNo }}</span>
          <span class="time">{{ formatTime(data.currentArticle.time) }}</span>
        </div>
        <div class="article-content" v-html="data.currentArticle.content"></div>
      </div>
    </el-dialog>

    <!-- 发布/编辑文章对话框 -->
    <el-dialog
        v-model="data.postDialogVisible"
        :title="data.postForm.id ? '编辑文章' : '发布文章'"
        width="800px"
        destroy-on-close
    >
      <el-form ref="postFormRef" :model="data.postForm" :rules="data.postRules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="data.postForm.title" placeholder="请输入文章标题" />
        </el-form-item>
        <el-form-item label="封面" prop="cover">
          <el-upload
              class="cover-uploader"
              action="http://localhost:8080/files/upload"
              :show-file-list="false"
              :on-success="handleCoverSuccess"
          >
            <img v-if="data.postForm.cover" :src="data.postForm.cover" class="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="简介" prop="descr">
          <el-input
              type="textarea"
              :rows="3"
              v-model="data.postForm.descr"
              placeholder="请输入文章简介"
          />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <div style="border: 1px solid #ccc">
            <Toolbar
                style="border-bottom: 1px solid #ccc"
                :editor="editorRef"
                :defaultConfig="toolbarConfig"
                :mode="mode"
            />
            <Editor
                style="height: 500px; overflow-y: hidden;"
                v-model="data.postForm.content"
                :defaultConfig="editorConfig"
                :mode="mode"
                @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.postDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPost">发布</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref, shallowRef, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, StarFilled, ChatDotRound, ChatLineRound, User, Plus, Search, Document } from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const router = useRouter()
const postFormRef = ref()
const editorRef = shallowRef()
const mode = 'default'
const toolbarConfig = {}
const editorConfig = { MENU_CONF: {} }

const baseUrl = 'http://localhost:8080'
editorConfig.MENU_CONF['uploadImage'] = {
  server: baseUrl + '/files/wang/upload',
  fieldName: 'file',
}

const data = reactive({
  articles: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  sortBy: 'time',
  searchQuery: '',
  articleVisible: false,
  currentArticle: null,
  isLoggedIn: false,
  showMyArticles: false,
  postDialogVisible: false,
  postForm: {
    id: null,
    title: '',
    cover: '',
    descr: '',
    content: ''
  },
  postRules: {
    title: [
      { required: true, message: '请输入文章标题', trigger: 'blur' }
    ],
    descr: [
      { required: true, message: '请输入文章简介', trigger: 'blur' }
    ],
    content: [
      { required: true, message: '请输入文章内容', trigger: 'blur' }
    ]
  }
})

// 组件销毁时，销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 加载文章列表
const loadArticles = async () => {
  try {
    const params = {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      sortBy: data.sortBy,
      title: data.searchQuery
    }

    if (data.showMyArticles) {
      const userNo = JSON.parse(localStorage.getItem('tutor-user')).userNo
      const res = await request.get(`/article/user/${userNo}`)
      if (res.code === '200') {
        data.articles = await Promise.all(res.data.map(async article => {
          const [commentCount, isLiked] = await Promise.all([
            getCommentCount(article.id),
            checkUserLiked(article.id)
          ])
          return {
            ...article,
            showComments: false,
            newComment: '',
            isLiked,
            commentCount
          }
        }))
        data.total = res.data.length
      }
    } else {
      const res = await request.get('/article/selectPage', { params })
      if (res.code === '200') {
        data.articles = await Promise.all(res.data.list.map(async article => {
          const [commentCount, isLiked] = await Promise.all([
            getCommentCount(article.id),
            checkUserLiked(article.id)
          ])
          return {
            ...article,
            showComments: false,
            newComment: '',
            isLiked,
            commentCount
          }
        }))
        data.total = res.data.total
      }
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
  }
}

// 获取评论数
const getCommentCount = async (articleId) => {
  try {
    const res = await request.get(`/article/comments/${articleId}`)
    return res.code === '200' ? res.data.length : 0
  } catch (error) {
    console.error('获取评论数失败:', error)
    return 0
  }
}

// 检查用户是否点赞
const checkUserLiked = async (articleId) => {
  if (!data.isLoggedIn) return false
  try {
    const userNo = JSON.parse(localStorage.getItem('tutor-user')).userNo
    const res = await request.get(`/article/like/check/${articleId}/${userNo}`)
    return res.code === '200' ? res.data : false
  } catch (error) {
    console.error('检查点赞状态失败:', error)
    return false
  }
}

// 查看文章详情
const viewArticle = (article) => {
  // 1. 点赞数 +1
  try {
    request.put(`/article/addClickCount/${article.id}`)
  } catch (e) {
    ElMessage.error('点击数更新失败')
  }
  data.currentArticle = article
  data.articleVisible = true
}

// 获取点赞数并更新
const getLikeCount = async (articleId) => {
  try {
    const res = await request.get(`/article/likes/${articleId}`)
    if (res.code === '200') {
      // 更新点赞数
      article.likes = res.data  // 假设 article 是你的文章数据对象
    } else {
      console.error('获取点赞数失败')
    }
  } catch (error) {
    console.error('请求失败:', error)
  }
}


// 处理点赞
// 处理点赞
const handleLike = async (article) => {
  if (!data.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    // 执行点赞操作
    const res = await request.post('/article/like', {
      articleId: article.id,
      userNo: JSON.parse(localStorage.getItem('tutor-user')).userNo
    })
    if (res.code === '200') {
      // 更新点赞状态和数量
      article.isLiked = !article.isLiked
      article.likes = article.isLiked ? (article.likes || 0) + 1 : (article.likes || 1) - 1
      ElMessage.success(article.isLiked ? '点赞成功' : '取消点赞')

      // 刷新点赞数
      await getLikeCount(article.id)
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败')
  }
}


// 切换评论区显示
const toggleComments = async (article) => {
  if (!article.comments) {
    try {
      const res = await request.get(`/article/comments/${article.id}`)
      if (res.code === '200') {
        article.comments = res.data
      }
    } catch (error) {
      console.error('加载评论失败:', error)
      ElMessage.error('加载评论失败')
    }
  }
  article.showComments = !article.showComments
}

// 提交评论
const submitComment = async (article) => {
  if (!article.newComment.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    const res = await request.post('/article/comment', {
      articleId: article.id,
      username: JSON.parse(localStorage.getItem('tutor-user')).username,
      userNo: JSON.parse(localStorage.getItem('tutor-user')).userNo,
      content: article.newComment
    })
    if (res.code === '200') {
      article.comments = article.comments || []
      article.comments.unshift(res.data)
      article.commentCount = (article.commentCount || 0) + 1
      article.newComment = ''
      ElMessage.success('评论成功')
    }
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error('评论失败')
  }
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 检查登录状态
const checkLoginStatus = () => {
  const userInfo = JSON.parse(localStorage.getItem('tutor-user') || 'null')
  data.isLoggedIn = userInfo !== null
}

// 切换显示我的文章
const toggleMyArticles = () => {
  if (!data.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  data.showMyArticles = !data.showMyArticles
  data.pageNum = 1
  loadArticles()
}

// 打开发布文章对话框
const handleAddPost = () => {
  if (!data.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  data.postForm = {
    id: null,
    title: '',
    cover: '',
    descr: '',
    content: ''
  }
  data.postDialogVisible = true
}

// 封面上传成功
const handleCoverSuccess = (res) => {
  data.postForm.cover = res.data
}

// 提交文章
const submitPost = () => {
  if (!postFormRef.value) return
  postFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = data.postForm.id ? '/article/update' : '/article/add'
        const res = await request.post(url, {
          ...data.postForm,
          userNo: JSON.parse(localStorage.getItem('tutor-user')).userNo
        })
        if (res.code === '200') {
          data.postDialogVisible = false
          ElMessage.success(data.postForm.id ? '更新成功' : '发布成功')
          loadArticles()
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 编辑文章
const handleEditArticle = (article) => {
  data.postForm = { ...article }
  data.postDialogVisible = true
}

// 删除文章
const handleDeleteArticle = async (article) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      type: 'warning'
    })
    const res = await request.delete(`/article/deleteById/${article.id}`)
    if (res.code === '200') {
      ElMessage.success('删除成功')
      loadArticles()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 判断是否是当前用户的文章
const isMyArticle = (article) => {
  if (!data.isLoggedIn) return false
  const userNo = JSON.parse(localStorage.getItem('tutor-user')).userNo
  return article.userNo === userNo
}

onMounted(() => {
  checkLoginStatus()
  loadArticles()
})
</script>

<style scoped>
.forum-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
  text-align: left;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  margin: 0 0 10px;
}

.page-description {
  font-size: 16px;
  color: var(--el-text-color-secondary);
  margin: 0;
}

.toolbar-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.toolbar-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
}

.search-input {
  width: 300px;
}

.sort-group {
  margin-left: auto;
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-card {
  transition: all 0.3s ease;
  border-radius: 8px;
}

.article-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--el-box-shadow-light);
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.article-info {
  flex: 1;
}

.article-title {
  margin: 0 0 10px;
  font-size: 20px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  cursor: pointer;
}

.article-title:hover {
  color: var(--el-color-primary);
}

.article-meta {
  display: flex;
  gap: 15px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.article-actions {
  display: flex;
  gap: 8px;
}

.article-content {
  margin-bottom: 15px;
}

.article-description {
  margin: 0 0 15px;
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.article-cover {
  width: 100%;
  max-height: 300px;
  border-radius: 8px;
  object-fit: cover;
}

.article-stats {
  display: flex;
  gap: 20px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.article-stats .el-icon {
  margin-right: 4px;
}

.comments-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.comments-list {
  margin-bottom: 20px;
}

.no-comments {
  text-align: center;
  color: var(--el-text-color-secondary);
  padding: 20px 0;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.username {
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.comment-time {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.comment-content {
  color: var(--el-text-color-regular);
  line-height: 1.6;
  margin-left: 40px;
}

.comment-input {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  text-align: right;
}

.login-tip {
  text-align: center;
  color: var(--el-text-color-secondary);
  padding: 20px 0;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.article-detail {
  padding: 20px;
}

.article-detail .article-meta {
  margin-bottom: 20px;
}

.article-detail .article-content {
  line-height: 1.8;
}

.cover-uploader {
  display: flex;
  justify-content: center;
}

.cover-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.cover-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.cover {
  width: 200px;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 120px;
  text-align: center;
  line-height: 120px;
  background-color: var(--el-fill-color-light);
  border-radius: 6px;
}

@media (max-width: 768px) {
  .forum-container {
    padding: 10px;
  }

  .toolbar-wrapper {
    flex-direction: column;
    gap: 10px;
  }

  .toolbar-left {
    flex-direction: column;
    width: 100%;
  }

  .search-input {
    width: 100%;
  }

  .sort-group {
    margin-left: 0;
    width: 100%;
    display: flex;
    justify-content: center;
  }

  .article-title {
    font-size: 18px;
  }

  .article-actions {
    flex-direction: column;
  }
}
</style>