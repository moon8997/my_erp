<template>
  <div class="page">
    <div class="page-header">
      <h1>상품 등록</h1>
      <p class="desc">새로운 상품 정보를 입력해주세요</p>
    </div>

    <div class="card">
      <form class="form" @submit.prevent="onSubmit">
        <!-- 상품 이미지 업로드 섹션 -->
        <div class="row" style="display: flex; justify-content: center;">
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
        <div class="row">
          <label class="label" for="name">
            <i class="fas fa-tag"></i> 상품명<span class="req">*</span>
          </label>
          <div class="field">
            <input 
              id="name"
              type="text" 
              v-model.trim="form.name" 
              placeholder="상품명을 입력하세요" 
              required
            />
          </div>
        </div>
        
        <!-- 가격 정보 -->
        <div class="row" style="grid-template-columns: 1fr 1fr; gap: 16px;">
          <div class="row">
            <label class="label" for="price">
              <i class="fas fa-won-sign"></i> 판매가격
            </label>
            <div class="field">
              <input 
                id="price"
                type="number" 
                v-model.number="form.price" 
                placeholder="판매가격을 입력하세요"
              />
            </div>
          </div>
          
          <div class="row">
            <label class="label" for="cost">
              <i class="fas fa-coins"></i> 원가
            </label>
            <div class="field">
              <input 
                id="cost"
                type="number" 
                v-model.number="form.cost" 
                placeholder="원가를 입력하세요"
              />
            </div>
          </div>
        </div>
        
        <!-- 진열 위치와 공급처 -->
        <div class="row" style="grid-template-columns: 1fr 1fr; gap: 16px;">
          <div class="row">
            <label class="label" for="location">
              <i class="fas fa-map-marker-alt"></i> 진열 위치
            </label>
            <div class="field">
              <input 
                id="location"
                type="text" 
                v-model.trim="form.location" 
                placeholder="진열 위치를 입력하세요"
              />
            </div>
          </div>

          <div class="row">
            <label class="label" for="supplier">
              <i class="fas fa-truck"></i> 공급처
            </label>
            <div class="field">
              <input 
                id="supplier"
                type="text" 
                v-model.trim="form.supplier" 
                placeholder="공급처를 입력하세요"
              />
            </div>
          </div>
        </div>
        
        <!-- 버튼 -->
        <div class="row" style="margin-top: 3px;">
          <div class="actions">
            <button type="button" class="btn" @click="$router.back()">
              <i class="fas fa-times"></i> 취소
            </button>
            <button type="submit" class="btn primary" :disabled="submitting || isCheckingDuplicate">
              <i class="fas fa-check"></i> {{ submitting ? '저장 중...' : '저장' }}
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import './styles/common.css'
// 폼 리셋은 유틸 없이 수동으로 처리합니다

