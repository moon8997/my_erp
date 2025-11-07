<template>
  <div class="modal-backdrop" v-if="show" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>상품 수정</h2>
        <button class="close-button" @click="closeModal">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
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
              <button type="button" class="btn" @click="closeModal">
                <i class="fas fa-times"></i> 취소
              </button>
              <button type="submit" class="btn primary" :disabled="submitting">
                <i class="fas fa-check"></i> {{ submitting ? '저장 중...' : '저장' }}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <!-- 토스트 메시지 -->
    <div v-if="toast.show" class="toast">
      {{ toast.message }}
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import axios from 'axios'
import '../views/styles/common.css'

export default {
  name: 'ProductEditModal',
  props: {
    show: {
      type: Boolean,
      required: true
    },
    productId: {
      type: Number,
      required: true
    }
  },
  emits: ['close', 'update'],
  setup(props, { emit }) {
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
    const toast = ref({ show: false, message: '' })

    const showToast = (message) => {
      toast.value.message = message
      toast.value.show = true
      setTimeout(() => {
        toast.value.show = false
      }, 3000)
    }

    const fetchProductData = async () => {
      try {
        const response = await axios.get(`/api/products/${props.productId}`)
        if (response.data.success) {
          const product = response.data.product
          form.name = product.productName
          form.price = product.salePrice
          form.cost = product.costPrice
          form.supplier = product.supplier
          form.location = product.displayLocation
          preview.value = product.imageUrl
        }
      } catch (error) {
        console.error('상품 정보를 불러오는데 실패했습니다:', error)
        showToast('상품 정보를 불러오는데 실패했습니다')
      }
    }

    const triggerFileInput = () => { 
      fileInput.value.click() 
    }

    const onFileChange = (e) => { 
      const file = e.target.files?.[0] 
      if (!file) return 
      
      if (file.size > 10 * 1024 * 1024) { 
        showToast('파일 크기는 10MB를 초과할 수 없습니다.') 
        return 
      } 
      
      if (!file.type.match('image/jpeg') && !file.type.match('image/png')) { 
        showToast('JPG 또는 PNG 파일만 업로드 가능합니다.') 
        return 
      } 
      
      form.imageFile = file 
      
      const reader = new FileReader() 
      reader.onload = (e) => { 
        preview.value = String(reader.result || '') 
      } 
      reader.readAsDataURL(file)   
    }

    const onSubmit = async () => {
      if (submitting.value) return
      submitting.value = true
    
      try {
        let imageUrl = preview.value
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
    
        const productData = {
          productName: form.name,
          salePrice: form.price,
          costPrice: form.cost,
          supplier: form.supplier,
          displayLocation: form.location,
          imageUrl: imageUrl
        }
    
        const response = await axios.put(`/api/products/${props.productId}`, productData)
        
        if (response.data.success) {
          showToast('상품 수정 완료')
          emit('update')
          closeModal()
        } else {
          throw new Error(response.data.message || '상품 수정 실패')
        }
      } catch (error) {
        if (error.response && error.response.status === 409) {
          showToast('이미 등록된 상품명입니다')
        } else {
          showToast(error.message || '상품 수정 중 오류가 발생했습니다')
        }
      } finally {
        submitting.value = false
      }
    }

    const closeModal = () => {
      emit('close')
    }

    onMounted(() => {
      if (props.show) {
        document.body.classList.add('modal-open')
        fetchProductData()
      }
    })

    watch(() => props.show, (newVal) => {
      if (newVal) {
        document.body.classList.add('modal-open')
        fetchProductData()
      } else {
        document.body.classList.remove('modal-open')
      }
    })

    onUnmounted(() => {
      document.body.classList.remove('modal-open')
    })

    return {
      form,
      preview,
      submitting,
      fileInput,
      toast,
      triggerFileInput,
      onFileChange,
      onSubmit,
      closeModal
    }
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 14px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
  padding: 4px;
}

.close-button:hover {
  color: #000;
}

.modal-body {
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
  padding: 0 16px;
}

.btn.primary {
  background: #030213;
  color: #fff;
  border: none;
}

.actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 24px;
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

/* 토스트 메시지 */
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
  z-index: 1001;
  font-size: 14px;
}

</style>