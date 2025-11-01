<script setup>
defineProps({
  msg: {
    type: String,
    required: true
  }
});
</script>

<template>
  <div class="card">
    <h2>{{ msg }}</h2>
    <p>프록시된 API 예시: <code>GET /api/health</code></p>
  </div>
  </template>

<style scoped>
.card {
  padding: 16px 20px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
}
</style>


