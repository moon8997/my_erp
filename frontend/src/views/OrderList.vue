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
          <button class="btn date-filter-btn" @click="toggleDatePicker">
            <i class="far fa-calendar-alt"></i>
            기간 선택
          </button>
          <!-- Date picker component will be added here -->
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
import './styles/common.css'

export default {
  name: 'OrderList',
  setup() {
    const router = useRouter()
    const searchQuery = ref('')
    const response = ref(null)
    const selectedDateRange = ref({
      start: new Date().toISOString().split('T')[0],
      end: new Date().toISOString().split('T')[0]
    })

    const orders = computed(() => {
      if (!response.value?.data) return []

      // 주문 데이터를 고객명과 주문일자로 그룹화
      const groupedOrders = response.value.data.reduce((acc, sale) => {
        const key = `${sale.customerName}_${sale.saleAt}`
        if (!acc[key]) {
          acc[key] = {
            customerName: sale.customerName,
            orderDate: sale.saleAt,
            products: [],
            totalAmount: 0
          }
        }
        
        acc[key].products.push({
          name: sale.productName,
          quantity: sale.quantity,
          price: sale.productPrice
        })
        
        acc[key].totalAmount += sale.quantity * sale.productPrice
        return acc
      }, {})

      // 그룹화된 주문을 배열로 변환하고 날짜순으로 정렬
      return Object.values(groupedOrders)
        .sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate))
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
      selectedDateRange,
      formatDate,
      formatPrice,
      goToAddOrder
    }
  }
}
</script>

<style scoped>
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
}
</style>