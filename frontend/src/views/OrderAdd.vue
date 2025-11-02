<template>
  <div class="page">
    <header class="page-header">
      <h1>주문 등록</h1>
      <p class="desc">주문 정보를 입력해주세요</p>
    </header>

    <section class="card">
      <form @submit.prevent="submitOrder" class="form">
        <div class="row">
          <label class="label" for="customerName">
            <i class="fas fa-building"></i> 상호명 <span class="req">*</span>
          </label>
          <div class="field with-icon" style="position:relative;">
            <input
              id="customerName"
              v-model="form.customerName"
              type="text"
              placeholder="상호명을 입력하세요"
              @input="onCustomerInput"
              @focus="showDropdown = true"
              @blur="hideDropdown"
              autocomplete='off'
              ref="customerInputRef"
            />
            <ul v-if="showDropdown && filteredCustomers.length" class="dropdown">
              <li
                v-for="c in filteredCustomers"
                :key="c"
                @mousedown="selectCustomer(c)"
              >
                {{ c }}
              </li>
            </ul>
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
            />
          </div>
        </div>

        <!-- 고객 상호명 기반 추천 상품 섹션 -->
        <div v-if="topProducts.length" class="top-products">
          <div class="top-products-header">해당 고객이 자주 주문한 상품</div>
          <div class="top-products-list">
            <button
              v-for="p in topProducts"
              :key="p"
              type="button"
              class="chip"
              @click="insertTopProduct(p)"
            >
              {{ p }}
            </button>
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
          <button type="submit" class="btn primary">
            <i class="fas fa-check"></i> 주문 등록
          </button>
          <button type="button" class="btn" @click="onCancel">
            <i class="fas fa-times"></i> 취소
          </button>
        </div>
      </form>
    </section>
    <div v-if="toast.show" class="toast">{{ toast.message }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from '../api/client';
import './styles/common.css'

// sales 테이블 컬럼을 참고한 폼 데이터 구조
// SALE_AT(saleDate), CUSTOMER_ID는 여기서는 상호명으로 입력받고
// 상품 항목은 PRODUCT_ID 대신 productName으로 입력, QUANTITY 포함
// CREATED_AT/UPDATED_AT은 전송 시점에 세팅, DELETED은 항상 0

const router = useRouter();

function todayStr() {
  const d = new Date();
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
}

const form = ref({
  customerName: '',
  saleDate: todayStr(),
  items: [
    { productName: '', quantity: 1, price: 0 },
  ],
});

// 초기 폼 스냅샷 (성공 후 기존값으로 복원하기 위함)
const initialFormSnapshot = JSON.parse(JSON.stringify(form.value));

const addItem = () => {
  form.value.items.push({ productName: '', quantity: 1, price: 0 });
};

const removeItem = (idx) => {
  form.value.items.splice(idx, 1);
};

// 고객 추천 상품 상태
const topProducts = ref([]);
const loadingTopProducts = ref(false);

const fetchTopProducts = async (companyName) => {
  const name = (companyName || '').trim();
  if (!name) { topProducts.value = []; return; }
  try {
    loadingTopProducts.value = true;
    const res = await axios.get('/customers/top-products', { params: { companyName: name } });
    const list = Array.isArray(res.data?.products) ? res.data.products : [];
    topProducts.value = list;
  } catch (e) {
    const status = e?.response?.status;
    // 주문 내역이 없는 경우(404/400 등)도 에러 표출 없이 빈 추천으로 처리
    if (status === 404 || status === 400 || status === 204) {
      topProducts.value = [];
    } else {
      console.error('top-products error', e);
      topProducts.value = [];
    }
  } finally {
    loadingTopProducts.value = false;
  }
};

// 입력 요소 참조 및 토스트
const customerInputRef = ref(null);
const productInputRefs = ref([]);
const toast = ref({ show: false, message: '' });
const showToast = (msg) => {
  toast.value.message = msg;
  toast.value.show = true;
  setTimeout(() => { toast.value.show = false; }, 2000);
};

// 성공 후 폼을 기존(초기)값으로 되돌리고 현재 페이지 유지
const resetFormToInitialValues = () => {
  // 폼 필드 초기화
  form.value = JSON.parse(JSON.stringify(initialFormSnapshot));

  // 추천/드롭다운/필터 상태 초기화
  topProducts.value = [];
  filteredCustomers.value = [];
  showDropdown.value = false;
  productDropdown.value = {};
  productFiltered.value = {};
  activeProductIndex.value = 0;

  // 포커스 첫 입력란으로 이동
  customerInputRef.value && customerInputRef.value.focus();
};

// 드롭다운 선택에 의한 블러는 검증 제외 플래그
let ignoreCustomerBlurValidation = false;
const ignoreProductBlurValidation = {};

const submitOrder = async () => {
  try {
    const payload = {
      customerName: form.value.customerName,
      saleDate: form.value.saleDate,
      items: form.value.items
        .filter(it => it.productName && it.productName.trim() !== '')
        .map((it) => ({
          productName: it.productName,
          quantity: it.quantity,
        })),
    };
    const res = await axios.post('/sales', payload);
    if (res.data && res.data.success) {
      showToast(`주문 등록 완료!`);
      // 성공 후 폼을 기존값으로 되돌리고 페이지에 머무르기
      resetFormToInitialValues();
    } else {
      showToast(res.data?.message || '주문 등록 실패');
    }
  } catch (e) {
    console.error(e);
    showToast(e.response?.data?.message || '주문 등록 중 오류가 발생했습니다');
  }
};

const onCancel = () => {
  router.back();
};

// 부트스트랩 데이터(전체 목록)
const allCustomerNames = ref([]);
const allProductNames = ref([]);
const allProducts = ref([]); // 가격 정보를 포함한 전체 상품 데이터

onMounted(async () => {
  try {
    // 고객 목록과 상품 정보 가져오기
    const bootstrapRes = await axios.get('/lookup/bootstrap');
    if (bootstrapRes.data.success) {
      // 고객 목록 설정
      allCustomerNames.value = Array.isArray(bootstrapRes.data.customers) ? bootstrapRes.data.customers : [];
      
      // 상품 정보 설정
      allProducts.value = Array.isArray(bootstrapRes.data.products) ? bootstrapRes.data.products : [];
      allProductNames.value = allProducts.value.map(p => p.productName);
    } else {
      console.error('데이터 로딩 실패:', bootstrapRes.data);
    }
    
    // 초성 캐시 계산
    customerChosungs.value = allCustomerNames.value.map(getChosung);
    productChosungs.value = allProductNames.value.map(getChosung);
  } catch (e) {
    console.error('bootstrap error', e);
  }
});

// 고객 자동완성 상태 및 로컬 필터
const showDropdown = ref(false);
const filteredCustomers = ref([]);
let customerTimer = null;
const customerChosungs = ref([]);

const onCustomerInput = (e) => {
  const term = e.target.value.trim();
  showDropdown.value = !!term;
  if (customerTimer) clearTimeout(customerTimer);
  customerTimer = setTimeout(() => {
    filteredCustomers.value = filterList(term, allCustomerNames.value, customerChosungs.value);
  }, 180);
};

const selectCustomer = (name) => {
  ignoreCustomerBlurValidation = true;
  form.value.customerName = name;
  showDropdown.value = false;
  // 정확한 상호명 선택 시 추천 상품 로드
  fetchTopProducts(name);
};

const hideDropdown = () => {
  setTimeout(() => {
    showDropdown.value = false;
    maybeLoadTopProducts();
    // 선택이 아닌 일반 블러일 때 존재 여부 검증
    const name = form.value.customerName?.trim();
    if (name && !allCustomerNames.value.includes(name) && !ignoreCustomerBlurValidation) {
      showToast('존재하지 않는 거래처입니다.');
      customerInputRef.value && customerInputRef.value.focus();
    }
    ignoreCustomerBlurValidation = false;
  }, 150);
};

const maybeLoadTopProducts = () => {
  const name = form.value.customerName.trim();
  if (!name) { topProducts.value = []; return; }
  if (allCustomerNames.value.includes(name)) {
    fetchTopProducts(name);
  } else {
    topProducts.value = [];
  }
};

// 상품 자동완성 상태 및 로컬 필터
const productDropdown = ref({}); // { idx: boolean }
const productFiltered = ref({}); // { idx: string[] }
const productTimers = {}; // { idx: number }
const productChosungs = ref([]);

const onProductInput = (idx, e) => {
  const term = e.target.value.trim();
  productDropdown.value[idx] = !!term;
  if (productTimers[idx]) clearTimeout(productTimers[idx]);
  productTimers[idx] = setTimeout(() => {
    productFiltered.value[idx] = filterList(term, allProductNames.value, productChosungs.value);
  }, 180);
};

const activeProductIndex = ref(0);
const onProductFocus = (idx) => {
  activeProductIndex.value = idx;
  productDropdown.value[idx] = true;
};

const selectProduct = (idx, name) => {
  ignoreProductBlurValidation[idx] = true;
  form.value.items[idx].productName = name;
  productDropdown.value[idx] = false;
  
  // 선택한 상품의 가격 자동 설정
  const product = allProducts.value.find(p => p.productName === name);
  if (product && product.salePrice) {
    form.value.items[idx].price = product.salePrice;
  }
};

const hideProductDropdown = (idx) => {
  setTimeout(() => {
    productDropdown.value[idx] = false;
    const val = form.value.items[idx]?.productName?.trim();
    if (val && !allProductNames.value.includes(val) && !ignoreProductBlurValidation[idx]) {
      showToast('존재하지 않는 상품입니다.');
      const el = productInputRefs.value?.[idx];
      if (el && typeof el.focus === 'function') el.focus();
    } else if (val && allProductNames.value.includes(val)) {
      // 유효한 상품명이면 가격 자동 설정
      const product = allProducts.value.find(p => p.productName === val);
      if (product && product.salePrice) {
        form.value.items[idx].price = product.salePrice;
      }
    }
    ignoreProductBlurValidation[idx] = false;
  }, 150);
};

// --------- 한글 초성 유틸 및 필터 ----------
const CHO = ['ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'];
const HANGUL_BASE = 0xAC00;
const HANGUL_LAST = 0xD7A3;
const getChosung = (str) => {
  if (!str) return '';
  let out = '';
  for (const ch of str) {
    const code = ch.codePointAt(0);
    if (code >= HANGUL_BASE && code <= HANGUL_LAST) {
      const idx = Math.floor((code - HANGUL_BASE) / 588);
      out += CHO[idx] || ch;
    } else {
      out += ch;
    }
  }
  return out;
};

const filterList = (term, names, chosungs) => {
  const q = term.trim();
  if (!q) return [];
  const qLower = q.toLowerCase();
  const qChosung = getChosung(q);
  const res = [];
  for (let i = 0; i < names.length; i++) {
    const name = names[i];
    const matchText = name.toLowerCase().includes(qLower);
    const matchCho = (chosungs[i] || '').includes(qChosung);
    if (matchText || matchCho) {
      res.push(name);
      if (res.length >= 10) break;
    }
  }
  return res;
};

const insertTopProduct = (name) => {
  // 이미 같은 이름의 상품이 있으면 수량 +1
  const existingIdx = form.value.items.findIndex(it => it.productName.trim() === name.trim());
  if (existingIdx !== -1) {
    form.value.items[existingIdx].quantity += 1;
    return;
  }

  // 마지막 빈 행을 우선 채우고, 없으면 새 행 추가
  let i = -1;
  if (form.value.items.length === 0) {
    form.value.items.push({ productName: '', quantity: 1, price: 0 });
    i = form.value.items.length - 1;
  } else {
    for (let k = form.value.items.length - 1; k >= 0; k--) {
      const it = form.value.items[k];
      const hasName = !!(it.productName && it.productName.trim());
      if (!hasName) { i = k; break; }
    }
    if (i === -1) {
      form.value.items.push({ productName: '', quantity: 1, price: 0 });
      i = form.value.items.length - 1;
    }
  }
  activeProductIndex.value = i;
  form.value.items[i].productName = name;
  productDropdown.value[i] = false;

  // 선택한 상품의 가격 자동 설정
  const product = allProducts.value.find(p => p.productName === name);
  if (product && product.salePrice) {
    form.value.items[i].price = product.salePrice;
  }
};
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

.items-row { margin-top: 8px; }
.items-header { display: flex; justify-content: space-between; align-items: center; }
.items { background: #f9fafb; border: 1px solid #e5e7eb; border-radius: 10px; padding: 16px; display: grid; gap: 12px; }
.item { display: grid; grid-template-columns: 2fr 0.8fr 1fr auto; gap: 10px; align-items: end; }

.actions { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

.btn { height: 36px; border-radius: 8px; border: 1px solid rgba(0,0,0,.1); background: #fff; cursor: pointer; }
.btn.primary { background: #030213; color: #fff; border: none; }
.btn.add { width: 106px; }
.btn.remove { background: #fff0f0; border-color: #ffd1d1; }

/* 공통 드롭다운 스타일 */
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
.dropdown li:hover { background: #f9fafb; }
.dropdown li + li { border-top: 1px solid #f3f4f6; }

/* 추천 상품 섹션 */
.top-products {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 12px;
}
.top-products-header {
  font-size: 14px;
  color: #374151;
  margin-bottom: 8px;
}
.top-products-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.chip {
  height: 28px;
  padding: 0 10px;
  border-radius: 16px;
  border: 1px solid #d1d5db;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
}
.chip:hover { background: #f3f4f6; }

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