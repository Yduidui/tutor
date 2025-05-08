<template>
  <div class="news-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
          v-model="data.title"
          placeholder="请输入标题"
          clearable
          prefix-icon="Search"
          @keyup.enter="load"
        />
        <el-select
          v-model="data.type"
          placeholder="请选择类型"
          clearable
          style="width: 120px"
        >
          <el-option label="公告" value="公告" />
          <el-option label="资讯" value="资讯" />
          <el-option label="更新" value="更新" />
        </el-select>
        <el-button type="primary" @click="load">搜索</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="action-card" shadow="hover">
      <div class="action-wrapper">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增</el-button>
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
        class="news-table"
      >
        <el-table-column type="selection" width="55" fixed />
        <el-table-column label="标题" prop="title" min-width="100" show-overflow-tooltip />
        <el-table-column label="描述" prop="descr" min-width="200" show-overflow-tooltip />
        <el-table-column label="类型" prop="type" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTag(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="作者" prop="author" width="120" />
        <el-table-column label="发布时间" prop="createTime" width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="230">
          <template #default="scope">
            <span>{{ formatTime(scope.row.updateTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <div class="table-actions">
              <el-tooltip content="查看" placement="top">
                <el-button type="primary" :icon="View" circle @click="view(scope.row.content)" />
              </el-tooltip>
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

    <!-- 抽屉式表单 -->
    <el-drawer
      v-model="data.formVisible"
      :title="data.form.id ? '编辑文章' : '新增文章'"
      size="800px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="data.form"
        label-width="80px"
        class="news-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="data.form.title" placeholder="请输入标题" />
        </el-form-item>
        
        <el-form-item label="类型" prop="type">
          <el-select v-model="data.form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="公告" value="公告" />
            <el-option label="资讯" value="资讯" />
            <el-option label="更新" value="更新" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="简介" prop="descr">
          <el-input
            v-model="data.form.descr"
            type="textarea"
            :rows="3"
            placeholder="请输入简介"
          />
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <div class="editor-wrapper">
            <Toolbar
              class="editor-toolbar"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              :mode="mode"
            />
            <Editor
              class="editor"
              v-model="data.form.content"
              :defaultConfig="editorConfig"
              :mode="mode"
              @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="drawer-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 查看内容弹窗 -->
    <el-dialog
      title="内容详情"
      v-model="data.viewVisible"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
      class="view-dialog"
    >
      <div class="editor-content-view" v-html="data.content"></div>
      <template #footer>
        <el-button @click="data.viewVisible = false" class="close-button">关闭</el-button>
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
import { onBeforeUnmount, shallowRef, onMounted } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { reactive, ref } from "vue"
import { Search, Delete, Edit, View } from "@element-plus/icons-vue"
import request from "@/utils/request.js"
import { ElMessage, ElMessageBox } from "element-plus"
import dayjs from 'dayjs'

const formRef = ref()
const editorRef = shallowRef()
const mode = 'default'
const editorConfig = { MENU_CONF: {} }

const baseUrl = 'http://localhost:8080'
editorConfig.MENU_CONF['uploadImage'] = {
  server: baseUrl + '/files/wang/upload',
  fieldName: 'file',
}

const data = reactive({
  title: null,
  type: null,
  tabledata: [],
  content: null,
  viewVisible: false,
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  ids: [],
})

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日 HH:mm:ss')
}

// 获取类型标签样式
const getTypeTag = (type) => {
  const types = {
    '公告': 'warning',
    '资讯': 'success',
    '更新': 'info'
  }
  return types[type] || 'info'
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor
}

const view = (content) => {
  data.content = content
  data.viewVisible = true
}

// 异步请求
const load = () => {
  request.get('/news/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      title: data.title,
      type: data.type,
    }
  }).then((res) => {
    data.tabledata = res.data.list
    data.total = res.data.total
  })
}

const reset = () => {
  data.title = null
  data.type = null
  load()
}

const handleAdd = () => {
  data.formVisible = true
  data.form = {}
}

const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

const save = () => {
  data.form.id ? update() : add()
}

const add = () => {
  request.post('/news/add', data.form).then((res) => {
    if (res.code === '200') {
      data.formVisible = false
      ElMessage.success('操作成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const update = () => {
  request.put('/news/update', data.form).then((res) => {
    if (res.code === '200') {
      data.formVisible = false
      ElMessage.success('操作成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const del = (id) => {
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete(`/news/deleteById/` + id).then((res) => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}

const handleSelectionChange = (rows) => {
  data.ids = rows.map(row => row.id)
}

const delBatch = () => {
  if (data.ids.length === 0) {
    ElMessage.warning('请选择数据')
    return
  }
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete('/news/deleteBatch', { data: data.ids }).then((res) => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}

// 初始化
onMounted(() => {
  load()
})
</script>

<style scoped>
.news-container {
  padding: 20px;
}

.search-card,
.action-card,
.table-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-wrapper .el-input {
  width: 300px;
}

.action-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
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

.news-form {
  padding: 20px;
}

.editor-wrapper {
  height: 400px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
}

.editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
}

.editor {
  flex:1;
  overflow-y: hidden;
}

.editor-content-view {
  padding: 20px;
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

.view-dialog {
  max-width: 90%;
}

.close-button {
  margin-left: auto;
}

@media (max-width: 768px) {
  .search-wrapper {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-wrapper .el-input {
    width: 100%;
  }
  
  .action-wrapper {
    flex-wrap: wrap;
  }
  
  .table-actions {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
