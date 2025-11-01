<template>
  <section class="register-container">
    <h1>회원가입</h1>
    <form @submit.prevent="register">
      <div>
        <label for="newId">아이디</label>
        <input id="newId" v-model="id" required />
      </div>
      <div>
        <label for="newPass">비밀번호</label>
        <input id="newPass" v-model="password" type="password" required />
      </div>
      <button type="submit">가입하기</button>
    </form>
    <div v-if="msg">{{ msg }}</div>
  </section>
</template>
<script setup>
import { ref } from 'vue';
import axios from '../api/client';
import './styles/common.css';

const id = ref('');
const password = ref('');
const msg = ref('');

async function register() {
  msg.value = '';
  try {
    const res = await axios.post('/account/register', {
      id: id.value,
      password: password.value
    });
    if (res.data.success) {
      msg.value = '가입 완료!';
      id.value = '';
      password.value = '';
    }
  } catch (e) {
    if (e.response && e.response.data && e.response.data.message) {
      msg.value = e.response.data.message;
    } else {
      msg.value = '가입 중 에러!';
    }
  }
}
</script>
<style scoped>
.register-container { max-width:320px; margin:60px auto; padding:32px 24px; background:#fff; border-radius:10px; box-shadow:0 2px 10px rgba(50,50,50,.1);}
form > div { margin-bottom:14px; }
label { display:block; margin-bottom:6px; font-weight:bold; }
input { width:100%; padding:8px; box-sizing:border-box; }
button { width:100%; padding:10px 0; background:#059; color:#fff; font-weight:bold; border:none; border-radius:6px; cursor:pointer;}
</style>
