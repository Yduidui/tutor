<template>
  <div class="message-center">
    <!-- 左侧好友列表侧边栏 -->
    <div class="sidebar" :class="{ 'is-collapse': isCollapse }">
      <div class="sidebar-header">
        <div class="title">好友</div>
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索好友"
            prefix-icon="el-icon-search"
            clearable
            @input="handleSearch"
          />
        </div>
        <div class="collapse-btn" @click="isCollapse = !isCollapse">
          <el-icon :class="{ 'is-reverse': isCollapse }">
            <arrow-right />
          </el-icon>
        </div>
      </div>

      <div class="sidebar-content">
        <!-- 搜索结果显示 -->
        <div v-if="searchResult.length > 0" class="search-result">
          <div v-for="user in searchResult" :key="user.userNo" class="friend-item">
            <el-avatar :size="40" :src="user.avatar || '/logo.png'" />
            <div class="friend-info">
              <div class="friend-name">{{ user.name }}</div>
              <div class="friend-detail">
                <span class="username">{{ user.username }}</span>
                <el-tag size="small" :type="user.role === 'STU' ? 'success' : 'warning'">
                  {{ user.role === 'STU' ? '学生' : '教师' }}
                </el-tag>
              </div>
            </div>
            <div class="friend-actions">
              <el-button
                v-if="!isFriend(user.userNo)"
                type="primary"
                size="small"
                :loading="user.loading"
                @click="handleAddFriend(user)"
              >
                添加好友
              </el-button>
              <el-tag v-else type="success" size="small">已是好友</el-tag>
            </div>
          </div>
        </div>

        <!-- 好友列表内容 -->
        <div v-else class="friend-list">
          <!-- 好友请求部分 -->
          <div class="friend-section">
            <div class="section-header" @click="toggleSection(sections.friendRequests)">
              <div class="header-left">
                <span>{{ sections.friendRequests.title }}</span>
                <el-badge v-if="sections.friendRequests.badge" :value="sections.friendRequests.badge" class="badge" />
                <span v-if="sections.friendRequests.count" class="friend-count">({{ sections.friendRequests.count }})</span>
              </div>
              <el-icon :class="['arrow', { 'is-reverse': sections.friendRequests.isExpanded }]">
                <arrow-right />
              </el-icon>
            </div>
            <div v-if="sections.friendRequests.isExpanded && friendInvites.length > 0" class="section-content expanded">
              <div v-for="invite in friendInvites" :key="invite.id" class="request-item">
                <el-avatar :size="40" :src="invite.avatar || '/logo.png'" />
                <div class="request-info">
                  <div class="request-name">{{ invite.name }}</div>
                  <div class="request-time">{{ formatTime(invite.createdAt) }}</div>
                </div>
                <div class="request-actions">
                  <el-button
                    v-if="invite.status === 'PENDING'"
                    type="success"
                    size="small"
                    @click="handleAcceptRequest(invite)"
                  >
                    接受
                  </el-button>
                  <el-button
                    v-if="invite.status === 'PENDING'"
                    type="danger"
                    size="small"
                    @click="handleRejectRequest(invite)"
                  >
                    拒绝
                  </el-button>
                  <el-tag v-else-if="invite.status === 'ACCEPTED'" type="success" size="small">
                    已接受
                  </el-tag>
                  <el-tag v-else type="info" size="small">已拒绝</el-tag>
                </div>
              </div>
              <div v-if="friendInvites.length === 0" class="empty-requests">
                <el-empty description="暂无好友请求" />
              </div>
            </div>
          </div>

          <!-- 我的工具部分 -->
          <div class="friend-section">
            <div class="section-header" @click="toggleSection(sections.tools)">
              <div class="header-left">
                <span>{{ sections.tools.title }}</span>
                <span v-if="sections.tools.count" class="friend-count">({{ sections.tools.count }})</span>
              </div>
            </div>
            <div v-if="sections.tools.isExpanded" class="section-content expanded">
              <div
                class="friend-item"
                @click="selectFriend({ name: '机器人助手', username: 'chatbot' })"
              >
                <el-avatar :size="40" :src="'/logo.png'" />
                <div class="friend-info">
                  <div class="friend-name">机器人助手</div>
                  <div class="friend-detail">
                    <span class="username">chatbot</span>
                    <el-tag size="small">助手</el-tag>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 好友列表部分 -->
          <div class="friend-section">
            <div class="section-header" @click="toggleSection(sections.friends)">
              <div class="header-left">
                <span>{{ sections.friends.title }}</span>
                <span v-if="sections.friends.count" class="friend-count">({{ sections.friends.count }})</span>
              </div>
            </div>
            <div v-if="sections.friends.isExpanded" class="section-content expanded">
              <div
                v-for="friend in friends"
                :key="friend.userNo"
                class="friend-item"
                :class="{ 'friend-item-active': selectedFriend?.userNo === friend.userNo }"
                @click="selectFriend(friend)"
              >
                <el-avatar :size="40" :src="friend.avatar || '/logo.png'" />
                <div class="friend-info">
                  <div class="friend-name">{{ friend.name }}</div>
                  <div class="friend-detail">
                    <span class="username">{{ friend.username }}</span>
                    <el-tag size="small" :type="friend.role === 'STU' ? 'success' : 'warning'">
                      {{ friend.role === 'STU' ? '学生' : '教师' }}
                    </el-tag>
                  </div>
                </div>
                <div class="friend-actions">
                  <el-button type="text" @click.stop="showFriendMenu(friend)">
                    <el-icon><more /></el-icon>
                  </el-button>
                </div>
              </div>
              <div v-if="friends.length === 0" class="empty-friends">
                <el-empty description="暂无好友" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-area">
      <ChatBot v-if="isChatBot" />
      <div v-else-if="selectedFriend" class="chat-container">
        <div class="chat-header">
          <div class="chat-title">
            <span>{{ selectedFriend.name }}</span>
            <span class="chat-subtitle">{{ selectedFriend.role === 'STU' ? '学生' : '教师' }}</span>
          </div>
        </div>
        <div class="chat-content">
          <div  v-for="(msg, index) in messages.slice().reverse()" :key="index"
               :class="['message-item', { 'message-self': msg.isSelf }]">
            <div class="message-avatar">
              <el-avatar :size="40" :src="msg.isSelf ? currentUser.avatar : selectedFriend.avatar || '/logo.png'" />
            </div>
            <div class="message-content">
              <div class="message-text">{{ msg.content }}</div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <div class="input-wrapper">
            <el-input
              v-model="messageInput"
              type="textarea"
              :rows="3"
              placeholder="输入消息..."
              @keyup.enter.native="sendMessage"
              class="message-textarea"
            />
            <div class="input-actions">
              <el-button type="primary" @click="sendMessage" class="send-button">
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
        </div>
      </div>
      <div v-else class="chat-placeholder">
        <el-empty description="选择一个好友开始聊天" />
      </div>
    </div>

    <!-- 好友操作菜单 -->
    <el-dialog
      v-model="showMenu"
      title="好友操作"
      width="300px"
      :close-on-click-modal="false"
    >
      <div class="friend-menu">
        <div class="menu-item" @click="handleDeleteFriend">
          <el-icon><delete /></el-icon>
          <span>删除好友</span>
        </div>
        <div class="menu-item" @click="handleBlockFriend">
          <el-icon><warning /></el-icon>
          <span>拉黑好友</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowRight, More, Delete, Warning } from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import dayjs from 'dayjs'
