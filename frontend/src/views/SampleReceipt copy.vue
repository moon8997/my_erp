<template>
  <!-- 기존 sample.html의 본문만 추출하여 Vue 템플릿으로 변환 -->
  <div class="x21">
    <table style="border-collapse:collapse; table-layout:fixed; width:246pt">
      <colgroup>
        <col class="x21" style="background:none;width:15pt">
        <col class="x21" style="background:none;width:17.25pt">
        <col class="x21" style="width:0pt">
        <col class="x21" style="background:none;width:13.5pt">
        <col class="x21" style="background:none;width:33pt">
        <col class="x21" span="11" style="background:none;width:13.5pt">
        <col class="x21" style="background:none;width:19.5pt">
      </colgroup>

      <tbody class="topBody">

      <tr style="height:20.25pt">
        <td colspan="2" class="x21" style="height:20.25pt;"><a name="Print_Area"></a></td>
        <td class="x21" style="width:0;overflow:hidden;line-height:0;"></td>
        <td colspan="14" class="x21"></td>
      </tr>

      <tr style="height:24pt">
        <td class="x21" style="height:24pt;"></td>
        <td colspan="16" class="x81" id="tc0" style="border-right:2px solid #000000;">영 수 증</td>
      </tr>

      <tr style="height:18pt">
        <td class="x21" style="height:18pt;"></td>
        <td colspan="2" class="x26" id="tc1" style="border-right:none;">NO.</td>
        <td colspan="12" class="x84" style="border-bottom:2px solid #000000;">{customerName}</td>
        <td colspan="2" class="x86" style="border-right:2px solid #000000;">귀 하</td>
      </tr>

      <tr style="height:24pt">
        <td class="x21" style="height:24pt;"></td>
        <td rowspan="4" class="x88" style="border-bottom:2px solid #000000;height:93.75pt;">공<br/><br/>급<br/><br/>자</td>
        <td colspan="3" class="x91" id="tc2" style="border-right:2px solid #000000;border-bottom:1px solid #000000;">사업자<br/>등록번호</td>
        <td colspan="12" class="x93" style="border-right:2px solid #000000;border-bottom:1px solid #000000; font-weight: bold; font-size: larger;">{{ sale.businessRegNo || '762-20-02138' }}</td>
      </tr>

      <tr style="height:24pt">
        <td class="x21" style="height:24pt;"></td>
        <td colspan="3" class="x96" id="tc3" style="border-right:2px solid #000000;border-bottom:1px solid #000000;">상&nbsp;&nbsp;호</td>
        <td colspan="7" class="x98" style="border-right:1px solid #000000;border-bottom:1px solid #000000; font-weight: bold; font-size: larger;">나라케이스</td>
        <td class="x29">성<br/>명</td>
        <td colspan="4" class="x108" style="border-right:2px solid #000000;border-bottom:1px solid #000000;">성문수</td>
      </tr>

      <tr style="height:24pt">
        <td class="x21" style="height:24pt;"></td>
        <td colspan="3" class="x103" id="tc4" style="border-right:2px solid #000000;border-bottom:1px solid #000000;">사업장<br/>소재지</td>
        <td colspan="12" class="x105" style="border-right:2px solid #000000;border-bottom:1px solid #000000;">
          서울시 종로구 창경구로 109<br/>
          세운스퀘어라이프관 2층 201호
        </td>
      </tr>

      <tr style="height:24pt">
        <td class="x21" style="height:24pt;"></td>
        <td colspan="3" class="x101" id="tc5" style="border-right:2px solid #000000;border-bottom:2px solid #000000;">업&nbsp;&nbsp;태</td>
        <td colspan="5" class="x114" style="border-bottom:3px solid windowtext;">도소매</td>
        <td class="x30">종<br/>목</td>
        <td colspan="6" class="x111" style="border-right:2px solid #000000;">케이스</td>
      </tr>

      <tr style="height:15pt">
        <td class="x21" style="height:15pt;"></td>
        <td colspan="4" class="x63" id="tc6" style="border-bottom:1px solid #000000;">작성일</td>
        <td colspan="9" class="x65" style="border-right:3px solid windowtext;border-bottom:1px solid windowtext;">공급가 총액</td>
        <td colspan="3" class="x68" style="border-right:2px solid #000000;border-bottom:1px solid #000000;">비고</td>
      </tr>

      <tr style="height:23.25pt">
        <td class="x21" style="height:23.25pt;"></td>
        <td colspan="4" class="x56" id="tc7" style="border-bottom:2px solid #000000;"></td>
        <td class="x31">￦</td>
        <td colspan="8" class="x58" style="border-right:3px solid windowtext;border-bottom:3px solid windowtext;">{sumunitprice}</td>
        <td colspan="3" class="x60" style="border-right:2px solid #000000;border-bottom:2px solid #000000;"></td>
      </tr>

      <tr style="height:15pt">
        <td class="x21" style="height:15pt;"></td>
        <td colspan="16" class="x71" id="tc8" style="border-right:2px solid #000000;">위 금액을 영수(청구)함.</td>
      </tr>

      <!-- 항목 헤더 -->
      <tr style="height:15pt">
        <td class="x21" style="height:15pt;"></td>
        <td colspan="3" class="x75" id="tc9" style="border-right:1px solid #000000;border-bottom:2px solid #000000;overflow:hidden;">월일</td>
        <td colspan="4" class="x76" style="border-right:1px solid #000000;border-bottom:2px solid #000000;">품목</td>
        <td colspan="2" class="x76" style="border-right:1px solid #000000;border-bottom:2px solid #000000;">수량</td>
        <td colspan="3" class="x77" style="border-right:1px solid #000000;border-bottom:2px solid #000000;">단가</td>
        <td colspan="4" class="x76" style="border-right:2px solid #000000;border-bottom:2px solid #000000;">공급가액(금액)</td>
      </tr>

      </tbody>

      <!-- 항목 영역: 총 15행 반복 (첫 행만 데이터 표시, 나머지는 공백) -->
      <tbody class="saleDataBody">
        <tr v-for="n in 15" :key="n" style="height:18.75pt">
          <td class="x21" style="height:18.75pt;"></td>
          <td colspan="3" class="x117" id="tc10" style="border-right:1px solid #000000;border-bottom:1px solid #000000;">{saleMonthDay}</td>
          <td colspan="4" class="x50" style="border-right:1px solid #000000;border-bottom:1px solid #000000;">{productName}</td>
          <td colspan="2" class="x50" style="border-right:1px solid #000000;border-bottom:1px solid #000000;">{quantity}</td>
          <td colspan="3" class="x51" style="border-right:1px solid #000000;border-bottom:1px solid #000000;">{productPrice}</td>
          <td colspan="4" class="x54" style="border-right:2px solid #000000;border-bottom:1px solid #000000;">{unitPrice}</td>
        </tr>
      </tbody>

      <tfoot>
        <tr style="height:25.5pt">
          <td class="x21" style="height:25.5pt;"></td>
          <td colspan="16" class="x116" id="tc25">TEL : 02&#41; 2276-0921~2</td>
        </tr>
      </tfoot>
    </table>
  </div>
