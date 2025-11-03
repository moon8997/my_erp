<template>
  <div class="page">
    <div class="page-header">
      <h1>주문 관리</h1>
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
        <div v-for="order in orders" :key="`${order.customerName}_${order.orderDate}`" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <h3>{{ order.customerName }}</h3>
              <p class="order-date">{{ formatDate(order.orderDate) }}</p>
            </div>
            <div class="order-badge">
              {{ order.products.length }}개 상품
            </div>
          </div>

          <div class="products-container">
            <div v-for="(product, index) in order.products" :key="index" class="product-item">
              <div class="product-info">
                <p class="product-name">{{ product.name }}</p>
                <p class="product-quantity">수량: {{ product.quantity }}개</p>
              </div>
              <p class="product-price">{{ formatPrice(product.price * product.quantity) }}</p>
            </div>
          </div>

          <div class="order-total">
            <span class="total-label">총 금액</span>
            <span class="total-amount">{{ formatPrice(order.totalAmount) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import DatePicker from 'vue-datepicker-next'
import 'vue-datepicker-next/index.css'
import './styles/common.css'

export default {
  name: 'OrderList',
  components: {
    DatePicker
  },
  setup() {
    const router = useRouter()
    const searchQuery = ref('')
    const response = ref(null)
    const dateRange = ref([new Date(), new Date()])

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
      if (!response.value) return []

      // 주문 데이터를 고객명과 주문일자로 그룹화
      const groupedOrders = response.value.reduce((acc, sale) => {
        const key = `${sale.customerName}_${sale.saleAt}`
        if (!acc[key]) {
          acc[key] = {
            customerName: sale.customerName,
            orderDate: sale.saleAt,
            products: {},
            totalAmount: 0
          }
        }
        
        // 상품 ID를 키로 사용하여 같은 상품을 하나로 합침
        const productKey = sale.productId
        if (!acc[key].products[productKey]) {
          acc[key].products[productKey] = {
            name: sale.productName,
            quantity: 0,
            price: sale.unitPrice
          }
        }
        
        acc[key].products[productKey].quantity += sale.quantity
        acc[key].totalAmount += sale.quantity * sale.unitPrice
        return acc
      }, {})

      // products 객체를 배열로 변환
      Object.values(groupedOrders).forEach(order => {
        order.products = Object.values(order.products)
      })

      // 그룹화된 주문을 배열로 변환하고 상호명, 날짜순으로 정렬
      return Object.values(groupedOrders)
        .sort((a, b) => {
          // 먼저 상호명으로 정렬
          const nameCompare = a.customerName.localeCompare(b.customerName, 'ko')
          // 상호명이 같은 경우 날짜로 정렬 (오름차순 - 과거가 위로)
          if (nameCompare === 0) {
            return new Date(a.orderDate) - new Date(b.orderDate)
          }
          return nameCompare
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
        console.log(res.data)
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

    onMounted(() => {
      fetchOrders()
    })

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
      goToAddOrder
    }
  }
}
</script>

<style scoped>
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

.order-date {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
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

.product-name {
  font-size: 14px;
  color: var(--text-color);
  margin: 0;
  margin-bottom: 4px;
}

.product-quantity {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.product-price {
  font-size: 14px;
  color: var(--primary-color);
  margin: 0;
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
    flex-direction: column;
    gap: 8px;
  }

  .order-badge {
    align-self: flex-start;
  }

  .product-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .product-price {
    align-self: flex-end;
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
</style>