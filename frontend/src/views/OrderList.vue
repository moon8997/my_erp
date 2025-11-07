<template>
  <div class="page">
      <div class="page-header">
      <div class="header-top">
        <h1>주문 관리</h1>
        <button class="btn primary payment-prep-btn" @click="goToCollectionPrep">수금준비</button>
      </div>
      <p class="desc">등록된 주문을 조회하고 관리할 수 있습니다</p>
    </div>

    <div class="card">
      <div class="order-list-header">
        <div class="search-container">
          <div class="field search-input">
            <i class="fas fa-search search-icon"></i>
            <input 
              type="text" 
              placeholder="상호명 또는 상품명으로 검색..."
              v-model="searchQuery"
            >
          </div>
          <button class="btn primary add-order-btn" @click="goToAddOrder">
            <i class="fas fa-plus"></i>
            주문 추가
          </button>
        </div>
        <div class="filter-container">
          <div class="date-filter-section">
            <div class="date-picker-container">
              <DatePicker
                  v-model:value="dateRange"
                  range
                  :format="'YYYY-MM-DD'"
                  :default-value="[new Date(), new Date()]"
                  :placeholder="'YYYY-MM-DD ~ YYYY-MM-DD'"
                  @change="handleDateChange"
                  @clear="handleClear"
                  :editable="false"
                />
            </div>
          </div>
        </div>
      </div>
      
      <div class="order-list-content">
        <div v-for="order in orders" :key="order.saleId || `${order.customerName}_${order.orderDate}`" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <h3>
                {{ order.customerName }}
                <i v-if="order.billStatus === 1" class="fas fa-check-circle bill-check-icon" aria-label="billed"></i>
              </h3>
              <p class="order-date">{{ formatDate(order.orderDate) }}</p>
            </div>
            <div class="order-actions">
              <button class="btn edit-btn" @click="editOrder(order)">
                <i class="fas fa-edit"></i>
              </button>
              <div class="order-badge">
                {{ order.products.length }}개 상품
              </div>
            </div>
          </div>

          <div class="products-container">
            <div v-for="(product, index) in order.products" :key="index" class="product-item">
              <div class="product-info">
                <p class="product-name">{{ product.name }}</p>
                <p class="product-quantity">수량: {{ product.quantity }}개</p>
              </div>
              <div class="product-actions">
                <p class="product-price">{{ formatPrice(product.price) }}</p>
                <button class="btn delete-btn" @click="deleteProduct(order, product)" text-size="small">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </div>
          </div>

          <div class="order-total">
            <span class="total-label">총 금액</span>
            <span class="total-amount">{{ formatPrice(order.totalAmount) }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="toast.show" class="toast">{{ toast.message }}</div>

    <!-- 주문 수정 모달 -->
    <OrderEditModal
      v-if="showEditModal"
      :show="showEditModal"
      :order-data="selectedOrder"
      @close="closeEditModal"
      @update="handleOrderUpdate"
    />

    <!-- 인쇄 전용 영역: A4(가로) 한 장에 영수증 3개 배치 -->
    <div class="print-area" v-if="showPrint">
      <div v-for="(page, pIdx) in printPages" :key="pIdx" class="print-page">
        <div class="print-grid">
          <div v-for="(receipt, idx) in page" :key="receipt.customerId + '_' + idx" class="receipt-cell">
            <SampleReceipt :order="receipt" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import DatePicker from 'vue-datepicker-next'
import 'vue-datepicker-next/index.css'
import './styles/common.css'
import OrderEditModal from '../components/OrderEditModal.vue'
import SampleReceipt from './SampleReceipt.vue'

export default {
  name: 'OrderList',
  components: {
    DatePicker,
    OrderEditModal,
    SampleReceipt
  },
  setup() {
    const router = useRouter()
    const searchQuery = ref('')
    const response = ref(null)
    const dateRange = ref([new Date(), new Date()])
    const toast = ref({
      show: false,
      message: ''
    })

    // Toast 표시 함수
    const showToast = (msg) => {
      toast.value.message = msg;
      toast.value.show = true;
      setTimeout(() => { toast.value.show = false; }, 2000);
    }

    // 오늘 날짜 가져오기 (서울 시간 기준)
    const today = new Date()
    
    // 날짜 문자열로 변환 (YYYY-MM-DD)
    const startOfDay = today.toISOString().split('T')[0]
    const endOfDay = startOfDay

    // 선택한 날짜 범위
    const selectedDateRange = ref({ start: startOfDay, end: endOfDay })

    // 오늘 날짜로 초기화하는 함수
    const resetToToday = () => {
      const today = new Date()
      const todayStr = today.toISOString().split('T')[0]
      dateRange.value = [today, today]
      selectedDateRange.value = { start: todayStr, end: todayStr }
      return fetchOrders()
    }

    // clear 버튼 클릭 시 오늘 날짜로 초기화
    const handleClear = () => {
      resetToToday()
    }
    
    const handleDateChange = async () => {
      if (dateRange.value && dateRange.value.length === 2) {
        // 날짜를 YYYY-MM-DD 형식으로 변환 (timezone 고려)
        const formatToYYYYMMDD = (date) => {
          const year = date.getFullYear()
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const day = String(date.getDate()).padStart(2, '0')
          return `${year}-${month}-${day}`
        }

        // 선택된 날짜 범위 업데이트
        selectedDateRange.value = {
          start: formatToYYYYMMDD(dateRange.value[0]),
          end: formatToYYYYMMDD(dateRange.value[1])
        }
        await fetchOrders()
      }
    }

    const orders = computed(() => {
      console.log(response.value)
      if (!response.value) return []

      // 주문 데이터를 saleId로 우선 그룹화하고, 없으면 고객명+주문일자로 그룹화
      const groupedOrders = response.value.reduce((acc, sale) => {
        const key = sale.saleId ?? `${sale.customerName}_${sale.saleAt}`
        if (!acc[key]) {
          acc[key] = {
            saleId: sale.saleId,
            customerName: sale.customerName,
            orderDate: sale.saleAt,
            products: {},
            totalAmount: 0,
            billStatus: Number(sale.billStatus || 0)
          }
        }

        // 청구 상태 (billStatus)가 하나라도 1이면 카드에 표시
        if (Number(sale.billStatus || 0) === 1) {
          acc[key].billStatus = 1
        }

        // 상품 ID를 키로 사용하여 같은 상품을 하나로 합침
        const productKey = sale.productId
        if (!acc[key].products[productKey]) {
          acc[key].products[productKey] = {
            productId: sale.productId,
            name: sale.productName,
            quantity: 0,
            price: sale.unitPrice
          }
        }

        acc[key].products[productKey].quantity += sale.quantity
        acc[key].totalAmount += sale.unitPrice
        return acc
      }, {})

      // products 객체를 배열로 변환
      Object.values(groupedOrders).forEach(order => {
        order.products = Object.values(order.products)
      })

      // 그룹화된 주문을 배열로 변환하고 날짜(오름차순) 우선, 같은 날짜 내 상호명 정렬
      return Object.values(groupedOrders)
        .sort((a, b) => {
          const dateCompare = new Date(a.orderDate) - new Date(b.orderDate)
          if (dateCompare !== 0) return dateCompare
          return a.customerName.localeCompare(b.customerName, 'ko')
        })
        .filter(order => {
          // 검색어로 필터링
          if (!searchQuery.value) return true
          const query = searchQuery.value.toLowerCase()
          return order.customerName.toLowerCase().includes(query) ||
                 order.products.some(p => p.name.toLowerCase().includes(query))
        })
    })

    const fetchOrders = async () => {
      try {
        const params = {}
        if (searchQuery.value) {
          params.searchQuery = searchQuery.value
        }
        if (selectedDateRange.value) {
          params.startDate = selectedDateRange.value.start
          params.endDate = selectedDateRange.value.end
        }

        const res = await axios.get('/api/sales', { params })
        response.value = res.data
      } catch (error) {
        console.error('Failed to fetch orders:', error)
      }
    }

    const formatDate = (date) => {
      const d = new Date(date)
      return `${d.getFullYear()}년 ${d.getMonth() + 1}월 ${d.getDate()}일`
    }

    const formatDateRange = (start, end) => {
      if (start === end) {
        return formatDate(start)
      }
      return `${formatDate(start)} ~ ${formatDate(end)}`
    }

    const formatPrice = (price) => {
      return price.toLocaleString('ko-KR') + '원'
    }

    const goToAddOrder = () => {
      router.push('/order/add')
    }

    // 인쇄용 데이터
    const showPrint = ref(false)
    const printOrders = ref([])

    const goToCollectionPrep = async () => {
      try {
        // billStatus가 0인(미청구) 항목만 대상으로 처리
        const sourceSales = (response.value || []).filter(sale => Number(sale?.billStatus || 0) === 0)
        if (sourceSales.length === 0) {
          showToast('영수증을 생성할 미청구 주문이 없습니다')
          return
        }

        // 고객별로 items를 유지하면서 그룹화 (CollectionPrep과 동일한 스키마)
        const grouped = sourceSales.reduce((acc, sale) => {
          const key = sale.customerId
          if (!acc[key]) {
            acc[key] = {
              customerId: sale.customerId,
              customerName: sale.customerName,
              orderDate: new Date(),
              items: [],
              totalAmount: 0
            }
          }
          acc[key].items.push({
            saleId: sale.saleId,
            saleAt: sale.saleAt,
            productId: sale.productId,
            productName: sale.productName,
            quantity: sale.quantity,
            productPrice: sale.productPrice,
            unitPrice: sale.unitPrice
          })
          acc[key].totalAmount += sale.unitPrice
          return acc
        }, {})
        // Bill 생성 API 요청 (고객별 1건) + 매핑용 salesIds 포함
        const billRequests = Object.values(grouped).map(order => {
          const salesIdsSet = new Set((order.items || []).map(it => it.saleId).filter(id => id != null))
          return {
            customerId: order.customerId,
            totalCost: order.totalAmount,
            salesIds: Array.from(salesIdsSet)
          }
        })
        try {
          if (billRequests.length > 0) {
            const res = await axios.post('/api/bills', billRequests)
            if (res?.data?.success) {
              showToast(`수금 준비용 청구서 생성 완료 (${res.data.inserted}건)`)    
              // 청구서 생성 후 상태 반영을 위해 목록 새로고침
              await fetchOrders()
            } else {
              showToast(res?.data?.message || '청구서 생성 실패')
            }
          } else {
            showToast('생성할 청구서가 없습니다')
          }
        } catch (err) {
          console.error('Failed to create bills:', err)
          showToast(err?.response?.data?.message || '청구서 생성 중 오류가 발생했습니다')
        }
        // 항목이 15개를 넘으면 여러 영수증으로 분할
        const MAX_ROWS = 15
        const chunked = []
        Object.values(grouped).forEach(order => {
          const items = order.items || []
          if (items.length === 0) {
            chunked.push(order)
            return
          }
          // 고객 전체 합계를 각 영수증에 동일하게 표시
          const customerTotal = items.reduce((sum, it) => sum + Number(it?.unitPrice || 0), 0)
          const pageCount = Math.ceil(items.length / MAX_ROWS)
          for (let i = 0; i < items.length; i += MAX_ROWS) {
            const slice = items.slice(i, i + MAX_ROWS)
            chunked.push({
              customerId: order.customerId,
              customerName: order.customerName,
              orderDate: order.orderDate,
              items: slice,
              totalAmount: customerTotal,
              receiptPageIndex: Math.floor(i / MAX_ROWS) + 1,
              receiptPageCount: pageCount
            })
          }
        })

        printOrders.value = chunked
        showPrint.value = true

        // 렌더 이후 인쇄 트리거
        nextTick(() => {
          window.print()
          // 인쇄 종료 후 영역 숨김
          setTimeout(() => { showPrint.value = false }, 300)
        })
      } catch (e) {
        console.error('Failed to prepare print data:', e)
      }
    }

    // 3개씩 묶어서 페이지 구성
    const printPages = computed(() => {
      const pages = []
      const arr = printOrders.value || []
      for (let i = 0; i < arr.length; i += 3) {
        pages.push(arr.slice(i, i + 3))
      }
      return pages
    })

    const showEditModal = ref(false)
    const selectedOrder = ref(null)

    const editOrder = async (order) => {
      try {
        // sale_id로 주문 상세 정보 조회
        const response = await axios.get(`/api/sales/byId/${order.saleId}`);
        const saleList = response.data;
        
        if (saleList && saleList.length > 0) {
          // 첫 번째 항목에서 상호명과 주문일자 가져오기
          const firstSale = saleList[0];
          
          // 상품 정보 매핑
          const products = saleList.map(sale => ({
            name: sale.productName,
            quantity: sale.quantity,
            price: sale.unitPrice
          }));

          selectedOrder.value = {
            id: order.saleId,
            customerName: firstSale.customerName,
            orderDate: firstSale.saleAt,
            products: products
          };
          
          showEditModal.value = true;
        } else {
          showToast('주문 정보를 찾을 수 없습니다.');
        }
      } catch (error) {
        console.error('Failed to fetch order details:', error);
        showToast('주문 정보를 불러오는 중 오류가 발생했습니다.');
      }
    }

    const deleteProduct = async (order, product) => {
      if (!confirm(`${product.name} 상품을 삭제하시겠습니까?`)) {
        return
      }
      try {
        // TODO: API 호출하여 상품 삭제
        await axios.delete(`/api/sales/${order.saleId}/products/${product.productId}`)
        await fetchOrders() // 목록 새로고침
        showToast(`${product.name} 상품이 삭제되었습니다.`)
      } catch (error) {
        console.error('Failed to delete product:', error)
        showToast('상품 삭제 중 오류가 발생했습니다.')
      }
    }

    onMounted(() => {
      fetchOrders()
    })

    const closeEditModal = () => {
      showEditModal.value = false
      selectedOrder.value = null
    }

    const handleOrderUpdate = () => {
      fetchOrders() // 목록 새로고침
    }

    return {
      searchQuery,
      orders,
      dateRange,
      selectedDateRange,
      handleDateChange,
      handleClear,
      formatDate,
      formatDateRange,
      formatPrice,
      goToAddOrder,
      goToCollectionPrep,
      showEditModal,
      selectedOrder,
      editOrder,
      deleteProduct,
      closeEditModal,
      handleOrderUpdate,
      toast,
      showPrint,
      printPages
    }
  }
}
</script>

<style scoped>
.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.payment-prep-btn {
  min-width: 106px;
  height: 32px;
}

.filter-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 16px;
  gap: 16px;
}

