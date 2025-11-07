<template>
  <div class="page">
    <div class="page-header">
      <h1>거래처 리스트</h1>
      <p class="desc">등록된 거래처를 조회하고 관리할 수 있습니다</p>
    </div>

    <div class="card">
      <div class="form">
        <div class="row">
          <div class="field">
            <input
              type="text"
              v-model="searchQuery"
              placeholder="상호명, 전화번호, 주소로 검색"
            />
            <i class="fas fa-search" style="position: absolute; right: 12px; top: 50%; transform: translateY(-50%); color: var(--text-muted);"></i>
          </div>
        </div>
      </div>

      <div class="table-container">
        <table class="table">
          <thead>
            <tr>
              <th>상호명</th>
              <th>연락처</th>
              <th>주소</th>
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="6" class="text-center">
                <div class="loading">
                  <div class="spinner"></div>
                  <p>고객 정보를 불러오는 중...</p>
                </div>
              </td>
            </tr>
            <tr v-else-if="displayedCustomers.length === 0">
              <td colspan="6" class="text-center">
                {{ searchQuery ? '검색 결과가 없습니다' : '등록된 고객이 없습니다' }}
              </td>
            </tr>
            <tr v-else v-for="customer in displayedCustomers" :key="customer.customerId" class="hover:bg-gray-50">
              <td data-label="상호명">{{ customer.companyName }}</td>
              <td data-label="연락처">{{ customer.phone }}</td>
              <td data-label="주소" class="text-ellipsis" :title="customer.address">{{ customer.address }}</td>
              <td data-label="관리">
                <div class="actions">
                  <button class="btn" @click="editCustomer(customer.customerId)" title="고객 정보 수정">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button class="btn remove" @click="deleteCustomer(customer.customerId)" title="고객 삭제">
                    <i class="fas fa-trash-alt"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 토스트 메시지 -->
    <div v-if="toast.show" class="toast">
      {{ toast.message }}
    </div>

    <!-- 고객 수정 모달 -->
    <CustomerEditModal 
      v-if="showEditModal"
      :show="showEditModal"
      :customer-id="selectedCustomerId"
      @close="closeEditModal"
      @update="handleCustomerUpdate"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import CustomerEditModal from '../components/CustomerEditModal.vue'

export default {
  name: 'CustomerList',
  components: {
    CustomerEditModal
  },
  setup() {
    const customers = ref([])
    const loading = ref(false)
    const searchQuery = ref('')
    const showEditModal = ref(false)
    const selectedCustomerId = ref(null)
    const toast = ref({ show: false, message: '' })

    // 토스트 메시지 표시
    const showToast = (message) => {
      toast.value.message = message
      toast.value.show = true
      setTimeout(() => {
        toast.value.show = false
      }, 3000)
    }

    // 고객 목록 조회
    const fetchCustomers = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/customers')
        if (response.data.success) {
          customers.value = response.data.customers
        } else {
          showToast('고객 목록을 불러오는데 실패했습니다')
        }
      } catch (error) {
        console.error('고객 목록 조회 중 오류 발생:', error)
        showToast('고객 목록을 불러오는데 실패했습니다')
      } finally {
        loading.value = false
      }
    }

    // 고객 삭제
    const deleteCustomer = async (customerId) => {
      if (!confirm('정말로 이 고객을 삭제하시겠습니까?')) return

      try {
        const response = await axios.delete(`/api/customers/${customerId}`)
        if (response.data.success) {
          showToast('고객이 성공적으로 삭제되었습니다')
          await fetchCustomers()
        } else {
          throw new Error(response.data.message || '고객 삭제 실패')
        }
      } catch (error) {
        showToast(error.message || '고객 삭제 중 오류가 발생했습니다')
      }
    }

    // 고객 수정 모달 열기
    const editCustomer = (customerId) => {
      selectedCustomerId.value = customerId
      showEditModal.value = true
    }

    // 고객 수정 모달 닫기
    const closeEditModal = () => {
      showEditModal.value = false
      selectedCustomerId.value = null
    }

    // 고객 정보 업데이트 후 처리
    const handleCustomerUpdate = async () => {
      await fetchCustomers()
    }

    // 검색어에 따른 필터링된 고객 목록
    const displayedCustomers = computed(() => {
      const query = searchQuery.value.toLowerCase().trim()
      if (!query) return customers.value

      return customers.value.filter(customer => 
        (customer.companyName?.toLowerCase() || '').includes(query) ||
        (customer.phone || '').includes(query) ||
        (customer.address?.toLowerCase() || '').includes(query)
      )
    })

    // 날짜 포맷팅
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 컴포넌트 마운트 시 고객 목록 조회
    onMounted(() => {
      fetchCustomers()
    })

    return {
      customers,
      displayedCustomers,
      searchQuery,
      loading,
      showEditModal,
      selectedCustomerId,
      toast,
      formatDate,
      editCustomer,
      deleteCustomer,
      closeEditModal,
      handleCustomerUpdate
    }
  }
}
</script>

