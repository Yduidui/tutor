<template>
  <div class="student-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
          v-model="data.name"
          placeholder="请输入学生姓名"
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
        <el-button type="primary" icon="Plus" @click="handleAdd">新增学生</el-button>
        <el-button type="danger" icon="Delete" :disabled="data.ids.length === 0" @click="delBatch">
          批量删除
        </el-button>
        <el-upload
          class="upload-btn"
          action="http://localhost:8080/student/import"
          :show-file-list="false"
          :on-success="importSuccess"
        >
          <el-button type="warning" icon="Upload">导入</el-button>
        </el-upload>
        <el-button type="info" icon="Download" @click="exportData">导出</el-button>
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
        <el-table-column label="头像" width="80" fixed>
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column label="账号" prop="username" min-width="120" />
        <el-table-column label="姓名" prop="name" min-width="100" />
        <el-table-column label="性别" prop="sex" width="80" />
        <el-table-column label="手机号" prop="phone" min-width="120" />
        <el-table-column label="邮箱" prop="email" min-width="180" />
        <el-table-column label="学校" prop="school" min-width="150" />
        <el-table-column label="年级" prop="grade" min-width="100" />
        <el-table-column label="班级" prop="className" min-width="100" />
        <el-table-column label="注册时间" prop="registrationTime" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.registrationTime) }}</span>
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
          :page-sizes="[10, 20, 50, 100]"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="data.total"
          @size-change="load"
          @current-change="load"
        />
      </div>
    </el-card>

    <!-- 抽屉式表单 -->
    <el-drawer
      v-model="data.formVisible"
      :title="data.form.id ? '编辑学生信息' : '新增学生'"
      size="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :rules="data.rules"
        :model="data.form"
        label-width="100px"
        class="student-form"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="头像" prop="avatar">
              <el-upload
                class="avatar-uploader"
                action="http://localhost:8080/files/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
              >
                <el-avatar v-if="data.form.avatar" :size="100" :src="data.form.avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="data.form.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="data.form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="data.form.sex">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="data.form.birthday"
                type="date"
                placeholder="选择出生日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="data.form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="data.form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学校" prop="school">
              <el-input v-model="data.form.school" placeholder="请输入学校名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-select v-model="data.form.grade" placeholder="请选择年级" style="width: 100%">
                <el-option label="一年级" value="一年级" />
                <el-option label="二年级" value="二年级" />
                <el-option label="三年级" value="三年级" />
                <el-option label="四年级" value="四年级" />
                <el-option label="五年级" value="五年级" />
                <el-option label="六年级" value="六年级" />
                <el-option label="初一" value="初一" />
                <el-option label="初二" value="初二" />
                <el-option label="初三" value="初三" />
                <el-option label="高一" value="高一" />
                <el-option label="高二" value="高二" />
                <el-option label="高三" value="高三" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="班级" prop="className">
              <el-input v-model="data.form.className" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="个人简介" prop="descr">
              <el-input
                v-model="data.form.descr"
                type="textarea"
                :rows="4"
                placeholder="请输入个人简介"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="drawer-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 批量操作工具栏 -->
    <div v-if="data.ids.length > 0" class="batch-toolbar">
      <el-tag type="info">已选择 {{ data.ids.length }} 项</el-tag>
      <el-button type="danger" size="small" @click="delBatch">批量删除</el-button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { Search, Delete, Edit, Plus, User, Upload, Download } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from 'dayjs';

const formRef = ref();

const data = reactive({
  name: null,
  tabledata: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  ids: [],
  rules: {
    username: {
      required: true,
      message: "请输入账号",
      trigger: "blur",
    }
  }
});

const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日HH时mm分ss秒');
};

// 导出数据
const exportData = () => {
  window.open('http://localhost:8080/student/export');
};

// 导入成功回调
const importSuccess = () => {
  ElMessage.success('导入成功');
  load();
};

// 头像上传成功
const handleAvatarSuccess = (res) => {
  data.form.avatar = res.data;
};

// 异步请求加载数据
const load = () => {
  request.get('/student/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
    }
  }).then((res) => {
    data.tabledata = res.data.list;
    data.total = res.data.total;
  });
};
load();

// 重置查询条件
const reset = () => {
  data.name = null;
  load();
};

// 新增学生信息
const handleAdd = () => {
  data.formVisible = true;
  data.form = {};  // 清空数据，避免脏数据
};

// 编辑学生信息
const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row)); // 深拷贝
  data.formVisible = true;
};

// 保存方法（新增或编辑）
const save = () => {
  formRef.value.validate(valid => {
    if (valid) {
      data.form.id ? update() : add();
    }
  });
};

// 新增操作
const add = () => {
  request.post('/student/add', data.form).then((res) => {
    if (res.code === '200') {
      data.formVisible = false;
      ElMessage.success('操作成功');
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

// 更新操作
const update = () => {
  request.put('/student/update', data.form).then((res) => {
    if (res.code === '200') {
      data.formVisible = false;
      ElMessage.success('操作成功');
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

// 删除单条学生记录
const del = (id) => {
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete(`/student/deleteById/` + id).then((res) => {
      if (res.code === '200') {
        ElMessage.success('操作成功');
        load();
      } else {
        ElMessage.error(res.msg);
      }
    });
  }).catch();
};

// 选中批量删除
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
    request.delete('/student/deleteBatch', { data: data.ids }).then((res) => {
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
.student-container {
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

.student-form {
  padding: 20px;
}

.avatar-uploader {
  display: flex;
  justify-content: center;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.drawer-footer {
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
</style>