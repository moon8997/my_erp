package com.myproject.caseNara.controller;

import com.myproject.caseNara.model.Product;
import com.myproject.caseNara.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 상품 이미지 업로드
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProductImage(@RequestPart("image") MultipartFile image) {
        try {
            String imageUrl = productService.uploadProductImage(image);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "imageUrl", imageUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "이미지 업로드 실패: " + e.getMessage()));
        }
    }

    // 상품 등록
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            productService.insertProduct(product);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "productId", product.getProductId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 상품명 중복 검사
    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicateProductName(@RequestParam("productName") String productName) {
        try {
            boolean isDuplicate = productService.checkDuplicateProductName(productName);
            return ResponseEntity.ok(Map.of("success", true, "isDuplicate", isDuplicate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 모든 상품 조회
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(Map.of("success", true, "products", products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            if (product != null) {
                return ResponseEntity.ok(Map.of("success", true, "product", product));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 상품 삭제 (소프트 삭제)
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            int result = productService.deleteProduct(productId);
            if (result > 0) {
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        try {
            int result = productService.updateProduct(productId, product);
            if (result > 0) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "상품 수정 완료"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().startsWith("이미 등록된 상품명입니다")) {
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

    // 로컬 업로드 폴더의 고아 파일 정리
    @PostMapping("/cleanup-orphan-uploads")
    public ResponseEntity<?> cleanupOrphanUploads() {
        try {
            int deletedCount = productService.cleanupUnmappedLocalUploads();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "deletedCount", deletedCount
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "고아 파일 정리 실패: " + e.getMessage()
            ));
        }
    }
}