<template>
  <div class="page">
    <div class="page-header">
      <div class="header-top">
        <h1>수금 관리</h1>
      </div>
      <p class="desc">날짜, 상호명, 총/남은 금액을 확인하고 입금/완납 처리합니다</p>
    </div>

    <div class="card filters-card">
      <div class="order-list-header">
        <div class="search-container">
          <div class="field search-input">
            <i class="fas fa-search search-icon"></i>
            <input
              type="text" 
              placeholder="상호명으로 검색..."
              v-model="searchQuery"
            >
          </div>
          <button class="btn primary add-order-btn" @click="toggleUnsettled">
            {{ unsettledOnly ? '전체 보기' : '미납만 보기' }}
          </button>
        </div>
        <div class="filter-container">
          <div class="preset-filter-section">
            <div class="preset-buttons">
              <button class="btn" @click="setPreset('all')">전체</button>
              <button class="btn" @click="setPreset('thisWeek')">이번주</button>
              <button class="btn" @click="setPreset('lastWeek')">저번주</button>
              <button class="btn" @click="setPreset('thisMonth')">이번달</button>
              <button class="btn" @click="setPreset('thisYear')">올해</button>
            </div>
            <div class="custom-range">
              <input type="date" v-model="customStart" class="date-input" />
              <span class="tilde">~</span>
              <input type="date" v-model="customEnd" class="date-input" />
              <button class="btn" @click="applyCustomRange">적용</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="card"><div>불러오는 중...</div></div>
    <div v-else-if="visibleCards.length === 0" class="card"><div>표시할 데이터가 없습니다</div></div>

    <div class="bill-list">
      <div
        v-for="b in visibleCards"
        :key="b.billId"
        class="bill-card"
        :class="statusClass(b.status)"
      >
        <div class="bill-header">
          <div class="bill-info">
            <h3>{{ b.customerName }}</h3>
            <p class="bill-date">{{ formatDate(b.createdAt) }}</p>
          </div>
          <div class="bill-badge">
            상태: {{ statusLabel(b.status) }}
          </div>
        </div>

        <div class="bill-body">
          <div class="amount-row">
            <div>총 금액</div>
            <div class="amount">{{ formatPrice(b.totalCost) }}</div>
          </div>
          <div class="received-row">
            <div>받은 금액</div>
            <div class="received-amount">{{ formatPrice(b.totalCost - b.remainCost) }}</div>
          </div>
        </div>

        <div class="bill-actions">
          <div class="actions-left">
            <button class="btn info print-btn" @click="openReceiptModal(b)" style="margin-right: 6px;">영수증</button>
            <button class="btn warning rollback-btn" @click="rollback(b)">롤백</button>
          </div>
          <div class="actions-right" v-if="b.status !== 2">
            <input
              type="number"
              min="0"
              class="receive-input"
              v-model.number="receiveInputs[b.billId]"
              placeholder="받은금액"
              style="margin-right: 3px;"
            />
            <button class="btn primary apply-btn" @click="applyReceive(b)">반영</button>
            <button class="btn settle-btn" @click="settle(b)">완납</button>
          </div>
        </div>
        <div class="amount-row">
          <div>남은 금액</div>
        <div class="amount">{{ formatPrice(b.remainCost) }}</div>
          </div>
      </div>
    </div>

    <div v-if="toast.show" class="toast">{{ toast.message }}</div>

    <!-- 모달: 영수증 표시 -->
    <div v-if="showReceiptModal" class="modal-backdrop" @click.self="closeReceiptModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <button class="modal-print" @click="printCurrentReceipt" aria-label="인쇄">
            <i class="fas fa-print" aria-hidden="true"></i>
          </button>
          <button class="modal-close" @click="closeReceiptModal" aria-label="닫기">×</button>
        </div>
        <div class="modal-body">
          <SampleReceipt v-if="currentReceipt" :order="currentReceipt" />
        </div>
        <div class="modal-footer" v-if="modalReceipts.length > 1">
          <button class="btn" @click="prevReceipt" :disabled="modalPageIndex === 0">이전</button>
          <span class="page-indicator">{{ modalPageIndex + 1 }} / {{ modalReceipts.length }}</span>
          <button class="btn" @click="nextReceipt" :disabled="modalPageIndex >= modalReceipts.length - 1">다음</button>
        </div>
      </div>
    </div>

    <!-- 인쇄 전용 영역: OrderList.vue의 레이아웃과 동일한 크기 -->
    <div class="print-area" v-if="showPrint">
      <div class="print-page">
        <div class="print-grid">
          <div class="receipt-cell">
            <SampleReceipt v-if="currentReceipt" :order="currentReceipt" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick } from 'vue'
