<template>
  <div class="personal-info-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :span="8">
        <el-card class="info-card" shadow="hover">
          <div class="avatar-container">
            <el-upload
                class="avatar-uploader"
                action="http://localhost:8080/files/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
            >
              <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <h2 class="user-name">{{ data.user.name }}</h2>
            <p class="user-role">{{ getRoleName(data.user.role) }}</p>
          </div>
          <el-divider />
          <div class="info-list">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>用户名：{{ data.user.username }}</span>
            </div>
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>手机号：{{ data.user.phone }}</span>
            </div>
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <span>邮箱：{{ data.user.email }}</span>
            </div>
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>注册时间：{{ formatTime(data.user.registrationTime) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧信息编辑区域 -->
      <el-col :span="16">
        <el-card class="edit-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button type="primary" @click="handleEdit" v-if="!data.isEditing">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
            </div>
          </template>

          <el-form
              ref="formRef"
              :model="data.form"
              :rules="data.rules"
              label-width="100px"
              :disabled="!data.isEditing"
          >
            <el-form-item label="姓名" prop="name">
              <el-input v-model="data.form.name" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="data.form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="data.form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="个人简介" prop="descr">
              <el-input
                  v-model="data.form.descr"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入个人简介"
              />
            </el-form-item>
          </el-form>

          <div class="form-footer" v-if="data.isEditing">
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" @click="save">保存</el-button>
          </div>
        </el-card>

        <!-- 安全设置卡片 -->
        <el-card class="security-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>安全设置</span>
            </div>
          </template>

          <div class="security-list">
            <div class="security-item">
              <div class="security-info">
                <h3>账户密码</h3>
                <p>建议您定期更换密码，设置一个包含字母、数字和特殊字符的密码会更安全</p>
              </div>
              <el-button type="primary" link @click="handleChangePassword">
                <el-icon><Key /></el-icon>修改密码
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>


  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import {
  User,
  Phone,
  Message,
  Calendar,
  Edit,
  Plus,
  Key
} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import { useRouter, useRoute } from 'vue-router'
import dayjs from 'dayjs';
const router = useRouter()
const route = useRoute()
const formRef = ref();
const passwordFormRef = ref();

const data = reactive({
  user : JSON.parse(localStorage.getItem('tutor-user')),
  isEditing: false,
  form: {},
  rules: {
    name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
    phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
    email: [{ required: true, message: "请输入邮箱", trigger: "blur" }]
  },
});

// 获取用户信息
const loadUserInfo = () => {
  if(data.user.role === "STU"){
    request.get("/student/selectById/" + data.user.id).then((res) => {
      if (res.code === "200") {
        data.user = res.data;
        data.form = { ...res.data };
//         展开运算符 ...:
//         使用 { ...res.data } 时，JavaScript 会创建一个新的对象，并将 res.data 中的所有可枚举属性复制到这个新对象中。
// 展开运算符允许您快速复制对象的属性，而不是手动列出每个属性。
      }
    });
  }else if(data.user.role === "TEA"){
    request.get("/teacher/selectById/" + data.user.id).then((res) => {
      if (res.code === "200") {
        data.user = res.data;
        data.form = { ...res.data };
      }
    });
  }else if(data.user.role === "ADMIN"){
    request.get("/admin/selectById/" + data.user.id).then((res) => {
      if (res.code === "200") {
        data.user = res.data;
        data.form = { ...res.data };
      }
    });
  }

};
const formatTime = (time) => {
  return dayjs(time).format('YYYY年MM月DD日HH时mm分ss秒');
};

// 获取角色名称
const getRoleName = (role) => {
  const roleMap = {
    ADMIN: "系统管理员",
    TEA: "教师",
    STU: "学生"
  };
  return roleMap[role] || "未知角色";
};

// 编辑
const handleEdit = () => {
  data.isEditing = true;
};

// 取消编辑
const cancelEdit = () => {
  data.isEditing = false;
  data.form = { ...data.user };
};

// 保存
const save = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if(data.user.role === "STU"){
        request.put("/student/update", data.form).then((res) => {
          if (res.code === "200") {
            ElMessage.success("保存成功");
            data.isEditing = false;
            loadUserInfo();
          } else {
            ElMessage.error(res.msg);
          }
        });
      }else if(data.user.role === "TEA"){
        request.put("/teacher/update", data.form).then((res) => {
          if (res.code === "200") {
            ElMessage.success("保存成功");
            data.isEditing = false;
            loadUserInfo();
          } else {
            ElMessage.error(res.msg);
          }
        });
      }else if(data.user.role === "ADMIN"){
        request.put("/admin/update", data.form).then((res) => {
          if (res.code === "200") {
            ElMessage.success("保存成功");
            data.isEditing = false;
            loadUserInfo();
          } else {
            ElMessage.error(res.msg);
          }
        });
      }
    }
  });
};

// 修改密码
const handleChangePassword = () => {
  router.push('/user/changePassword');
};

// 评论
const submitComment = () => {
  router.push('/user/commentSubmit');
};



// 头像上传成功
const handleAvatarSuccess = (res) => {
  if (res.code === '200') {
    data.form.avatar = res.data;
    console.log(res.data);
    ElMessage.success("头像上传成功，正在保存...");
    setTimeout(() => {
      save(); // 延迟保存，确保 avatar 值已绑定
    }, 300); // 300ms 可视为等待绑定完毕
  } else {
    ElMessage.error("头像上传失败");
  }

};


onMounted(() => {
  if(data.user){
    loadUserInfo();
  }
});
</script>

<style scoped>
.personal-info-container {
  padding: 20px;
}

.info-card {
  height: 100%;
}

.avatar-container {
  text-align: center;
  padding: 20px 0;
}

.avatar-uploader {
  display: inline-block;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  border-radius: 50%;
  background-color: var(--el-fill-color-light);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  margin: 15px 0 5px;
  font-size: 20px;
  color: var(--el-text-color-primary);
}

.user-role {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.info-list {
  padding: 0 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  color: var(--el-text-color-regular);
}

.info-item .el-icon {
  font-size: 18px;
  color: var(--el-text-color-secondary);
}

.edit-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.security-list {
  padding: 0 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.security-item:last-child {
  border-bottom: none;
}

.security-info h3 {
  margin: 0 0 5px;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.security-info p {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-button) {
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>