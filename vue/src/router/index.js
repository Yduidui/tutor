import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/user/homepage'
    },  // 默认展示用户首页

    {
      path: '/manager',
      component: () => import('../views/Manager.vue'),
      children: [
        {
          path: 'home',
          name: 'home',
          meta: { title: '主页' },
          component: () => import('../views/Home.vue')
        },
        {
          path: 'admin',
          name: 'admin',
          meta: { title: '管理员信息页' },
          component: () => import('../views/Admin.vue')
        },
        {
          path: 'course',
          name: 'course',
          meta: { title: '课程信息页' },
          component: () => import('../views/Course.vue')
        },
        {
          path: 'student',
          name: 'student',
          meta: { title: '学生信息页' },
          component: () => import('../views/Student.vue')
        },
        {
          path: 'teacher',
          name: 'teacher',
          meta: { title: '老师信息页' },
          component: () => import('../views/Teacher.vue')
        },
        {
          path: 'subject',
          name: 'subject',
          meta: { title: '课程信息页' },
          component: () => import('../views/Subject.vue')
        },
        {
          path: 'personalinfo',
          name: 'personalinfo',
          meta: { title: '个人信息页' },
          component: () => import('../views/PersonalInfo.vue')
        },
        {
          path: 'changePassword',
          name: 'changePassword',
          meta: { title: '修改密码' },
          component: () => import('../views/changePassword.vue')
        },
        {
          path: 'news',
          name: 'news',
          meta: { title: '新闻页' },
          component: () => import('../views/News.vue')
        },
        {
          path: 'article',
          name: 'article',
          meta: { title: '论坛页' },
          component: () => import('../views/Article.vue')
        },
        {
          path: 'help',
          name: 'help',
          meta: { title: '工单管理页' },
          component: () => import('../views/Help.vue')
        },
        {
          path: 'order',
          name: 'order',
          meta: { title: '订单管理页' },
          component: () => import('../views/Order.vue')
        },
        {
          path: 'appointment',
          name: 'appointment',
          meta: { title: '订单管理页' },
          component: () => import('../views/Appointment.vue')
        },
        {
          path: 'comment',
          name: 'comment',
          meta: { title: '评论管理页' },
          component: () => import('../views/Comment.vue')
        },
        {
          path: 'visit',
          name: 'visit',
          meta: { title: '访问管理页' },
          component: () => import('../views/Visit.vue')
        },
      ]
    },

    {
      path: '/user',
      component: () => import('../views/user/Navbar.vue'),  // 用户父组件
      children: [
        {
          path: 'homepage',
          name: 'homepage',
          meta: { title: '首页' },
          component: () => import('../views/user/HomePage.vue')
        },
        {
          path: 'courseCenter',
          name: 'courseCenter',
          meta: { title: '课程中心' },
          component: () => import('../views/user/CourseCenter.vue')
        },
        {
          path: 'course/:id',
          name: 'courseDetail',
          meta: { title: '课程中心' },
          component: () => import('../views/user/CourseDetail.vue')
        },
        {
          path: 'teacherShowcase',
          name: 'teacherShowcase',
          meta: { title: '老师风采' },
          component: () => import('../views/user/TeacherShowcase.vue')
        },
        {
          path: 'teacher/:id',
          name: 'teacherDetail',
          meta: { title: '老师风采' },
          component: () => import('../views/user/TeacherDetail.vue')
        },
        {
          path: 'myCourses',
          name: 'myCourses',
          meta: { title: '我的课程' },
          component: () => import('../views/user/MyCourses.vue')
        },
        {
          path: 'newsCenter',
          name: 'newsCenter',
          meta: { title: '咨询/公告' },
          component: () => import('../views/user/NewsCenter.vue')
        },
        {
          path: 'news/:id',
          name: 'newsDetail',
          meta: { title: '公告详情' },
          component: () => import('../views/user/NewsDetail.vue')
        },
        {
          path: 'personalInfo',
          name: 'personalInfo',
          meta: { title: '个人信息' },
          component: () => import('../views/PersonalInfo.vue')
        },
        {
          path: 'changePassword',
          name: 'changePassword',
          meta: { title: '修改密码' },
          component: () => import('../views/ChangePassword.vue')
        },
        {
          path: 'payOrder',
          name: 'payOrder',
          meta: { title: '修改密码' },
          component: () => import('../views/user/PayOrder.vue')
        },
        {
          path: 'mySchedule',
          name: 'MySchedule',
          meta: { title: '我的排课' },
          component: () => import('@/views/user/MySchedule.vue'),
          children: [
            {
              path: '',
              name: 'ScheduleCalendar',
              meta: { title: '排课信息' },
              component: () => import('@/views/user/ScheduleCalendar.vue')
            }
          ]
        },
        {
          path: 'chat',
          name: 'chat',
          meta: { title: '修改密码' },
          component: () => import('../views/user/ChatBot.vue')
        },
        {
          path: 'messageCenter',
          name: 'messageCenter',
          meta: { title: '消息中心' },
          component: () => import('../views/user/MessageCenter.vue')
        },
        {
          path: 'teacherDataCenter',
          name: 'teacherDataCenter',
          meta: { title: '数据中心' },
          component: () => import('../views/user/TeacherDataCenter.vue')
        },
        {
          path: 'commentSubmit',
          name: 'commentSubmit',
          meta: { title: '发表评论' },
          component: () => import('../views/user/CommentSubmit.vue')
        },
        {
          path: 'forum',
          name: 'forum',
          meta: { title: '发表评论' },
          component: () => import('../views/user/Forum.vue')
        },
        {
          path: 'helpCenter',
          name: 'helpCenter',
          meta: { title: '帮助中心' },
          component: () => import('../views/user/HelpCenter.vue')
        },
        {
          path: 'test',
          name: 'test',
          meta: { title: 'test' },
          component: () => import('../views/user/Test.vue')
        },
      ]
    },

    {
      path: '/login',
      name: '登录',
      meta: { title: '登录界面' },
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: '注册',
      meta: { title: '注册界面' },
      component: () => import('../views/Register.vue')
    },
    {
      path: '/404',
      name: 'NotFound',
      meta: { title: '404' },
      component: () => import('../views/404.vue') },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404' },
  ]
})


router.beforeEach((to, from, next) => {
  document.title = to.meta.title
  next()
})

export default router