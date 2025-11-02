package com.myproject.caseNara.service;

import com.myproject.caseNara.mapper.ProductMapper;
import com.myproject.caseNara.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;
    
    @Autowired(required = false)
    private LookupService lookupService;

    /**
     * 새로운 상품을 등록합니다.
     *
     * @param product 등록할 상품 정보
     * @return 등록된 상품 정보
     */
    public Product insertProduct(Product product) {
        productMapper.insertProduct(product);
        if (lookupService != null) {
            lookupService.invalidateProducts();
        }
        return product;
    }

    /**
     * 상품명 중복 여부를 확인합니다.
     *
     * @param productName 확인할 상품명
     * @return 중복 여부 (true: 중복, false: 중복 아님)
     * @throws IllegalArgumentException 상품명이 null이거나 빈 문자열인 경우
     */
    public boolean checkDuplicateProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명이 필요합니다.");
        }
        
        Product existingProduct = productMapper.getProductByName(productName.trim());
        return existingProduct != null;
    }

    /**
     * 상품 이미지를 업로드하고 이미지 URL을 반환합니다.
     *
     * @param image 업로드할 이미지 파일
     * @return 업로드된 이미지의 URL
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public String uploadProductImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "/uploads/" + fileName;
    }

    /**
     * 모든 상품 목록을 조회합니다.
     *
     * @return 전체 상품 목록
     */
    public List<Product> getAllProducts() {
        return productMapper.getAllProducts();
    }

    /**
     * 상품 ID로 특정 상품을 조회합니다.
     *
     * @param productId 조회할 상품의 ID
     * @return 조회된 상품 정보
     */
    public Product getProductById(Long productId) {
        return productMapper.getProductById(productId);
    }

    /**
     * 상품명으로 특정 상품을 조회합니다.
     *
     * @param productName 조회할 상품명
     * @return 조회된 상품 정보
     */
    public Product getProductByName(String productName) {
        return productMapper.getProductByName(productName);
    }

    /**
     * 특정 상품을 삭제합니다.
     *
     * @param productId 삭제할 상품의 ID
     * @return 삭제된 레코드 수
     */
    public int deleteProduct(Long productId) {
        int result = productMapper.deleteProduct(productId);
        if (result > 0 && lookupService != null) {
            lookupService.invalidateProducts();
        }
        return result;
    }

    /**
     * 상품 정보를 수정합니다.
     *
     * @param productId 수정할 상품의 ID
     * @param product 수정할 상품 정보
     * @return 수정된 레코드 수
     */
    public int updateProduct(Long productId, Product product) {
        if (product == null || product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("상품 정보가 올바르지 않습니다.");
        }

        // 기존 상품 정보 조회
        Product existingProduct = productMapper.getProductById(productId);
        if (existingProduct == null) {
            throw new IllegalArgumentException("수정할 상품을 찾을 수 없습니다.");
        }

        // 상품명이 변경되었을 경우에만 중복 체크
        if (!existingProduct.getProductName().equals(product.getProductName())) {
            if (checkDuplicateProductName(product.getProductName())) {
                throw new IllegalArgumentException("이미 등록된 상품명입니다");
            }
        }

        product.setProductId(productId);
        int result = productMapper.updateProduct(product);
        if (result > 0 && lookupService != null) {
            lookupService.invalidateProducts();
        }
        return result;
    }

}