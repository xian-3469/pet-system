<template>
  <div class="wrapper">
    <div
      class="login-form-wrapper"
      style="margin: 150px auto; background-color: #fff; width: 400px; height: 300px; padding: 20px; opacity: 0.95; border-radius: 10px; border: 1px solid var(--pet-border);">
      <div style="margin: 20px 0; text-align: center; font-size: 28px; color: rgb(64, 64, 64); display: flex; align-items: center; justify-content: center;">
        <img src="@/assets/front-logo.jpg"
          style="height: 40px; width: auto; margin-right: 12px; flex-shrink: 0; -webkit-user-drag: none;-khtml-user-drag: none;-moz-user-drag: none;user-drag: none;" />
        <b style="line-height: 1.2;">宠物服务平台</b>
      </div>
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-form-item prop="username">
          <el-input size="large" prefix-icon="iconfont icon-r-user2" v-model="user.username" style="font-size: 22px;"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="large" prefix-icon="iconfont icon-r-lock" show-password v-model="user.password"  style="font-size: 22px;"></el-input>
        </el-form-item>
        <el-form-item style="margin: 10px 0; text-align: right">
          <el-button type="" autocomplete="off" @click="$router.push('/register')"> 注册</el-button>
          <el-button type="" autocomplete="off" @click="login"> 登录</el-button>

        </el-form-item>
        <el-form-item style="margin: 10px 0; text-align: right">
          <el-button type="text" autocomplete="off" @click="handlePass"> 找回密码</el-button>
        </el-form-item>
      </el-form>
    </div>


    <el-dialog title="找回密码" :append-to-body="true" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="100px" >
        <el-form-item label="用户名">
          <el-input v-model="pass.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="pass.phone" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false"> 取 消</el-button>
        <el-button type="primary" @click="passwordBack"> 重置密码</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { resetRouter, setRoutes } from "@/router";

export default {
  name: "Login",
  data() {
    return {
      user: {},
      pass: {},
      dialogFormVisible: false,
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
      }
    }
  },
  created() {
    // 重置路由
    resetRouter()
  },
  methods: {
    login() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          this.request.post("/user/login", this.user).then(res => {
            if (res.code === '200') {
              localStorage.setItem("user", JSON.stringify(res.data))  // 存储用户信息到浏览器
              localStorage.setItem("menus", JSON.stringify(res.data.menus))  // 存储用户信息到浏览器

              // 动态设置当前用户的路由
              resetRouter()  // 先重置路由
              setRoutes()    // 重新设置路由
              // 等待路由注册完成后再跳转
              this.$nextTick(() => {
                this.$router.push("/front/home")
              })
              this.$message.success("登录成功")
            } else {
              this.$message.error(res.msg)
            }
          }).catch(e => {
            console.log(e);

            if (
              e.response == undefined ||
              e.response.data == undefined
            ) {
              this.$message({
                showClose: true,
                message: e,
                type: "error",
                duration: 5000,
              });
            } else {
              this.$message({
                showClose: true,
                message: e.response.data,
                type: "error",
                duration: 5000,
              });
            }

          })
        }
      });
    },
    handlePass() {
      this.dialogFormVisible = true
      this.pass = {}
    },
    passwordBack() {
      this.request.put("/user/reset", this.pass).then(res => {
        if (res.code === '200') {
          this.$message.success("重置密码成功，新密码为：123，请尽快修改密码")
          this.dialogFormVisible = false
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style>
.wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  overflow-y: auto;
  height: 100%;
  background: url("../assets/background.jpg") center top / cover no-repeat;
  overflow: hidden;
}

/* 登录框样式优化 */
.login-form-wrapper {
  border-color: var(--pet-border) !important;
  box-shadow: 0 4px 20px var(--pet-shadow);
}
</style>
