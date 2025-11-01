package com.myproject.caseNara.controller;

import com.myproject.caseNara.mapper.CustomerMapper;
import com.myproject.caseNara.mapper.ProductMapper;
import com.myproject.caseNara.mapper.SalesMapper;
import com.myproject.caseNara.model.Customer;
import com.myproject.caseNara.model.Product;
import com.myproject.caseNara.model.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    @Autowired
    private SalesMapper salesMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ProductMapper productMapper;

    // 요청 DTO
    public record CreateOrderItem(String productName, Integer quantity) {}
    public record CreateOrderRequest(String customerName, String saleDate, List<CreateOrderItem> items) {}

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest req) {
        try {
            if (req == null || req.items() == null || req.items().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "상품 항목이 필요합니다."));
            }

            Customer customer = customerMapper.findByCompanyName(req.customerName());
            if (customer == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "상호명을 찾을 수 없습니다."));
            }

            OffsetDateTime saleAt = OffsetDateTime.parse(req.saleDate() + "T00:00:00+09:00");

            int inserted = 0;
            for (CreateOrderItem item : req.items()) {
                Product product = productMapper.getProductByName(item.productName());
                if (product == null) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "상품을 찾을 수 없습니다: " + item.productName()));
                }

                Sale sale = Sale.builder()
                        .customerId(customer.getCustomerId())
                        .productId(product.getProductId())
                        .quantity(item.quantity())
                        .unitPrice(product.getSalePrice()*item.quantity())
                        .saleAt(saleAt)
                        .deleted(0)
                        .build();

                inserted += salesMapper.insertSale(sale);
            }

            return ResponseEntity.ok(Map.of("success", true, "inserted", inserted));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}