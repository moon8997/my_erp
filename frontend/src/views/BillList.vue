<template>
  <div class="page">
    <div class="page-header">
      <h1>청구서 매핑 조회</h1>
      <p class="desc">상태=0인 청구서와 연결된 판매 항목(SALES_ID)을 확인합니다</p>
    </div>

    <div class="card">
      <div class="table-container">
        <table class="table">
          <thead>
            <tr>
              <th>Bill ID</th>
              <th>Customer ID</th>
              <th>Total</th>
              <th>Remain</th>
              <th>Status</th>
              <th>Created At</th>
              <th>Sales ID</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="7" class="text-center">불러오는 중...</td>
            </tr>
            <tr v-else-if="rows.length === 0">
              <td colspan="7" class="text-center">표시할 데이터가 없습니다</td>
            </tr>
            <tr v-else v-for="(r, idx) in rows" :key="`${r.billId}_${r.salesId}_${idx}`">
              <td>{{ r.billId }}</td>
              <td>{{ r.customerId }}</td>
              <td>{{ formatPrice(r.totalCost) }}</td>
              <td>{{ formatPrice(r.remainCost) }}</td>
              <td>{{ r.status }}</td>
              <td>{{ formatDate(r.createdAt) }}</td>
              <td>{{ r.salesId }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  
</template>

<script>
import axios from 'axios'
import './styles/common.css'

export default {
  name: 'BillList',
  data() {
    return {
      loading: false,
      rows: []
    }
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await axios.get('/api/bills/with-sales')
        if (res?.data?.success) {
          this.rows = res.data.rows || []
        } else {
          this.rows = []
        }
      } catch (err) {
        console.error('Failed to load bills with sales:', err)
        this.rows = []
      } finally {
        this.loading = false
      }
    },
    formatPrice(n) {
      if (n == null) return '-'
      try { return Number(n).toLocaleString('ko-KR') + '원' } catch { return String(n) }
    },
    formatDate(dt) {
      if (!dt) return '-'
      const d = new Date(dt)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    }
  },
  mounted() {
    this.fetchData()
  }
}
</script>

<style scoped>
.table-container { overflow-x: auto; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { border: 1px solid var(--border-color); padding: 8px; font-size: 14px; }
.table th { background: var(--input-bg); text-align: left; }
.text-center { text-align: center; }
</style>

