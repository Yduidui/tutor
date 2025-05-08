<template>
    <div class="chat-container">
      <div class="chat-wrapper">
        <!-- 聊天消息区域 -->
        <div class="chat-messages" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index"
               :class="['message', message.role]">
            <div class="message-avatar">
              <img v-if="message.role === 'user'" src="@/assets/logo.png" alt="用户">
              <img v-else src="@/assets/logo.png" alt="AI助手">
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
            </div>
          </div>
          <div v-if="loading" class="message assistant">
            <div class="message-avatar">
              <img src="@/assets/logo.png" alt="AI助手">
            </div>
            <div class="message-content">
              <div class="message-text">
                <span class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </span>
              </div>
            </div>
          </div>
        </div>
  
        <!-- 输入区域 -->
        <div class="chat-input-area">
          <div class="input-container">
            <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="3"
                placeholder="输入消息，Shift+Enter换行"
                @keyup.enter.native="handleKeyEnter"
                resize="none"
                class="message-textarea"
            />
            <div class="input-actions">
              <el-button
                  type="primary"
                  @click="sendMessage"
                  :loading="loading"
                  class="send-button"
                  round
              >
                <template #icon>
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="22" y1="2" x2="11" y2="13"></line>
                    <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                  </svg>
                </template>
                发送
              </el-button>
            </div>
          </div>
          <div class="input-footer">
            <span class="hint-text">AI助手可能会出错，请核实重要信息</span>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, nextTick } from 'vue'
  import { ElMessage } from 'element-plus'
  import request from '@/utils/request'
  
  const messages = ref([])
  const inputMessage = ref('')
  const loading = ref(false)
  const messagesContainer = ref(null)
  
  // 格式化消息内容（处理换行和链接）
  const formatMessage = (content) => {
    return content
        .replace(/\n/g, '<br>')
        .replace(/(https?:\/\/[^\s]+)/g, '<a href="$1" target="_blank">$1</a>')
  }
  
  // 处理回车键
  const handleKeyEnter = (e) => {
    if (e.shiftKey) {
      // Shift+Enter 换行
      inputMessage.value += '\n'
    } else {
      // 直接Enter发送
      sendMessage()
      e.preventDefault()
    }
  }
  
  // 发送消息
  const sendMessage = async () => {
    if (!inputMessage.value.trim()) {
      ElMessage.warning('请输入消息')
      return
    }
  
    // 添加用户消息
    messages.value.push({
      role: 'user',
      content: inputMessage.value
    })
  
    // 清空输入框
    const userMessage = inputMessage.value
    inputMessage.value = ''
    loading.value = true
  
    try {
      // 滚动到底部
      await nextTick()
      scrollToBottom()
  
      // 发送请求
      const res = await request.post('/chat/send', {
        messages: messages.value
      })
  
      if (res.code === '200') {
        // 添加AI回复
        messages.value.push({
          role: 'assistant',
          content: res.data
        })
      } else {
        ElMessage.error(res.msg || '发送失败')
      }
    } catch (error) {
      ElMessage.error('发送失败，请稍后重试')
    } finally {
      loading.value = false
      // 滚动到底部
      await nextTick()
      scrollToBottom()
    }
  }
  
  // 滚动到底部
  const scrollToBottom = () => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  }
  
  // 初始化欢迎消息
  onMounted(() => {
    messages.value.push({
      role: 'assistant',
      content: '你好！我是AI助手，有什么可以帮你的吗？'
    })
    scrollToBottom()
  })
  </script>
  
  <style scoped>
  .chat-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    background-color: #f7f7f8;
    padding: 0;
    margin: 0;
  }
  
  .chat-wrapper {
    display: flex;
    flex-direction: column;
    height: 100%;
    max-width: 900px;
    margin: 0 auto;
    width: 100%;
  }
  
  .chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 20px 20px 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .message {
    display: flex;
    gap: 16px;
    padding: 12px 0;
  }
  
  .message-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    overflow: hidden;
    flex-shrink: 0;
  }
  
  .message-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .message-content {
    flex: 1;
    max-width: calc(100% - 52px);
  }
  
  .message-text {
    line-height: 1.6;
    font-size: 15px;
    color: #374151;
    padding-top: 6px;
  }
  
  .message-text a {
    color: #2563eb;
    text-decoration: underline;
  }
  
  .message-text pre {
    background-color: #f3f4f6;
    padding: 12px;
    border-radius: 6px;
    overflow-x: auto;
  }
  
  .message-text code {
    font-family: 'Menlo', 'Monaco', 'Courier New', monospace;
    font-size: 14px;
  }
  
  .message.user {
    flex-direction: row-reverse;
  }
  
  .message.user .message-content {
    align-items: flex-end;
  }
  
  .message.user .message-text {
    background-color: #2563eb;
    color: white;
    padding: 10px 16px;
    border-radius: 18px 18px 4px 18px;
  }
  
  .message.assistant .message-text {
    background-color: white;
    padding: 10px 16px;
    border-radius: 18px 18px 18px 4px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  }
  
  .chat-input-area {
    padding: 12px 16px 24px;
    background-color: #f7f7f8;
    border-top: 1px solid #e5e7eb;
  }
  
  .input-container {
    position: relative;
    border-radius: 12px;
    background-color: white;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 8px;
  }
  
  .message-textarea {
    :deep(.el-textarea__inner) {
      border: none;
      box-shadow: none;
      resize: none;
      padding: 8px 12px;
      font-size: 15px;
      line-height: 1.5;
      min-height: 60px;
      max-height: 200px;
    }
  }
  
  .input-actions {
    display: flex;
    justify-content: flex-end;
    padding: 8px 4px 0;
  }
  
  .send-button {
    padding: 8px 16px;
    font-weight: 500;
    background-color: #2563eb;
    border: none;
  }
  
  .input-footer {
    text-align: center;
    margin-top: 12px;
  }
  
  .hint-text {
    font-size: 12px;
    color: #9ca3af;
  }
  
  .typing-indicator {
    display: inline-flex;
    align-items: center;
    height: 20px;
  }
  
  .typing-indicator span {
    width: 8px;
    height: 8px;
    margin: 0 2px;
    background-color: #9ca3af;
    border-radius: 50%;
    display: inline-block;
    animation: typing 1.4s infinite ease-in-out;
  }
  
  .typing-indicator span:nth-child(1) {
    animation-delay: 0s;
  }
  
  .typing-indicator span:nth-child(2) {
    animation-delay: 0.2s;
  }
  
  .typing-indicator span:nth-child(3) {
    animation-delay: 0.4s;
  }
  
  @keyframes typing {
    0%, 60%, 100% {
      transform: translateY(0);
    }
    30% {
      transform: translateY(-5px);
    }
  }
  </style>