package com.myproject.caseNara.controller;

import com.myproject.caseNara.mapper.CustomerMapper;
import com.myproject.caseNara.mapper.SalesMapper;
import com.myproject.caseNara.model.Customer;
import com.myproject.caseNara.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired(required = false)
    private LookupService lookupService;
    @Autowired
    private SalesMapper salesMapper;

    // 고객(거래처) 등록
    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(@RequestBody(required = false) Customer customer) {
        try {
            if (customer == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Customer data is null"));
            }
            // System.out.println(customer);
            customerMapper.insertCustomer(customer);
            if (lookupService != null) lookupService.invalidateCustomers();
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            // System.out.println("아아아");
            body.put("success", true);
            if (customer.getCustomerId() != null) {
                body.put("customerId", customer.getCustomerId());
            }
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            java.util.Map<String, Object> error = new java.util.HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage() != null ? e.getMessage() : "Unknown error");
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 특정 상호명의 인기 상품 Top 5 반환
    @GetMapping("/top-products")
    public ResponseEntity<?> getTopProducts(@RequestParam("companyName") String companyName) {
        try {
            String name = (companyName == null) ? "" : companyName.trim();
            // 공백 또는 존재하지 않는 상호명인 경우에도 에러 대신 빈 추천 리스트로 응답
            if (name.isEmpty()) {
                return ResponseEntity.ok(Map.of("success", true, "products", List.of()));
            }

            Customer customer = customerMapper.findByCompanyName(name);
            if (customer == null) {
                return ResponseEntity.ok(Map.of("success", true, "products", List.of()));
            }

            List<String> products = salesMapper.listTopProductNamesByCompanyName(name);
            if (products == null) products = List.of();
            return ResponseEntity.ok(Map.of("success", true, "products", products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 상호명 중복 검사
    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicateCompanyName(@RequestParam("companyName") String companyName) {
        try {
            String name = (companyName == null) ? "" : companyName.trim();
            if (name.isEmpty()) {
                // 빈 문자열이면 사용 가능 처리
                return ResponseEntity.ok(Map.of("success", true, "isDuplicate", false));
            }

            Customer existing = customerMapper.findByCompanyName(name);
            boolean isDuplicate = existing != null;
            return ResponseEntity.ok(Map.of(
                "success", true,
                "isDuplicate", isDuplicate,
                "message", isDuplicate ? "이미 존재하는 상호명입니다." : "사용 가능한 상호명입니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}