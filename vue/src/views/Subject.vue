<template>
  <div class="subject-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
            v-model="data.name"
            placeholder="请输入科目名称"
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
        <el-button type="primary" icon="Plus" @click="handleAdd">新增科目</el-button>
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
        <el-table-column label="科目名称" prop="name" min-width="150" show-overflow-tooltip />
        <el-table-column label="科目分类" prop="category" min-width="150" show-overflow-tooltip />
        <el-table-column label="适用级别" prop="level" min-width="150" show-overflow-tooltip />
        <el-table-column label="是否可用" prop="isActive" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isActive? 'success' : 'danger'">
              {{ scope.row.isActive? '可用' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdAt" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" prop="updatedAt" min-width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.updatedAt) }}</span>
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

    <!-- 科目编辑对话框 -->
    <el-dialog
        :title="data.form.id? '编辑科目' : '新增科目'"
        v-model="data.formVisible"
        width="600px"
        destroy-on-close
    >
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="100px" class="subject-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科目名称" prop="name">
              <el-input v-model="data.form.name" placeholder="请输入科目名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科目分类" prop="category">
              <el-select v-model="data.form.category" placeholder="请选择科目分类" clearable filterable>
                <el-option
                    v-for="item in categoryOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适用级别" prop="level">
              <el-select v-model="data.form.level" placeholder="请选择适用级别" clearable>
                <el-option
                    v-for="level in levelOptions"
                    :key="level"
                    :label="level"
                    :value="level"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否可用" prop="isActive">
              <el-switch
                  v-model="data.form.isActive"
                  :active-value="1"
                  :inactive-value="0"
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

    <!-- 批量操作工具栏 -->
    <div v-if="data.ids.length > 0" class="batch-toolbar">
      <el-tag type="info">已选择 {{ data.ids.length }} 项</el-tag>
      <el-button type="danger" size="small" @click="delBatch">批量删除</el-button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from "vue";
import { Search, Delete, Edit, Plus } from "@element-plus/icons-vue";
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
    name: [
      { required: true, message: "请输入科目名称", trigger: "blur" }
    ],
    category: [
      { required: true, message: "请输入科目分类", trigger: "blur" }
    ],
    level: [
      { required: true, message: "请输入适用级别", trigger: "blur" }
    ],
    isActive: [
      { required: true, message: "请选择是否可用", trigger: "change" }
    ]
  }
});

const categoryOptions = [
  { label: "K12教育", value: "K12教育" },
  { label: "语言培训", value: "语言培训" },
  { label: "考试辅导", value: "考试辅导" },
  { label: "兴趣培养", value: "兴趣培养" },
  { label: "职业技能", value: "职业技能" },
  { label: "大学课程", value: "大学课程" }
];

const levelMap = {
  "K12教育": ["幼儿园小班", "小学一年级", "初一", "高三"],
  "语言培训": ["启蒙级", "初级", "中级", "高级", "专业级"],
  "考试辅导": ["基础班", "强化班", "冲刺班", "押题班"],
  "兴趣培养": ["入门", "初级", "中级", "高级", "大师"],
  "职业技能": ["Level 1", "Level 2", "Level 3", "高级"],
  "大学课程": ["大一", "大二", "大三", "研究生"]
};

const levelOptions = ref([]);

watch(() => data.form.category, (val) => {
  levelOptions.value = levelMap[val] || [];
  data.form.level = "";  // 清空上一次选的级别
});

const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日HH时mm分ss秒');
};

// 加载数据
const load = () => {
  request.get('/subject/selectPage', {
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

// 重置搜索
const reset = () => {
  data.name = null;
  load();
};

// 新增科目
const handleAdd = () => {
  data.formVisible = true;
  data.form = {};
};

// 编辑科目
const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

// 保存
const save = () => {
  data.form.id? update() : add();
};

// 新增
const add = () => {
  request.post('/subject/add', data.form).then((res) => {
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
  request.put('/subject/update', data.form).then((res) => {
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
    request.delete(`/subject/deleteById/` + id).then((res) => {
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
    request.delete('/subject/deleteBatch', { data: data.ids }).then((res) => {
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
.subject-container {
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

.search-wrapper.el-input {
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

.subject-form {
  padding: 20px;
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
