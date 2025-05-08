import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import '@/assets/global.css'
// 引入全局css,工程所有css全生效
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import request from '@/utils/request.js'  // 确保路径正确

const app = createApp(App)

app.use(router)
app.use(ElementPlus,{
    locale:zhCn,
})

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// ✅ 配置全局路由守卫，记录访问日志
router.beforeEach((to, from, next) => {
    const user = JSON.parse(localStorage.getItem('tutor-user') || '{}')
    const userNo = user.userNo || 'anonymous'
    const excludedPaths = ['/login', '/404']

    if (!excludedPaths.includes(to.path)) {
        request.post('/redis/visit', null, {
            params: {
                userNo,
                path: to.fullPath,
            },
        }).catch((error) => {
            console.error('记录访问日志失败', error)
        })
    }

    next()
})
app.mount('#app')