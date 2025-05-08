<template>
  <div class="schedule-container">
    <!-- 未登录提示 -->
    <div v-if="!data.user" class="login-tip">
      <el-empty description="请先登录">
        <el-button type="primary" @click="router.push('/login')">去登录</el-button>
      </el-empty>
    </div>
    
    <!-- 已登录内容 -->
    <template v-if="data.user">
      <!-- 教师信息卡片 -->
      <el-card v-if="data.teacherInfo" class="teacher-info-card" shadow="hover">
        <div class="teacher-info">
          <el-avatar :size="60" :src="data.teacherInfo.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="teacher-details">
            <h3>{{ data.teacherInfo.name }}</h3>
            <p>{{ data.teacherInfo.subject }}</p>
          </div>
    </div>
      </el-card>

      <!-- 日历视图 -->
      <router-view :appointments="data.appointments" @eventClick="showAppointmentDetail" @dateClick="showAddAppointment" />
    
    <!-- 列表视图 -->
    <div class="list-view">
      <div class="view-header">
        <h3>课程安排</h3>
        <div class="view-controls">
            <el-button type="primary" @click="refreshSchedule">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
            <template v-if="data.user.role === 'TEA'">
              <el-button type="primary" @click="showAddAppointment()">
                <el-icon><Plus /></el-icon> 新增预约
              </el-button>
            </template>
          </div>
        </div>
        <el-table :data="filteredAppointments"
                  style="width: 100%">
          <el-table-column prop="date" label="日期" width="130">
          <template #default="{ row }">
              {{ formatDate(row.date) }}
          </template>
        </el-table-column>
          <el-table-column prop="time" label="时间" width="200">
          <template #default="{ row }">
            {{ row.startTime }}-{{ row.endTime }}
          </template>
        </el-table-column>
          <el-table-column prop="courseTitle" label="课程" width="300"/>
          <el-table-column prop="studentName" label="预约人" width="140"  />
          <el-table-column prop="teacherName" label="教师" width="140"  />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
              <!-- 如果是学生身份才显示操作 -->
              <template v-if="data.user.role === 'STU'">
                <!-- 预约成功，显示“请支付 + 取消” -->
                <template v-if="row.status === 'booked'">
                  <el-button
                      type="warning"
                      size="small"
                      @click="handlePay(row)"
                  >支付</el-button>
                  <el-button
                      type="danger"
                      size="small"
                      @click="handleCancel(row)"
                  >取消</el-button>
                </template>

                <!-- 如果被取消，显示灰色标签 -->
                <template v-else-if="row.status === 'cancelled'">
                  <el-tag type="info">已取消</el-tag>
                </template>

                <!-- 其他状态，比如 available 或自定义 -->
                <template v-else>
                  <el-button size="small" @click="handleCancel(row)">取消</el-button>
                </template>
              </template>
          </template>
        </el-table-column>

      </el-table>
      </div>

      <!-- 预约详情弹窗 -->
      <el-dialog
          v-model="data.dialogVisible"
          :title="data.selectedDate ? formatDate(data.selectedDate) : '预约详情'"
          width="680px"
          destroy-on-close
      >
        <el-table :data="data.selectedAppointments" style="width: 100%">
          <el-table-column prop="courseTitle" label="课程" width="100" />
          <el-table-column prop="startTime" label="开始时间" width="100" />
          <el-table-column prop="endTime" label="结束时间" width="100" />
          <el-table-column prop="studentName" label="预约人" width="80"  />
          <el-table-column prop="teacherName" label="教师" width="80"  />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <!-- 只有学生角色才能看到预约按钮 -->
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <template v-if="data.user.role === 'STU'">
                <el-button
                    v-if="row.status === 'available'"
                    type="primary"
                    @click="() => handleAppointment(row)"
                >点此预约</el-button>

                <el-button
                    v-else-if="row.status === 'booked'"
                    type="warning"
                    @click="handlePay(row)"
                >请支付</el-button>

                <el-tag v-else type="info">不可操作</el-tag>
              </template>
            </template>
          </el-table-column>

        </el-table>
      </el-dialog>


      <!-- 新增预约弹窗 -->
      <el-dialog
          v-model="data.addDialogVisible"
          title="新增预约"
          width="500px"
          destroy-on-close
      >
        <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="100px">
          <el-form-item label="课程" prop="courseId">
            <el-select v-model="data.form.courseId" placeholder="请选择课程" style="width: 100%">
              <el-option
                  v-for="course in data.courseList"
                  :key="course.id"
                  :label="course.title"
                  :value="course.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="日期" prop="date">
            <el-date-picker
                v-model="data.form.date"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                :disabled-date="disabledDate"
            />
          </el-form-item>
          <el-form-item label="时间段" prop="timeRange">
            <el-time-picker
                v-model="data.form.timeRange"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                placeholder="选择时间范围"
                style="width: 100%"
                format="HH:mm"
                value-format="HH:mm"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="data.addDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleAddAppointment">确定</el-button>
    </div>
        </template>
      </el-dialog>

