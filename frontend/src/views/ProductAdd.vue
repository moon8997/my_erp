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
            <label class="label" for="sellingPrice">
              <i class="fas fa-won-sign"></i> 판매가격
            </label>
            <div class="field">
              <input 
                id="sellingPrice"
                type="number" 
                v-model.number="form.sellingPrice" 
                placeholder="판매가격을 입력하세요"
              />
            </div>
          </div>
          
          <div class="row">
            <label class="label" for="costPrice">
              <i class="fas fa-coins"></i> 원가
            </label>
            <div class="field">
              <input 
                id="costPrice"
                type="number" 
                v-model.number="form.costPrice" 
                placeholder="원가를 입력하세요"
              />
            </div>
          </div>
        </div>
        
        <!-- 진열 위치와 공급처 -->
        <div class="row" style="grid-template-columns: 1fr 1fr; gap: 16px;">
          <div class="row">
            <label class="label" for="displayLocation">
              <i class="fas fa-map-marker-alt"></i> 진열 위치
            </label>
            <div class="field">
              <input 
                id="displayLocation"
                type="text" 
                v-model.trim="form.displayLocation" 
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
        <div class="row" style="margin-top: 16px;">
          <div class="actions">
            <button type="button" class="btn" @click="$router.back()">
              <i class="fas fa-times"></i> 취소
            </button>
            <button type="submit" class="btn primary">
              <i class="fas fa-check"></i> 저장
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
import { useRouter } from 'vue-router'
import axios from 'axios'
import './styles/common.css'

export default {
  name: 'ProductAdd',
  setup() {
    const router = useRouter()
    const fileInput = ref(null)
    const preview = ref(null)
    const form = ref({
      name: '',
      sellingPrice: null,
      costPrice: null,
      displayLocation: '',
      supplier: '',
      imageFile: null
    })
    const toast = ref({ show: false, message: '' })

    const showToast = (message) => {
      toast.value.message = message
      toast.value.show = true
      setTimeout(() => {
        toast.value.show = false
      }, 3000)
    }

    const triggerFileInput = () => {
      fileInput.value.click()
    }

    const onFileChange = (event) => {
      const file = event.target.files[0]
      if (!file) return

      if (!['image/jpeg', 'image/png'].includes(file.type)) {
        showToast('JPG 또는 PNG 파일만 업로드 가능합니다')
        return
      }

      if (file.size > 10 * 1024 * 1024) {
        showToast('파일 크기는 10MB를 초과할 수 없습니다')
        return
      }

      form.value.imageFile = file
      const reader = new FileReader()
      reader.onload = (e) => {
        preview.value = e.target.result
      }
      reader.readAsDataURL(file)
    }

    const onSubmit = async () => {
      // 상품명 필수 체크
      if (!form.value.name.trim()) {
        showToast('상품명은 필수 입력 항목입니다')
        return
      }

      try {
        const formData = new FormData()
        formData.append('name', form.value.name)
        if (form.value.sellingPrice) {
          formData.append('sellingPrice', form.value.sellingPrice)
        }
        if (form.value.costPrice) {
          formData.append('costPrice', form.value.costPrice)
        }
        if (form.value.displayLocation) {
          formData.append('displayLocation', form.value.displayLocation)
        }
        if (form.value.supplier) {
          formData.append('supplier', form.value.supplier)
        }
        if (form.value.imageFile) {
          formData.append('image', form.value.imageFile)
        }

        await axios.post('/api/products', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        router.push('/product/list')
      } catch (error) {
        console.error('상품 등록 실패:', error)
        showToast('상품 등록에 실패했습니다')
      }
    }

    return {
      fileInput,
      preview,
      form,
      toast,
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

.form { display: grid; gap: 16px; }

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
