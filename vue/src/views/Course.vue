<template>
  <div class="course-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
            v-model="data.title"
            placeholder="请输入课程标题"
            clearable
            prefix-icon="Search"
            @keyup.enter="load"
        />
        <el-button type="primary" @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="action-card" shadow="hover">
      <div class="action-wrapper">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增课程</el-button>
        <el-button type="danger" icon="Delete" :disabled="data.ids.length === 0" @click="delBatch">
          批量删除
        </el-button>
      </div>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="hover">
      <el-table
          :data="data.tabledata"
          stripe
          border
          highlight-current-row
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" fixed />
        <el-table-column label="标题" prop="title" min-width="150" show-overflow-tooltip />
        <el-table-column label="封面" width="100" fixed>
          <template #default="scope">
            <el-image
                v-if="scope.row.cover"
                :src="scope.row.cover"
                :preview-src-list="[scope.row.cover]"
                class="course-cover"
                preview-teleported
            />
          </template>
        </el-table-column>
        <el-table-column label="简介" prop="description" min-width="200" show-overflow-tooltip />
        <el-table-column label="科目" prop="subjectName" width="180" />
        <el-table-column label="老师" prop="teacherName" width="180" />
        <el-table-column label="学生" prop="studentNames" width="180" />
        <el-table-column label="开始时间" prop="startTime" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.startTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结束时间" prop="endTime" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.endTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="持续时间" prop="duration" width="180" />
        <el-table-column label="状态" prop="status" width="180" />
        <el-table-column label="价格" prop="price" width="180" />
        <el-table-column label="创建时间" prop="createTime" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="编辑" placement="top">
                <el-button type="primary" :icon="Edit" circle @click="handleUpdate(scope.row)" />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button type="danger" :icon="Delete" circle @click="del(scope.row.id)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页控件 -->
      <div class="pagination-wrapper">
        <el-pagination
            v-model:current-page="data.pageNum"
            v-model:page-size="data.pageSize"
            :page-sizes="[5, 10, 15, 20]"
            :background="true"
            layout="total, sizes, prev, pager, next, jumper"
            :total="data.total"
            @size-change="load"
            @current-change="load"
        />
      </div>
    </el-card>

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
            <el-form-item label="老师" prop="teacherId">
              <el-select v-model="data.form.teacherId" placeholder="请选择老师" style="width: 100%">
                <el-option v-for="item in data.teacherList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>


            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学生" prop="studentIds">
              <el-select v-model="data.form.studentIds" placeholder="请选择学生" style="width: 100%">
                <el-option v-for="item in data.studentList" :key="item.id" :label="item.name" :value="item.id" />
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

    <!-- 内容预览对话框 -->
    <el-dialog
        title="课程内容"
        v-model="data.viewVisible"
        width="800px"
        :close-on-click-modal="false"
        destroy-on-close
    >
      <div class="editor-content-view" v-html="data.content"></div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.viewVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量操作工具栏 -->
    <div v-if="data.ids.length > 0" class="batch-toolbar">
      <el-tag type="info">已选择 {{ data.ids.length }} 项</el-tag>
      <el-button type="danger" size="small" @click="delBatch">批量删除</el-button>
    </div>
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { onBeforeUnmount, shallowRef } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { reactive, ref } from "vue";
import { Search, Delete, Edit, Plus, View } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from 'dayjs';

const formRef = ref();
const editorRef = shallowRef();
const mode = 'default';

//后端wangEditor上传图片用
// const editorConfig = { MENU_CONF: {} };

// const baseUrl = 'http://localhost:8080';
// editorConfig.MENU_CONF['uploadImage'] = {
//   server: baseUrl + '/files/course/upload',
//   fieldName: 'file',
// };

const data = reactive({
  title: null,
  tabledata: [],
  content: null,
  viewVisible: false,
  pageNum: 1,
  pageSize: 10,
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
  request.get('/course/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      title: data.title,
    }
  }).then((res) => {
    data.tabledata = res.data.list;
    data.total = res.data.total;
  });
};
load();

// 重置搜索
const reset = () => {
  data.title = null;
  load();
};

// 新增课程
const handleAdd = () => {
  data.formVisible = true;
  data.form = {};
};

// 编辑课程
const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

// 保存
const save = () => {
  data.form.id ? update() : add();
};

// 新增
const add = () => {
  request.post('/course/add', data.form).then((res) => {
    if (res.code === '200') {
      data.formVisible = false;
      ElMessage.success('操作成功');
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

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
</script>

<style scoped>
.course-container {
  padding: 20px;
}

.search-card,
.action-card,
.table-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-wrapper,
.action-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-wrapper .el-input {
  width: 300px;
}

.action-wrapper {
  flex-wrap: wrap;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.course-form {
  padding: 20px;
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

.editor-container {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
}

.batch-toolbar {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background-color: var(--el-bg-color);
  padding: 10px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 100;
}

.course-cover {
  width: 70px;
  height: 50px;
  border-radius: 4px;
  object-fit: cover;
}


.editor-content-view {
  padding: 20px;
  line-height: 1.8;
}
</style>