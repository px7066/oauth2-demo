<template>
  <div class="home">
    <p v-for="item in message" :key="item">{{item}}</p>
  </div>
</template>

<script lang="ts">
import { Options, Vue } from 'vue-class-component'
import axios from 'axios'

@Options({
  data() {
    return {
      message: ''
    }
  },
  created() {
    this.sample()
  },
  methods: {
    sample() {
      axios.get('messages').then(res => {
        this.message = res.data
      })
    }
  }
})
export default class Home extends Vue {}
</script>