export default {
  name: 'ProductAdd',
  setup() {
    const router = useRouter()
    const form = reactive({ 
      name: '', 
      price: null, 
      cost: null, 
      supplier: '', 
      location: '', 
      imageFile: null 
    })
    // 스냅샷 사용 없이 기본값으로 직접 리셋합니다
    
    const preview = ref('') 
    const submitting = ref(false) 
    const fileInput = ref(null) 
    const isCheckingDuplicate = ref(false) 
    const duplicateCheckTimer = ref(null) 
    const loading = ref(false)
    const toast = ref({ show: false, message: '' })

    const showToast = (message) => {
      toast.value.message = message
      toast.value.show = true
      setTimeout(() => {
        toast.value.show = false
      }, 3000)
    }

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
        const response = await axios.get('/api/products/check-duplicate', { 
          params: { productName: productName.trim() } 
        }) 
        
        if (response.data && response.data.success) { 
          return response.data.isDuplicate 
        } 
        return false 
      } catch (error) { 
        console.error('중복 검사 중 오류:', error) 
        showToast('중복 검사 중 오류가 발생했습니다.') 
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
        showToast('파일 크기는 10MB를 초과할 수 없습니다.') 
        return 
      } 
      
      // 이미지 파일 타입 체크 
      if (!file.type.match('image/jpeg') && !file.type.match('image/png')) { 
        showToast('JPG 또는 PNG 파일만 업로드 가능합니다.') 
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

    const onSubmit = async () => {
      // 제출 전 중복 검사
      const isDuplicate = await checkDuplicateProductName(form.name)
      if (isDuplicate) {
        showToast('이미 존재하는 상품명입니다. 다른 이름을 사용해주세요.', 'error')
        return
      }

      if (loading.value) return
      loading.value = true
    
      try {
        // 이미지 업로드
        let imageUrl = ''
        if (form.imageFile) {
          const formData = new FormData()
          formData.append('image', form.imageFile)
          
          const imageResponse = await axios.post('/api/products/upload-image', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
    
          if (!imageResponse.data.success) {
            throw new Error('이미지 업로드 실패')
          }
          imageUrl = imageResponse.data.imageUrl
        }
        
        // 상품 데이터 생성
        const productData = {
          productName: form.name,
          salePrice: form.price,
          costPrice: form.cost,
          supplier: form.supplier,
          displayLocation: form.location,
          imageUrl: imageUrl
        }
    
        // 상품 등록
        const response = await axios.post('/api/products/add', productData)
        
        if (response.data.success) {
          showToast('상품 등록 완료')
          resetForm()
        } else {
          throw new Error(response.data.message || '상품 등록 실패')
        }
      } catch (error) {
        showToast(error.message || '상품 등록 중 오류가 발생했습니다', 'error')
      } finally {
        loading.value = false
      }
    }

    // 성공 후 폼 초기화
    const resetForm = () => {
      // 수동으로 각 필드를 초기 상태로 되돌립니다
      form.name = ''
      form.price = null
      form.cost = null
      form.supplier = ''
      form.location = ''
      form.imageFile = null

      preview.value = ''
      if (fileInput.value) {
        fileInput.value.value = null
      }
      isCheckingDuplicate.value = false
      if (duplicateCheckTimer.value) {
        clearTimeout(duplicateCheckTimer.value)
        duplicateCheckTimer.value = null
      }
    }

    return {
      fileInput,
      preview,
      form,
      toast,
      submitting,
      isCheckingDuplicate,
      triggerFileInput,
      onFileChange,
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

.form { display: grid; gap: 15px; }

.row { display: grid; gap: 8px; }
.label { font-size: 14px; color: #0a0a0a; }
.req { color: #fb2c36; }

.field input {
  width: 100%;
  height: 36px;
  border-radius: 8px;
  border: 1px solid transparent;
  background: #f3f3f5;
  padding: 0 12px;
}

.btn {
  height: 36px;
  border-radius: 8px;
  border: 1px solid rgba(0,0,0,.1);
  background: #fff;
  cursor: pointer;
}
.btn.primary {
  background: #030213;
  color: #fff;
  border: none;
}
.btn.add {
  width: 106px;
}
.btn.remove {
  background: #fff0f0;
  border-color: #ffd1d1;
}

/* 이미지 업로드 스타일 */
.image-upload-container {
  width: 100%;
  max-width: 320px;
}

@media (max-width: 768px) {
  .image-upload-container {
    max-width: 213px;  /* 320px * (2/3) ≈ 213px */
  }
}
.image-upload-area {
  width: 100%;
  aspect-ratio: 1;
  border: 2px dashed #e5e7eb;
  border-radius: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

.image-upload-area:hover {
  border-color: #d1d5db;
  background: #f9fafb;
}

.image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.upload-icon {
  font-size: 48px;
  color: #9ca3af;
}

.upload-button-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.upload-icon-small {
  font-size: 16px;
  color: #4b5563;
}

.upload-link {
  color: #2563eb;
  font-size: 14px;
}

.upload-info {
  color: #6b7280;
  font-size: 12px;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-input {
  display: none;
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
