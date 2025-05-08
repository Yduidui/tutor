<template>
  <div class="help-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
          v-model="data.title"
          placeholder="请输入工单标题"
          clearable
          prefix-icon="Search"
          @keyup.enter="load"
          style="width:200px"
        />
        <el-input
          v-model="data.userNo"
          placeholder="请输入用户编号"
          clearable
          prefix-icon="User"
          @keyup.enter="load"
          style="width:200px"
        />
        <el-select v-model="data.priority" placeholder="优先级" style="width:200px" clearable>
          <el-option label="低" :value="0" />
          <el-option label="中" :value="1" />
          <el-option label="高" :value="2" />
        </el-select>
        <el-select v-model="data.status" placeholder="工单状态" style="width:200px" clearable>
          <el-option label="新建" :value="0" />
          <el-option label="处理中" :value="1" />
          <el-option label="已解决" :value="2" />
        </el-select>
        <el-button type="primary" @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="action-card" shadow="hover">
      <div class="action-wrapper">
        <el-button type="danger" icon="Delete" :disabled="data.ids.length === 0" @click="delBatch">
          批量删除
        </el-button>
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
        <el-table-column label="工单标题" prop="title" min-width="150" />
        <el-table-column label="问题描述" prop="content" min-width="200" show-overflow-tooltip />
        <el-table-column label="用户编号" prop="userNo" width="120" />
        <el-table-column label="优先级" width="100">
          <template #default="scope">
            <el-tag :type="getPriorityType(scope.row.priority)">
              {{ getPriorityText(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdAt" width="180">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="查看详情" placement="top">
                <el-button type="primary" :icon="View" circle @click="handleView(scope.row)" />
              </el-tooltip>
              <el-tooltip content="回复" placement="top">
                <el-button type="success" :icon="ChatDotRound" circle @click="handleReply(scope.row)" />
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

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="data.drawerVisible"
      title="工单详情"
      size="600px"
      destroy-on-close
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="工单标题">{{ data.form.title }}</el-descriptions-item>
        <el-descriptions-item label="用户编号">{{ data.form.userNo }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ data.form.content }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityType(data.form.priority)">
            {{ getPriorityText(data.form.priority) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(data.form.status)">
            {{ getStatusText(data.form.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(data.form.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="回复内容">{{ data.form.reply || '暂无回复' }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ data.form.repliedBy || '暂无回复' }}</el-descriptions-item>
        <el-descriptions-item label="回复时间">
          {{ data.form.repliedAt ? formatTime(data.form.repliedAt) : '暂无回复' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <!-- 回复对话框 -->
    <el-dialog
      v-model="data.replyDialogVisible"
      title="回复工单"
      width="500px"
      destroy-on-close
    >
      <el-form :model="data.replyForm" label-width="80px">
        <el-form-item label="回复内容">
          <el-input
            v-model="data.replyForm.reply"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply">确认</el-button>
        </span>
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
import { reactive, ref } from "vue";
import { Search, Delete, View, ChatDotRound, User } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from 'dayjs';

const data = reactive({
  title: null,
  userNo: null,
  priority: null,
  status: null,
  tabledata: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  ids: [],
  drawerVisible: false,
  replyDialogVisible: false,
  form: {},
  replyForm: {
    id: null,
    reply: '',
    status: 1
  }
});

const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日HH时mm分ss秒');
};

const getPriorityType = (priority) => {
  const types = ['info', 'warning', 'danger'];
  return types[priority] || 'info';
};

const getPriorityText = (priority) => {
  const texts = ['低', '中', '高'];
  return texts[priority] || '未知';
};

const getStatusType = (status) => {
  const types = ['info', 'warning', 'success'];
  return types[status] || 'info';
};

const getStatusText = (status) => {
  const texts = ['新建', '处理中', '已解决'];
  return texts[status] || '未知';
};

// 导出数据
const exportData = () => {
  window.open('http://localhost:8080/help/export');
};

// 异步请求加载数据
const load = () => {
  request.get('/help/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      title: data.title,
      userNo: data.userNo,
      priority: data.priority,
      status: data.status
    }
  }).then((res) => {
    data.tabledata = res.data.list;
    data.total = res.data.total;
  });
};
load();

// 重置查询条件
const reset = () => {
  data.title = null;
  data.userNo = null;
  data.priority = null;
  data.status = null;
  load();
};

// 查看详情
const handleView = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.drawerVisible = true;
};

// 回复工单
const handleReply = (row) => {
  data.replyForm.id = row.id;
  data.replyForm.reply = row.reply || '';
  data.replyForm.status = row.status;
  data.replyDialogVisible = true;
};

// 提交回复
const submitReply = () => {
  const { id, reply } = data.replyForm;
  if (!reply) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  if (!id) {
    ElMessage.error('工单 ID 不存在');
    return;
  }

  const updateData = {
    id,
    reply,
    status: 2, // 设置为已解决
    repliedBy: JSON.parse(localStorage.getItem('tutor-user')).userNo // 从localStorage获取管理员ID
  };

  request.post('/help/reply', updateData).then((res) => {
    if (res.code === '200') {
      ElMessage.success('回复成功');
      data.replyDialogVisible = false;
      load(); // 重新加载列表
    } else {
      ElMessage.error(res.msg || '回复失败');
    }
  });
};

// 删除单条记录
const del = (id) => {
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete(`/help/deleteById/` + id).then((res) => {
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
    request.delete('/help/deleteBatch', { data: data.ids }).then((res) => {
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
.help-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>