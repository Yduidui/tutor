<template>
  <div class="comment-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
            v-model="data.keyword"
            placeholder="请输入搜索内容"
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
        <el-table-column label="ID" prop="id" min-width="150" show-overflow-tooltip />
        <el-table-column label="老师" prop="teacherName" width="180" />
        <el-table-column label="学生" prop="studentName" width="180" />
        <el-table-column label="创建时间" prop="createdTime" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createdTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="内容" prop="content" width="180" />
        <el-table-column label="状态" prop="isDeleted" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.isDeleted === 1 ? 'danger' : 'success'">
              {{ scope.row.isDeleted === 1 ? '已删除' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="更新" placement="top">
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

    <!-- 批量操作工具栏 -->
    <div v-if="data.ids.length > 0" class="batch-toolbar">
      <el-tag type="info">已选择 {{ data.ids.length }} 项</el-tag>
      <el-button type="danger" size="small" @click="delBatch">批量删除</el-button>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import {Search, Delete, Edit} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from 'dayjs';

const data = reactive({
  keyword: null,
  tabledata: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  ids: [],
});

const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日HH时mm分ss秒');
};

// 加载数据
const load = () => {
  request.get('/comment/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      keyword: data.keyword,

    }
  }).then((res) => {
    data.tabledata = res.data.list;
    data.total = res.data.total;
  });
};
load();

// 重置搜索
const reset = () => {
  data.keyword = null;
  load();
};

// 更新状态
// 更新状态（切换 isDeleted）
const handleUpdate = (row) => {
  const action = row.isDeleted ? '恢复为正常状态' : '标记为已删除';
  ElMessageBox.confirm(`确定要将该评论${action}吗？`, '状态更新确认', { type: 'warning' }).then(() => {
    request.put(`/comment/updateIsDeleted/${row.id}`).then((res) => {
      if (res.code === '200') {
        ElMessage.success('状态更新成功');
        load();
      } else {
        ElMessage.error(res.msg);
      }
    });
  }).catch(() => {});
};


// 删除
const del = (id) => {
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete(`/comment/deleteById/` + id).then((res) => {
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

  ElMessageBox.confirm('确定要批量删除这些评论吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.put('/comment/deleteBatch', { ids: data.ids }).then(res => {
      if (res.code === '200') {
        ElMessage.success('批量删除成功');
        load();
        data.ids = [];
      } else {
        ElMessage.error(res.msg);
      }
    });
  }).catch(() => {});
};
</script>

<style scoped>
.comment-container {
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

.comment-form {
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

.comment-cover {
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