<template>
  <div class="article-container">
    <!-- 顶部搜索区域 -->
    <el-card class="search-card" shadow="hover">
      <div class="search-wrapper">
        <el-input
            v-model="data.title"
            placeholder="请输入文章标题"
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
        <el-button type="primary" icon="Plus" @click="handleAdd">新增文章</el-button>
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
                class="article-cover"
                preview-teleported
            />
          </template>
        </el-table-column>
        <el-table-column label="简介" prop="descr" min-width="200" show-overflow-tooltip />
        <el-table-column label="作者账号" prop="userNo" width="120" />
        <el-table-column label="点赞数" prop="likes" width="50" />
        <el-table-column label="点击数" prop="clickCount" width="50" />
        <el-table-column label="发布时间" prop="time" min-width="180" />
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

    <!-- 文章编辑对话框 -->
    <el-dialog
        :title="data.form.id ? '编辑文章' : '新增文章'"
        v-model="data.formVisible"
        width="800px"
        destroy-on-close
    >
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="100px" class="article-form">
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

        <el-form-item label="标题" prop="title">
          <el-input v-model="data.form.title" placeholder="请输入文章标题" />
        </el-form-item>

        <el-form-item label="简介" prop="descr">
          <el-input
              type="textarea"
              :rows="3"
              v-model="data.form.descr"
              placeholder="请输入文章简介"
          />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <div style="border: 1px solid #ccc">
            <Toolbar
                style="border-bottom: 1px solid #ccc"
                :editor="editorRef"
                :defaultConfig="toolbarConfig"
                :mode="mode"
            />
            <Editor
                style="height: 500px; overflow-y: hidden;"
                v-model="data.form.content"
                :defaultConfig="editorConfig"
                :mode="mode"
                @onCreated="handleCreated"
            />
          </div>
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
        title="文章内容"
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
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { onBeforeUnmount, shallowRef } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { reactive, ref } from "vue"
import { Search, Delete, Edit, Plus } from "@element-plus/icons-vue"
import request from "@/utils/request.js"
import { ElMessage, ElMessageBox } from "element-plus"

const formRef = ref()
const editorRef = shallowRef()
const mode = 'default'
const toolbarConfig = {}
const editorConfig = { MENU_CONF: {} }

const baseUrl = 'http://localhost:8080'
editorConfig.MENU_CONF['uploadImage'] = {
  server: baseUrl + '/files/wang/upload',
  fieldName: 'file',
}

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
  rules: {
    title: [
      { required: true, message: "请输入文章标题", trigger: "blur" }
    ],
    descr: [
      { required: true, message: "请输入文章简介", trigger: "blur" }
    ],
    content: [
      { required: true, message: "请输入文章内容", trigger: "blur" }
    ]
  }
})

// 组件销毁时，销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor
}

// 封面上传成功
const handleCoverSuccess = (res) => {
  data.form.cover = res.data
}

// 加载数据
const load = () => {
  request.get('/article/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      title: data.title,
    }
  }).then((res) => {
    data.tabledata = res.data.list
    data.total = res.data.total
  })
}
load()

// 重置搜索
const reset = () => {
  data.title = null
  load()
}

// 新增文章
const handleAdd = () => {
  data.formVisible = true
  data.form = {}
}

// 编辑文章
const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

// 保存
const save = () => {
  if (!formRef.value) return
  formRef.value.validate((valid) => {
    if (valid) {
      data.form.id ? update() : add()
    }
  })
}

// 新增
const add = () => {
  request.post('/article/add', data.form).then((res) => {
    if (res.code === '200') {
      data.formVisible = false
      ElMessage.success('操作成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 更新
const update = () => {
  request.put('/article/update', data.form).then((res) => {
    if (res.code === '200') {
      data.formVisible = false
      ElMessage.success('操作成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 删除
const del = (id) => {
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete(`/article/deleteById/` + id).then((res) => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}

// 表格多选
const handleSelectionChange = (rows) => {
  data.ids = rows.map(row => row.id)
}

// 批量删除
const delBatch = () => {
  if (data.ids.length === 0) {
    ElMessage.warning('请选择数据')
    return
  }
  ElMessageBox.confirm('删除数据后无法恢复', '删除确认', { type: 'warning' }).then(() => {
    request.delete('/article/deleteBatch', { data: data.ids }).then((res) => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}
</script>

<style scoped>
.article-container {
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

.article-form {
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

.article-cover {
  width: 70px;
  height: 50px;
  border-radius: 4px;
  object-fit: cover;
}

.editor-content-view {
  padding: 20px;
  line-height: 1.8;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
}
</style>