</template>

<script>
export default {
  name: 'SampleReceipt',
  props: {
    // 백엔드 Sale.java에 맞춘 데이터를 주입할 수 있도록 매핑
    sale: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    saleDate() {
      // sale.saleAt이 문자열/Date 모두 대응
      const v = this.sale?.saleAt;
      const d = v ? new Date(v) : new Date();
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${y}-${m}-${day}`;
    },
    saleMonthDay() {
      const v = this.sale?.saleAt;
      const d = v ? new Date(v) : new Date();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${m}/${day}`;
    },
    lineAmount() {
      const qty = Number(this.sale?.quantity || 0);
      const price = Number(this.sale?.unitPrice || 0);
      return qty * price;
    },
    totalAmount() {
      // 단일 라인만 계산 (필요 시 배열로 확장 가능)
      return this.lineAmount;
    }
  }
}
</script>

<style scoped>
.saleDataBody tr td {
  text-align: center;
}
/* sample.html에서 옮긴 전체 스타일 (필요 주석 유지) */
/* Clean global styles */
a { color: blue; }
a:visited { color: purple; }
table { border: 0; border-collapse: collapse; border-spacing: 0; }

/* Default cell style */
.style0 {
  text-align: left;
  vertical-align: middle;
  white-space: nowrap;
  color: #000000;
  font-size: 11pt;
  font-weight: 400;
  font-style: normal;
  font-family: 돋움, sans-serif;
  padding: 0;
}

