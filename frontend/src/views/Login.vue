<template>
  <section class="login-container">
    <h1>로그인</h1>
    <form @submit.prevent="submit">
      <div>
        <label for="username">아이디</label>
        <input id="username" v-model="username" type="text" required />
      </div>
      <div>
        <label for="password">비밀번호</label>
        <input id="password" v-model="password" type="password" required />
      </div>
      <button type="submit">로그인</button>
    </form>
    <div v-if="error" class="error">{{ error }}</div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import './styles/common.css';

const username = ref('');
const password = ref('');
const error = ref('');

import axios from '../api/client';

async function submit() {
  error.value = '';
  if (!username.value || !password.value) {
    error.value = '아이디와 비밀번호를 입력하세요!';
    return;
  }

  try {
    const res = await axios.post('/account/login', {
      id: username.value,
      password: password.value
    });
    if (res.data.success) {
      // alert('로그인 성공!');
      // 로그인 성공 시 localStorage에 로그인 상태와 사용자 ID를 저장하고 index(메인) 페이지로 이동
      localStorage.setItem('isLoggedIn', 'true');
      localStorage.setItem('userId', res.data.userId);
      window.location.href = '/';
    }
  } catch (e) {
    if (e.response && e.response.data && e.response.data.message) {
      error.value = e.response.data.message;
    } else {
      error.value = '로그인 중 에러가 발생했습니다.';
    }
  }
}
</script>

<style scoped>
.login-container {
  max-width: 320px;
  margin: 60px auto;
  padding: 32px 24px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(50, 50, 50, .1);
}
form > div {
  margin-bottom: 14px;
}
label {
  display: block;
  margin-bottom: 6px;
  font-weight: bold;
}
input[type='text'], input[type='password'] {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
button {
  width: 100%;
  padding: 10px 0;
  background: #6366f1;
  color: #fff;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.error {
  margin-top: 10px;
  color: red;
}
</style>
