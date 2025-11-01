<template>
  <div class="page">
    <div class="page-header">
      <h1>페이지를 찾을 수 없습니다</h1>
      <p class="desc">요청하신 페이지가 존재하지 않습니다. (404)</p>
    </div>
    <div class="card" style="text-align: center;">
      <router-link to="/" class="btn primary">홈으로 돌아가기</router-link>
    </div>
  </div>
</template>

<script>
import './styles/common.css'

export default {
  name: 'NotFound'
}
</script>
