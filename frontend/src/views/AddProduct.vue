<template>
  <div class="page add-product" :aria-labelledby="ids.title">
    <div class="app">
      <div class="container header">
        <div class="heading">
          <h1 :id="ids.title">상품 등록</h1>
        </div>
        <div class="paragraph">
          <p>새로운 상품 정보를 입력해주세요</p>
        </div>
      </div>

      <div class="card">
        <form class="add-product-form" @submit.prevent="onSubmit">
            <!-- 상품 이미지 업로드 섹션 -->
            <div class="form-container">
              <div class="form-label">
                <label>상품 이미지</label>
              </div>
              <div class="image-upload-container">
                <div class="image-upload-area" @click="triggerFileInput">
                  <div class="image-placeholder" v-if="!preview">
                    <div class="icon-container">
                      <div class="upload-icon">
                        <i class="fas fa-image"></i>
                      </div>
                    </div>
                    <div class="upload-button-container">
                      <div class="upload-icon-small">
                        <i class="fas fa-upload"></i>
                      </div>
                      <div class="upload-text">
                        <span class="upload-link">이미지 업로드</span>
                      </div>
                    </div>
                    <div class="upload-info">
                      <p>JPG, PNG 파일 (최대 10MB)</p>
                    </div>
                  </div>
                  <img v-else :src="preview" class="preview-image" alt="상품 이미지 미리보기" />
                </div>
                <input 
                  type="file" 
                  ref="fileInput" 
                  @change="onFileChange" 
                  accept="image/jpeg, image/png"
                  class="file-input"
                />
              </div>
            </div>
            
            <!-- 상품명 입력 필드 -->
            <div class="form-container">
              <div class="form-label">
                <label for="name">상품명<span class="required">*</span></label>
              </div>
              <div class="input-wrapper">
                <input 
                  id="name"
                  type="text" 
                  v-model.trim="form.name" 
                  placeholder="상품명을 입력하세요" 
                  class="form-input"
                  required
                />
              </div>
            </div>
            
            <!-- 판매가격 입력 필드 -->
            <div class="form-container two-col">
              <div class="form-container">
                <div class="form-label">
                  <label for="price">판매가격<span class="required">*</span></label>
                </div>
                <div class="input-container">
                  <div class="input-wrapper">
                    <input 
                      id="price"
                      type="number" 
                      v-model.number="form.price" 
                      placeholder="0" 
                      class="form-input"
                      min="0"
                      required
                    />
                  </div>
                  <div class="input-suffix">
                    <span>원</span>
                  </div>
                </div>
              </div>
              
              <!-- 원가 입력 필드 -->
              <div class="form-container">
                <div class="form-label">
                  <label for="cost">원가<span class="required">*</span></label>
                </div>
                <div class="input-container">
                  <div class="input-wrapper">
                    <input 
                      id="cost"
                      type="number" 
                      v-model.number="form.cost" 
                      placeholder="0" 
                      class="form-input"
                      min="0"
                    />
                  </div>
                  <div class="input-suffix">
                    <span>원</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 공급처 입력 필드 -->
            <div class="form-container two-col">
              <div class="form-container">
                <div class="form-label">
                  <label for="supplier">공급처<span class="required">*</span></label>
                </div>
                <div class="input-wrapper">
                  <input 
                    id="supplier"
                    type="text" 
                    v-model.trim="form.supplier" 
                    placeholder="공급처를 입력하세요" 
                    class="form-input"
                  />
                </div>
              </div>
              
              <!-- 진열 위치 입력 필드 -->
              <div class="form-container">
                <div class="form-label">
                  <label for="location">진열 위치<span class="required">*</span></label>
                </div>
                <div class="input-wrapper">
                  <input 
                    id="location"
                    type="text" 
                    v-model.trim="form.location" 
                    placeholder="진열 위치를 입력하세요" 
                    class="form-input"
                  />
                </div>
              </div>
            </div>
            
            <!-- 저장 버튼 -->
            <div class="form-actions">
              <button class="save-button" type="submit" :disabled="submitting">저장</button>
            </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import api from '../api/client'
import { useToast } from "vue-toastification"
import './styles/common.css'
// 페이지 컴포넌트로 사용하므로 모달 상태 제어 제거
const emit = defineEmits(['submit'])
const toast = useToast()

const ids = { title: `add-product-title` }

const form = reactive({
  name: '',
  price: null,
  cost: null,
  supplier: '',
  location: '',
  imageFile: null
})

const preview = ref('')
const submitting = ref(false)
const fileInput = ref(null)
const isCheckingDuplicate = ref(false)
const duplicateCheckTimer = ref(null)

