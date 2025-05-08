<template>
  <div class="schedule-calendar">
    <FullCalendar
      ref="calendarRef"
      :options="calendarOptions"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import zhLocale from '@fullcalendar/core/locales/zh-cn'

const props = defineProps({
  appointments: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits([
  'eventClick',
  'dateClick'
])

const calendarRef = ref()
// 计算属性：将预约数据转换为日历事件
const computedEvents = computed(() => {
  return props.appointments.map(apt => ({
    id: apt.id,
    title: apt.courseTitle,
    start: `${apt.date} ${apt.startTime}`,
    end: `${apt.date} ${apt.endTime}`,
    extendedProps: {
      status: apt.status,
      studentName: apt.studentName,
      teacherName: apt.teacherName
    }
  }))
})
// 日历配置
const calendarOptions = reactive({
  plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
  initialView: 'timeGridWeek',
  locale: zhLocale,
  headerToolbar: {
    left: 'prev,next today',
    center: 'title',
    right: 'dayGridMonth,timeGridWeek,timeGridDay'
  },
  buttonText: {
    today: '今天',
    month: '月',
    week: '周',
    day: '日'
  },
  allDaySlot: false,
  slotMinTime: '08:00:00',
  slotMaxTime: '22:00:00',
  slotDuration: '01:00:00',
  expandRows: true,
  height: 'auto',
  events: computedEvents,
  eventClick: (info) => {
    emit('eventClick', info.event)
  },
  dateClick: (info) => {
    emit('dateClick', info)
  },
  eventContent: (arg) => {
    return {
      html: `
        <div class="fc-event-content">
          <div class="fc-event-title">${arg.event.title}</div>
          <div class="fc-event-time">${arg.timeText}</div>
        </div>
      `
    }
  },
  eventClassNames: (arg) => {
    const classes = ['fc-event']
    if (arg.event.extendedProps.status === 'booked') {
      classes.push('fc-event-booked')
    } else if (arg.event.extendedProps.status === 'cancelled') {
      classes.push('fc-event-cancelled')
    }
    return classes
  }
})



// 切换视图
const changeView = (view) => {
  if (calendarRef.value) {
    calendarRef.value.getApi().changeView(view)
  }
}

// 更新事件
const updateEvents = (events) => {
  if (calendarRef.value) {
    calendarRef.value.getApi().removeAllEvents()
    calendarRef.value.getApi().addEventSource(events)
  }
}

// 获取当前视图
const getCurrentView = () => {
  if (calendarRef.value) {
    return calendarRef.value.getApi().view.type
  }
  return null
}

defineExpose({
  changeView,
  updateEvents,
  getCurrentView
})
</script>

<style scoped>
.schedule-calendar {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

:deep(.fc) {
  font-family: inherit;
}

:deep(.fc-event) {
  cursor: pointer;
  border: none;
  background-color: var(--el-color-primary);
  transition: all 0.3s;
}

:deep(.fc-event:hover) {
  transform: scale(1.02);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.fc-event-booked) {
  background-color: var(--el-color-success);
}

:deep(.fc-event-cancelled) {
  background-color: var(--el-color-danger);
}

:deep(.fc-event-content) {
  padding: 4px;
  color: #fff;
}

:deep(.fc-event-title) {
  font-weight: bold;
  margin-bottom: 2px;
}

:deep(.fc-event-time) {
  font-size: 12px;
  opacity: 0.8;
}

:deep(.fc-timegrid-slot) {
  height: 60px;
}

:deep(.fc-timegrid-slot-lane) {
  cursor: pointer;
}

:deep(.fc-timegrid-slot-lane:hover) {
  background-color: var(--el-fill-color-light);
}

:deep(.fc-timegrid-now-indicator-line) {
  border-color: var(--el-color-primary);
}

:deep(.fc-timegrid-now-indicator-arrow) {
  border-color: var(--el-color-primary);
}

/* 月视图样式 */
:deep(.fc-daygrid-day) {
  cursor: pointer;
}

:deep(.fc-daygrid-day:hover) {
  background-color: var(--el-fill-color-light);
}

:deep(.fc-daygrid-day-number) {
  font-weight: bold;
}

:deep(.fc-daygrid-day.fc-day-today) {
  background-color: var(--el-color-primary-light-9);
}

/* 周视图样式 */
:deep(.fc-timegrid-slot) {
  border-bottom: 1px solid var(--el-border-color-light);
}

:deep(.fc-timegrid-slot-lane) {
  position: relative;
}

:deep(.fc-timegrid-slot-lane::after) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--el-fill-color-light);
  opacity: 0.3;
  pointer-events: none;
}

/* 日视图样式 */
:deep(.fc-timegrid-axis) {
  font-weight: bold;
}

:deep(.fc-timegrid-slot-label) {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style> 