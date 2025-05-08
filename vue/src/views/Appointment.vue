<template>
  <div class="appointment-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
            v-model="data.courseTitle"
            placeholder="请输入课程名称"
            clearable
            prefix-icon="Search"
            @keyup.enter="load"
        />
        <el-select v-model="data.status" placeholder="预约状态" style="width:110px" clearable>
          <el-option label="可预约" value="available" />
          <el-option label="已预约" value="booked" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
        <el-button type="primary" @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="action-card" shadow="hover">
      <div class="action-wrapper">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增预约</el-button>
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
        <el-table-column label="课程" prop="courseTitle" min-width="150" show-overflow-tooltip />
        <el-table-column label="教师" prop="teacherName" width="120" />
        <el-table-column label="学生" prop="studentName" width="120" />
        <el-table-column label="日期" prop="date" width="120">
          <template #default="scope">
            <span>{{ formatDate(scope.row.date) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" prop="startTime" width="120" />
        <el-table-column label="结束时间" prop="endTime" width="120" />
        <el-table-column label="状态" prop="status" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="编辑" placement="top">
                <el-button type="primary" :icon="Edit" circle @click="handleUpdate(scope.row)" />
              </el-tooltip>
              <el-tooltip content="取消预约" placement="top" v-if="canCancel(scope.row)">
                <el-button type="warning" :icon="Close" circle @click="handleCancel(scope.row)" />
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

    <!-- 预约编辑对话框 -->
    <el-dialog
        :title="data.form.id ? '编辑预约' : '新增预约'"
        v-model="data.formVisible"
        width="600px"
        destroy-on-close
    >
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="data.form.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option
                v-for="course in data.courseList"
                :key="course.id"
                :label="course.title"
                :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker
              v-model="data.form.date"
              type="date"
              placeholder="选择日期"
              style="width: 100%"
              :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="时间段" prop="timeRange">
          <el-time-picker
              v-model="data.form.timeRange"
              is-range
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              placeholder="选择时间范围"
              style="width: 100%"
              format="HH:mm"
              value-format="HH:mm"
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

    <!-- 批量操作工具栏 -->
    <div v-if="data.ids.length > 0" class="batch-toolbar">
      <el-tag type="info">已选择 {{ data.ids.length }} 项</el-tag>
      <el-button type="danger" size="small" @click="delBatch">批量删除</el-button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import { Search, Delete, Edit, Plus, Close } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from 'dayjs';

const formRef = ref();

const data = reactive({
  courseTitle: null,
  status: null,
  tabledata: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  ids: [],
  courseList: [],
  rules: {
    courseId: [
      { required: true, message: "请选择课程", trigger: "change" }
    ],
    date: [
      { required: true, message: "请选择日期", trigger: "change" }
    ],
    timeRange: [
      { required: true, message: "请选择时间段", trigger: "change" }
    ]
  }
});

// 加载课程列表
const loadCourses = () => {
  request.get("/course/selectAll").then(res => {
    data.courseList = res.data;
  });
};

// 加载预约数据
const load = () => {
  request.get('/appointment/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      courseTitle: data.courseTitle,
      status: data.status
    }
  }).then((res) => {
    data.tabledata = res.data.list;
    data.total = res.data.total;
  });
};

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD');
};

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    available: 'info',
    booked: 'success',
    cancelled: 'danger'
  };
  return types[status] || 'info';
};

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    available: '可预约',
    booked: '已预约',
    cancelled: '已取消'
  };
  return texts[status] || status;
};

// 检查是否可以取消预约
const canCancel = (row) => {
  return row.status === 'booked' && 
         dayjs(`${row.date} ${row.startTime}`).isAfter(dayjs());
};

// 重置搜索
const reset = () => {
  data.courseTitle = null;
  data.status = null;
  load();
};

// 新增预约
const handleAdd = () => {
  data.formVisible = true;
  data.form = {};
};

// 编辑预约
const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.form.timeRange = [row.startTime, row.endTime];
  data.formVisible = true;
};

// 保存预约
const save = () => {
  if (!formRef.value) return;
  
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const appointment = {
          ...data.form,
          date: dayjs(data.form.date).format('YYYY-MM-DD'),
          startTime: data.form.timeRange[0],
          endTime: data.form.timeRange[1],
          status: 'available'
        };
        
        if (appointment.id) {
          await request.put('/appointment/update', appointment);
        } else {
          await request.post('/appointment/add', appointment);
        }
        
        ElMessage.success('操作成功');
        data.formVisible = false;
        load();
      } catch (error) {
        ElMessage.error('操作失败');
      }
    }
  });
};

// 取消预约
const handleCancel = async (row) => {
  try {
    await request.post(`/appointment/cancel/${row.id}`);
    ElMessage.success('取消预约成功');
    load();
  } catch (error) {
    ElMessage.error('取消预约失败');
  }
};

// 删除预约
const del = (id) => {
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete(`/appointment/delete/${id}`).then((res) => {
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
    request.delete('/appointment/deleteBatch', { data: data.ids }).then((res) => {
      if (res.code === '200') {
        ElMessage.success('操作成功');
        load();
      } else {
        ElMessage.error(res.msg);
      }
    });
  }).catch();
};

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7;
};

// 初始化
onMounted(() => {
  load();
  loadCourses();
});
</script>

<style scoped>
.appointment-container {
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
</style>