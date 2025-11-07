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
}
