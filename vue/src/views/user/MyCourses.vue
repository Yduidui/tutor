<template>
  <div class="my-courses-container">
    <!-- 未登录提示 -->
    <div v-if="!data.user" class="login-tip">
      <el-empty description="请先登录">
        <el-button type="primary" @click="router.push('/login')">去登录</el-button>
      </el-empty>
    </div>

    <!-- 已登录内容 -->
    <template v-if="data.user && data.user.role === 'STU'">
      <!-- 学生课程列表 -->
      <div class="course-list">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in data.tabledata" :key="course.id">
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
    </template>

    <!-- 教师视图 -->
    <template v-if="data.user && data.user.role === 'TEA'">
      <div class="teacher-actions">
        <el-button type="primary" @click="handleAdd">发布新课程</el-button>
      </div>
      <!-- 课程列表 -->
      <div class="course-list">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in data.tabledata" :key="course.id">
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

      <!-- 发布新课程表单弹窗 -->
      <!-- 课程编辑对话框 -->
      <el-dialog
          :title="data.form.id ? '编辑课程' : '新增课程'"
          v-model="data.formVisible"
          width="800px"
          destroy-on-close
      >
        <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="100px" class="course-form">
          <el-form-item label="封面" prop="cover">
            <el-upload
                class="cover-uploader"
                action="http://localhost:8080/files/upload"
                :show-file-list="false"
                :on-success="handleCoverSuccess"
            >
              <img v-if="data.form.cover" :src="data.form.cover" class="cover" />
              <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="标题" prop="title">
                <el-input v-model="data.form.title" placeholder="请输入课程标题" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="科目" prop="subject">
                <el-select v-model="data.form.subjectId" placeholder="请选择科目" style="width: 100%">
                  <el-option v-for="item in data.subjectList" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="开始时间" prop="startTime">
                <el-date-picker
                    v-model="data.form.startTime"
                    type="datetime"
                    placeholder="选择开始时间"
                    style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="结束时间" prop="endTime">
                <el-date-picker
                    v-model="data.form.endTime"
                    type="datetime"
                    placeholder="选择结束时间"
                    style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="状态" prop="status">
                <el-select v-model="data.form.status" placeholder="请选择状态" style="width: 100%">
                  <el-option label="未开始" value="未开始" />
                  <el-option label="进行中" value="进行中" />
                  <el-option label="已结束" value="已结束" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="价格" prop="price">
                <el-input-number v-model="data.form.price" :min="0" :precision="2" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="简介" prop="description">
            <el-input
                type="textarea"
                :rows="3"
                v-model="data.form.description"
                placeholder="请输入课程简介"
            />
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="data.formVisible = false">取消</el-button>
            <el-button type="primary" @click="save">保存</el-button>
          </div>
        </template>
      </el-dialog>

    </template>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="data.pageNum"
        v-model:page-size="data.pageSize"
        :page-sizes="[8, 12, 16, 20]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="data.total"
      />
    </div>

  </div>
</template>

<script setup >
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import {Search, Delete, Edit, Plus, View, Picture, User, Collection} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from 'dayjs';
import { ref, computed, onMounted, reactive ,onBeforeUnmount, shallowRef} from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref();
const editorRef = shallowRef();

// 使用reactive管理状态
const data = reactive({
  user : JSON.parse(localStorage.getItem('tutor-user')),
  title: null,
  tabledata: [],
  content: null,
  viewVisible: false,
  pageNum: 1,
  pageSize: 8,
  total: 0,
  formVisible: false,
  form: {},
  ids: [],
  subjectList:[],
  teacherList:[],
  studentList:[],
  rules: {
    title: [
      { required: true, message: "请输入课程标题", trigger: "blur" }
    ],
    subject: [
      { required: true, message: "请选择科目", trigger: "change" }
    ],
    teacherId: [
      { required: true, message: "请选择老师", trigger: "change" }
    ],
    startTime: [
      { required: true, message: "请选择开始时间", trigger: "change" }
    ],
    endTime: [
      { required: true, message: "请选择结束时间", trigger: "change" }
    ],
    status: [
      { required: true, message: "请选择状态", trigger: "change" }
    ],
    price: [
      { required: true, message: "请输入价格", trigger: "blur" }
    ]
  }
});
request.get("/subject/selectAll").then(res => {
  data.subjectList = res.data;
})
request.get("/teacher/selectAll").then(res => {
  data.teacherList = res.data;
})
request.get("/student/selectAll").then(res => {
  data.studentList = res.data;
})


const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日HH时mm分ss秒');
};

// 组件销毁时，销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});

const handleCreated = (editor) => {
  editorRef.value = editor;
};


// 封面上传成功
const handleCoverSuccess = (res) => {
  data.form.cover = res.data;
};

// 加载数据
const load = () => {
  if (data.user && data.user.role === 'STU') {
    request.get('/course/selectPageByStudentId', {
      params: {
        studentId: data.user.id,
        pageNum: data.pageNum,
        pageSize: data.pageSize
      }
    }).then((res) => {
    if (res.code === '200') {
        data.tabledata = res.data.list
        data.total = res.data.total
      }
    })
  } else {
    request.get('/course/selectPageByTeacherId', {
      params: {
        teacherId:data.user.id,
        pageNum: data.pageNum,
        pageSize: data.pageSize,
      }
    }).then((res) => {
      data.tabledata = res.data.list;
      data.total = res.data.total;
    });
  }
};


// 查看详情
const viewDetail = (id) => {
  router.push(`/user/course/${id}`)
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

// 重置搜索
const reset = () => {
  data.title = null;
  load();
};

// 新增课程
const handleAdd = () => {
  data.formVisible = true;
  data.form = {
    teacherId: data.user.id,  // ✅ 直接赋值当前登录用户的 ID
  };
};

// 编辑课程
const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

// 保存课程（新增或更新）
const save = () => {
  data.form.id ? update() : add()
}

const add = () => {
  request.post('/course/add', data.form).then((res) => {
    if (res.code === '200') {
      data.formVisible = false
      ElMessage.success('课程发布成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}


// 更新
const update = () => {
  request.put('/course/update', data.form).then((res) => {
    if (res.code === '200') {
      data.formVisible = false;
      ElMessage.success('操作成功');
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

// 删除
const del = (id) => {
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete(`/course/deleteById/` + id).then((res) => {
      if (res.code === '200') {
        ElMessage.success('操作成功');
        load();
      } else {
        ElMessage.error(res.msg);
      }
    });
  }).catch();
};

// 表格多选
const handleSelectionChange = (rows) => {
  data.ids = rows.map(row => row.id);
};

// 批量删除
const delBatch = () => {
  if (data.ids.length === 0) {
    ElMessage.warning('请选择数据');
    return;
  }
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete('/course/deleteBatch', { data: data.ids }).then((res) => {
      if (res.code === '200') {
        ElMessage.success('操作成功');
        load();
      } else {
        ElMessage.error(res.msg);
      }
    });
  }).catch();
};

// 分页大小改变
const handleSizeChange = (val) => {
  data.pageSize = val
  load()
}

// 页码改变
const handleCurrentChange = (val) => {
  data.pageNum = val
  load()
}

onMounted(() => {
  if (data.user) {
    load();
  }
});




</script>

<style scoped>
.my-courses-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.login-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.role-switch {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.teacher-actions {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
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
.course-form {
  padding: 20px;
}

.cover {
  width: 200px;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
}
.cover-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
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
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style> 