<template>
  <div class="help-center">
    <!-- 顶部banner -->
    <div class="banner">
      <div class="banner-content">
        <h1>帮助中心</h1>
        <p>我们随时为您提供帮助和支持</p>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <!-- 左侧导航 -->
      <div class="sidebar">
        <el-menu
          :default-active="activeMenu"
          class="help-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="submit">
            <el-icon><Plus /></el-icon>
            <span>提交工单</span>
          </el-menu-item>
          <el-menu-item index="my-tickets">
            <el-icon><Document /></el-icon>
            <span>我的工单</span>
          </el-menu-item>
          <el-menu-item index="faq">
            <el-icon><QuestionFilled /></el-icon>
            <span>常见问题</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容 -->
      <div class="content">
        <!-- 提交工单表单 -->
        <div v-if="activeMenu === 'submit'" class="submit-form">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <h2>提交工单</h2>
                <p class="subtitle">请详细描述您的问题，我们会尽快处理</p>
              </div>
            </template>
            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-width="100px"
              class="help-form"
            >
              <el-form-item label="标题" prop="title">
                <el-input
                  v-model="form.title"
                  placeholder="请输入工单标题"
                  maxlength="50"
                  show-word-limit
                />
              </el-form-item>
              <el-form-item label="优先级" prop="priority">
                <el-radio-group v-model="form.priority">
                  <el-radio :label="0">低</el-radio>
                  <el-radio :label="1">中</el-radio>
                  <el-radio :label="2">高</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="问题描述" prop="content">
                <el-input
                  v-model="form.content"
                  type="textarea"
                  :rows="6"
                  placeholder="请详细描述您遇到的问题..."
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitForm">提交工单</el-button>
                <el-button @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>

        <!-- 我的工单列表 -->
        <div v-if="activeMenu === 'my-tickets'" class="ticket-list">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <h2>我的工单</h2>
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索工单"
                  prefix-icon="Search"
                  clearable
                  @input="handleSearch"
                  style="width: 200px"
                />
              </div>
            </template>
            <el-table :data="tickets" style="width: 100%">
              <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
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
              <el-table-column prop="createdAt" label="创建时间" width="180">
                <template #default="scope">
                  <span>{{ formatTime(scope.row.createdAt) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="scope">
                  <el-button type="primary" link @click="viewTicket(scope.row)">
                    查看详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>

        <!-- 常见问题 -->
        <div v-if="activeMenu === 'faq'" class="faq-section">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <h2>常见问题</h2>
              </div>
            </template>
            <el-collapse>
              <el-collapse-item
                v-for="(faq, index) in faqs"
                :key="index"
                :title="faq.question"
                :name="index"
              >
                <div class="faq-answer">{{ faq.answer }}</div>
              </el-collapse-item>
            </el-collapse>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 工单详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="工单详情"
      width="600px"
      destroy-on-close
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="工单标题">{{ currentTicket.title }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ currentTicket.content }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityType(currentTicket.priority)">
            {{ getPriorityText(currentTicket.priority) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentTicket.status)">
            {{ getStatusText(currentTicket.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatTime(currentTicket.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="回复内容">
          {{ currentTicket.reply || '暂无回复' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理人">
          {{ currentTicket.repliedBy || '暂无回复' }}
        </el-descriptions-item>
        <el-descriptions-item label="回复时间">
          {{ currentTicket.repliedAt ? formatTime(currentTicket.repliedAt) : '暂无回复' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Plus, Document, QuestionFilled, Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';
import dayjs from 'dayjs';

const activeMenu = ref('submit');
const formRef = ref();
const dialogVisible = ref(false);
const currentTicket = ref({});
const searchKeyword = ref('');
const tickets = ref([]);

// 表单数据
const form = reactive({
  title: '',
  content: '',
  priority: 0
});

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入工单标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入问题描述', trigger: 'blur' },
    { min: 10, max: 500, message: '长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ]
};

// 常见问题数据
const faqs = [
  {
    question: '如何修改个人信息？',
    answer: '您可以在个人中心页面点击"编辑资料"按钮来修改您的个人信息。'
  },
  {
    question: '如何预约课程？',
    answer: '在课程列表页面选择您感兴趣的课程，点击"立即预约"按钮，选择合适的时间段即可完成预约。'
  },
  {
    question: '如何取消预约？',
    answer: '在"我的预约"页面找到需要取消的预约，点击"取消预约"按钮即可。请注意，取消预约可能会影响您的信用评分。'
  }
];

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日HH时mm分ss秒');
};

// 获取优先级类型
const getPriorityType = (priority) => {
  const types = ['info', 'warning', 'danger'];
  return types[priority] || 'info';
};

// 获取优先级文本
const getPriorityText = (priority) => {
  const texts = ['低', '中', '高'];
  return texts[priority] || '未知';
};

// 获取状态类型
const getStatusType = (status) => {
  const types = ['info', 'warning', 'success'];
  return types[status] || 'info';
};

// 获取状态文本
const getStatusText = (status) => {
  const texts = ['新建', '处理中', '已解决'];
  return texts[status] || '未知';
};

// 处理菜单选择
const handleMenuSelect = (key) => {
  activeMenu.value = key;
  if (key === 'my-tickets') {
    loadTickets();
  }
};

// 提交工单
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const userNo = JSON.parse(localStorage.getItem('tutor-user')).userNo;
      const submitData = {
        ...form,
        userNo,
        status: 0
      };

      request.post('/help/add', submitData).then((res) => {
        if (res.code === '200') {
          ElMessage.success('工单提交成功');
          resetForm();
          activeMenu.value = 'my-tickets';
          loadTickets();
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  });
};

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields();
};

// 加载工单列表
const loadTickets = () => {
  const userNo = JSON.parse(localStorage.getItem('tutor-user')).userNo;
  request.get('/help/selectAll', {
    params: { userNo }
  }).then((res) => {
    tickets.value = res.data;
  });
};

// 搜索工单
const handleSearch = () => {
  if (searchKeyword.value) {
    request.get('/help/search', {
      params: { keyword: searchKeyword.value }
    }).then((res) => {
      tickets.value = res.data;
    });
  } else {
    loadTickets();
  }
};

// 查看工单详情
const viewTicket = (ticket) => {
  currentTicket.value = ticket;
  dialogVisible.value = true;
};

onMounted(() => {
  if (activeMenu.value === 'my-tickets') {
    loadTickets();
  }
});
</script>

<style scoped>
.help-center {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.banner {
  background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%);
  color: white;
  padding: 60px 0;
  text-align: center;
}

.banner-content h1 {
  font-size: 2.5em;
  margin: 0;
  margin-bottom: 10px;
}

.banner-content p {
  font-size: 1.2em;
  margin: 0;
  opacity: 0.9;
}

.main-content {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  display: flex;
  gap: 20px;
}

.sidebar {
  width: 200px;
  flex-shrink: 0;
}

.help-menu {
  border-radius: 8px;
  overflow: hidden;
}

.content {
  flex: 1;
  min-width: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 1.5em;
  color: var(--el-text-color-primary);
}

.subtitle {
  color: var(--el-text-color-secondary);
  margin: 5px 0 0;
}

.help-form {
  max-width: 800px;
  margin: 0 auto;
}

.faq-answer {
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.el-card {
  border-radius: 8px;
  margin-bottom: 20px;
}

:deep(.el-collapse-item__header) {
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-collapse-item__content) {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}
</style>