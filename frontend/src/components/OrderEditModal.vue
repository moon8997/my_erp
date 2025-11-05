<template>
  <div class="modal-backdrop" v-if="show" @click.self="closeModal">
    <div class="modal-content">
      <div class="modal-header">
        <h2>주문 수정</h2>
        <button class="close-button" @click="closeModal">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
        <form class="form" @submit.prevent="onSubmit">
          <div class="row">
            <label class="label" for="customerName">
              <i class="fas fa-building"></i> 상호명
            </label>
            <div class="field">
              <input
                id="customerName"
                v-model="form.customerName"
                type="text"
                readonly
              />
            </div>
          </div>

          <div class="row">
            <label class="label" for="saleDate">
              <i class="fas fa-calendar"></i> 주문일자 <span class="req">*</span>
            </label>
            <div class="field">
              <input
                id="saleDate"
                v-model="form.saleDate"
                type="date"
                class="date-input"
              />
            </div>
          </div>

          <div class="row items-row">
            <div class="items-header">
              <span class="label">
                <i class="fas fa-box"></i> 주문 상품 <span class="req">*</span>
              </span>
              <button type="button" class="btn add" @click="addItem">
                <i class="fas fa-plus"></i> 추가
              </button>
            </div>

            <div class="items">
              <div v-for="(item, idx) in form.items" :key="idx" class="item">
                <div class="field" style="position:relative;">
                  <label class="label" :for="`productName-${idx}`">
                    <i class="fas fa-tag"></i> 상품명
                  </label>
                  <input
                    :id="`productName-${idx}`"
                    v-model="item.productName"
                    type="text"
                    placeholder="상품명을 입력하세요"
                    @input="(e) => onProductInput(idx, e)"
                    @focus="onProductFocus(idx)"
                    @blur="() => hideProductDropdown(idx)"
                    autocomplete='off'
                    :ref="el => (productInputRefs[idx] = el)"
                  />
                  <ul v-if="productDropdown[idx] && (productFiltered[idx]?.length)" class="dropdown">
                    <li
                      v-for="p in productFiltered[idx]"
                      :key="p"
                      @mousedown="() => selectProduct(idx, p)"
                    >
                      {{ p }}
                    </li>
                  </ul>
                </div>

                <div class="field">
                  <label class="label" :for="`quantity-${idx}`">
                    수량
                  </label>
                  <input
                    :id="`quantity-${idx}`"
                    v-model.number="item.quantity"
                    type="number"
                    min="1"
                    placeholder="0"
                  />
                </div>

                <div class="field">
                  <label class="label" :for="`price-${idx}`">
                    가격
                  </label>
                  <input
                    :id="`price-${idx}`"
                    v-model.number="item.price"
                    type="number"
                    readonly
                  />
                </div>
                <button type="button" class="btn remove" @click="removeItem(idx)">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </div>
          </div>
          
          <div>
            <label class="label" for="totalAmount">
              <i class="fas fa-calculator"></i> 총액 <span class="req">*</span>
            </label>
            <div class="field">
              <input
                id="totalAmount"
                :value="form.items.reduce((sum, item) => sum + (item.price || 0) * (item.quantity || 0), 0)"
                type="number"
                min="0"
                placeholder="0"
                readonly
              />
            </div>
          </div>

          <div class="actions">
            <button type="submit" class="btn primary" :disabled="submitting">
              <i class="fas fa-check"></i> {{ submitting ? '저장 중...' : '저장' }}
            </button>
            <button type="button" class="btn" @click="closeModal">
              <i class="fas fa-times"></i> 취소
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="toast.show" class="toast">{{ toast.message }}</div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue';
import axios from '../api/client';
// 유틸리티 함수 임포트
import { getChosung, filterList } from '../utils/util';

