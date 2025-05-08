<template>
  <div class="course-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
            v-model="data.title"
            placeholder="请输入订单号"
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
        <el-button type="primary" icon="Plus" @click="handleAdd">新增订单</el-button>
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
        <el-table-column label="订单号" prop="orderNo" min-width="150" show-overflow-tooltip />
        <el-table-column label="学生" prop="studentName" min-width="200" show-overflow-tooltip />
        <el-table-column label="课程" prop="courseTitle" width="180" />
        <el-table-column label="老师" prop="teacherName" width="180" />

        <el-table-column label="开始时间" prop="orderDate" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.orderDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" prop="status" width="180" />
        <el-table-column label="付款方式" prop="paymentMethod" width="180" />
        <el-table-column label="订单总金额" prop="totalPrice" width="180" />

        <el-table-column label="支付时间" prop="paymentDate" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.paymentDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单过期时间" prop="expirationDate" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.expirationDate) }}</span>
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

    <!-- 订单编辑对话框 -->
    <el-dialog
        :title="data.form.id ? '编辑订单' : '新增订单'"
        v-model="data.formVisible"
        width="800px"
        destroy-on-close
    >
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="100px" class="course-form">

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程" prop="courseId">
              <el-select v-model="data.form.courseId" placeholder="请选择课程" style="width: 100%">
                <el-option v-for="item in data.courseList" :key="item.id" :label="item.title" :value="item.id" />
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
            <el-form-item label="学生" prop="studentId">
              <el-select v-model="data.form.studentId" placeholder="请选择学生" style="width: 100%">
                <el-option v-for="item in data.studentList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="下单时间" prop="orderDate">
              <el-date-picker
                  v-model="data.form.orderDate"
                  type="datetime"
                  placeholder="选择下单时间"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方法" prop="paymentMethod">
              <el-select v-model="data.form.paymentMethod" placeholder="请选择支付方法" style="width: 100%">
                <el-option label="微信" value="微信" />
                <el-option label="支付宝" value="支付宝" />
                <el-option label="VISA" value="VISA" />
              </el-select>
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
            <el-form-item label="订单总金额" prop="totalPrice">
              <el-input-number v-model="data.form.totalPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="支付时间" prop="paymentDate">
              <el-date-picker
                  v-model="data.form.paymentDate"
                  type="datetime"
                  placeholder="选择支付时间"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单过期时间" prop="expirationDate">
              <el-date-picker
                  v-model="data.form.expirationDate"
                  type="datetime"
                  placeholder="选择订单过期时间"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>


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
        title="订单内容"
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
  courseList:[],
  teacherList:[],
  studentList:[],
  rules: {
    title: [
      { required: true, message: "请输入订单标题", trigger: "blur" }
    ],
    subject: [
      { required: true, message: "请选择科目", trigger: "change" }
    ],
    teacherId: [
      { required: true, message: "请选择老师", trigger: "change" }
    ],
    orderDate: [
      { required: true, message: "请选择下单时间", trigger: "change" }
    ],
    status: [
      { required: true, message: "请选择状态", trigger: "change" }
    ],
    price: [
      { required: true, message: "请输入价格", trigger: "blur" }
    ]
  }
});
request.get("/course/selectAll").then(res => {
  data.courseList = res.data;
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
  request.get('/order/selectPage', {
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

// 新增订单
const handleAdd = () => {
  data.formVisible = true;
  data.form = {};
};

// 编辑订单
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
  request.post('/order/add', data.form).then((res) => {
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
  request.put('/order/update', data.form).then((res) => {
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
    request.delete(`/order/deleteById/` + id).then((res) => {
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
    request.delete('/order/deleteBatch', { data: data.ids }).then((res) => {
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

.el-icon.cover-uploader-icon {
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