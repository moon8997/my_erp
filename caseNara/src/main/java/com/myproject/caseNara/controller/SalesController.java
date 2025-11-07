package com.myproject.caseNara.controller;

import com.myproject.caseNara.model.Sale;
import com.myproject.caseNara.service.SalesService;
import com.myproject.caseNara.service.SalesService.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    @Autowired
    private SalesService salesService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        try {
            int inserted = salesService.createOrder(request);
            return ResponseEntity.ok(Map.of("success", true, "inserted", inserted));
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "주문 생성 중 오류가 발생했습니다";
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", errorMessage
            ));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Sale>> listSales(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        // 날짜가 지정되지 않은 경우 한국 시간 기준 오늘 날짜를 기본값으로 사용
        if (startDate == null && endDate == null) {
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
            startDate = today.toString();
            endDate = today.toString();
        }
        return ResponseEntity.ok(salesService.listSales(startDate, endDate));
    }


    @GetMapping("/byId/{saleId}")
    public ResponseEntity<List<Sale>> getOrderById(@PathVariable Long saleId) {
        return ResponseEntity.ok(salesService.findSalesById(saleId));
    }

    @PutMapping("/{saleId}")
    public ResponseEntity<?> updateOrder(
            @PathVariable Long saleId,
            @RequestBody OrderRequest request) {
        try {
            salesService.updateOrder(saleId, request);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "주문 수정 중 오류가 발생했습니다";
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", errorMessage
            ));
        }
    }

    @PutMapping("/bill-status/reset/{saleId}")
    public ResponseEntity<?> resetBillStatus(@PathVariable Long saleId) {
        try {
            salesService.resetBillStatusForSaleId(saleId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "청구 상태 초기화 중 오류가 발생했습니다";
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", errorMessage
            ));
        }
    }

    @DeleteMapping("/{saleId}/products/{productId}")
    public ResponseEntity<?> deleteOrderItem(
            @PathVariable Long saleId,
            @PathVariable Long productId) {
        try {
            salesService.deleteOrderItem(saleId, productId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "주문 항목 삭제 중 오류가 발생했습니다";
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", errorMessage
            ));
        }
    }
}