<style scoped>
/* 테이블 관련 스타일 */
.table-container {
  overflow-x: auto;
  margin: 0 -24px;
  -webkit-overflow-scrolling: touch;
}

.table {
  width: 100%;
  border-collapse: collapse;
  min-width: 800px;
}

.table th,
.table td {
  padding: 12px 24px;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.table th {
  font-weight: 600;
  color: var(--text-secondary);
  background: var(--bg-color);
  white-space: nowrap;
}

.table td {
  color: var(--text-color);
}

.text-ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

@media screen and (max-width: 768px) {
  .table-container {
    margin: 0 -16px;
  }

  .table {
    min-width: unset;
  }

  .table thead {
    display: none;
  }

  .table tbody tr {
    display: block;
    padding: 16px;
    border-bottom: 1px solid var(--border-color);
  }

  .table tbody td {
    display: flex;
    padding: 8px 0;
    border: none;
    align-items: center;
  }

  .table tbody td::before {
    content: attr(data-label);
    font-weight: 600;
    width: 100px;
    min-width: 100px;
    color: var(--text-secondary);
  }

  .text-ellipsis {
    max-width: unset;
    white-space: normal;
    overflow: visible;
    text-overflow: clip;
    word-break: break-word;
  }

  /* 주소 열은 모바일에서 줄바꿈 허용 */
  .table tbody td[data-label="주소"] {
    white-space: normal;
    overflow: visible;
    text-overflow: clip;
    word-break: break-word;
  }

  .actions {
    justify-content: flex-end;
    width: calc(100% - 100px);
  }
}

/* 로딩 스피너 */
.spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 10px;
  border: 3px solid var(--input-bg);
  border-top: 3px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 검색 박스 아이콘 위치 조정 */
.search-box {
  position: relative;
  max-width: 400px;
}

.search-box i {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
}

.search-box input {
  padding-left: 36px;
  width: 100%;
}

/* 반응형 스타일 */
@media (max-width: 896px) {
  .table-container {
    margin: 0 -16px;
  }

  .table th,
  .table td {
    padding: 12px 16px;
  }

  .text-ellipsis {
    max-width: 300px;
  }
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
  z-index: 1000;
  font-size: 14px;
}

/* 버튼 스타일 */
.btn.icon {
  padding: 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.btn.icon.danger {
  background-color: var(--background);
  color: var(--danger, #dc3545);
  border: 1px solid var(--danger, #dc3545);
}

.btn.icon.danger:hover {
  background-color: var(--danger, #dc3545);
  color: white;
}

@media (max-width: 480px) {
  .btn.icon {
    padding: 6px;
    min-width: 32px;
    height: 32px;
  }
  
  .actions {
    gap: 4px;
  }
}
</style>
