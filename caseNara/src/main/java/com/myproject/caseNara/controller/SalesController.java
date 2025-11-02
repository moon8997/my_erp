package com.myproject.caseNara.controller;

import com.myproject.caseNara.model.Sale;
import com.myproject.caseNara.service.SalesService;
import com.myproject.caseNara.service.SalesService.CreateOrderRequest;
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
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            int inserted = salesService.createOrder(request);
            return ResponseEntity.ok(Map.of("success", true, "inserted", inserted));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
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
}