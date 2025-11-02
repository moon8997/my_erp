<template>
  <div class="modal-overlay" v-if="show" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>고객 정보 수정</h2>
        <button class="close-button" @click="closeModal">
          <i class="fas fa-times"></i>
        </button>
      </div>
      <div class="modal-body">
        <form @submit.prevent="onSubmit" class="form">
          <div class="row">
            <label class="label" for="companyName">
              <i class="fas fa-building"></i> 상호명<span class="req">*</span>
            </label>
            <div class="field">
              <input
                id="companyName"
                type="text"
                v-model.trim="form.companyName"
                placeholder="상호명을 입력하세요"
                required
              />
            </div>
          </div>

          <div class="row">
            <label class="label" for="phone">
              <i class="fas fa-phone"></i> 전화번호<span class="req">*</span>
            </label>
            <div class="field">
              <input
                id="phone"
                type="tel"
                v-model.trim="form.phone"
                placeholder="전화번호를 입력하세요"
              />
            </div>
          </div>

          <div class="row">
            <label class="label" for="address">
              <i class="fas fa-map-marker-alt"></i> 주소<span class="req">*</span>
            </label>
            <div class="field">
              <input
                id="address"
                type="text"
                v-model.trim="form.address"
                placeholder="주소를 입력하세요"
              />
            </div>
          </div>

          <div class="row" style="margin-top: 16px;">
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
import { ref, reactive, onMounted, watch } from 'vue'
import axios from 'axios'

export default {
  name: 'CustomerEditModal',
  props: {
    show: {
      type: Boolean,
      required: true
    },
    customerId: {
      type: Number,
      required: true
    }
  },
  emits: ['close', 'update'],
  setup(props, { emit }) {
    const form = ref({
      companyName: '',
      phone: '',
      address: ''
    })
    
    const submitting = ref(false)
    const toast = ref({ show: false, message: '' })

    const showToast = (message) => {
      toast.value.message = message
      toast.value.show = true
      setTimeout(() => {
        toast.value.show = false
      }, 3000)
    }

    const validateForm = () => {
      if (!form.value.companyName.trim()) {
        showToast('상호명을 입력해주세요')
        return false
      }
      return true
    }

    const fetchCustomerData = async () => {
      if (!props.customerId) return

      try {
        const response = await axios.get(`/api/customers/${props.customerId}`)
        if (response.data.success) {
          const customer = response.data.customer
          form.value = {
            companyName: customer.companyName || '',
            phone: customer.phone || '',
            address: customer.address || ''
          }
        } else {
          throw new Error(response.data.message || '고객 정보를 불러오는데 실패했습니다')
        }
      } catch (error) {
        showToast(error.message || '고객 정보를 불러오는데 실패했습니다')
      }
    }

    const onSubmit = async () => {
      if (submitting.value) return
      if (!validateForm()) return
      submitting.value = true
    
      try {
        const customerData = {
          companyName: form.value.companyName.trim(),
          phone: form.value.phone.trim(),
          address: form.value.address.trim()
        }
    
        const response = await axios.put(`/api/customers/${props.customerId}`, customerData)
        
        if (response.data.success) {
          showToast('고객 정보가 성공적으로 수정되었습니다')
          emit('update')
          closeModal()
        } else {
          throw new Error(response.data.message || '고객 정보 수정 실패')
        }
      } catch (error) {
        if (error.response?.status === 409) {
          showToast('이미 등록된 상호명입니다')
        } else {
          showToast(error.message || '고객 정보 수정 중 오류가 발생했습니다')
        }
      } finally {
        submitting.value = false
      }
    }

    const closeModal = () => {
      form.value = {
        companyName: '',
        phone: '',
        address: ''
      }
      emit('close')
    }

    onMounted(() => {
      if (props.show) {
        fetchCustomerData()
      }
    })

    watch(() => props.show, (newVal) => {
      if (newVal) {
        fetchCustomerData()
      }
    })

    return {
      form,
      submitting,
      toast,
      onSubmit,
      closeModal
    }
  }
}
</script>

<style scoped>
/* 모달 관련 스타일 */
.modal-overlay {
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
  padding: 0 16px;
}

.btn.primary {
  background: #030213;
  color: #fff;
  border: none;
}

.actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
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

@media (max-width: 480px) {
  .modal-content {
    width: 95%;
    max-height: 95vh;
  }

  .modal-body {
    padding: 16px;
  }
}
</style>