<!--      &lt;!&ndash; 支付按钮 &ndash;&gt;-->
<!--      <div v-if="data.showPaymentButton && data.currentAppointment" class="payment-section">-->
<!--        <el-button type="primary" @click="handlePayment">-->
<!--          立即支付-->
<!--        </el-button>-->
<!--      </div>-->
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Refresh, Calendar, User } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const formRef = ref()

// 使用reactive管理状态
const data = reactive({
  user: JSON.parse(localStorage.getItem('tutor-user')),
  course:{},
  viewMode: 'month',
  currentDate: new Date(),
  appointments: [],
  dialogVisible: false,
  addDialogVisible: false,
  selectedDate: null,
  selectedAppointments: [],
  courseList: [],
  teacherInfo: null,
  form: {
    courseId: null,
    date: null,
    timeRange: null,
    remark: ''
  },
  rules: {
    courseId: [
      {required: true, message: '请选择课程', trigger: 'change'}
    ],
    date: [
      {required: true, message: '请选择日期', trigger: 'change'}
    ],
    timeRange: [
      {required: true, message: '请选择时间段', trigger: 'change'}
    ]
  },
  timeSlots: [
    '08:00', '09:00', '10:00', '11:00', '12:00',
    '13:00', '14:00', '15:00', '16:00', '17:00',
    '18:00', '19:00', '20:00'
  ],
  showPaymentButton: false,
  currentAppointment: null
})

// 获取当前周的日期
const weekDays = computed(() => {
  const startOfWeek = dayjs(data.currentDate).startOf('week')
  return Array.from({length: 7}, (_, i) => {
    const date = startOfWeek.add(i, 'day')
    return {
      date: date.format('YYYY-MM-DD'),
      weekday: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.day()]
    }
  })
})

// 加载教师信息
const loadTeacherInfo = async (teacherId) => {
  try {
    const res = await request.get(`/teacher/selectById/${teacherId}`)
    if (res.code === '200') {
      data.teacherInfo = res.data
    }
  } catch (error) {
    console.error('获取教师信息失败:', error)
  }
}

// 加载预约数据
const loadAppointments = async () => {
  if (!data.user) return

  try {
    let endpoint
    if (router.currentRoute.value.query.teacherId) {
      // 查看特定教师的预约
      endpoint = `/appointment/selectByTeacherId/${router.currentRoute.value.query.teacherId}`
  } else {
      // 查看自己的预约
      endpoint = data.user.role === 'TEA'
          ? `/appointment/selectByTeacherId/${data.user.id}`
          : `/appointment/selectByStudentId/${data.user.id}`
    }

    const res = await request.get(endpoint)
    if (res.code === '200') {
      data.appointments = res.data
    }
  } catch (error) {
    console.error('加载预约数据失败:', error)
  }
}

// 加载课程列表
const loadCourses = async () => {
  if (!data.user) return

  try {
    const endpoint = data.user.role === 'TEA'
        ? `/course/selectPageByTeacherId?teacherId=${data.user.id}&pageNum=1&pageSize=10`
        : `/course/selectAll`

    const res = await request.get(endpoint)
    if (res.code === '200') {
      data.courseList = res.data.list
    }
  } catch (error) {
    console.error('加载课程数据失败:', error)
  }
}


const showAppointmentDetail = (appointmentEvent) => {
  console.log('选中的预约信息:', appointmentEvent);

  // 提取 EventImpl 中的详细数据，并格式化时间
  const appointment = {
    id: appointmentEvent.id,
    courseTitle: appointmentEvent.title,  // 课程标题
    startTime: dayjs(appointmentEvent.start).format('YYYY-MM-DD HH:mm'),  // 格式化开始时间
    endTime: dayjs(appointmentEvent.end).format('YYYY-MM-DD HH:mm'),      // 格式化结束时间
    studentName: appointmentEvent.extendedProps.studentName,  // 学生姓名
    teacherName: appointmentEvent.extendedProps.teacherName,  // 教师姓名
    status: appointmentEvent.extendedProps.status // 状态

  };

  // 更新 selectedAppointments 数据
  data.selectedAppointments = [appointment];
  console.log('当前selectedAppointments:', data.selectedAppointments);

  data.selectedDate = dayjs(appointmentEvent.start).format('YYYY-MM-DD');  // 格式化选中日期
  data.dialogVisible = true;  // 打开弹窗
};

