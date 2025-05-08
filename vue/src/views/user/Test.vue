<!--<template>-->
<!--  <div class="chat-container">-->
<!--    <div class="chat-messages">-->
<!--      <div v-for="(msg, index) in messages" :key="index" class="message-item">-->
<!--        <strong>{{ msg.fromUser === userId ? '我' : '对方' }}:</strong> {{ msg.content }}-->
<!--      </div>-->
<!--    </div>-->

<!--    <div class="chat-input">-->
<!--      <el-input-->
<!--          v-model="inputText"-->
<!--          placeholder="请输入消息"-->
<!--          @keyup.enter="sendMessage"-->
<!--      />-->
<!--      <el-button type="primary" @click="sendMessage">发送</el-button>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref, onMounted, onBeforeUnmount } from 'vue';-->
<!--import { WebSocketManager } from '@/utils/WebSocketManager.js'; // 工具类路径注意大小写-->
<!--import { ElMessage } from 'element-plus';-->

<!--// 登录用户 ID（假设从 localStorage 获取）-->
<!--const user = JSON.parse(localStorage.getItem("tutor-user") || '{}');-->
<!--const userId = user.id;-->

<!--// 你聊天的对象 ID（暂时写死，后续你可以通过点击好友设置）-->
<!--const toUserId = 2; -->

<!--// 消息相关-->
<!--const messages = ref([]);-->
<!--const inputText = ref('');-->
<!--let wsManager = null;-->

<!--// 发送消息-->
<!--function sendMessage() {-->
<!--  const content = inputText.value.trim();-->
<!--  if (!content) return;-->

<!--  const msg = {-->
<!--    type: 'chat',-->
<!--    fromUser: userId,-->
<!--    toUser: toUserId,-->
<!--    content: content,-->
<!--    timestamp: new Date(),-->
<!--  };-->

<!--  // 本地展示消息-->
<!--  messages.value.push(msg);-->

<!--  // 通过 WebSocket 发送-->
<!--  wsManager.sendMessage(msg);-->

<!--  // 清空输入框-->
<!--  inputText.value = '';-->
<!--}-->

<!--// 页面加载时建立连接-->
<!--onMounted(() => {-->
<!--  if (!userId) {-->
<!--    ElMessage.error('请先登录');-->
<!--    return;-->
<!--  }-->

<!--  wsManager = new WebSocketManager(userId);-->

<!--  wsManager.addMessageHandler((msg) => {-->
<!--    // 只显示发给我的消息-->
<!--    if (msg.fromUser === toUserId) {-->
<!--      messages.value.push(msg);-->
<!--    }-->
<!--  });-->
<!--});-->

<!--// 页面卸载时断开连接-->
<!--onBeforeUnmount(() => {-->
<!--  wsManager?.disconnect();-->
<!--});-->
<!--</script>-->

<!--<style scoped>-->
<!--.chat-container {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  height: 100%;-->
<!--}-->
<!--.chat-messages {-->
<!--  flex: 1;-->
<!--  overflow-y: auto;-->
<!--  padding: 10px;-->
<!--  border: 1px solid #ccc;-->
<!--  margin-bottom: 10px;-->
<!--}-->
<!--.message-item {-->
<!--  margin-bottom: 8px;-->
<!--}-->
<!--.chat-input {-->
<!--  display: flex;-->
<!--  gap: 10px;-->
<!--}-->
<!--</style>-->
