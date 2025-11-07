<template>
  <div class="page">
    <div class="page-header">
      <h1>상품 리스트</h1>
      <p class="desc">등록된 상품을 조회하고 관리할 수 있습니다</p>
    </div>

    <div class="card">
      <div class="form">
        <div class="row">
          <div class="field" style="margin-bottom: 16px;">
            <input 
              type="text" 
              v-model="searchQuery" 
              placeholder="상품명 또는 진열위치로 검색" 
              @input="filterProducts"
            >
            <i class="fas fa-search" style="position: absolute; right: 12px; top: 50%; transform: translateY(-50%); color: var(--text-muted);"></i>
          </div>
          <div class="field grid-selector" style="margin-bottom: 16px; max-width: 120px; margin-left: auto;">
            <select v-model="gridColumns" class="form-select custom-select">
              <option value="2">2개씩 정렬</option>
              <option value="3">3개씩 정렬</option>
              <option value="4">4개씩 정렬</option>
              <option value="5">5개씩 정렬</option>
            </select>
            <i class="fas fa-th" style="position: absolute; right: 12px; top: 50%; transform: translateY(-50%); color: var(--text-muted); pointer-events: none;"></i>
          </div>
        </div>
      </div>

      <div class="grid" ref="productGrid" :style="gridStyle">
        <div v-for="product in displayedProducts" :key="product.productId" class="card" style="padding: 0; overflow: hidden;">
          <div style="position: relative; padding-top: 100%; overflow: hidden; background-color: #f8f9fa;">
            <template v-if="product.imageUrl">
              <img 
                :src="product.imageUrl" 
                :alt="product.productName" 
                style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover;"
              >
            </template>
            <template v-else>
              <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;">
                <i class="fas fa-box-open" style="font-size: 48px; color: #adb5bd;"></i>
              </div>
            </template>
          </div>
          <div style="padding: 16px;">
            <h3 class="product-title text-ellipsis" style="font-size: 16px; margin: 0 0 8px; line-height: 1.4;">
              {{ product.productName }}
            </h3>
            <p class="text-ellipsis" style="color: var(--text-secondary); font-size: 14px; margin: 0 0 12px;">
              <i class="fas fa-map-marker-alt"></i> {{ product.displayLocation }}
            </p>
            <div style="margin-bottom: 16px;">
              <p class="text-ellipsis" style="font-size: 14px; margin: 0 0 4px;">
                판매가: {{ formatPrice(product.salePrice) }}원
              </p>
              <p class="text-ellipsis" style="color: var(--text-secondary); font-size: 14px; margin: 0;">
                원가: {{ formatPrice(product.costPrice) }}원
              </p>
            </div>
            <div class="actions" style="margin: 0;">
              <button class="btn edit-btn" @click="editProduct(product.productId)" title="상품 수정">
                <i class="fas fa-edit"></i>
              </button>
              <button class="btn remove delete-btn" @click="deleteProduct(product.productId)" title="상품 삭제">
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
          </div>
        </div>
        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>상품을 불러오는 중...</p>
        </div>
      </div>
    </div>

    <!-- 토스트 메시지 -->
    <div v-if="toast.show" class="toast">
      {{ toast.message }}
    </div>

    <!-- 상품 수정 모달 -->
    <ProductEditModal 
      v-if="showEditModal"
      :show="showEditModal"
      :product-id="selectedProductId"
      @close="closeEditModal"
      @update="handleProductUpdate"
    />
  </div>
</template>

<script>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import ProductEditModal from '../components/ProductEditModal.vue'