import axios from 'axios'
import './styles/common.css'
import SampleReceipt from './SampleReceipt.vue'

export default {
  name: 'BillList',
  components: { SampleReceipt },
  setup() {
    const loading = ref(false)
    const rows = ref([])
    const receiveInputs = ref({})
    const toast = ref({ show: false, message: '' })
    const searchQuery = ref('')
    const unsettledOnly = ref(true)

    const showToast = (msg) => {
      toast.value.message = msg
      toast.value.show = true
      setTimeout(() => { toast.value.show = false }, 2000)
    }

    const dateRange = ref([new Date('1970-01-01'), new Date('2099-12-31')])
    const selectedDateRange = ref({ start: '1970-01-01', end: '2099-12-31' })

    const handleClear = () => {
      dateRange.value = [new Date('1970-01-01'), new Date('2099-12-31')]
      selectedDateRange.value = { start: '1970-01-01', end: '2099-12-31' }
      fetchData()
    }

    const fmt = (d) => `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`

    const getThisWeek = () => {
      const now = new Date()
      const day = now.getDay() // 0=Sun..6=Sat
      const diffToMonday = (day + 6) % 7
      const monday = new Date(now); monday.setDate(now.getDate() - diffToMonday)
      const sunday = new Date(monday); sunday.setDate(monday.getDate() + 6)
      return { start: fmt(monday), end: fmt(sunday) }
    }

    const getLastWeek = () => {
      const now = new Date()
      const day = now.getDay()
      const diffToMonday = (day + 6) % 7
      const lastMonday = new Date(now); lastMonday.setDate(now.getDate() - diffToMonday - 7)
      const lastSunday = new Date(lastMonday); lastSunday.setDate(lastMonday.getDate() + 6)
      return { start: fmt(lastMonday), end: fmt(lastSunday) }
    }

    const getThisMonth = () => {
      const now = new Date()
      const first = new Date(now.getFullYear(), now.getMonth(), 1)
      const last = new Date(now.getFullYear(), now.getMonth() + 1, 0)
      return { start: fmt(first), end: fmt(last) }
    }

    const getThisYear = () => {
      const now = new Date()
      const first = new Date(now.getFullYear(), 0, 1)
      const last = new Date(now.getFullYear(), 11, 31)
      return { start: fmt(first), end: fmt(last) }
    }

    const setPreset = async (preset) => {
      let range
      switch (preset) {
        case 'all': range = { start: '1970-01-01', end: '2099-12-31' }; break
        case 'thisWeek': range = getThisWeek(); break
        case 'lastWeek': range = getLastWeek(); break
        case 'thisMonth': range = getThisMonth(); break
        case 'thisYear': range = getThisYear(); break
        default: range = { start: '1970-01-01', end: '2099-12-31' }
      }
      selectedDateRange.value = { ...range }
      await fetchData()
    }

    const customStart = ref('')
    const customEnd = ref('')
    const applyCustomRange = async () => {
      if (!customStart.value || !customEnd.value) return
      selectedDateRange.value = { start: customStart.value, end: customEnd.value }
      await fetchData()
    }

    const fetchData = async () => {
      loading.value = true
      try {
        const params = { startDate: selectedDateRange.value.start, endDate: selectedDateRange.value.end }
        const res = await axios.get('/api/bills', { params })
        if (res?.data?.success) {
          rows.value = res.data.bills || []
        } else {
          rows.value = []
        }
      } catch (err) {
        console.error('Failed to load bills:', err)
        rows.value = []
      } finally {
        loading.value = false
      }
    }

    const cards = computed(() => {
      return (rows.value || []).map(r => ({
        billId: r.billId,
        customerId: r.customerId,
        customerName: r.customerName,
        totalCost: Number(r.totalCost || 0),
        remainCost: Number(r.remainCost || 0),
        status: Number(r.status || 0),
        createdAt: r.createdAt
      }))
    })

    const visibleCards = computed(() => {
      const q = (searchQuery.value || '').trim().toLowerCase()
      return cards.value
        .filter(b => {
          if (!q) return true
          const name = (b.customerName || '').toLowerCase()
          return name.includes(q)
        })
        .filter(b => (unsettledOnly.value ? b.status !== 2 : true))
        .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
    })

    const formatPrice = (n) => {
      try { return Number(n).toLocaleString('ko-KR') + '원' } catch { return String(n) }
    }
    const formatDate = (dt) => {
      if (!dt) return '-'
      const d = new Date(dt)
      return `${String(d.getFullYear()).slice(0,4)}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
    }
    const statusLabel = (s) => {
      if (s === 2) return '완납'
      if (s === 1) return '부분미납'
      return '미납'
    }
    const statusClass = (s) => {
      return s === 2 ? 'status-green' : (s === 1 ? 'status-yellow' : 'status-red')
    }

    const applyReceive = async (b) => {
      const amount = Number(receiveInputs.value[b.billId] || 0)
      if (!amount || amount <= 0) {
        showToast('받은금액을 입력하세요')
        return
      }
      try {
        const res = await axios.put(`/api/bills/${b.billId}/receive`, { amount })
        if (res?.data?.success) {
          showToast('입금 반영 완료')
          receiveInputs.value[b.billId] = 0
          await fetchData()
        } else {
          showToast(res?.data?.message || '입금 반영 실패')
        }
      } catch (err) {
        console.error('Failed to apply receive:', err)
        showToast('입금 반영 중 오류가 발생했습니다')
      }
    }

    const settle = async (b) => {
      try {
        const res = await axios.put(`/api/bills/${b.billId}/settle`)
        if (res?.data?.success) {
          showToast('완납 처리 완료')
          await fetchData()
        } else {
          showToast(res?.data?.message || '완납 처리 실패')
        }
      } catch (err) {
        console.error('Failed to settle bill:', err)
        showToast('완납 처리 중 오류가 발생했습니다')
      }
    }

    const rollback = async (b) => {
      try {
        const res = await axios.put(`/api/bills/${b.billId}/rollback`)
        if (res?.data?.success) {
          showToast('롤백 완료')
          receiveInputs.value[b.billId] = 0
          await fetchData()
        } else {
          showToast(res?.data?.message || '롤백 실패')
        }
      } catch (err) {
        console.error('Failed to rollback bill:', err)
        showToast('롤백 중 오류가 발생했습니다')
      }
    }

    // 모달 영수증 표시 상태 및 데이터
    const showReceiptModal = ref(false)
    const modalReceipts = ref([])
    const modalPageIndex = ref(0)
    const currentReceipt = computed(() => modalReceipts.value[modalPageIndex.value] || null)

    const openReceiptModal = async (b) => {
      try {
        // 1) Bill에 연결된 salesId 목록 조회 (상태와 무관하게 billId 기준)
        let salesIds = []
        try {
          const res = await axios.get(`/api/bills/${b.billId}/sales-ids`)
          if (res?.data?.success) {
            salesIds = (res.data.salesIds || []).filter(id => Number.isFinite(Number(id))).map(id => Number(id))
          }
        } catch (e) {
          console.warn('Fallback to /api/bills/with-sales due to error:', e)
        }
        // Fallback: 구버전 API (status=0만 반환)를 통해 보조 조회
        if (salesIds.length === 0) {
          try {
            const res2 = await axios.get('/api/bills/with-sales')
            const rows2 = res2?.data?.rows || []
            salesIds = rows2
              .filter(r => Number(r?.billId) === Number(b.billId))
              .map(r => Number(r?.salesId))
              .filter(id => Number.isFinite(id))
          } catch (e2) {
            console.error('Failed to fetch sales IDs via fallback:', e2)
          }
        }

        // 2) 각 saleId에 대한 상세 품목 조회 및 통합
        const allItems = []
        if (salesIds.length > 0) {
          const lists = await Promise.all(salesIds.map(async (sid) => {
            try {
              const detail = await axios.get(`/api/sales/byId/${sid}`)
              return Array.isArray(detail?.data) ? detail.data : []
            } catch (e) {
              console.error('Failed to fetch sale details for', sid, e)
              return []
            }
          }))
          lists.forEach(list => {
            list.forEach(sale => {
              allItems.push({
                saleId: sale.saleId,
                saleAt: sale.saleAt,
                productId: sale.productId,
                productName: sale.productName,
                quantity: sale.quantity,
                productPrice: sale.productPrice,
                unitPrice: sale.unitPrice
              })
            })
          })
        }

        // 3) 합계/미수금 및 페이지 분할 계산
        const outstanding = 0
        const perPage = outstanding > 0 ? 14 : 15
        const customerTotal = Number.isFinite(Number(b.totalCost)) && Number(b.totalCost) > 0
          ? Number(b.totalCost)
          : allItems.reduce((sum, it) => sum + Number(it?.unitPrice || 0), 0)
        
        const chunked = []
        if (allItems.length === 0) {
          // 품목이 없으면 1페이지 합계/미수금만 표시
          chunked.push({
            customerId: b.customerId,
            customerName: b.customerName,
            orderDate: b.createdAt,
            items: [],
            totalAmount: customerTotal,
            outstandingAmount: outstanding,
            receiptPageIndex: 1,
            receiptPageCount: 1
          })
        } else {
          const pageCount = Math.ceil(allItems.length / perPage)
          for (let i = 0; i < allItems.length; i += perPage) {
            const slice = allItems.slice(i, i + perPage)
            chunked.push({
              customerId: b.customerId,
              customerName: b.customerName,
              orderDate: b.createdAt,
              items: slice,
              totalAmount: customerTotal,
              outstandingAmount: 0,
              receiptPageIndex: Math.floor(i / perPage) + 1,
              receiptPageCount: pageCount
            })
          }
        }

        // 4) 모달에 데이터 표시
        modalReceipts.value = chunked
        modalPageIndex.value = 0
        showReceiptModal.value = true
      } catch (e) {
        console.error('Failed to open receipt modal:', e)
      }
    }

    const closeReceiptModal = () => {
      showReceiptModal.value = false
      modalReceipts.value = []
      modalPageIndex.value = 0
    }
    const prevReceipt = () => {
      if (modalPageIndex.value > 0) modalPageIndex.value -= 1
    }
    const nextReceipt = () => {
      if (modalPageIndex.value < modalReceipts.value.length - 1) modalPageIndex.value += 1
    }

    // 인쇄: OrderList의 방식과 동일하게 별도 print-area 렌더 후 트리거
    const showPrint = ref(false)
    const printCurrentReceipt = async () => {
      showPrint.value = true
      await nextTick()
      window.print()
      setTimeout(() => { showPrint.value = false }, 300)
    }

    onMounted(() => { fetchData() })

    return {
      loading,
      rows,
      cards,
      visibleCards,
      dateRange,
      selectedDateRange,
      setPreset,
      customStart,
      customEnd,
      applyCustomRange,
      handleClear,
      formatPrice,
      formatDate,
      statusLabel,
      statusClass,
      applyReceive,
      settle,
      rollback,
      receiveInputs,
      toast,
      showToast,
      searchQuery,
      unsettledOnly,
      toggleUnsettled: () => { unsettledOnly.value = !unsettledOnly.value },
      // 모달 영수증
      showReceiptModal,
      modalReceipts,
      modalPageIndex,
      currentReceipt,
      openReceiptModal,
      closeReceiptModal,
      prevReceipt,
      nextReceipt,
      // 인쇄
      showPrint,
      printCurrentReceipt
    }
  }
}
</script>

<style scoped>
.filters-card { margin-bottom: 16px; }
.filter-container { display: flex; justify-content: flex-end; align-items: center; margin-bottom: 16px; gap: 16px; }
.preset-filter-section { display: flex; flex-direction: column; align-items: center; gap: 8px; width: 100%; }
.preset-buttons { display: flex; gap: 8px; justify-content: center; }
.custom-range { display: flex; gap: 8px; align-items: center; justify-content: center; }
.date-input { height: 32px; padding: 0 8px; border: 1px solid #dee2e6; border-radius: 4px; }
.tilde { color: #6c757d; }

.preset-buttons .btn {
  white-space: nowrap;
  flex-shrink: 0;
  padding: 6px 12px;
  font-size: 14px;
  line-height: 1;
}

@media (max-width: 480px) {
  .preset-buttons { flex-wrap: wrap; gap: 6px; }
  .preset-buttons .btn { font-size: 13px; padding: 6px 10px; }
  .custom-range { flex-wrap: wrap; }
  .date-input { width: 140px; }
}
.search-container { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; gap: 16px; }
.search-input { position: relative; flex: 1; }
.search-icon { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); color: var(--placeholder-color); }
.search-input input { padding-left: 40px; }
.add-order-btn { min-width: 106px; }
.bill-list { display: flex; flex-direction: column; gap: 12px; }
.bill-card { border: 1px solid var(--border-color); border-radius: var(--border-radius-lg); padding: 16px; }
.bill-header { display: flex; justify-content: space-between; align-items: flex-start; }
.bill-info h3 { margin: 0; font-size: 18px; }
.bill-date { font-size: 14px; color: var(--text-secondary); }
.bill-badge { background: var(--input-bg); border-radius: var(--border-radius); padding: 4px 12px; font-size: 12px; }
.bill-body { margin-top: 12px; }
.amount-row { display: flex; justify-content: space-between; padding: 8px 0; border-top: 1px solid var(--border-color); }
.amount { font-weight: 600; color: var(--primary-color); }
.bill-actions { display: flex; gap: 8px; margin-top: 12px; align-items: center; justify-content: space-between; }
.receive-input { width: 140px; height: 32px; padding: 0 8px; border: 1px solid #dee2e6; border-radius: 4px; }
.settle-btn { background-color: #2ecc71; color: white; border: none; }
.apply-btn { margin-right: 12px; }
.rollback-btn { background-color: #f1c40f; color: white; border: none; }
.print-btn { background-color: #3498db; color: white; border: none; }

.received-row { display: flex; justify-content: space-between; padding: 8px 0; border-top: 1px dashed var(--border-color); margin-top: 8px; }
.received-amount { font-weight: 600; color: #34495e; }

/* 상태별 배경색 */
.status-red { background-color: #ffe5e5; }
.status-yellow { background-color: #fff9db; }
.status-green { background-color: #e8f8f2; }

.toast { position: fixed; bottom: 16px; left: 50%; transform: translateX(-50%); background: rgba(0,0,0,0.7); color: #fff; padding: 8px 12px; border-radius: 6px; }

/* 인쇄 레이아웃: 화면에서는 숨기고 인쇄 시 글로벌 규칙으로 표시 */
/* 모달 스타일 */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.modal-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 24px rgba(0,0,0,0.2);
  /* width: 820px; */
  max-width: 95vw;
  max-height: 95vh;
  display: flex;
  flex-direction: column;
  position: relative;
}
.modal-close {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border: none;
  background: transparent;
  color: #666;
  font-size: 18px;
  cursor: pointer;
  border-radius: 50%;
}
.modal-close:hover {
  color: #000;
  background: rgba(0,0,0,0.06);
}
.modal-header { position: relative; height: 15px; }
.modal-print {
  position: absolute;
  top: 8px;
  right: 40px;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border: none;
  background: transparent;
  color: #666;
  font-size: 18px;
  cursor: pointer;
  border-radius: 50%;
}
.modal-print:hover {
  color: #000;
  background: rgba(0,0,0,0.06);
}
.modal-body { padding: 0px 36px 12px 16px; overflow: auto; }
.modal-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 12px 16px;
  border-top: 1px solid var(--border-color);
}
.page-indicator { color: var(--text-secondary); }
</style>

<style scoped>
/* 인쇄 레이아웃: 화면에서는 숨기고 인쇄 시에만 표시 */
.print-area { display: none; }
.print-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8mm; }
.receipt-cell { page-break-inside: avoid; }
.print-page { page-break-after: always; }

@media print {
  /* OrderList.vue와 동일한 인쇄 크기/레이아웃 적용 */
  @page { size: A4 landscape; margin: 0 0 0 3mm; }
  html, body { margin: 0; padding: 0; }
  .bill-card {display: none !important;}
  /* 전체 화면 요소 숨기고 인쇄 영역만 표시 */
  body * { visibility: hidden !important; }
  .print-area, .print-area * { visibility: visible !important; }
  .print-area { display: block !important; position: absolute; left: 0; top: 0; width: 100%; }

  /* BillList 화면 요소는 인쇄 시 숨김 */
  .page-header, .card, .toast, .modal-backdrop, .modal-container, .modal-header, .modal-body, .modal-footer { display: none !important; }

  /* 공백/여백 최소화 */
  .print-grid { gap: 4mm !important; align-content: start; }
  .receipt-cell { break-inside: avoid; margin: 0; padding: 0; }

  /* 마지막 페이지는 page-break 제거 */
  .print-page:last-child { page-break-after: auto; }
}
</style>
<!-- 모달 외부 전역 스타일 필요 시 추가 영역 -->
<style>

</style>

