package com.myproject.caseNara.service;

import com.myproject.caseNara.mapper.CustomerMapper;
import com.myproject.caseNara.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LookupService {

    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;

    private final AtomicReference<List<String>> customerNamesCache = new AtomicReference<>();
    private final AtomicReference<List<String>> productNamesCache = new AtomicReference<>();

    public LookupService(CustomerMapper customerMapper, ProductMapper productMapper) {
        this.customerMapper = customerMapper;
        this.productMapper = productMapper;
    }

    public List<String> getAllCustomerNames() {
        List<String> current = customerNamesCache.get();
        if (current == null) {
            current = customerMapper.listAllCompanyNames();
            customerNamesCache.set(current);
        }
        return current;
    }

    public List<String> getAllProductNames() {
        List<String> current = productNamesCache.get();
        if (current == null) {
            current = productMapper.listAllProductNames();
            productNamesCache.set(current);
        }
        return current;
    }

    public void invalidateCustomers() {
        customerNamesCache.set(null);
    }

    public void invalidateProducts() {
        productNamesCache.set(null);
    }

    public void invalidateAll() {
        invalidateCustomers();
        invalidateProducts();
    }
}