// 显示新增预约弹窗
const showAddAppointment = (date, timeSlot) => {
  data.form = {
    courseId: null,
    date: date,
    timeRange: timeSlot ? [timeSlot, timeSlot] : null,
    remark: ''
  }
  data.addDialogVisible = true
}


const getOrderInfoByAppointmentId = async (appointmentId) => {
  try {
    // 1. 查询 appointment 信息，获取 courseId 和 teacherId
    const appointmentRes = await request.get(`/appointment/selectById/${appointmentId}`);
    if (appointmentRes.code !== '200') {
      throw new Error('无法获取预约信息');
    }

    const { courseId, teacherId } = appointmentRes.data;

    // 2. 查询课程信息，获取价格
    const courseRes = await request.get(`/course/selectById/${courseId}`);
    if (courseRes.code !== '200') {
      throw new Error('无法获取课程信息');
    }

    const { price } = courseRes.data;

    return {
      courseId,
      teacherId,
      totalPrice: price || 0
    };
  } catch (err) {
    console.error('获取订单信息失败:', err);
    ElMessage.error('获取订单信息失败');
    return null;
  }
};

const handleAppointment = async () => {
  const appointmentId = data.selectedAppointments[0].id;
  const studentId = data.user.id;

  try {
    // 1. 先发起预约
    const res = await request.post(`/appointment/book/${appointmentId}?studentId=${studentId}`);
    if (res.code !== '200') {
      ElMessage.error(res.msg || '预约失败');
      return;
    }

    ElMessage.success('预约成功');
    data.dialogVisible = false;

    // 2. 获取补充信息：courseId、teacherId、price
    const orderInfo = await getOrderInfoByAppointmentId(appointmentId);
    if (!orderInfo) return;

    // 3. 构造订单并创建
    const orderData = {
      appointmentSlotId: appointmentId,
      studentId,
      ...orderInfo,
      status: '待支付',
      paymentMethod: '未选择'
    };

    const orderRes = await request.post('/order/add', orderData);
    if (orderRes.code === '200') {
      ElMessage.success('订单已创建，请尽快支付');
      loadAppointments();
    } else {
      ElMessage.error('订单创建失败: ' + orderRes.msg);
    }
  } catch (err) {
    console.error('处理预约时出错:', err);
    ElMessage.error('操作失败，请稍后再试');
  }
};


const handlePay = async () => {
  if (!data.user) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }

  try {
    // 获取当前预约 ID 和学生 ID
    const appointmentId = data.selectedAppointments[0].id;
    const studentId = data.user.id;

    // 根据预约 ID 和学生 ID 查询对应的订单
    const res = await request.get(`/order/selectByAppointment/${appointmentId}?studentId=${studentId}`);

    if (res.code === '200' && res.data && res.data.orderNo) {
      const orderNo = res.data.orderNo;
      // 调用支付宝支付接口
      window.open(`http://localhost:8080/alipay/pay?orderNo=${orderNo}`);
    } else {
      ElMessage.error(res.msg || '未找到订单，请确认已预约');
    }
  } catch (err) {
    console.error('发起支付失败:', err);
    ElMessage.error('发起支付失败，请稍后再试');
  }
};



// 刷新预约数据
const refreshSchedule = () => {
  loadAppointments()
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('MM月DD日')
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    available: 'info',
    booked: 'success',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

const handleCancel = async (appointment) => {
  try {
    await request.post(`/appointment/cancel/${appointment.id}`, null, {
      params: {
        accountId: data.user.id,
        role: data.user.role
      }
    })
    ElMessage.success('取消预约成功')
    loadAppointments()
    data.dialogVisible = false
  } catch (error) {
    ElMessage.error('取消预约失败')
  }
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 新增预约
const handleAddAppointment = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const appointment = {
          courseId: data.form.courseId,
          date: dayjs(data.form.date).format('YYYY-MM-DD'),
          startTime: data.form.timeRange[0],
          endTime: data.form.timeRange[1],
          studentId: data.user.role === 'STU' ? data.user.id : null,
          teacherId: data.user.role === 'TEA' ? data.user.id : null
        }

        await request.post('/appointment/add', appointment)
        ElMessage.success('预约成功')
        data.addDialogVisible = false
        loadAppointments()
      } catch (error) {
        ElMessage.error('预约失败')
      }
    }
  })
}

