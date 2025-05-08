<template>
  <div class="teacher-data-center">
    <!-- 欢迎区域 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1>欢迎回来，{{ data.user.name }}</h1>
          <p class="welcome-subtitle">今天是 {{ currentDate }}，祝您工作愉快！</p>
        </div>
        <div class="welcome-avatar">
          <el-avatar :size="80" :src="data.user.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>数据统计</span>
            </div>
          </template>

          <div class="data-overview">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="data-item">
                  <div class="data-title">平均评分</div>
                  <div class="data-value">{{ averageRating }}</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="data-item">
                  <div class="data-title">评价总数</div>
                  <div class="data-value">{{ totalComments }}</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="data-item">
                  <div class="data-title">订单总数</div>
                  <div class="data-value">{{ totalAppointments }}</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="data-item">
                  <div class="data-title">课程总数</div>
                  <div class="data-value">{{ totalCourses }}</div>
                </div>
              </el-col>
            </el-row>
          </div>

          <div class="charts-container">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="chart-item">
                  <div class="chart-title">评分分布</div>
                  <div id="bar" class="chart"></div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="chart-item">
                  <!-- 顶部横向布局：标题 + 选择框 -->
                  <div class="chart-header">
                    <div class="chart-title" >订单趋势</div>
                    <!-- 时间粒度选择框 -->
                    <el-select v-model="timeRange" @change="updateTimeRange(timeRange)" placeholder="选择时间粒度" style="width: 150px;">
                      <el-option label="按年" value="year"></el-option>
                      <el-option label="按月" value="month"></el-option>
                      <el-option label="按周" value="week"></el-option>
                      <el-option label="按天" value="day"></el-option>
                    </el-select>
                  </div>
                  <!-- 图表容器 -->
                  <div id="line" class="chart"></div>
                </div>
              </el-col>


            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted, onUnmounted, reactive, computed} from 'vue'
import { ElMessage} from 'element-plus'
import request from '@/utils/request'
import * as echarts from 'echarts'
import {User} from "@element-plus/icons-vue";

const data = reactive({
  user: JSON.parse(localStorage.getItem('tutor-user')),
});

const averageRating = ref(0)
const totalComments = ref(0)
const totalAppointments = ref(0)
const totalCourses = ref(0)



// 当前日期
const currentDate = computed(() => {
  const date = new Date();
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long"
  });
});

const GetAverageRating = async () => {
  try {
    const res = await request.get(`/teacherData/getAverageRating/${data.user.id}`)
    averageRating.value = res.data
  } catch (error) {
    ElMessage.error("获取平均评分失败")
  }
}

const GetTotalComments = async () => {
  try {
    const res = await request.get(`/teacherData/getCommentCount/${data.user.id}`)
    totalComments.value = res.data
  } catch (error) {
    ElMessage.error("获取评论总数失败")
  }
}

const GetTotalAppointments = async () => {
  try {
    const res = await request.get(`/teacherData/getTotalAppointments/${data.user.id}`)
    totalAppointments.value = res.data
  } catch (error) {
    ElMessage.error("获取订单总数失败")
  }
}


const GetTotalCourses = async () => {
  try {
    const res = await request.get(`/teacherData/getCourseCount/${data.user.id}`)
    totalCourses.value = res.data
  } catch (error) {
    ElMessage.error("获取课程总数失败")
  }
}

const initBarChart = async () => {
  const chartDom = document.getElementById('bar');
  if (!chartDom) return;

  const barChart = echarts.init(chartDom);

  const res = await request.get(`/teacherData/getRatingDistribution/${data.user.id}`);
  const rawData = res.data || [];

  // 创建一个完整的评分范围映射（1~5 星）
  const ratingMap = {
    1: 0,
    2: 0,
    3: 0,
    4: 0,
    5: 0
  };

  // 将后端返回的数据填充到 ratingMap 中
  rawData.forEach(item => {
    const rating = Math.floor(item.rating); // 确保是整数
    ratingMap[rating] = item.count;
  });

  // 构造图表用的数据
  const ratings = [1, 2, 3, 4, 5].map(num => `${num} 星`);
  const counts = [1, 2, 3, 4, 5].map(num => ratingMap[num]);

  const option = {
    title: {
      text: '评分分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: ratings,
      name: '评分'
    },
    yAxis: {
      type: 'value',
      name: '评论数',
      minInterval: 1 // 保证最小间隔是 1，避免出现小数刻度
    },
    series: [
      {
        name: '数量',
        type: 'bar',
        data: counts,
        itemStyle: {
          color: '#67C23A'
        },
        barWidth: '50%'
      }
    ]
  };

  barChart.setOption(option);
};


const initLineChart = async (timeRange = 'day') => {
  const chartDom = document.getElementById('line');
  if (!chartDom) return;

  const lineChart = echarts.init(chartDom);

  // 获取数据，传递 timeRange 和 teacherId
  const res = await request.get(`/teacherData/getAppointmentDistribution/${data.user.id}`, {
    params: {
      timeRange: timeRange,  // 'month', 'week', 'year' 或 'day'
    }
  });
  const rawData = res.data || [];

  // 去重并确保日期格式正确
  const dateSet = new Set();
  const filteredData = rawData.filter(item => {
    if (dateSet.has(item.date)) {
      return false;  // 去重
    }
    dateSet.add(item.date);
    return true;
  });

  // 提取日期和数量
  const dates = filteredData.map(item => item.date);
  const counts = filteredData.map(item => item.count);

  // 横坐标显示的格式化方式
  let axisLabelFormatter;
  if (timeRange === 'week') {
    axisLabelFormatter = function (value) {
      return value;  // 格式化周数据：例如 2025-W18
    };
  } else if (timeRange === 'month') {
    axisLabelFormatter = function (value) {
      return value;  // 格式化月数据：例如 2025-05
    };
  } else if (timeRange === 'year') {
    axisLabelFormatter = function (value) {
      return value;  // 格式化年数据：例如 2025
    };
  } else if (timeRange === 'day') {
    axisLabelFormatter = function (value) {
      return value;  // 格式化天数据：例如 2025-05-18
    };
  }

  // 配置图表
  const option = {
    title: {
      text: '订单趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates,
      name: '日期',
      axisLabel: {
        formatter: axisLabelFormatter  // 动态格式化横坐标标签
      }
    },
    yAxis: {
      type: 'value',
      name: '订单数',
      minInterval: 1,  // 设置y轴显示为整数
      axisLabel: {
        formatter: '{value}'  // 显示整数
      }
    },
    series: [
      {
        name: '订单数',
        type: 'line',
        data: counts,
        smooth: true,  // 曲线平滑
        lineStyle: {
          color: '#409EFF'
        },
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  };

  lineChart.setOption(option);
};




// 绑定事件，选择时间粒度时重新渲染图表
const updateTimeRange = (timeRange) => {
  initLineChart(timeRange);
};



onMounted(() => {
  GetAverageRating()
  GetTotalComments()
  GetTotalAppointments()
  GetTotalCourses()

  initBarChart()
  // 初始加载：按周查看
  initLineChart('day')
})

</script>


<style scoped>
.teacher-data-center {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.welcome-text h1 {
  margin: 0;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.welcome-subtitle {
  margin: 10px 0 0;
  color: var(--el-text-color-secondary);
}

.data-overview {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #303133;
  font-size: 16px;
}

.data-item {
  text-align: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.data-item:hover {
  background-color: #e4e9f2;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.data-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.data-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.charts-container {
  margin-top: 20px;
}

.chart-item {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.chart-header{
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chart-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.chart {
  padding: 10px;
  width: 610px;
  height: 400px;

}
</style>