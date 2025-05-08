<template>
  <div class="home-container">
    <!-- 第一层：统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card visits-card">
          <div class="stat-icon">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">总访问量</div>
            <h2 class="stat-value">{{ totalVisits }}</h2>
            <div class="stat-trend">
              <span class="trend-up">↑ 12%</span>
              <span class="trend-text">较上周</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card users-card">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">总用户数</div>
            <h2 class="stat-value">{{ totalUsers }}</h2>
            <div class="stat-trend">
              <span class="trend-up">↑ 8%</span>
              <span class="trend-text">较上周</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card courses-card">
          <div class="stat-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">总课程数</div>
            <h2 class="stat-value">{{ totalCourses }}</h2>
            <div class="stat-trend">
              <span class="trend-up">↑ 15%</span>
              <span class="trend-text">较上周</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card orders-card">
          <div class="stat-icon">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">总订单数</div>
            <h2 class="stat-value">{{ totalOrders }}</h2>
            <div class="stat-trend">
              <span class="trend-up">↑ 20%</span>
              <span class="trend-text">较上周</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二层：图表 + 公告 -->
    <el-row :gutter="20" class="mt-4">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">数据趋势</span>
              <el-radio-group v-model="timeRange" @change="updateTimeRange(timeRange)" size="small" class="time-range-group">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">全年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div id="line" class="chart-container" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">用户分布</span>
              <el-tooltip content="显示各角色用户占比" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div id="pie" class="chart-container" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 第三层：访问趋势 + 便捷操作 -->
    <el-row :gutter="20" class="mt-4">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">访问趋势</span>
              <el-tooltip content="显示每日访问量统计" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div id="bar" class="chart-container" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="quick-actions-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">便捷操作</span>
            </div>
          </template>
          <div class="button-grid">
            <button class="square-btn" @click="handleUpdate">
              <el-icon><Bell /></el-icon>
              <span>发布公告</span>
            </button>
            <button class="square-btn">
              <el-icon><List /></el-icon>
              <span>查看订单</span>
            </button>
            <button class="square-btn">
              <el-icon><Setting /></el-icon>
              <span>设置中心</span>
            </button>
            <button class="square-btn">
              <el-icon><QuestionFilled /></el-icon>
              <span>帮助中心</span>
            </button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第四层 -->
    <el-row :gutter="20" class="mt-4">
      <el-col :span="8">
        <el-card shadow="hover" class="latest-users-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon"><User /></el-icon>
                <span>最新用户</span>
              </div>
              <el-button type="primary" link class="view-more-btn" @click="$router.push('/manager/student')">查看更多</el-button>
            </div>
          </template>
          <div class="users-list">
            <div v-for="user in latestUsers" :key="user.id" class="user-item">
              <el-avatar :size="40" :src="user.avatar" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="user-info">
                <div class="user-name">{{ user.name }}</div>
                <div class="user-role">
                  <el-tag size="small" :type="getRoleType(user.role)">{{ user.role }}</el-tag>
                </div>
              </div>
              <div class="user-time">{{ formatTime(user.createTime) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="popular-articles-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon"><Document /></el-icon>
                <span>热门文章</span>
              </div>
              <el-button type="primary" link class="view-more-btn" @click="$router.push('/manager/article')">查看更多</el-button>
            </div>
          </template>
          <div class="articles-list">
            <div v-for="article in popularArticles" :key="article.id" class="article-item">
              <el-avatar :size="40" :src="article.cover" class="article-cover">
                <el-icon><Picture /></el-icon>
              </el-avatar>
              <div class="article-info">
                <div class="article-title">{{ article.title }}</div>
                <div class="article-stats">
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ article.likes }}
                  </span>
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ article.clickCount }}
                  </span>
                </div>
              </div>
              <div class="user-time">{{ formatTime(article.time) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="latest-helps-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon"><Service /></el-icon>
                <span>待处理工单</span>
              </div>
              <el-button type="primary" link class="view-more-btn" @click="$router.push('/manager/help')">查看更多</el-button>
            </div>
          </template>
          <div class="helps-list">
            <div v-for="help in latestHelps" :key="help.id" class="help-item">
              <div class="help-info">
                <div class="help-title">{{ help.title }}</div>
                <div class="help-meta">
                  <el-tag size="small" :type="getStatusType(help.status)">{{ getStatusText(help.status) }}</el-tag>
                  <el-tag size="small" :type="getPriorityType(help.priority)" class="ml-2">
                    {{ getPriorityText(help.priority) }}
                  </el-tag>
                </div>
              </div>
              <div class="help-time">{{ formatTime(help.createdAt) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-4">
      <el-col :span="12">
        <el-card shadow="hover" class="recent-orders-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon"><List /></el-icon>
                <span>最近订单</span>
              </div>
              <el-button type="primary" link class="view-more-btn" @click="$router.push('/manager/order')">查看更多</el-button>
            </div>
          </template>
          <div class="orders-list">
            <div v-for="order in recentOrders" :key="order.id" class="order-item">
              <div class="order-info">
                <div class="order-title">
                  <span class="order-no">{{ order.orderNo }}</span>
                  <el-tag size="small" :type="getOrderStatusType(order.status)" class="ml-2">
                    {{ getOrderStatusText(order.status) }}
                  </el-tag>
                </div>
                <div class="order-meta">
                  <span class="meta-item">
                    <el-icon><User /></el-icon>
                    {{ order.studentName }}
                  </span>
                  <span class="meta-item">
                    <el-icon><User /></el-icon>
                    {{ order.teacherName }}
                  </span>
                  <span class="meta-item price">
                    <el-icon><Money /></el-icon>
                    ¥{{ order.totalPrice }}
                  </span>
                </div>
              </div>
              <div class="order-time">{{ formatTime(order.createTime) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover" class="announcements-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon"><Bell /></el-icon>
                <span>系统公告</span>
              </div>
              <el-button type="primary" link class="view-more-btn" @click="$router.push('/manager/news')">查看更多</el-button>
            </div>
          </template>
          <div class="announcements-list">
            <div v-for="announcement in announcements" :key="announcement.id" class="announcement-item">
              <div class="announcement-info">
                <div class="announcement-title">
                  <span>{{ announcement.title }}</span>
                  <el-tag size="small" :type="getAnnouncementType(announcement.type)" class="ml-2">
                    {{ announcement.type }}
                  </el-tag>
                </div>
                <div class="announcement-desc">{{ announcement.descr }}</div>
              </div>
              <div class="announcement-time">{{ formatTime(announcement.createTime) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 抽屉式表单 -->
  <el-drawer
      v-model="data.formVisible"
      :title="'新增公告'"
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
        <el-button type="primary" @click="add">保存</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import {ref, onMounted, onBeforeUnmount,reactive, shallowRef} from 'vue'
import * as echarts from 'echarts'
import {ElMessage} from "element-plus";
import request from "@/utils/request.js"
import dayjs from "dayjs";
import {User, View, Reading, ShoppingCart, Bell, List, Setting, QuestionFilled, Document, Picture, Star, Service, InfoFilled, Money} from "@element-plus/icons-vue";
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";
import { useRoute } from 'vue-router';

const editorConfig = { MENU_CONF: {} }
const formRef = ref()
const editorRef = shallowRef()
const mode = 'default'
const baseUrl = 'http://localhost:8080'
editorConfig.MENU_CONF['uploadImage'] = {
  server: baseUrl + '/files/wang/upload',
  fieldName: 'file',
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const data = reactive({
  content: null,
  formVisible: false,
  form: {},
})

// 第一层数据
const totalVisits = ref(0)
const totalUsers = ref(0)
const totalCourses = ref(0)
const totalOrders = ref(0)


// 公告、订单、用户、文章
const announcements = ref([])
const recentOrders = ref([])
const latestUsers = ref([])
const popularArticles = ref([])
const latestHelps = ref([])

const getTotalVisits = () => {
  request.get('/adminData/getAllVisitCount').then((res) => {
    if (res.code === '200') {
      totalVisits.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error('获取总访问量失败')
  })
}

const getTotalUsers = () => {
  request.get('/adminData/getAllUserCount').then((res) => {
    if (res.code === '200') {
      totalUsers.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error('获取总用户数失败')
  })
}

const getTotalCourses = () => {
  request.get('/adminData/getAllUserCount').then((res) => {
    if (res.code === '200') {
      totalCourses.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error('获取总课程数失败')
  })
}

const getTotalOrders = () => {
  request.get('/adminData/getAllOrderCount').then((res) => {
    if (res.code === '200') {
      totalOrders.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error('获取总订单数失败')
  })
}

const initLineChart = async (timeRange = 'week') => {
  const chartDom = document.getElementById('line');
  if (!chartDom) return;

  const chart = echarts.init(chartDom);

  const res = await request.get('/adminData/getLineData', {
    params: { timeRange }
  });

  const rawData = res.data || {};

  const seriesData = [];
  const dateSet = new Set();

  const seriesNames = {
    teacher: '老师',
    student: '学生',
    order: '订单',
    visit: '访问量'
  };

  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C'];

  for (const key in rawData) {
    const dataList = rawData[key];
    dataList.forEach(item => dateSet.add(item.date));
  }

  const dates = Array.from(dateSet).sort();

  for (const key in rawData) {
    const map = new Map(rawData[key].map(item => [item.date, item.count]));
    const dataArr = dates.map(date => map.get(date) || 0);
    seriesData.push({
      name: seriesNames[key],
      type: 'line',
      data: dataArr,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 3,
        shadowColor: 'rgba(0,0,0,0.1)',
        shadowBlur: 10
      },
      itemStyle: {
        color: colors[Object.keys(rawData).indexOf(key)]
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          {
            offset: 0,
            color: colors[Object.keys(rawData).indexOf(key)]
          },
          {
            offset: 1,
            color: 'rgba(255,255,255,0.1)'
          }
        ])
      }
    });
  }

  chart.setOption({
    title: {
      text: '平台数据趋势',
      left: 'center',
      top: 10,
      textStyle: {
        fontSize: 16,
        fontWeight: 500
      }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.9)',
      borderColor: '#eee',
      borderWidth: 1,
      textStyle: {
        color: '#666'
      },
      formatter: function(params) {
        let result = params[0].axisValue + '<br/>';
        params.forEach(param => {
          result += `${param.marker} ${param.seriesName}: ${param.value}<br/>`;
        });
        return result;
      }
    },
    legend: {
      top: '10%',
      data: Object.values(seriesNames),
      textStyle: {
        color: '#666'
      }
    },
    grid: {
      top: '20%',
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45,
        color: '#666'
      },
      axisLine: {
        lineStyle: {
          color: '#eee'
        }
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: {
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          color: '#eee',
          type: 'dashed'
        }
      }
    },
    series: seriesData,
    animationDuration: 1000
  });
};


const initPieChart = async () => {
  const chartDom = document.getElementById('pie');
  if (!chartDom) return;

  const chart = echarts.init(chartDom);

  const res = await request.get('/adminData/getUserPieData');
  const data = res.data || {};

  const pieData = [
    { name: '学生', value: data.student || 0 },
    { name: '老师', value: data.teacher || 0 },
    { name: '管理员', value: data.admin || 0 }
  ];

  const colors = ['#409EFF', '#67C23A', '#E6A23C'];

  chart.setOption({
    title: {
      text: '用户角色占比',
      left: 'center',
      top: 10,
      textStyle: {
        fontSize: 16,
        fontWeight: 500
      }
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.9)',
      borderColor: '#eee',
      borderWidth: 1,
      textStyle: {
        color: '#666'
      },
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      bottom: 10,
      left: 'center',
      textStyle: {
        color: '#666'
      }
    },
    series: [
      {
        name: '用户数量',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        labelLine: {
          show: false
        },
        data: pieData.map((item, index) => ({
          ...item,
          itemStyle: {
            color: colors[index]
          }
        }))
      }
    ]
  });
};

const initBarChart = async () => {
  const chartDom = document.getElementById('bar');
  if (!chartDom) return;

  const chart = echarts.init(chartDom);

  const res = await request.get('/adminData/getVisitBarData');
  const data = res.data || [];

  const xData = ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'];
  const yData = Array(7).fill(0);

  data.forEach(item => {
    const index = xData.indexOf(item.day);
    if (index !== -1) {
      yData[index] = item.count;
    }
  });

  const option = {
    title: {
      text: '本周访问趋势',
      left: 'center',
      top: 10,
      textStyle: {
        fontSize: 16,
        fontWeight: 500
      }
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.9)',
      borderColor: '#eee',
      borderWidth: 1,
      textStyle: {
        color: '#666'
      },
      formatter: '{b}: {c} 次访问'
    },
    grid: {
      top: '20%',
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xData,
      axisLabel: {
        color: '#666'
      },
      axisLine: {
        lineStyle: {
          color: '#eee'
        }
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          color: '#eee',
          type: 'dashed'
        }
      }
    },
    series: [{
      data: yData,
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          {
            offset: 0,
            color: '#409EFF'
          },
          {
            offset: 1,
            color: '#36D1DC'
          }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: '#66b1ff'
            },
            {
              offset: 1,
              color: '#5cd6e6'
            }
          ])
        }
      }
    }]
  };

  chart.setOption(option);
};