// 检查某天是否有预约
const hasAppointments = (date) => {
  return data.appointments.some(apt => apt.date === date)
}

// 获取某天的预约数量
const getAppointmentCount = (date) => {
  return data.appointments.filter(apt => apt.date === date).length
}

// 获取指定日期和时间的预约
const getAppointmentsByTime = (date, time) => {
  return data.appointments.filter(apt =>
      apt.date === date &&
      apt.startTime <= time &&
      apt.endTime > time
  )
}


// 获取预约样式类
const getAppointmentClass = (appointment) => {
  const classes = ['appointment']
  if (appointment.status === 'booked') {
    classes.push('booked')
  } else if (appointment.status === 'cancelled') {
    classes.push('cancelled')
  }
  return classes
}




const showDayDetail = (date) => {
  console.log('选中的日期:', date);
  data.selectedDate = date;
  data.selectedAppointments = data.appointments.filter(apt => apt.date === date);  // 筛选当天的预约
  console.log('当前当天的预约:', data.selectedAppointments);  // 输出当前的selectedAppointments
  data.dialogVisible = true;  // 打开弹窗
};


const filteredAppointments = computed(() => {
  if (data.user.role === 'TEA') {
    return data.appointments; // 老师看全部
  } else if (data.user.role === 'STU') {
    return data.appointments.filter(item => item.studentId === data.user.id); // 学生只看自己的
  } else {
    return []; // 其他角色默认空
  }
});


// 判断是否可以取消预约，并返回原因（如果有）
const getCancelReason = (appointment) => {
  if (!data.user) return '未登录'

  const now = dayjs()
  const appointmentTime = dayjs(`${appointment.date} ${appointment.startTime}`)
  const isFuture = appointmentTime.isAfter(now)

  if (!isFuture) return '已过预约时间'

  if (data.user.role === 'STU') {
    if (appointment.status !== 'booked') return '仅可取消已预约课程'
    if (data.user.id !== appointment.studentId) return '无权限取消此预约'
  }

  if (data.user.role === 'TEA') {
    if (data.user.id !== appointment.teacherId) return '非该教师发布'
    if (appointment.status !== 'available' && appointment.status !== 'booked') return '当前状态不可取消'
  }

  return null // 可以取消
}


// 检查是否可以取消预约
const canCancel = (appointment) => {
  if (!data.user) return false
  const now = dayjs()
  const appointmentTime = dayjs(`${appointment.date} ${appointment.startTime}`)

  // 预约时间必须还没开始
  const isFuture = appointmentTime.isAfter(now)

  // 学生可以取消自己预约的课程
  if (data.user.role === 'STU' && appointment.status === 'booked' && data.user.id === appointment.studentId && isFuture) {
    return true
  }

  // 教师可以取消自己发布的课程（available 或 booked 状态）
  if (data.user.role === 'TEA' && data.user.id === appointment.teacherId && isFuture) {
    return appointment.status === 'available' || appointment.status === 'booked'
  }

  return false
}


// 处理预约点击
const handleAppointmentClick = async (event) => {
  if (!data.user) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (data.user.role !== 'STU') {
    ElMessage.warning('只有学生可以预约课程')
    return
  }

  const appointment = {
    courseId: event.courseId,
    teacherId: event.teacherId,
    studentId: data.user.id,
    date: event.start,
    startTime: event.start,
    endTime: event.end,
    status: 'available'
  }

  try {
    const response = await request.post('/appointment/book', appointment)
    if (response.code === '200') {
      ElMessage.success('预约成功')
      // 刷新日历数据
      loadAppointments()
      // 显示支付按钮
      data.showPaymentButton = true
      data.currentAppointment = response.data
    } else {
      ElMessage.error(response.msg)
    }
  } catch (error) {
    ElMessage.error('预约失败，请稍后重试')
  }
}

