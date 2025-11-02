package com.myproject.caseNara.service;

import com.myproject.caseNara.mapper.CustomerMapper;
import com.myproject.caseNara.mapper.ProductMapper;
import com.myproject.caseNara.model.Product;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LookupService {

    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;

    private final AtomicReference<List<String>> customerNamesCache = new AtomicReference<>();
    private final AtomicReference<List<Product>> productNamesCache = new AtomicReference<>();

    /**
     * LookupService 생성자입니다.
     *
     * @param customerMapper 고객 매퍼
     * @param productMapper 상품 매퍼
     */
    public LookupService(CustomerMapper customerMapper, ProductMapper productMapper) {
        this.customerMapper = customerMapper;
        this.productMapper = productMapper;
    }

    /**
     * 모든 고객의 상호명 목록을 조회합니다.
     * 캐시된 데이터가 있으면 캐시를 반환하고, 없으면 DB에서 조회하여 캐시에 저장합니다.
     *
     * @return 전체 고객 상호명 목록
     */
    public List<String> getAllCustomerNames() {
        List<String> current = customerNamesCache.get();
        if (current == null) {
            current = customerMapper.listAllCompanyNames();
            customerNamesCache.set(current);
        }
        return current;
    }

    /**
     * 모든 상품명 목록을 조회합니다.
     * 캐시된 데이터가 있으면 캐시를 반환하고, 없으면 DB에서 조회하여 캐시에 저장합니다.
     *
     * @return 전체 상품명 목록
     */
    public List<Product> getAllProduct() {
        List<Product> current = productNamesCache.get();
        if (current == null) {
            current = productMapper.getAllProducts();
            productNamesCache.set(current);
        }
        return current;
    }

    /**
     * 고객 상호명 캐시를 무효화합니다.
     * 다음 조회 시 DB에서 새로운 데이터를 가져옵니다.
     */
    public void invalidateCustomers() {
        customerNamesCache.set(null);
    }

    /**
     * 상품명 캐시를 무효화합니다.
     * 다음 조회 시 DB에서 새로운 데이터를 가져옵니다.
     */
    public void invalidateProducts() {
        productNamesCache.set(null);
    }

    /**
     * 모든 캐시를 무효화합니다.
     * 고객 상호명과 상품명 캐시를 모두 초기화합니다.
     */
    public void invalidateAll() {
        invalidateCustomers();
        invalidateProducts();
    }
}