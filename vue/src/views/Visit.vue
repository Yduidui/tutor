<template>
  <div class="visit-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
          v-model="data.userNo"
          placeholder="请输入用户账号"
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
        <el-table-column label="用户账号" prop="userNo" min-width="120" />
        <el-table-column label="访问路径" prop="path" min-width="200" />
        <el-table-column label="IP地址" prop="ip" min-width="140" />
        <el-table-column label="访问时间" prop="visitTime" min-width="180">
          <template #default="scope">
            <span>{{ formatTime(scope.row.visitTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用户代理" prop="userAgent" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="查看详情" placement="top">
                <el-button type="primary" :icon="View" circle @click="handleView(scope.row)" />
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
      title="访问记录详情"
      size="600px"
      destroy-on-close
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ data.form.id }}</el-descriptions-item>
        <el-descriptions-item label="用户账号">{{ data.form.userNo }}</el-descriptions-item>
        <el-descriptions-item label="访问路径">{{ data.form.path }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ data.form.ip }}</el-descriptions-item>
        <el-descriptions-item label="访问时间">{{ formatTime(data.form.visitTime) }}</el-descriptions-item>
        <el-descriptions-item label="用户代理">{{ data.form.userAgent }}</el-descriptions-item>
      </el-descriptions>
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
import { Search, Delete, View } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import dayjs from 'dayjs';

const data = reactive({
  userNo: null,
  tabledata: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  ids: [],
  drawerVisible: false,
  form: {}
});

const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日HH时mm分ss秒');
};


// 异步请求加载数据
const load = () => {
  request.get('/pageView/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      userNo: data.userNo,
    }
  }).then((res) => {
    data.tabledata = res.data.list;
    data.total = res.data.total;
  });
};
load();

// 重置查询条件
const reset = () => {
  data.userNo = null;
  load();
};

// 查看详情
const handleView = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.drawerVisible = true;
};

// 删除单条记录
const del = (id) => {
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete(`/pageView/deleteById/` + id).then((res) => {
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
    request.delete('/pageView/deleteBatch', { data: data.ids }).then((res) => {
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
.visit-container {
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