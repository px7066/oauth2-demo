<style scoped>
@import url('https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css');
@import url('https://getbootstrap.com/docs/4.0/examples/signin/signin.css');
/* .login_form{
  width: 400px;
  text-align: center;
} */
</style>
<template>
  <div class="login_form">
    <!-- <el-form ref="login" :model="loginForm" label-width="0px">
      <el-form-item>
        <el-input v-model="loginForm.username" placeholder="username" />
      </el-form-item>
      <el-form-item>
        <el-input v-model="loginForm.password" placeholder="password" type="password" @keyup.enter="onSubmit"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">登录</el-button>
      </el-form-item>
    </el-form> -->
    <form class="form-signin" method="post" action="http://auth-server:9000/login">
      <h2 class="form-signin-heading">Please sign in</h2>
      <p>
        <label for="username" class="sr-only">Username</label>
        <input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
      </p>
      <p>
        <label for="password" class="sr-only">Password</label>
        <input type="password" id="password" name="password" class="form-control" placeholder="Password" required>
      </p>
      <input name="_csrf" type="hidden" :value="csrfToken" />
      <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
    </form>
  </div>
</template>
<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import axios from 'axios'
import qs from 'qs'

@Options({
  created() {
    axios.defaults.baseURL = 'http://auth-server:9000/'
    this.getCsrfToken()
  },
  data() {
    return {
      loginForm: {
      },
      csrfToken: undefined
    }
  },
  methods: {
    onSubmit() {
      this.loginForm._csrf = this.csrfToken
      console.log(this.loginForm)
      axios({
        method:"post",
        url:"../login",
        headers: {
          "content-type": "application/x-www-form-urlencoded",
          "Upgrade-Insecure-Requests": 1,
          "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
        },
        withCredentials: false,
        data: qs.stringify(this.loginForm)
      }).then((res)=> {
        console.log(res)
      })
    },
    getCsrfToken() {
      axios.get('getCsrfToken').then(res => {
        console.log(res)
        this.csrfToken = res.data
      })
    }
  }
})
export default class Login extends Vue {}
</script>