/* Default table cell */
td {
  text-align: left;
  vertical-align: middle;
  white-space: nowrap;
  color: #000000;
  font-size: 11pt;
  font-weight: 400;
  font-style: normal;
  font-family: 돋움, sans-serif;
  padding: 0;
}

/* 모든 선(테두리)을 검은색으로 강제 적용 */
table, table *, td, th {
  border-color: #000000 !important;
  border-top-color: #000000 !important;
  border-right-color: #000000 !important;
  border-bottom-color: #000000 !important;
  border-left-color: #000000 !important;
}

/* 이하 .x*** 클래스 정의를 sample.html에서 전부 이전 */
.x15
 {
 text-align:general;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:11pt;
 font-weight:400;
 font-style:normal;
 font-family:돋움,sans-serif;
 }
.x21
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 }
.x26
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:400;
 font-style:italic;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:1px dotted #C0C0C0;
 border-bottom:none;
 border-left:2px solid #000000;
 }
.x27
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:none;
 border-left:1px dotted #C0C0C0;
 }
.x28
 {
 text-align:general;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 }
.x29
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x30
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid windowtext;
 border-bottom:3px solid windowtext;
 border-left:1px solid windowtext;
 }
.x31
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:3px solid windowtext;
 border-left:3px solid windowtext;
 }
.x32
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:2px solid windowtext;
 border-left:1px solid #000000;
 }
.x33
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:2px solid windowtext;
 border-left:1px solid #000000;
 }
.x34
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:2px solid windowtext;
 border-left:none;
 }
.x35
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:2px solid windowtext;
 border-left:none;
 }
.x36
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:2px solid windowtext;
 border-left:1px solid #000000;
 }
.x37
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:2px solid #000000;
 border-bottom:2px solid windowtext;
 border-left:1px solid #000000;
 }
.x38
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x39
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x40
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x41
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x42
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x43
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x44
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:none;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x45
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x46
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x47
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:none;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x48
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:none;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x49
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:none;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x50
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x51
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:2px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x52
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:2px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x53
 {
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x54
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x55
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:2px solid #000000;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x56
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:2px solid #000000;
 border-left:2px solid #000000;
 }
.x57
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:2px solid #000000;
 border-left:none;
 }
.x58
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:3px solid windowtext;
 border-left:none;
 }
.x59
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:3px solid windowtext;
 border-bottom:3px solid windowtext;
 border-left:none;
 }
.x60
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:1px dotted #C0C0C0;
 border-bottom:2px solid #000000;
 border-left:none;
 }
.x61
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:1px dotted #C0C0C0;
 border-bottom:2px solid #000000;
 border-left:1px dotted #C0C0C0;
 }
.x62
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:2px solid #000000;
 border-bottom:2px solid #000000;
 border-left:1px dotted #C0C0C0;
 }
.x63
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:2px solid #000000;
 }
.x64
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x65
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:3px solid windowtext;
 border-right:none;
 border-bottom:1px solid windowtext;
 border-left:3px solid windowtext;
 }
.x66
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:3px solid windowtext;
 border-right:none;
 border-bottom:1px solid windowtext;
 border-left:none;
 }
.x67
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:3px solid windowtext;
 border-right:3px solid windowtext;
 border-bottom:1px solid windowtext;
 border-left:none;
 }
.x68
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:1px dotted #C0C0C0;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x69
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:1px dotted #C0C0C0;
 border-bottom:1px solid #000000;
 border-left:1px dotted #C0C0C0;
 }
.x70
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px dotted #C0C0C0;
 }
.x71
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:1px dotted #C0C0C0;
 border-bottom:none;
 border-left:2px solid #000000;
 }