.date-filter-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.date-picker-container {
  background-color: white;
}

.date-filter-section :deep(.mx-datepicker) {
  width: 280px;
}

.date-filter-section :deep(.mx-input) {
  text-align: center;
  height: 38px;
  font-size: 14px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  background-color: #f8f9fa;
}

.date-filter-section :deep(.mx-input:hover) {
  border-color: #adb5bd;
}

.current-date {
  color: #2c3e50;
  font-size: 15px;
  font-weight: 500;
  background-color: #f8f9fa;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}
.search-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  gap: 16px;
}

.search-input {
  position: relative;
  flex: 1;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--placeholder-color);
}

.search-input input {
  padding-left: 40px;
}

.add-order-btn {
  min-width: 106px;
}

.order-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  margin-bottom: 16px;
  padding: 24px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.order-info h3 {
  font-size: 18px;
  color: var(--text-color);
  margin: 0;
  margin-bottom: 4px;
}

.bill-check-icon {
  color: #2ecc71; /* green */
  font-size: 16px;
  margin-left: 8px;
  vertical-align: middle;
}

.order-date {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.order-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.edit-btn {
  width: 25px;
  height: 25px;
  margin-bottom: 5px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius);
}


.order-badge {
  background: var(--input-bg);
  border-radius: var(--border-radius);
  padding: 4px 12px;
  font-size: 12px;
  color: var(--text-color);
}

