<template>
  <div class="page add-customer" :aria-labelledby="ids.title">
    <div class="app">
      <div class="container header">
        <div class="heading">
          <h1 :id="ids.title">거래처 등록</h1>
        </div>
        <div class="paragraph">
          <p>새로운 거래처 정보를 입력해주세요</p>
        </div>
      </div>

      <div class="card">
        <form class="customer-form" @submit.prevent="onSubmit">
          <!-- 상호명 -->
          <div class="form-container">
            <div class="form-label">
              <label for="company">상호명 <span class="required">*</span></label>
            </div>
            <div class="input-wrapper">
              <input
                id="company"
                type="text"
                v-model.trim="form.company"
                placeholder="상호명을 입력하세요"
                class="form-input"
                required
              />
            </div>
          </div>

          <!-- 전화번호 -->
          <div class="form-container">
            <div class="form-label">
              <label for="phone">전화번호</label>
            </div>
            <div class="input-wrapper">
              <input
                id="phone"
                type="tel"
                v-model.trim="form.phone"
                placeholder="010-0000-0000"
                class="form-input"
              />
            </div>
          </div>

          <!-- 주소 -->
          <div class="form-container">
            <div class="form-label">
              <label for="address">주소</label>
            </div>
            <div class="input-wrapper">
              <input
                id="address"
                type="text"
                v-model.trim="form.address"
                placeholder="기본 주소를 입력하세요"
                class="form-input"
              />
            </div>
          </div>


          <!-- 버튼 -->
          <div class="form-actions">
            <button class="save-button" type="submit" :disabled="submitting">거래처 등록</button>
            <button class="cancel-button" type="button" @click="onCancel">취소</button>
          </div>
        </form>
      </div>
    </div>
  </div>
 </template>

 <script setup>
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/client'
import { useToast } from 'vue-toastification'
import './styles/common.css'

const router = useRouter()
const toast = useToast()
const ids = { title: 'add-customer-title' }

const form = reactive({
  company: '',
  phone: '',
  address: ''
})

const submitting = ref(false)
const isCheckingDuplicate = ref(false)
let duplicateCheckTimer = null

function onCancel() {
  // 뒤로가기 또는 홈으로 이동
  if (window.history.length > 1) router.back()
  else router.push('/')
}

async function onSubmit() {
  if (submitting.value) return
  
  // 제출 전 상호명 중복 검사
  const isDuplicate = await checkDuplicateCompanyName(form.company)
  if (isDuplicate) {
    toast.error('이미 존재하는 상호명입니다. 다른 이름을 사용해주세요.')
    return
  }

  submitting.value = true
  try {
    const payload = {
      companyName: form.company,
      phone: form.phone,
      address: form.address
    }
    // 백엔드 엔드포인트는 필요에 따라 조정하세요.
    const res = await api.post('/customers/add', payload)
    if (res.data && res.data.success) {
      toast.success('거래처가 등록되었습니다.')
      resetForm()
    } else {
      toast.error(res.data?.message || '거래처 등록에 실패했습니다.')
    }
  } catch (e) {
    toast.error('요청 중 오류가 발생했습니다.')
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  form.company = ''
  form.phone = ''
  form.address = ''
  form.addressDetail = ''
  form.memo = ''
}

// 상호명 중복 검사 API 호출 함수
async function checkDuplicateCompanyName(name) {
  const companyName = (name || '').trim()
  if (!companyName) return false
  try {
    isCheckingDuplicate.value = true
    const res = await api.get('/customers/check-duplicate', { params: { companyName } })
    if (res.data && res.data.success) {
      return !!res.data.isDuplicate
    }
    return false
  } catch (e) {
    console.error('상호명 중복 검사 오류:', e)
    toast.error('상호명 중복 검사 중 오류가 발생했습니다.')
    return false
  } finally {
    isCheckingDuplicate.value = false
  }
}

// 입력 중 실시간(디바운스) 중복 검사 및 알림
watch(() => form.company, (newVal) => {
  if (duplicateCheckTimer) {
    clearTimeout(duplicateCheckTimer)
  }
  const name = (newVal || '').trim()
  if (!name) return
  duplicateCheckTimer = setTimeout(async () => {
    const dup = await checkDuplicateCompanyName(name)
    if (dup) {
      toast.warning('이미 존재하는 상호명입니다.')
    }
  }, 700)
})
 </script>

<style scoped>
/* AddOrder.vue 스타일을 기준으로 통일 */
.page { max-width: 896px; margin: 32px auto; }
@media (max-width: 896px) { .page { margin: 15px; } }
.page-header h1 { font-size: 24px; margin: 0 0 8px 0; }
.desc { color: #4a5565; margin: 0 0 16px 0; }

.card {
  background: #fff;
  border: 1px solid rgba(0,0,0,.1);
  border-radius: 14px;
  padding: 24px;
}

.customer-form { display: flex; flex-direction: column; gap: 20px; }
.form-actions { margin-top: 8px; display: flex; gap: 12px; justify-content: center; }
.save-button, .cancel-button { height: 36px; border-radius: 8px; border: 1px solid rgba(0,0,0,.1); background: #fff; }
.save-button { background: #030213; color: #fff; border: none; }

.form-input { width: 100%; height: 36px; border-radius: 8px; border: 1px solid transparent; background: #f3f3f5; padding: 0 12px; }
</style>