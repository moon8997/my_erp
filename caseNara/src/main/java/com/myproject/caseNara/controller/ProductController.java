package com.myproject.caseNara.controller;

import com.myproject.caseNara.mapper.ProductMapper;
import com.myproject.caseNara.model.Product;
import com.myproject.caseNara.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;
    @Autowired(required = false)
    private LookupService lookupService;

    // 상품 등록
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            productMapper.insertProduct(product);
            if (lookupService != null) lookupService.invalidateProducts();
            return ResponseEntity.ok(Map.of("success", true, "productId", product.getProductId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Product>> getProducts() {
        try {
            List<Product> products = productMapper.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 상품명 중복 검사
    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicateProductName(@RequestParam("productName") String productName) {
        try {
            if (productName == null || productName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "상품명이 필요합니다."));
            }
            
            Product existingProduct = productMapper.getProductByName(productName.trim());
            boolean isDuplicate = existingProduct != null;
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "isDuplicate", isDuplicate,
                "message", isDuplicate ? "이미 존재하는 상품명입니다." : "사용 가능한 상품명입니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    
    // 이미지 URL 처리
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestBody Map<String, String> request) {
        try {
            String imageUrl = request.get("imageUrl");
            
            if (imageUrl == null || imageUrl.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "이미지 URL이 필요합니다."));
            }
            
            return ResponseEntity.ok(Map.of("success", true, "imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 모든 상품 조회
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productMapper.getAllProducts());
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        Product product = productMapper.getProductById(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 상품 삭제 (소프트 삭제)
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        int result = productMapper.deleteProduct(productId);
        if (result > 0) {
            if (lookupService != null) lookupService.invalidateProducts();
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        product.setProductId(productId);
        int result = productMapper.updateProduct(product);
        if (result > 0) {
            if (lookupService != null) lookupService.invalidateProducts();
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}