.product-item {
  background: var(--bg-color);
  border-radius: var(--border-radius);
  padding: 12px;
  margin-bottom: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 14px;
  color: var(--text-color);
  margin: 0;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-quantity {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.product-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 120px;
  justify-content: flex-end;
}

.product-price {
  font-size: 14px;
  color: var(--primary-color);
  margin: 0;
  text-align: right;
  white-space: nowrap;
}

.delete-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 25px;
  height: 25px;
  background-color: var(--danger-color, #000000);
  color: white;
  border: none;
  border-radius: var(--border-radius);
  cursor: pointer;
  padding: 0;
  flex-shrink: 0;
}

.delete-btn:hover {
  background-color: var(--danger-dark, #000000);
}

.order-total {
  border-top: 1px solid var(--border-color);
  margin-top: 16px;
  padding-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-label {
  font-size: 14px;
  color: var(--text-color);
}

.total-amount {
  font-size: 14px;
  color: var(--primary-color);
  font-weight: 600;
}

/* 인쇄 레이아웃 */
.print-area { display: none; }
.receipt-cell { page-break-inside: avoid; }
.print-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8mm; }
.print-page { page-break-after: always; }

/* (중복 제거) 인쇄 전용 규칙은 아래 글로벌 <style> 블록에서 통합 정의 */

@media (max-width: 768px) {
  .search-container {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .add-order-btn {
    width: 100%;
    justify-content: center;
  }

  .date-filter-btn {
    width: 100%;
    justify-content: center;
  }

  .order-header {
    flex-direction: row;
    justify-content: space-between;
    align-items: flex-start;
    gap: 8px;
  }

  .order-badge {
    align-self: center;
  }

  .product-item {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    gap: 8px;
  }

  .product-info {
    flex: 1;
    min-width: 0;
  }

  .product-name {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .product-actions {
    flex-shrink: 0;
  }

  .product-price {
    text-align: right;
    white-space: nowrap;
  }

  .filter-container {
    position: relative;
    display: flex;
    justify-content: flex-end;
    margin-bottom: 16px;
    width: 100%;
  }

  .date-filter-section {
    width: 100%;
  }

  .date-filter-section :deep(.mx-datepicker) {
    width: 100%;
  }

  .date-picker-container {
    width: 100%;
  }
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

<!-- 글로벌 인쇄 규칙: 헤더 숨김, 여백 최소화, 영수증만 출력 -->
<style>
@media print {
  @page { size: A4 landscape; margin: 0 0 0 3mm; }
  html, body { margin: 0; padding: 0; }
  
  /* 전체 화면 요소 숨기고 인쇄 영역만 표시 */
  body * { visibility: hidden !important; }
  .print-area, .print-area * { visibility: visible !important; }
  .print-area { display: block !important; position: absolute; left: 0; top: 0; width: 100%; }

  /* 전역 헤더(네비게이션) 강제 숨김 */
  .header-container { display: none !important; }
  /* 일반 화면 요소 숨김 (스코프 스타일 내 요소 포함) */
  .page-header, .card, .toast { display: none !important; }

  /* 페이지 분할 공백 제거 */
  .print-page { page-break-after: always; }
  .print-page:last-child { page-break-after: auto; }

  /* 여백/간격 최소화 */
  .print-grid { gap: 4mm !important; align-content: start; }
  .receipt-cell { page-break-inside: avoid; break-inside: avoid; margin: 0; padding: 0; }
  
}
</style>