// 处理支付
const handlePayment = async () => {
  if (!data.currentAppointment) {
    ElMessage.warning('请先预约课程');
    return;
  }

  const order = {
    appointmentId: data.currentAppointment.id,
    studentId: data.user.id,
    teacherId: data.currentAppointment.teacherId,
    courseId: data.currentAppointment.courseId,
    amount: data.currentAppointment.price,
    status: 'pending'
  };

  try {
    const response = await request.post('/order/insert', order);
    if (response.code === '200') {
      ElMessage.success('订单创建成功');
      // 跳转到支付页面
      router.push(`/user/payment/${response.data.data.id}`);
    } else {
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    ElMessage.error('创建订单失败，请稍后重试');
  }
};

// 初始化
onMounted(() => {
  if (data.user) {
    loadAppointments()
    loadCourses()
    // 如果是从教师详情页跳转过来的，加载教师信息
    if (router.currentRoute.value.query.teacherId) {
      loadTeacherInfo(router.currentRoute.value.query.teacherId)
    }
  }
})
</script>

<style scoped>
.schedule-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.login-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.view-switch {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.month-view {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.calendar-cell {
  height: 100%;
  padding: 4px;
  cursor: pointer;
  position: relative;
}

.calendar-cell p {
  height: 24px;
  margin: 0;
  text-align: center;
  line-height: 24px;
}

.calendar-cell p.has-appointments {
  color: var(--el-color-primary);
  font-weight: bold;
}

.appointment-count {
  font-size: 12px;
  color: var(--el-color-primary);
  text-align: center;
  margin-top: 4px;
}

.add-appointment {
  position: absolute;
  top: 4px;
  right: 4px;
  opacity: 0;
  transition: opacity 0.3s;
}

.calendar-cell:hover .add-appointment {
  opacity: 1;
}

.week-view {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.week-header {
  display: flex;
  border-bottom: 1px solid var(--el-border-color-light);
}

.time-column {
  width: 80px;
  padding: 10px;
  text-align: center;
  background-color: var(--el-fill-color-light);
  border-right: 1px solid var(--el-border-color-light);
}

.day-column {
  flex: 1;
  border-right: 1px solid var(--el-border-color-light);
}

.day-column:last-child {
  border-right: none;
}

.day-header {
  padding: 10px;
  text-align: center;
  background-color: var(--el-fill-color-light);
}

.date {
  font-weight: bold;
}

.weekday {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.week-body {
  display: flex;
  flex-direction: column;
}

.time-row {
  display: flex;
  min-height: 60px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.time-cell {
  width: 80px;
  padding: 10px;
  text-align: center;
  background-color: var(--el-fill-color-light);
  border-right: 1px solid var(--el-border-color-light);
}

.day-cell {
  flex: 1;
  padding: 4px;
  border-right: 1px solid var(--el-border-color-light);
  position: relative;
}

.day-cell:last-child {
  border-right: none;
}

.appointment-slot {
  position: absolute;
  left: 4px;
  right: 4px;
  padding: 4px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.appointment-slot:hover {
  transform: scale(1.02);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.appointment-slot.booked {
  background-color: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary-light-5);
}

.appointment-slot.cancelled {
  background-color: var(--el-color-danger-light-9);
  border: 1px solid var(--el-color-danger-light-5);
}

.appointment-info {
  font-size: 12px;
}

.appointment-info .title {
  font-weight: bold;
  margin-bottom: 2px;
}

.appointment-info .time {
  color: var(--el-text-color-secondary);
}

.empty-slot {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background-color: var(--el-fill-color-light);
  border: 1px dashed var(--el-border-color);
  border-radius: 4px;
  padding: 4px;
}

.empty-slot:hover {
  background-color: var(--el-fill-color);
  border-color: var(--el-color-primary);
}

.empty-slot-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: var(--el-text-color-secondary);
}

.empty-slot-content .el-icon {
  font-size: 16px;
}

.empty-slot:hover .empty-slot-content {
  color: var(--el-color-primary);
}

.list-view {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.view-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.view-header h3 {
  margin: 0;
  font-size: 18px;
  color: var(--el-text-color-primary);
}

.view-controls {
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .view-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .view-controls {
    width: 100%;
    justify-content: flex-end;
  }
}

.add-appointment-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 100;
}

.payment-section {
  margin-top: 20px;
  text-align: center;
}

.teacher-info-card {
  margin-bottom: 20px;
}

.teacher-info {
  display: flex;
    align-items: center;
  gap: 20px;
  padding: 20px;
}

.teacher-details h3 {
  margin: 0;
  font-size: 18px;
  color: var(--el-text-color-primary);
}

.teacher-details p {
  margin: 5px 0 0;
  color: var(--el-text-color-secondary);
}
</style> 