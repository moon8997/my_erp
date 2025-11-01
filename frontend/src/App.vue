<template>
  <!-- 공통 헤더: 로그인 시에만 표시됨 (Header.vue 내부에서 제어) -->
  <Header />
  <router-view />
  
</template>

<script setup>
import Header from './components/Header.vue'
</script>

<style scoped>
body {
  background: #f6f8fa;
}
</style>


