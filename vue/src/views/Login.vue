<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />
        <h2>在线辅导系统</h2>
        <p>欢迎回来，请登录您的账号</p>
      </div>

      <el-form
        ref="formRef"
        :model="data.form"
        :rules="data.rules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="data.form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            style="height: 40px; line-height: 40px;"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="data.form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
            style="height: 40px; line-height: 40px;"
          />
        </el-form-item>

        <el-form-item prop="role" >
          <el-select v-model="data.form.role" placeholder="请选择角色" size="large">
            <el-option value="ADMIN" label="管理员" />
            <el-option value="STU" label="学生" />
            <el-option value="TEA" label="老师" />
          </el-select>
        </el-form-item>

        <div class="form-options">
          <el-checkbox v-model="data.remember">记住我</el-checkbox>
          <el-button type="primary" link @click="handleForgotPassword">
            忘记密码？
          </el-button>
        </div>

        <el-form-item>
          <el-button
            type="primary"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="register-link">
          还没有账号？
          <el-button type="primary" link @click="handleRegister">
            立即注册
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const router = useRouter();
const formRef = ref();

const data = reactive({
  form: {
    username: "",
    password: "",
    role: "ADMIN"
  },
  remember: false,
  rules: {
    username: [
      { required: true, message: "请输入用户名", trigger: "blur" },
      { min: 3, message: "用户名长度不能小于3位", trigger: "blur" }
    ],
    password: [
      { required: true, message: "请输入密码", trigger: "blur" },
      { min: 6, message: "密码长度不能小于6位", trigger: "blur" }
    ],
    role: [
      { required: true, message: "请选择角色", trigger: "change" }
    ]
  }
});

// 登录
const handleLogin = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if(data.form.role === "ADMIN"){
        request
            .post("/login", data.form)
            .then((res) => {
              if (res.code === "200") {
                // 保存用户信息
                localStorage.setItem("tutor-user", JSON.stringify(res.data));
                // 记住用户名
                if (data.remember) {
                  localStorage.setItem("remember-username", data.form.username);
                } else {
                  localStorage.removeItem("remember-username");
                }
                ElMessage.success("登录成功");
                setTimeout(()=>{
                  location.href = '/manager/home'
                },500)
              } else {
                ElMessage.error(res.msg);
              }
            })
      }else if(data.form.role === "STU" || data.form.role === "TEA") {
        request
            .post("/login", data.form)
            .then((res) => {
              if (res.code === "200") {
                // 保存用户信息
                localStorage.setItem("tutor-user", JSON.stringify(res.data));
                // 记住用户名
                if (data.remember) {
                  localStorage.setItem("remember-username", data.form.username);
                } else {
                  localStorage.removeItem("remember-username");
                }
                ElMessage.success("登录成功");
                setTimeout(()=>{
                  location.href = '/user/Homepage'
                },500)
              } else {
                ElMessage.error(res.msg);
              }
            })
      }

    }
  });
};

// 注册
const handleRegister = () => {
  router.push("/register");
};

// 忘记密码
const handleForgotPassword = () => {
  ElMessage.info("请联系管理员重置密码");
};

onMounted(() => {
  const rememberedUsername = localStorage.getItem("remember-username");
  if (rememberedUsername) {
    data.form.username = rememberedUsername; // 自动填充用户名
    data.remember = true; // 同时勾选“记住我”
  }
});
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}

.login-header h2 {
  margin: 0 0 10px;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.login-header p {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.login-form {
  margin-top: 20px;
}

.role-select {
  width: 100%;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 40px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

</style>