export default {
  name: 'ProductList',
  components: {
    ProductEditModal
  },
  setup() {
    const router = useRouter()
    const products = ref([])
    const filteredProducts = ref([])
    const searchQuery = ref('')
    const loading = ref(false)
    const currentPage = ref(1)
    const itemsPerPage = 60
    const hasMore = ref(true)
    const productGrid = ref(null)
    const gridColumns = ref(parseInt(localStorage.getItem('productGridColumns')) || 3)
    const showEditModal = ref(false)
    const selectedProductId = ref(null)
    const isMobile = ref(false)

    // 윈도우 크기 변경 감지
    const handleResize = () => {
      isMobile.value = window.innerWidth <= 768
    }

    // 그리드 스타일 계산
    const gridStyle = computed(() => {
      // 모바일에서는 2열로 고정
      const columns = isMobile.value ? 2 : gridColumns.value
      const minWidth = 300 / (columns / 3)
      // 그리드 컬럼 설정 저장
      if (!isMobile.value) {
        localStorage.setItem('productGridColumns', columns.toString())
      }
      return {
        gridTemplateColumns: `repeat(${columns}, 1fr)`,
        gap: '20px',
        padding: '10px'
      }
    })

    // 화면에 표시할 상품 목록
    const displayedProducts = computed(() => {
      return filteredProducts.value.slice(0, currentPage.value * itemsPerPage)
    })

    const fetchProducts = async () => {
      try {
        const response = await axios.get('/api/products')
        if (response.data.success) {
          products.value = response.data.products
          filterProducts()
        } else {
          console.error('상품 목록을 불러오는데 실패했습니다:', response.data.message)
        }
      } catch (error) {
        console.error('상품 목록을 불러오는데 실패했습니다:', error)
      }
    }

    const filterProducts = () => {
      const query = searchQuery.value.toLowerCase()
      filteredProducts.value = products.value.filter(product => 
        (product.productName?.toLowerCase() || '').includes(query) ||
        (product.displayLocation?.toLowerCase() || '').includes(query)
      )
      currentPage.value = 1
      hasMore.value = filteredProducts.value.length > itemsPerPage
    }

    const loadMore = () => {
      if (loading.value || !hasMore.value) return

      loading.value = true
      setTimeout(() => {
        currentPage.value++
        hasMore.value = filteredProducts.value.length > currentPage.value * itemsPerPage
        loading.value = false
      }, 500)
    }

    const handleScroll = () => {
      if (!productGrid.value) return

      const gridRect = productGrid.value.getBoundingClientRect()
      const bottomOffset = gridRect.bottom - window.innerHeight

      if (bottomOffset < 200 && !loading.value && hasMore.value) {
        loadMore()
      }
    }

    const formatPrice = (price) => {
      return new Intl.NumberFormat('ko-KR').format(price)
    }

    const editProduct = (productId) => {
      selectedProductId.value = productId
      showEditModal.value = true
    }

    const closeEditModal = () => {
      showEditModal.value = false
      selectedProductId.value = null
    }

    const handleProductUpdate = () => {
      fetchProducts()
    }

    const toast = ref({ show: false, message: '' })

    const showToast = (message) => {
      toast.value.message = message
      toast.value.show = true
      setTimeout(() => {
        toast.value.show = false
      }, 3000)
    }

    const deleteProduct = async (productId) => {
      if (confirm('정말로 이 상품을 삭제하시겠습니까?')) {
        try {
          await axios.delete(`/api/products/${productId}`)
          await fetchProducts()
          showToast('상품이 성공적으로 삭제되었습니다')
        } catch (error) {
          console.error('상품 삭제에 실패했습니다:', error)
          showToast('상품 삭제에 실패했습니다')
        }
      }
    }

    onMounted(() => {
      fetchProducts()
      window.addEventListener('scroll', handleScroll)
      window.addEventListener('resize', handleResize)
      handleResize() // 초기 상태 설정
    })

    onUnmounted(() => {
      window.removeEventListener('scroll', handleScroll)
      window.removeEventListener('resize', handleResize)
    })

    return {
      products,
      filteredProducts,
      displayedProducts,
      searchQuery,
      loading,
      productGrid,
      gridColumns,
      gridStyle,
      formatPrice,
      filterProducts,
      editProduct,
      deleteProduct,
      toast,
      isMobile,
      showEditModal,
      selectedProductId,
      closeEditModal,
      handleProductUpdate
    }
  }
}
</script>

<style scoped>
.product-list-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

/* .page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 0.5rem;
} */

.description {
  color: #666;
  font-size: 1rem;
}

.actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.edit-btn, .delete-btn {
  padding: 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.edit-btn {
  background-color: #ffffff;
  color: #2196F3;
  border: 1px solid #2196F3;
}

.edit-btn:hover {
  background-color: #2196F3;
  color: white;
}

.delete-btn {
  background-color: var(--background);
  color: var(--danger, #dc3545);
  border: 1px solid var(--danger, #dc3545);
}

.delete-btn:hover {
  background-color: var(--danger, #dc3545);
  color: white;
}

@media (max-width: 480px) {
  .edit-btn, .delete-btn {
    padding: 6px;
    min-width: 32px;
    height: 32px;
  }
  
  .actions {
    gap: 4px;
  }
}
.actions {
  display: flex;
  gap: 0.5rem;
}

.edit-button,
.delete-button {
  flex: 1;
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  font-size: 0.875rem;
  transition: background-color 0.2s;
}

.edit-button {
  background-color: #2196F3;
  color: white;
}

.edit-button:hover {
  background-color: #1976D2;
}

.delete-button {
  background-color: #f44336;
  color: white;
}

.delete-button:hover {
  background-color: #d32f2f;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .product-list-container {
    padding: 1rem;
  }

  .actions {
    flex-direction: row;
    justify-content: flex-end;
    gap: 8px;
  }

  .search-box {
    max-width: none;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }

  .product-actions {
    flex-direction: column;
  }
}

/* 토스트 메시지 스타일 */
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
.loading {
  width: 100%;
  padding: 20px;
  text-align: center;
  grid-column: 1 / -1;
}

.spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 10px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 20px;
}

@media (max-width: 768px) {
  .grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}

@media (max-width: 480px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

.custom-select {
  appearance: none;
  background-color: var(--background);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 8px 32px 8px 12px;
  font-size: 14px;
  color: var(--text);
  cursor: pointer;
  transition: all 0.2s ease;
  width: 100%;
}

@media (max-width: 480px) {
  .grid-selector {
    display : none;
  }
}

.custom-select:hover {
  border-color: var(--primary);
}

.custom-select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2);
}

.text-ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}
</style>