function triggerFileInput() {
  fileInput.value.click()
}

// 상품명 중복 검사 함수
async function checkDuplicateProductName(productName) {
  if (!productName || productName.trim().length === 0) {
    return false
  }
  
  try {
    isCheckingDuplicate.value = true
    const response = await api.get('/products/check-duplicate', {
      params: { productName: productName.trim() }
    })
    
    if (response.data && response.data.success) {
      return response.data.isDuplicate
    }
    return false
  } catch (error) {
    console.error('중복 검사 중 오류:', error)
    toast.error('중복 검사 중 오류가 발생했습니다.')
    return false
  } finally {
    isCheckingDuplicate.value = false
  }
}

function onFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  
  // 파일 크기 체크 (10MB 제한)
  if (file.size > 10 * 1024 * 1024) {
    alert('파일 크기는 10MB를 초과할 수 없습니다.')
    return
  }
  
  // 이미지 파일 타입 체크
  if (!file.type.match('image/jpeg') && !file.type.match('image/png')) {
    alert('JPG 또는 PNG 파일만 업로드 가능합니다.')
    return
  }
  
  form.imageFile = file
  
  // 이미지 미리보기 생성
  const reader = new FileReader()
  reader.onload = (e) => {
    preview.value = String(reader.result || '')
  }
  reader.readAsDataURL(file)
}

// // 실시간 상품명 중복 검사 (디바운싱)
// watch(() => form.name, (newName) => {
//   if (duplicateCheckTimer.value) {
//     clearTimeout(duplicateCheckTimer.value)
//   }
  
//   if (newName && newName.trim().length > 0) {
//     duplicateCheckTimer.value = setTimeout(async () => {
//       const isDuplicate = await checkDuplicateProductName(newName)
//       if (isDuplicate) {
//         toast.warning('이미 존재하는 상품명입니다.')
//       }
//     }, 800) // 800ms 디바운싱
//   }
// })

async function onSubmit() {
  if (submitting.value) return
  
  // 제출 전 중복 검사
  const isDuplicate = await checkDuplicateProductName(form.name)
  if (isDuplicate) {
    toast.error('이미 존재하는 상품명입니다. 다른 이름을 사용해주세요.')
    return
  }
  
  submitting.value = true
  try {
    // 백엔드 Product 모델에 맞는 페이로드 구성
    const payload = {
      productName: form.name,
      salePrice: form.price,
      costPrice: form.cost,
      imageUrl: preview.value || ''
    }
    const res = await api.post('/products/add', payload)
    if (res.data && res.data.success) {
      toast.success('상품이 성공적으로 추가되었습니다.')
      resetForm()
    } else {
      toast.error(res.data?.message || '상품 추가에 실패했습니다.')
    }
  } catch (error) {
    console.error('상품 추가 중 오류:', error)
    toast.error('상품 추가 중 오류가 발생했습니다.')
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  form.name = ''
  form.price = null
  form.cost = null
  form.supplier = ''
  form.location = ''
  form.imageFile = null
  preview.value = ''
}
</script>

<style scoped>
/* AddOrder.vue 스타일을 기준으로 통일 */
.page {
  max-width: 896px;
  margin: 32px auto;
}
@media (max-width: 896px) {
  .page { margin: 15px; }
}
.page-header h1 {
  font-size: 24px;
  margin: 0 0 8px 0;
}
.desc { color: #4a5565; margin: 0 0 16px 0; }

.card {
  background: #fff;
  border: 1px solid rgba(0,0,0,.1);
  border-radius: 14px;
  padding: 24px;
}

/* 기존 폼 레이아웃 유지, 색감/간격만 조정 */
.add-product-form { display: flex; flex-direction: column; gap: 24px; }
.two-col { display: grid; grid-template-columns: 1fr; gap: 16px; }
@media (min-width: 768px) { .two-col { grid-template-columns: 1fr 1fr; } }

.preview-image { max-width: 100%; max-height: 200px; border-radius: 10px; }

.form-label label { font-size: 14px; color: #0a0a0a; }
.required { color: #fb2c36; }

.form-input,
.input-container input {
  width: 100%;
  height: 36px;
  border-radius: 8px;
  border: 1px solid transparent;
  background: #f3f3f5;
  padding: 0 12px;
}

.form-actions { margin-top: 20px; display: flex; justify-content: center; }
.save-button { height: 36px; border-radius: 8px; border: 1px solid rgba(0,0,0,.1); background: #030213; color: #fff; }
.upload-info p { color: #4a5565; }
</style>
