<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />
        <h2>在线辅导系统</h2>
        <p>创建您的账号</p>
      </div>

      <el-form
        ref="formRef"
        :model="data.form"
        :rules="data.rules"
        class="register-form"
      >
        <el-form-item prop="role">
          <el-radio-group v-model="data.form.role" class="role-group">
            <el-radio-button value="STU" label="学生">学生</el-radio-button>
            <el-radio-button value="TEA" label="老师">教师</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="username">
          <el-input
            v-model="data.form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="data.form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="data.form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="register-button"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="login-link">
          已有账号？
          <el-button type="primary" link @click="handleLogin">
            立即登录
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { User, Lock, Phone, Message } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const router = useRouter();
const formRef = ref();

const data = reactive({
  form: {
    role: "STU",
    username: "",
    password: "",
    confirmPassword: "",
  },
  rules: {
    role: [{ required: true, message: "请选择角色", trigger: "change" }],
    username: [
      { required: true, message: "请输入用户名", trigger: "blur" },
      { min: 3, message: "用户名长度不能小于3位", trigger: "blur" }
    ],
    password: [
      { required: true, message: "请输入密码", trigger: "blur" },
      { min: 6, message: "密码长度不能小于6位", trigger: "blur" }
    ],
    confirmPassword: [
      { required: true, message: "请确认密码", trigger: "blur" },
      {
        validator: (rule, value, callback) => {
          if (value !== data.form.password) {
            callback(new Error("两次输入的密码不一致"));
          } else {
            callback();
          }
        },
        trigger: "blur"
      }
    ],
  }
});

// 注册
const handleRegister = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      console.log("此次登录角色为:" + data.form.role)
      if(data.form.role === "STU") {
        request
            .post("/student/register", data.form)
            .then((res) => {
              if (res.code === "200") {
                ElMessage.success("注册成功，请登录");
                router.push("/login");
              } else {
                ElMessage.error(res.msg);
              }
            }).catch((error) => {
          ElMessage.error('注册失败，请检查网络或服务器状态');
        });
      }else if(data.form.role === "TEA"){
        request
            .post("/teacher/register", data.form)
            .then((res) => {
              if (res.code === "200") {
                ElMessage.success("注册成功，请登录");
                router.push("/login");
              } else {
                ElMessage.error(res.msg);
              }
            }).catch((error) => {
          ElMessage.error('注册失败，请检查网络或服务器状态');
        });
      }

    }
  });
};

// 登录
const handleLogin = () => {
  router.push("/login");
};
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
  padding: 40px 0;
}

.register-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}

.register-header h2 {
  margin: 0 0 10px;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.register-header p {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.register-form {
  margin-top: 20px;
}

.role-group {
  width: 100%;
  display: flex;
  gap: 10px;
}

.role-group :deep(.el-radio-button__inner) {
  width: 100%;
}

.register-button {
  width: 100%;
  height: 40px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

:deep(.el-input__wrapper) {
  height: 40px;
}

:deep(.el-button) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

@media (max-width: 480px) {
  .register-box {
    width: 90%;
    padding: 20px;
  }
}
</style>