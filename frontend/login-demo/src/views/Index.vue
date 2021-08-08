<template>
  <div class="home">
    <a href="https://client1-demo.lianpingkeji.net/">应用一，前后端不分离</a> <br/>
    <a href="https://client2-demo.lianpingkeji.net/#/index">应用二，前后端分离</a> <br/>
    <a href="https://auth-demo-docs.lianpingkeji.net/">对接文档地址</a>
  </div>
</template>

<script lang="ts">
import { Options, Vue } from 'vue-class-component'

@Options({
})
export default class Home extends Vue {}
</script>
