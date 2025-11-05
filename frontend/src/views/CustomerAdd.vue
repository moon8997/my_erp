<template>
  <div class="page">
    <div class="page-header">
      <h1>거래처 등록</h1>
      <p class="desc">새로운 거래처 정보를 입력해주세요</p>
    </div>

    <div class="card">
      <form class="form" @submit.prevent="onSubmit">
        <!-- 상호명 -->
        <div class="row">
          <label class="label" for="company">
            <i class="fas fa-building"></i> 상호명
            <span class="req">*</span>
          </label>
          <div class="field">
            <input
              id="company"
              type="text"
              v-model.trim="form.company"
              placeholder="상호명을 입력하세요"
              required
            />
          </div>
        </div>

        <!-- 전화번호 -->
        <div class="row">
          <label class="label" for="phone">
            <i class="fas fa-phone"></i> 전화번호
          </label>
          <div class="field">
            <input
              id="phone"
              type="tel"
              v-model.trim="form.phone"
              placeholder="010-0000-0000"
            />
          </div>
        </div>

        <!-- 주소 -->
        <div class="row">
          <label class="label" for="address">
            <i class="fas fa-map-marker-alt"></i> 주소
          </label>
          <div class="field">
            <input
              id="address"
              type="text"
              v-model.trim="form.address"
              placeholder="기본 주소를 입력하세요"
            />
          </div>
        </div>

        <!-- 버튼 -->
        <div class="row" style="margin-top: 16px;">
          <div class="actions">
            <button type="button" class="btn" @click="onCancel">
              <i class="fas fa-times"></i> 취소
            </button>
            <button type="submit" class="btn primary" :disabled="submitting">
              <i class="fas fa-check"></i> 거래처 등록
            </button>
          </div>
        </div>
      </form>
    </div>

    <!-- 토스트 메시지 -->
    <div v-if="toast.show" class="toast">
      {{ toast.message }}
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { deepClone, resetRefToSnapshot } from '../utils/util'
import { useRouter } from 'vue-router'
import axios from 'axios'
import './styles/common.css'

export default {
  name: 'CustomerAdd',
  setup() {
    const router = useRouter()
    const form = ref({
      company: '',
      phone: '',
      address: ''
    })
    const initialFormSnapshot = deepClone(form.value)
    const submitting = ref(false)
    const toast = ref({ show: false, message: '' })

    const showToast = (message) => {
      toast.value.message = message
      toast.value.show = true
      setTimeout(() => {
        toast.value.show = false
      }, 3000)
    }

    const onCancel = () => {
      if (window.history.length > 1) router.back()
      else router.push('/')
    }

    const onSubmit = async () => {
      if (submitting.value) return
      
      submitting.value = true
      try {
        const response = await axios.post('/api/customers/add', {
          companyName: form.value.company,
          phone: form.value.phone,
          address: form.value.address
        })

        if (response.data) {
          showToast('거래처 등록 완료')
          resetRefToSnapshot(form, initialFormSnapshot)
        }
      } catch (error) {
        console.error('거래처 등록 실패:', error)
        if (error.response?.status === 409) {
          showToast('이미 등록된 상호명입니다')
        } else {
          showToast('거래처 등록에 실패했습니다')
        }
      } finally {
        submitting.value = false
      }
    }

    return {
      form,
      submitting,
      toast,
      onCancel,
      onSubmit
    }
  }
}
</script>

<style scoped>
.page {
  max-width: 896px;
  margin: 32px auto;
}
@media (max-width: 896px) {
  .page {
    margin: 15px;
  }
}
.page-header h1 {
  font-size: 24px;
  margin: 0 0 8px 0;
}
.desc {
  color: #4a5565;
  margin: 0 0 16px 0;
}

.card {
  background: #fff;
  border: 1px solid rgba(0,0,0,.1);
  border-radius: 14px;
  padding: 24px;
}

.form { display: grid; gap: 16px; }

.row { display: grid; gap: 8px; }
.label { 
  font-size: 14px; 
  color: #0a0a0a;
  display: flex;
  align-items: center;
  gap: 8px;
}
.label i {
  color: #4a5565;
  width: 16px;
}
.req { color: #fb2c36; }

.field input {
  width: 100%;
  height: 36px;
  border-radius: 8px;
  border: 1px solid transparent;
  background: #f3f3f5;
  padding: 0 12px;
}

.actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.btn {
  height: 36px;
  border-radius: 8px;
  border: 1px solid rgba(0,0,0,.1);
  background: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.btn i {
  font-size: 14px;
}
.btn.primary {
  background: #030213;
  color: #fff;
  border: none;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 토스트 UI */
.toast {
  position: fixed;
  left: 50%;
  bottom: 24px;
  transform: translateX(-50%);
  background: #3b3c3d;
  color: #fff;
  padding: 10px 14px;
  border-radius: 8px;
  box-shadow: 0 6px 18px rgba(0,0,0,.2);
  z-index: 1000;
  font-size: 14px;
}
</style>