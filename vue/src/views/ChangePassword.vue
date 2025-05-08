<template>
  <div class="change-password-container">
    <el-card class="password-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><Key /></el-icon>
          <span>修改密码</span>
        </div>
      </template>
      <template v-if="data.user.role === 'STU' || data.user.role === 'TEA'">
        <div class="back_div">
          <el-button  @click="router.back()" class="back-button">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>

      </template>

      <el-form
          ref="formRef"
          :model="data.form"
          :rules="data.rules"
          label-width="100px"
          class="password-form"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
              v-model="data.form.oldPassword"
              type="password"
              placeholder="请输入原密码"
              show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
              v-model="data.form.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              v-model="data.form.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              class="submit-btn"
              :loading="data.loading"
              @click="handleSubmit"
          >
            确认修改
          </el-button>
        </el-form-item>
      </el-form>

      <div class="password-tips">
        <h3>密码修改提示：</h3>
        <ul>
          <li>密码长度至少6位，建议使用字母、数字和特殊字符的组合</li>
          <li>为了账户安全，建议定期更换密码</li>
          <li>请勿使用与其他网站相同的密码</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import {createWebHistory as $router, useRouter} from "vue-router";
import {ArrowLeft, Key, Lock} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const router = useRouter();
const formRef = ref();

const data = reactive({
  user : JSON.parse(localStorage.getItem('tutor-user')),
  form: {
    oldPassword: "",
    newPassword: "",
    confirmPassword: ""
  },
  loading: false,
  rules: {
    oldPassword: [
      { required: true, message: "请输入原密码", trigger: "blur" },
      { min: 6, message: "密码长度不能小于6位", trigger: "blur" }
    ],
    newPassword: [
      { required: true, message: "请输入新密码", trigger: "blur" },
      { min: 6, message: "密码长度不能小于6位", trigger: "blur" }
    ],
    confirmPassword: [
      { required: true, message: "请再次输入新密码", trigger: "blur" },
      {
        validator: (rule, value, callback) => {
          if (value !== data.form.newPassword) {
            callback(new Error("两次输入的密码不一致"));
          } else {
            callback();
          }
        },
        trigger: "blur"
      }
    ]
  }
});

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      data.loading = true;
      request
          .put("/user/password", data.form)
          .then((res) => {
            if (res.code === "200") {
              ElMessage.success("密码修改成功，请重新登录");
              router.push("/login");
            } else {
              ElMessage.error(res.msg);
            }
          })
          .finally(() => {
            data.loading = false;
          });
    }
  });
};
</script>

<style scoped>
.change-password-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.password-card {
  width: 600px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
  color: var(--el-text-color-primary);
}

.password-form {
  margin-top: 20px;
}

.submit-btn {
  width: 100%;
  height: 40px;
}

.password-tips {
  margin-top: 30px;
  padding: 20px;
  background-color: var(--el-fill-color-light);
  border-radius: 8px;
}

.password-tips h3 {
  margin: 0 0 10px;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.password-tips ul {
  margin: 0;
  padding-left: 20px;
  color: var(--el-text-color-secondary);
}

.password-tips li {
  margin-bottom: 8px;
  line-height: 1.5;
}
.back_div {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 5px;
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

:deep(.el-form-item__label) {
  font-weight: 500;
}
</style>
