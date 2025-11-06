<template>
  <div class="page">
    <div class="page-header">
      <div class="header-top">
        <h1>수금 준비</h1>
        <button class="btn" @click="goBack">뒤로가기</button>
      </div>
      <p class="desc">현재 조회된 데이터를 고객별로 영수증으로 묶어 표시합니다</p>
    </div>

    <div class="card">
      <div v-if="receipts.length === 0" class="empty">표시할 데이터가 없습니다. 주문 목록에서 수금준비를 눌러주세요.</div>
      <div v-else class="receipts-container">
        <div v-for="receipt in receipts" :key="receipt.customerId" class="receipt-wrapper">
          <div class="receipt-header">
            <h3>{{ receipt.customerName }}</h3>
            <span class="date">{{ formatDate(receipt.orderDate) }}</span>
          </div>
          <SampleReceipt :order="receipt" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import SampleReceipt from './SampleReceipt.vue'

export default {
  name: 'CollectionPrep',
  components: { SampleReceipt },
  setup() {
    const receipts = ref([])

    const formatDate = (date) => {
      const d = new Date(date)
      return `${d.getFullYear()}년 ${d.getMonth() + 1}월 ${d.getDate()}일`
    }

    const loadData = () => {
      try {
        const raw = sessionStorage.getItem('collectionPrepData')
        if (!raw) return
        const data = JSON.parse(raw)
        // 고객별 그룹화: 개별 판매 행(items)로 유지
        const grouped = data.reduce((acc, sale) => {
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

        receipts.value = Object.values(grouped)
      } catch (e) {
        console.error('Failed to load collection prep data:', e)
      }
    }

    const goBack = () => {
      window.history.back()
    }

    onMounted(loadData)

    return { receipts, goBack, formatDate }
  }
}
</script>

<style scoped>
.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.receipts-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.receipt-wrapper {
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  background: var(--card-bg);
}
.receipt-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.receipt-header h3 {
  margin: 0;
}
.receipt-header .date {
  color: var(--text-secondary);
}
.empty {
  padding: 24px;
  text-align: center;
  color: var(--text-secondary);
}
</style>