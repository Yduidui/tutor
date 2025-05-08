<template>
  <div class="teacher-showcase-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索教师姓名或科目"
          clearable
          prefix-icon="Search"
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <el-select v-model="searchForm.subjectId" placeholder="选择科目" clearable class="type-select">
          <el-option
            v-for="item in subjectList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </el-card>

    <!-- 教师列表 -->
    <div class="teacher-list">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="teacher in teacherList" :key="teacher.id">
          <el-card class="teacher-card" shadow="hover" @click="viewDetail(teacher.id)">
            <div class="teacher-avatar">
              <el-avatar :size="80" :src="teacher.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
            </div>
            <div class="teacher-info">
              <h3 class="teacher-name">{{ teacher.name }}</h3>
              <div class="teacher-meta">
                <div class="teacher-subject">
                  <el-icon><Collection /></el-icon>
                  {{ teacher.subject }}
                </div>
                <div class="teacher-school">
                  <el-icon><School /></el-icon>
                  {{ teacher.school }}
                </div>
              </div>
              <div class="teacher-rating">
                <el-rate
                  v-model="teacher.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                />
              </div>
              <div class="teacher-desc">{{ truncateDescription(teacher.descr) }}</div>
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
// 原脚本代码保持不变
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, User, Collection, School } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const searchForm = ref({
  keyword: '',
  subjectId: ''
})
const subjectList = ref([])
const teacherList = ref([])
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

// 获取教师列表
const fetchTeachers = async () => {
  try {
    const res = await request.get('/teacher/selectPage', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        name: searchForm.value.keyword,
        subjectId: searchForm.value.subjectId
      }
    })
    if (res.code === '200') {
      teacherList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  fetchTeachers()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    keyword: '',
    subjectId: ''
  }
  handleSearch()
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/user/teacher/${id}`)
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchTeachers()
}

// 页码改变
const handleCurrentChange = (val) => {
  pageNum.value = val
  fetchTeachers()
}

// 截取描述文本
const truncateDescription = (text) => {
  if (!text) return ''
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

// 初始化
onMounted(() => {
  fetchSubjects()
  fetchTeachers()
})
</script>

<style scoped>
.teacher-showcase-container {
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

.teacher-list {
  margin: 20px 0;
}

.teacher-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  position: relative;
}

.teacher-card:hover {
  transform: translateY(-5px);
}

.teacher-avatar {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
}

.teacher-info {
  text-align: center;
  padding: 0 15px;
}

.teacher-name {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 10px;
  color: #303133;
}

.teacher-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
}

.teacher-subject,
.teacher-school {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.teacher-rating {
  margin: 15px 0;
}

.teacher-desc {
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
  
  .teacher-card {
    margin-bottom: 15px;
  }
}
</style>