package com.myproject.caseNara.controller;

import com.myproject.caseNara.model.Bill;
import com.myproject.caseNara.model.BillWithSales;
import com.myproject.caseNara.service.BillService;
import com.myproject.caseNara.service.BillService.BillRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    /**
     * 여러 고객에 대한 Bill을 한 번에 생성 (수금준비 버튼에서 사용)
     */
    @PostMapping
    public ResponseEntity<?> createBills(@RequestBody List<BillRequest> requests) {
        try {
            int inserted = billService.createBills(requests);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "inserted", inserted
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    /**
     * 특정 판매건(saleId)을 기준으로 해당 판매건이 속한 청구서를 삭제합니다.
     * - 내부적으로 billId를 조회하고, 연결된 모든 sales의 billStatus를 초기화한 뒤
     *   BILLS_SALES 매핑과 BILLS 레코드를 순서대로 삭제합니다.
     */
    @DeleteMapping("/by-sale/{saleId}")
    public ResponseEntity<?> deleteBillBySale(@PathVariable Long saleId) {
        try {
            billService.deleteBillBySaleId(saleId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    

    /**
     * 특정 고객의 Bill 목록 조회
     */
    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<?> listByCustomer(@PathVariable Long customerId) {
        try {
            List<Bill> bills = billService.listByCustomerId(customerId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "bills", bills
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    /**
     * 상태=0인 청구서와 연결된 판매 정보를 조회합니다.
     */
    @GetMapping("/with-sales")
    public ResponseEntity<?> listBillsWithSales() {
        try {
            List<BillWithSales> rows = billService.listBillsWithSales();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "rows", rows
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    /**
     * 특정 BILL_ID의 매핑된 SALES_ID 목록 조회 (상태와 무관)
     */
    @GetMapping("/{billId}/sales-ids")
    public ResponseEntity<?> listSalesIdsByBill(@PathVariable Long billId) {
        try {
            List<Long> salesIds = billService.listSalesIdsByBillId(billId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "salesIds", salesIds
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    /**
     * 전체 Bill 목록 조회 (선택적 날짜 범위)
     */
    @GetMapping
    public ResponseEntity<?> listBills(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            if (startDate == null && endDate == null) {
                LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                startDate = today.toString();
                endDate = today.toString();
            }
            List<Map<String, Object>> bills = billService.listBills(startDate, endDate);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "bills", bills
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    /**
     * 받은 금액 반영: REMAIN_COST를 차감하고 상태를 업데이트합니다.
     */
    @PutMapping("/{billId}/receive")
    public ResponseEntity<?> applyReceive(@PathVariable Long billId, @RequestBody Map<String, Object> body) {
        try {
            Object amountObj = body.get("amount");
            if (amountObj == null) throw new IllegalArgumentException("amount 필드가 필요합니다");
            Integer amount = (amountObj instanceof Number) ? ((Number) amountObj).intValue() : Integer.parseInt(amountObj.toString());
            billService.applyReceive(billId, amount);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    /**
     * 완납 처리: REMAIN_COST=0, STATUS=2
     */
    @PutMapping("/{billId}/settle")
    public ResponseEntity<?> settleBill(@PathVariable Long billId) {
        try {
            billService.settleBill(billId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    /**
     * 롤백 처리: 받은금액을 0으로 만들고 상태를 미납(0)으로 초기화합니다.
     */
    @PutMapping("/{billId}/rollback")
    public ResponseEntity<?> rollbackBill(@PathVariable Long billId) {
        try {
            billService.rollbackBill(billId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }
}
