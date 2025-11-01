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
          <!-- <router-link to="/product/add" class="btn primary add">
            <i class="fas fa-plus"></i> 상품 추가
          </router-link> -->
        </div>
      </div>

      <div class="grid">
        <div v-for="product in filteredProducts" :key="product.id" class="card" style="padding: 0; overflow: hidden;">
          <div style="position: relative; padding-top: 100%; overflow: hidden;">
            <img 
              :src="product.imageUrl || '/default-product.png'" 
              :alt="product.name" 
              style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover;"
            >
          </div>
          <div style="padding: 16px;">
            <h3 style="font-size: 16px; margin: 0 0 8px;">{{ product.name }}</h3>
            <p style="color: var(--text-secondary); font-size: 14px; margin: 0 0 12px;">
              <i class="fas fa-map-marker-alt"></i> {{ product.displayLocation }}
            </p>
            <div style="margin-bottom: 16px;">
              <p style="font-size: 14px; margin: 0 0 4px;">판매가: {{ formatPrice(product.sellingPrice) }}원</p>
              <p style="color: var(--text-secondary); font-size: 14px; margin: 0;">원가: {{ formatPrice(product.costPrice) }}원</p>
            </div>
            <div class="actions" style="margin: 0;">
              <button class="btn" @click="editProduct(product.id)">
                <i class="fas fa-edit"></i> 수정
              </button>
              <button class="btn remove" @click="deleteProduct(product.id)">
                <i class="fas fa-trash-alt"></i> 삭제
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  name: 'ProductList',
  setup() {
    const router = useRouter()
    const products = ref([])
    const filteredProducts = ref([])
    const searchQuery = ref('')

    const fetchProducts = async () => {
      try {
        const response = await axios.get('/api/products')
        products.value = response.data
        filterProducts()
      } catch (error) {
        console.error('상품 목록을 불러오는데 실패했습니다:', error)
      }
    }

    const filterProducts = () => {
      const query = searchQuery.value.toLowerCase()
      filteredProducts.value = products.value.filter(product => 
        (product.name?.toLowerCase() || '').includes(query) ||
        (product.displayLocation?.toLowerCase() || '').includes(query)
      )
    }

    const formatPrice = (price) => {
      return new Intl.NumberFormat('ko-KR').format(price)
    }

    const editProduct = (productId) => {
      router.push(`/product/edit/${productId}`)
    }

    const confirmDelete = async (productId) => {
      if (confirm('정말로 이 상품을 삭제하시겠습니까?')) {
        try {
          await axios.delete(`/api/products/${productId}`)
          await fetchProducts()
        } catch (error) {
          console.error('상품 삭제에 실패했습니다:', error)
        }
      }
    }

    onMounted(fetchProducts)

    return {
      products,
      filteredProducts,
      searchQuery,
      formatPrice,
      filterProducts,
      editProduct,
      confirmDelete
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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  gap: 1rem;
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 400px;
}

.search-box input {
  width: 100%;
  padding: 0.75rem 2.5rem 0.75rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.search-icon {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
}

.add-button {
  background-color: #4CAF50;
  color: white;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1rem;
  transition: background-color 0.2s;
}

.add-button:hover {
  background-color: #45a049;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
}

.product-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image-container {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 1:1 Aspect Ratio */
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 1rem;
}

.product-name {
  font-size: 1.25rem;
  margin: 0 0 0.5rem;
  color: #333;
}

.product-location {
  color: #666;
  margin: 0 0 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.product-prices {
  margin-bottom: 1rem;
}

.selling-price {
  font-weight: bold;
  color: #333;
  margin: 0 0 0.25rem;
}

.cost-price {
  color: #666;
  margin: 0;
}

.product-actions {
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
    flex-direction: column;
    align-items: stretch;
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
</style>