.x72
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:1px dotted #C0C0C0;
 border-bottom:none;
 border-left:1px dotted #C0C0C0;
 }
.x73
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px dotted #C0C0C0;
 border-bottom:none;
 border-left:1px dotted #C0C0C0;
 }
.x74
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:2px solid #000000;
 border-bottom:none;
 border-left:1px dotted #C0C0C0;
 }
.x75
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:2px solid #000000;
 border-left:2px solid #000000;
 }
.x76
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:2px solid #000000;
 border-left:1px solid #000000;
 }
.x77
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:none;
 border-bottom:2px solid #000000;
 border-left:1px solid #000000;
 }
.x78
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:none;
 border-bottom:2px solid #000000;
 border-left:none;
 }
.x79
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:2px solid #000000;
 border-left:none;
 }
.x80
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:2px solid #000000;
 border-bottom:2px solid #000000;
 border-left:1px solid #000000;
 }
.x81
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:18pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:none;
 border-bottom:none;
 border-left:2px solid #000000;
 }
.x82
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:18pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:none;
 border-bottom:none;
 border-left:none;
 }
.x83
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:18pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:2px solid #000000;
 border-bottom:none;
 border-left:none;
 }
.x84
 {
 text-align:right;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:2px solid #000000;
 border-left:none;
 }
.x85
 {
 text-align:right;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:2px solid #000000;
 border-left:none;
 }
.x86
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 }
.x87
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:10pt;
 font-weight:700;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:none;
 border-right:2px solid #000000;
 border-bottom:none;
 border-left:none;
 }
.x88
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:2px solid #000000;
 }
.x89
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:2px solid #000000;
 }
.x90
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:2px solid #000000;
 border-left:2px solid #000000;
 }
.x91
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x92
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x93
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:2px solid #000000;
 }
.x94
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x95
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:2px solid #000000;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x96
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x97
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x98
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:2px solid #000000;
 }
.x99
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x100
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x101
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:2px solid #000000;
 border-left:1px solid #000000;
 }
.x102
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:2px solid #000000;
 border-bottom:2px solid #000000;
 border-left:1px solid #000000;
 }
.x103
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x104
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x105
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:2px solid #000000;
 }
.x106
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x107
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x108
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:1px solid #000000;
 }
.x109
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x110
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:2px solid #000000;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x111
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:none;
 border-left:none;
 }
.x112
 {
 text-align:center;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:none;
 border-left:none;
 }
.x113
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:2px solid #000000;
 border-bottom:none;
 border-left:none;
 }
.x114
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:3px solid windowtext;
 border-left:2px solid #000000;
 }
.x115
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:'맑은 고딕',sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:3px solid windowtext;
 border-left:none;
 }
.x116
 {
 text-align:center;
 vertical-align:middle;
 white-space:nowrap;
 background:auto;
 color:#000000;
 font-size:14pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:2px solid windowtext;
 border-right:none;
 border-bottom:none;
 border-left:none;
 }
.x117
 {
 text-align:distributed;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:2px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:2px solid #000000;
 }
.x118
 {
 text-align:distributed;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:2px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x119
 {
 text-align:distributed;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:2px solid #000000;
 }
.x120
 {
 text-align:distributed;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:none;
 }
.x121
 {
 text-align:distributed;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:none;
 border-bottom:2px solid windowtext;
 border-left:2px solid #000000;
 }
.x122
 {
 text-align:distributed;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:1px solid #000000;
 border-right:1px solid #000000;
 border-bottom:2px solid windowtext;
 border-left:none;
 }
.x123
 {
 text-align:distributed;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:none;
 border-right:none;
 border-bottom:1px solid #000000;
 border-left:2px solid #000000;
 }
.x124
 {
 text-align:distributed;
 vertical-align:middle;
 white-space:normal;word-wrap:break-word;
 background:auto;
 color:#000000;
 font-size:9pt;
 font-weight:400;
 font-style:normal;
 font-family:굴림,sans-serif;
 border-top:none;
 border-right:1px solid #000000;
 border-bottom:1px solid #000000;
 border-left:none;
 }
</style>