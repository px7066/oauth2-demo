<style scoped>
.login_form{
  width: 400px;
  text-align: center;
}
</style>
<template>
  <div class="login_form">
    <el-form ref="login" :model="loginForm" label-width="0px">
      <el-form-item>
        <el-input v-model="loginForm.username" placeholder="username" />
      </el-form-item>
      <el-form-item>
        <el-input v-model="loginForm.password" placeholder="password" type="password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import axios from 'axios'

@Options({
  created() {
    axios.defaults.baseURL = 'http://auth-server:9000/'
  },
  data() {
    return {
      loginForm: {}
    }
  },
  methods: {
    onSubmit(){
      let formData = new FormData()
      for(var key in this.loginForm){
        formData.append(key, this.loginForm[key])
      }
      axios({
        method:"post",
        url:"login",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded"
        },
        withCredentials: true,
        data: formData
      }).then((res)=>{
            console.log(res)
        })
      }
  }
})
export default class Login extends Vue {}
</script>