import {WebSocketManager} from '@/utils/WebSocketManager.js'
import ChatBot from './ChatBot.vue'

// 数据
const searchKeyword = ref('')
const searchResult = ref([])
const friends = ref([])
const friendInvites = ref([])
const loading = ref(false)
const showFriendRequests = ref(true)
const showMenu = ref(false)
const currentUser = JSON.parse(localStorage.getItem('tutor-user'))
const selectedFriend = ref(null)
const messages = ref([])
const messageInput = ref('')
const webSocketManager = ref(null)
const isCollapse = ref(false)
const isChatBot = ref(false)

// 计算属性
const unreadRequests = computed(() => {
  return friendInvites.value.filter(req => req.status === 'PENDING').length
})

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 检查是否是好友
const isFriend = (userNo) => {
  return friends.value.some(friend => friend.userNo === userNo)
}

// 搜索处理
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    searchResult.value = []
    return
  }

  try {
    loading.value = true
    // 同时搜索学生和教师
    const [studentRes, teacherRes] = await Promise.all([
      request.get('/student/search', { params: { keyword: searchKeyword.value } }),
      request.get('/teacher/search', { params: { keyword: searchKeyword.value } })
    ])

    const results = []
    if (studentRes.code === '200' && studentRes.data) {
      results.push(...studentRes.data.map(item => ({ ...item, role: 'STU' })))
    }
    if (teacherRes.code === '200' && teacherRes.data) {
      results.push(...teacherRes.data.map(item => ({ ...item, role: 'TEA' })))
    }

    // 过滤掉自己
    searchResult.value = results.filter(user => user.userNo !== currentUser.userNo)
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 加载好友列表
const loadFriends = async () => {
  try {
    loading.value = true
    // 获取好友列表
    const res = await request.get('/friendship/list', {
      params: { userNo: currentUser.userNo }
    })

    if (res.code === '200') {
      const friendshipList = res.data
      // 遍历每个好友，查询学生或教师信息
      const friendsInfo = []

      for (const friendship of friendshipList) {
        const { friendNo } = friendship

        // 首先查询学生信息
        let friend = null
        let studentRes = await request.get('/student/search', { params: { keyword: friendNo } })
        if (studentRes.code === '200' && studentRes.data.length > 0) {
          friend = { ...studentRes.data[0], role: 'STU' } // 设置角色为学生
        } else {
          // 如果未查询到学生，尝试查询教师信息
          let teacherRes = await request.get('/teacher/search', { params: { keyword: friendNo } })
          if (teacherRes.code === '200' && teacherRes.data.length > 0) {
            friend = { ...teacherRes.data[0], role: 'TEA' } // 设置角色为教师
          }
        }

        // 如果找到了好友信息，添加到结果列表
        if (friend) {
          friendsInfo.push(friend)
        }
      }

      // 将查询到的好友信息赋值
      friends.value = friendsInfo
    }
  } catch (error) {
    ElMessage.error('加载好友列表失败')
  } finally {
    loading.value = false
  }
}


// 加载好友请求
const loadFriendRequests = async () => {
  try {
    loading.value = true
    const res = await request.get('/friendship/pending', {
      params: { userNo: currentUser.userNo }
    })
    if (res.code === '200') {
      friendInvites.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载好友请求失败')
  } finally {
    loading.value = false
  }
}

// 添加好友
const handleAddFriend = async (user) => {
  try {
    user.loading = true
    // 创建发送方的好友关系记录
    const senderFriendship = {
      userNo: currentUser.userNo,
      friendNo: user.userNo,
      userRole: currentUser.role,
      friendRole: user.role,
      status: 'PENDING'
    }

    // 创建接收方的好友关系记录
    const receiverFriendship = {
      userNo: user.userNo,
      friendNo: currentUser.userNo,
      userRole: user.role,
      friendRole: currentUser.role,
      status: 'PENDING'
    }

    // 同时创建两条记录
    await Promise.all([
      request.post('/friendship/add', senderFriendship),
      request.post('/friendship/add', receiverFriendship)
    ])

    ElMessage.success('好友请求已发送')
    // 更新搜索结果的按钮状态
    const index = searchResult.value.findIndex(u => u.userNo === user.userNo)
    if (index !== -1) {
      searchResult.value[index].friendStatus = 'PENDING'
    }
  } catch (error) {
    console.error('添加好友失败:', error)
    ElMessage.error('发送失败')
  } finally {
    user.loading = false
  }
}

// 接受好友请求
const handleAcceptRequest = async (invite) => {
  try {
    console.log("好友申请数据：" + JSON.stringify(invite))
    
    // 接受好友请求
    const res = await request.post(`/friendship/accept/${invite.id}`)
    if (res.code === '200') {
      ElMessage.success('已接受好友请求')
      // 重新加载好友列表和请求列表
      await Promise.all([
        loadFriendRequests(),
        loadFriends()
      ])
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('接受好友请求失败:', error)
    ElMessage.error('操作失败')
  }
}

// 拒绝好友请求
const handleRejectRequest = async (invite) => {
  try {
    const res = await request.post(`/friendship/reject/${invite.id}`)
    if (res.code === '200') {
      ElMessage.success('已拒绝好友请求')
      await loadFriendRequests()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 显示好友菜单
const showFriendMenu = (friend) => {
  selectedFriend.value = friend
  console.log("-----------------------------------")
  console.log("好友的数据：" + JSON.stringify(selectedFriend.value))
  showMenu.value = true
}

// 删除好友
const handleDeleteFriend = async () => {
  if (!selectedFriend.value || !selectedFriend.value.userNo) return

  try {
    await ElMessageBox.confirm('确定要删除该好友吗？', '提示', {
      type: 'warning'
    })
console.log("当前userNo:" + currentUser.userNo + "------" + "选中好友userNo:" + selectedFriend.value.userNo)
    // 使用 userNo 和 friendNo 来删除好友
    const res = await request.delete(`/friendship/deleteByUserAndFriend`, {
      params: {
        userNo: currentUser.userNo, // 当前用户的 userNo
        friendNo: selectedFriend.value.userNo // 选择的好友的 userNo
      }
    })

    if (res.code === '200') {
      ElMessage.success('删除成功')
      await loadFriends()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  } finally {
    showMenu.value = false
    selectedFriend.value = null
  }
}


// 拉黑好友
const handleBlockFriend = async () => {
  if (!selectedFriend.value) return

  try {
    await ElMessageBox.confirm('确定要拉黑该好友吗？', '提示', {
      type: 'warning'
    })

    const res = await request.post('/friendship/block', {
      userNo: currentUser.userNo,
      friendNo: selectedFriend.value.userNo
    })

    if (res.code === '200') {
      ElMessage.success('拉黑成功')
      await loadFriends()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  } finally {
    showMenu.value = false
    selectedFriend.value = null
  }
}

// 切换好友请求显示
const toggleFriendRequests = () => {
  if (friendInvites.value.length > 0) {
    showFriendRequests.value = !showFriendRequests.value
  }
}

// 选择好友（预留聊天功能）
const selectFriend = async (friend) => {
  if (friend.name === '机器人助手') {
    isChatBot.value = true
    selectedFriend.value = null
  } else {
    isChatBot.value = false
    selectedFriend.value = friend
    messages.value = []
    await loadHistoryMessages()
  }
}

// 添加WebSocket消息处理函数
const handleWebSocketMessage = (message) => {
  console.log('收到消息:', message)
  if (message.type === 'chat') {
    messages.value.push({
      fromUserNo: message.from,
      content: message.content,
      createTime: new Date(),
      isSelf: message.from === currentUser.userNo
    })
    // 滚动到底部
    setTimeout(() => {
      const chatContent = document.querySelector('.chat-content')
      if (chatContent) {
        chatContent.scrollTop = chatContent.scrollHeight
      }
    }, 100)
  }
}

// 修改initWebSocket函数
const initWebSocket = async () => {
  if (!currentUser.userNo) return
  
  try {
    // 确保旧的连接已关闭
    if (webSocketManager.value) {
      webSocketManager.value.disconnect()
    }
    
    webSocketManager.value = new WebSocketManager(currentUser.userNo)
    webSocketManager.value.addMessageHandler(handleWebSocketMessage)
    console.log('WebSocket初始化完成')
  } catch (error) {
    console.error('WebSocket初始化失败:', error)
    ElMessage.error('连接服务器失败，请刷新页面重试')
  }
}

// 修改sendMessage函数
const sendMessage = async () => {
  if (!messageInput.value.trim() || !selectedFriend.value) return

  try {
    // 检查WebSocket连接状态
    if (!webSocketManager.value || !webSocketManager.value.ws || webSocketManager.value.ws.readyState !== WebSocket.OPEN) {
      await initWebSocket()
      // 等待连接建立
      await new Promise(resolve => setTimeout(resolve, 1000))
    }

    const message = {
      type: 'chat',
      from: currentUser.userNo,
      to: selectedFriend.value.userNo,
      content: messageInput.value
    }

    // 发送WebSocket消息
    webSocketManager.value.sendMessage(message)

    // 保存消息到数据库
    await request.post('/message/send', {
      fromUserNo: currentUser.userNo,
      toUserNo: selectedFriend.value.userNo,
      content: messageInput.value
    })

    // 添加到本地消息列表
    messages.value.push({
      fromUserNo: currentUser.userNo,
      content: messageInput.value,
      createTime: new Date(),
      isSelf: true
    })

    // 清空输入框
    messageInput.value = ''
    
    // 滚动到底部
    setTimeout(() => {
      const chatContent = document.querySelector('.chat-content')
      if (chatContent) {
        chatContent.scrollTop = chatContent.scrollHeight
      }
    }, 100)
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送失败，请重试')
  }
}

// 加载历史消息
const loadHistoryMessages = async () => {
  if (!selectedFriend.value) return

  try {
    const res = await request.get('/message/history', {
      params: {
        userNo: currentUser.userNo,
        friendNo: selectedFriend.value.userNo,
        limit: 50
      }
    })

    if (res.code === '200' && res.data) {
      messages.value = res.data.map(msg => ({
        ...msg,
        isSelf: msg.fromUserNo === currentUser.userNo
      }))
      
      // 滚动到底部
      setTimeout(() => {
        const chatContent = document.querySelector('.chat-content')
        if (chatContent) {
          chatContent.scrollTop = chatContent.scrollHeight
        }
      }, 100)
    }
  } catch (error) {
    console.error('加载历史消息失败:', error)
  }
}

// 添加section管理
const sections = ref({
  friendRequests: {
    title: '新的朋友',
    badge: unreadRequests,
    isExpanded: showFriendRequests
  },
  tools: {
    title: '我的工具',
    count: 1,
    isExpanded: true
  },
  friends: {
    title: '我的好友',
    count: friends.value.length,
    isExpanded: true
  }
})

// 修改toggleSection函数
const toggleSection = (section) => {
  section.isExpanded = !section.isExpanded
  if (section.title === '新的朋友') {
    showFriendRequests.value = section.isExpanded
  }
}

// 修改onMounted函数
onMounted(async () => {
  try {
    await Promise.all([
      loadFriends(),
      loadFriendRequests()
    ])
    await initWebSocket()
  } catch (error) {
    console.error('初始化失败:', error)
    ElMessage.error('初始化失败，请刷新页面重试')
  }
})

// 修改onUnmounted函数
onUnmounted(() => {
  if (webSocketManager.value) {
    webSocketManager.value.disconnect()
  }
})
</script>

<style scoped>
.message-center {
  height: 100vh;
  display: flex;
  background-color: #f5f5f5;
}

.sidebar {
  width: 300px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: white;
  border-right: 1px solid #e6e6e6;
  transition: all 0.3s ease;
}

.sidebar.is-collapse {
  width: 80px;
}

.sidebar.is-collapse .title,
.sidebar.is-collapse .search-box,
.sidebar.is-collapse .friend-info,
.sidebar.is-collapse .friend-actions,
.sidebar.is-collapse .request-info,
.sidebar.is-collapse .request-actions,
.sidebar.is-collapse .friend-count,
.sidebar.is-collapse .username,
.sidebar.is-collapse .friend-detail {
  display: none;
}

.sidebar.is-collapse .section-header {
  justify-content: center;
  padding: 12px 0;
}

.sidebar.is-collapse .friend-item,
.sidebar.is-collapse .request-item {
  justify-content: center;
  padding: 12px 0;
}

.sidebar.is-collapse .el-avatar {
  margin: 0;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e6e6e6;
  background-color: white;
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: relative;
}

.collapse-btn {
  position: absolute;
  right: 16px;
  top: 16px;
  cursor: pointer;
  transition: transform 0.3s;
}

.collapse-btn.is-reverse {
  transform: rotate(180deg);
}

.sidebar.is-collapse .collapse-btn {
  right: 50%;
  transform: translateX(50%);
}

.sidebar.is-collapse .collapse-btn.is-reverse {
  transform: translateX(50%) rotate(180deg);
}

.chat-area {
  flex: 1;
  background-color: white;
  display: flex;
  flex-direction: column;
  border-left: 1px solid #e6e6e6;
}

.chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-header {
  height: 60px;
  padding: 0 20px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  background-color: white;
}

.chat-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.chat-subtitle {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  gap: 12px;
  max-width: 70%;
}

.message-self {
  flex-direction: row-reverse;
  margin-left: auto;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-text {
  padding: 12px 16px;
  background-color: #f5f5f5;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message-self .message-text {
  background-color: #e6f7ff;
}

.message-time {
  font-size: 12px;
  color: #999;
  text-align: right;
}

.chat-input {
  padding: 12px 16px;
  background-color: white;
  border-top: 1px solid #e5e7eb;
  position: relative;
  z-index: 1;
}

.input-wrapper {
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
  border-radius: 20px;
}

.friend-section {
  background-color: white;
  margin-bottom: 1px;
}

.section-header {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
  transition: background-color 0.3s;
}

.section-header:hover {
  background-color: #f5f5f5;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header .arrow {
  transition: transform 0.3s;
  font-size: 16px;
  color: #999;
}

.section-header .arrow.is-reverse {
  transform: rotate(90deg);
}

.section-content {
  background-color: white;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease-out;
}

.section-content.expanded {
  max-height: 500px;
}

.friend-item, .request-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  transition: all 0.3s;
  border-bottom: 1px solid #f5f5f5;
}

.friend-item:hover {
  background-color: #f5f5f5;
}

.friend-item-active {
  background-color: #e6f7ff;
}

.friend-info, .request-info {
  margin-left: 12px;
  flex: 1;
  min-width: 0;
}

.friend-name, .request-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.friend-detail {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.request-time {
  font-size: 12px;
  color: #999;
}

.friend-actions, .request-actions {
  margin-left: 12px;
}

.badge {
  margin-left: 4px;
}

.friend-count {
  color: #999;
  font-size: 12px;
}

.empty-requests, .empty-friends {
  padding: 32px 0;
  text-align: center;
}
</style>