export default {
  name: 'OrderEditModal',
  props: {
    show: {
      type: Boolean,
      required: true
    },
    orderData: {
      type: Object,
      required: true
    }
  },
  emits: ['close', 'update'],
  setup(props, { emit }) {
    const form = reactive({
      customerName: '',
      saleDate: '',
      items: []
    });

    const submitting = ref(false);
    const toast = ref({ show: false, message: '' });
    const productInputRefs = ref([]);

    // 토스트 메시지 표시 함수
    const showToast = (message) => {
      toast.value.message = message;
      toast.value.show = true;
      setTimeout(() => {
        toast.value.show = false;
      }, 2000);
    };

    // 상품 자동완성 관련 상태
    const productDropdown = ref({});
    const productFiltered = ref({});
    const productTimers = {};
    const allProductNames = ref([]);
    const allProducts = ref([]);
    const productChosungs = ref([]);

    // 주문 데이터 초기화
    const initializeForm = () => {
      console.log("11")
      if (props.orderData) {
        form.customerName = props.orderData.customerName;
        // 날짜를 YYYY-MM-DD 형식으로 변환
        const date = new Date(props.orderData.orderDate);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        form.saleDate = `${year}-${month}-${day}`;
        
        form.items = props.orderData.products.map(p => ({
          productName: p.name,
          quantity: p.quantity,
          price: p.price
        }));
      }
    };

    // 상품 목록 불러오기
    const fetchProducts = async () => {
      try {
        const response = await axios.get('/lookup/bootstrap');
        if (response.data.success) {
          allProducts.value = Array.isArray(response.data.products) ? response.data.products : [];
          allProductNames.value = allProducts.value.map(p => p.productName);
          productChosungs.value = allProductNames.value.map(getChosung);
        }
      } catch (error) {
        console.error('상품 목록 로드 실패:', error);
      }
    };

    // 상품 입력 관련 함수들
    const onProductInput = (idx, e) => {
      const term = e.target.value.trim();
      productDropdown.value[idx] = !!term;
      if (productTimers[idx]) clearTimeout(productTimers[idx]);
      productTimers[idx] = setTimeout(() => {
        productFiltered.value[idx] = filterList(term, allProductNames.value, productChosungs.value);
      }, 180);
    };

    const onProductFocus = (idx) => {
      productDropdown.value[idx] = true;
    };

    const selectProduct = (idx, name) => {
      form.items[idx].productName = name;
      productDropdown.value[idx] = false;
      
      const product = allProducts.value.find(p => p.productName === name);
      if (product && product.salePrice) {
        form.items[idx].price = product.salePrice;
      }
    };

    const hideProductDropdown = (idx) => {
      setTimeout(() => {
        productDropdown.value[idx] = false;
        const val = form.items[idx]?.productName?.trim();
        if (val && !allProductNames.value.includes(val)) {
          showToast('존재하지 않는 상품입니다.');
          const el = productInputRefs.value?.[idx];
          if (el && typeof el.focus === 'function') el.focus();
        } else if (val && allProductNames.value.includes(val)) {
          const product = allProducts.value.find(p => p.productName === val);
          if (product && product.salePrice) {
            form.items[idx].price = product.salePrice;
          }
        }
      }, 150);
    };

    // 아이템 추가/제거
    const addItem = () => {
      form.items.push({ productName: '', quantity: 1, price: 0 });
    };

    const removeItem = (idx) => {
      form.items.splice(idx, 1);
    };

    // 폼 제출
    const onSubmit = async () => {
      if (submitting.value) return;
      submitting.value = true;

      try {
        const payload = {
          customerName: form.customerName,
          saleDate: form.saleDate,
          items: form.items
            .filter(it => it.productName && it.productName.trim() !== '')
            .map(it => ({
              productName: it.productName,
              quantity: it.quantity,
            }))
        };

        const saleId = props.orderData?.saleId ?? props.orderData?.id;
        const response = await axios.put(`/sales/${saleId}`, payload);
        if (response.data.success) {
          showToast('주문이 수정되었습니다.');
          emit('update');
          closeModal();
        } else {
          throw new Error(response.data.message || '주문 수정 실패');
        }
      } catch (error) {
        console.error('주문 수정 실패:', error);
        showToast(error.response?.data?.message || '주문 수정 중 오류가 발생했습니다.');
      } finally {
        submitting.value = false;
      }
    };

    const closeModal = () => {
      emit('close');
    };



    // 라이프사이클 훅
    onMounted(() => {
      fetchProducts();
      // 컴포넌트가 처음 생성될 때 이미 show가 true이거나 orderData가 존재하는 경우 대비
      if (props.show && props.orderData) {
        initializeForm();
      }
    });

    // 모달 열린 상태로 변경될 때 초기화
    watch(() => props.show, (newVal) => {
      if (newVal) {
        initializeForm();
      }
    });

    // 부모에서 orderData가 갱신되는 경우에도 초기화
    watch(() => props.orderData, (newVal) => {
      if (props.show && newVal) {
        initializeForm();
      }
    });

    return {
      form,
      submitting,
      toast,
      productInputRefs,
      productDropdown,
      productFiltered,
      onProductInput,
      onProductFocus,
      selectProduct,
      hideProductDropdown,
      addItem,
      removeItem,
      onSubmit,
      closeModal
    };
  }
};
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

.field input[readonly] {
  background: #e9ecef;
  cursor: not-allowed;
}

.items-row { margin-top: 8px; }
.items-header { display: flex; justify-content: space-between; align-items: center; }
.items { background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 10px; padding: 16px; display: grid; gap: 12px; }
.item { display: grid; grid-template-columns: 2fr 0.8fr 1fr auto; gap: 10px; align-items: end; }

.actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
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

.btn.primary:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.btn.add {
  width: 106px;
}

.btn.remove {
  background: #fff0f0;
  border-color: #ffd1d1;
  width: 36px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dropdown {
  position: absolute;
  top: 40px;
  left: 0;
  right: 0;
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  box-shadow: 0 10px 28px rgba(0,0,0,0.12);
  z-index: 60;
  max-height: 240px;
  overflow-y: auto;
  padding: 6px 0;
}

.dropdown li {
  padding: 10px 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 14px;
}

.dropdown li:hover {
  background: #f9fafb;
}

.dropdown li + li {
  border-top: 1px solid #f3f4f6;
}

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

.date-input {
  width: 100%;
  -webkit-appearance: none;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    max-height: 95vh;
  }

  .modal-body {
    padding: 16px;
  }

  .item {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>