const getRecentOrders = () => {
  request.get('/adminData/getRecentOrders').then((res) => {
    if (res.code === '200') {
      recentOrders.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error('获取最近订单列表失败')
  })
}


const getLatestUsers = () => {
  request.get('/adminData/getLatestUsers').then((res) => {
    if (res.code === '200') {
      latestUsers.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error('获取最近注册用户失败')
  })
}

const getPopularArticles = () => {
  request.get('/adminData/getPopularArticles').then((res) => {
    if (res.code === '200') {
      popularArticles.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error('获取热门文章失败')
  })
}

const getAnnouncements = () => {
  request.get('/adminData/getAnnouncements').then((res) => {
    if (res.code === '200') {
      announcements.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error('获取最近公告失败')
  })
}
const getLatestHelps = () => {
  request.get('/adminData/getLatestHelps').then((res) => {
    if (res.code === '200') {
      latestHelps.value = res.data
    } else {
      ElMessage.error(res.msg)
    }
  }).catch(() => {
    ElMessage.error('获取最近工单失败')
  })
}

// 绑定事件，选择时间粒度时重新渲染图表
const updateTimeRange = (timeRange) => {
  initLineChart(timeRange);
};

const handleCreated = (editor) => {
  editorRef.value = editor
}
const handleUpdate = (row) => {
  data.formVisible = true
}
//新增公告
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

const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日HH时mm分ss秒');
};

const getRoleType = (role) => {
  const types = {
    '学生': 'success',
    '老师': 'warning',
    '管理员': 'danger'
  }
  return types[role] || 'info'
}

const getOrderStatusType = (status) => {
  const types = {
    '待支付': 'warning',
    '已支付': 'success',
    '已取消': 'info',
    '已退款': 'danger'
  }
  return types[status] || 'info'
}

const getOrderStatusText = (status) => {
  return status || '未知状态'
}

const getAnnouncementType = (type) => {
  const types = {
    '公告': 'primary',
    '资讯': 'success',
    '更新': 'warning'
  }
  return types[type] || 'info'
}
const getStatusText = (status) => {
  switch (status) {
    case 0: return '新建';
    case 1: return '处理中';
    case 2: return '已解决';
    default: return '未知状态';
  }
};

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'danger';    // 新建 -> 红色
    case 1: return 'warning';   // 处理中 -> 橙色
    case 2: return 'success';   // 已解决 -> 绿色
    default: return '';
  }
};

const getPriorityText = (priority) => {
  switch (priority) {
    case 2: return '高';
    case 1: return '中';
    case 0: return '低';
    default: return '未知';
  }
};

const getPriorityType = (priority) => {
  switch (priority) {
    case 2: return 'danger';    // 高 -> 红色
    case 1: return 'warning';   // 中 -> 橙色
    case 0: return 'info';      // 低 -> 蓝色
    default: return '';
  }
};


// 页面加载时获取数据
onMounted(async () => {

  getTotalVisits()
  getTotalUsers()
  getTotalCourses()
  getTotalOrders()

  initLineChart('week')
  initPieChart()
  initBarChart()

  getRecentOrders()
  getLatestUsers()
  getPopularArticles()
  getAnnouncements()
  getLatestHelps()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.mt-4 {
  margin-top: 20px;
}

/* 统计卡片样式 */
.stat-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.visits-card .stat-icon {
  background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%);
}

.users-card .stat-icon {
  background: linear-gradient(135deg, #FF9A9E 0%, #FAD0C4 100%);
}

.courses-card .stat-icon {
  background: linear-gradient(135deg, #A18CD1 0%, #FBC2EB 100%);
}

.orders-card .stat-icon {
  background: linear-gradient(135deg, #84FAB0 0%, #8FD3F4 100%);
}

.stat-content {
  padding: 20px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 8px;
}

.trend-up {
  color: #67C23A;
  font-weight: 500;
}

.trend-down {
  color: #F56C6C;
  font-weight: 500;
}

.trend-text {
  color: #909399;
  font-size: 12px;
}

/* 图表卡片样式 */
.chart-card {
  background: linear-gradient(135deg, #ffffff 0%, #f5f7fa 100%);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.time-range-group {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 2px;
}

.chart-container {
  height: 300px;
  position: relative;
}

/* 卡片通用样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  font-size: 18px;
  color: #409EFF;
}

/* 用户列表样式 */
.users-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-item:hover {
  background-color: #f5f7fa;
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.user-role {
  display: flex;
  gap: 8px;
}

.user-time {
  font-size: 12px;
  color: #909399;
}

/* 文章列表样式 */
.articles-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.article-item:hover {
  background-color: #f5f7fa;
}

.article-info {
  flex: 1;
}

.article-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

/* 工单列表样式 */
.helps-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.help-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.help-item:hover {
  background-color: #f5f7fa;
}

.help-info {
  flex: 1;
}

.help-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.help-meta {
  display: flex;
  gap: 8px;
}

.help-time {
  font-size: 12px;
  color: #909399;
}

.ml-2 {
  margin-left: 8px;
}

/* 快捷操作卡片样式 */
.quick-actions-card {
  background: linear-gradient(135deg, #ffffff 0%, #f5f7fa 100%);
}

.button-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 0 16px; /* 增加左右内边距，确保从左右边界有空隙 */
}
/* 引入字体图标库后，可以自己定义按钮样式以丰富视觉效果 */
.square-btn {
  width: 100%;
  max-width: 120px;
  height: 134px;
  border: 2px dashed #999;
  background-color: transparent;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column; /* 图标和文字纵向排列 */
  transition: all 0.2s;
  padding: 8px;
  margin: 0 auto
}
.square-btn i {
  font-size: 24px;
  margin-bottom: 8px;
  color: #333;
}
.square-btn:hover {
  border-color: #409EFF;
  color: #409EFF;
}


/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-item,
.article-item,
.help-item {
  animation: fadeIn 0.3s ease-out;
}

/* 响应式调整 */
@media screen and (max-width: 1200px) {
  .el-col {
    width: 100%;
  }
  
  .chart-container {
    height: 250px;
  }
}

/* 订单列表样式 */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: linear-gradient(to right, #ffffff, #f8f9fa);
}

.order-item:hover {
  background: linear-gradient(to right, #f8f9fa, #f0f2f5);
  transform: translateX(5px);
}

.order-info {
  flex: 1;
}

.order-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.order-no {
  font-weight: 500;
  color: #303133;
}

.order-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}

.meta-item.price {
  color: #f56c6c;
  font-weight: 500;
}

.order-time {
  font-size: 12px;
  color: #909399;
}

/* 公告列表样式 */
.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.announcement-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: linear-gradient(to right, #ffffff, #f8f9fa);
}

.announcement-item:hover {
  background: linear-gradient(to right, #f8f9fa, #f0f2f5);
  transform: translateX(5px);
}

.announcement-info {
  flex: 1;
}

.announcement-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-weight: 500;
  color: #303133;
}

.announcement-desc {
  font-size: 13px;
  color: #606266;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.announcement-time {
  font-size: 12px;
  color: #909399;
  margin-left: 16px;
}

/* 卡片通用样式 */
.recent-orders-card,
.announcements-card {
  background: linear-gradient(135deg, #ffffff 0%, #f5f7fa 100%);
}

/* 动画效果 */
.order-item,
.announcement-item {
  animation: fadeIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.order-item:hover,
.announcement-item:hover {
  animation: slideIn 0.3s ease-out;
}
</style>
