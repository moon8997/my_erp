package com.myproject.caseNara.controller;

import com.myproject.caseNara.model.Customer;
import com.myproject.caseNara.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // 고객(거래처) 목록 조회
    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "customers", customers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    // 고객(거래처) 상세 조회
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long customerId) {
        try {
            Customer customer = customerService.getCustomerById(customerId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "customer", customer
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    // 고객(거래처) 등록
    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(@RequestBody(required = false) Customer customer) {
        try {
            customer = customerService.addCustomer(customer);
            Map<String, Object> body = Map.of(
                "success", true,
                "customerId", customer.getCustomerId()
            );
            return ResponseEntity.ok(body);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().startsWith("이미 등록된 상호명입니다")) {
                return ResponseEntity.status(409).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
            }
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    // 고객(거래처) 수정
    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long customerId, @RequestBody Customer customerDetails) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(customerId, customerDetails);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "customer", updatedCustomer
            ));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().startsWith("이미 등록된 상호명입니다")) {
                return ResponseEntity.status(409).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
            }
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    // 고객(거래처) 삭제
    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long customerId) {
        try {
            customerService.deleteCustomer(customerId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "고객이 성공적으로 삭제되었습니다"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    // 특정 상호명의 인기 상품 Top 5 반환
    @GetMapping("/top-products")
    public ResponseEntity<?> getTopProducts(@RequestParam("companyName") String companyName) {
        try {
            List<String> products = customerService.getTopProducts(companyName);
            return ResponseEntity.ok(Map.of("success", true, "products", products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
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

            Customer existing = customerService.findByCompanyName(name);
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