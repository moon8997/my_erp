package com.myproject.caseNara.service;

import com.myproject.caseNara.mapper.CustomerMapper;
import com.myproject.caseNara.mapper.SalesMapper;
import com.myproject.caseNara.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private SalesMapper salesMapper;
    
    @Autowired(required = false)
    private LookupService lookupService;

    /**
     * 상호명 중복을 확인합니다.
     *
     * @param companyName 확인할 상호명
     * @return 중복된 상호명이 있으면 true, 없으면 false
     * @throws IllegalArgumentException 상호명이 null이거나 비어있는 경우
     */
    public boolean checkDuplicateCompanyName(String companyName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("상호명이 필요합니다.");
        }
        
        Customer existingCustomer = customerMapper.findByCompanyName(companyName.trim());
        return existingCustomer != null;
    }

    /**
     * 새로운 고객을 등록합니다.
     *
     * @param customer 등록할 고객 정보
     * @return 등록된 고객 정보
     * @throws Exception 고객 정보가 null인 경우 또는 등록 중 오류 발생 시
     */
    public Customer addCustomer(Customer customer) throws Exception {
        if (customer == null) {
            throw new IllegalArgumentException("Customer data is null");
        }

        if (checkDuplicateCompanyName(customer.getCompanyName())) {
            throw new IllegalArgumentException("이미 등록된 상호명입니다: " + customer.getCompanyName());
        }
        
        customerMapper.insertCustomer(customer);
        if (lookupService != null) {
            lookupService.invalidateCustomers();
        }
        
        return customer;
    }

    /**
     * 모든 고객 목록을 조회합니다.
     *
     * @return 전체 고객 목록
     */
    public List<Customer> getAllCustomers() {
        return customerMapper.getAllCustomers();
    }

    /**
     * 특정 고객을 ID로 조회합니다.
     *
     * @param customerId 조회할 고객의 ID
     * @return 조회된 고객 정보
     * @throws RuntimeException 고객을 찾을 수 없는 경우
     */
    public Customer getCustomerById(Long customerId) {
        Customer customer = customerMapper.getCustomerById(customerId);
        if (customer == null) {
            throw new RuntimeException("고객을 찾을 수 없습니다: " + customerId);
        }
        return customer;
    }

    /**
     * 고객 정보를 수정합니다.
     *
     * @param customerId 수정할 고객의 ID
     * @param customerDetails 수정할 고객 정보
     * @return 수정된 고객 정보
     * @throws RuntimeException 고객을 찾을 수 없는 경우
     */
    public Customer updateCustomer(Long customerId, Customer customerDetails) {
        if (customerDetails == null || customerDetails.getCompanyName() == null || customerDetails.getCompanyName().trim().isEmpty()) {
            throw new IllegalArgumentException("상호명이 필요합니다.");
        }

        Customer existingCustomer = getCustomerById(customerId);
        String newCompanyName = customerDetails.getCompanyName().trim();
        
        // 상호명이 변경된 경우에만 중복 체크
        if (!existingCustomer.getCompanyName().equals(newCompanyName)) {
            if (checkDuplicateCompanyName(newCompanyName)) {
                throw new IllegalArgumentException("이미 등록된 상호명입니다: " + newCompanyName);
            }
        }
        
        existingCustomer.setCompanyName(newCompanyName);
        existingCustomer.setPhone(customerDetails.getPhone());
        existingCustomer.setAddress(customerDetails.getAddress());
        
        customerMapper.updateCustomer(existingCustomer);
        if (lookupService != null) {
            lookupService.invalidateCustomers();
        }
        
        return existingCustomer;
    }

    /**
     * 고객을 삭제합니다.
     *
     * @param customerId 삭제할 고객의 ID
     * @throws RuntimeException 고객을 찾을 수 없는 경우
     */
    public void deleteCustomer(Long customerId) {
        getCustomerById(customerId);
        customerMapper.deleteCustomer(customerId);
        if (lookupService != null) {
            lookupService.invalidateCustomers();
        }
    }

    /**
     * 특정 고객의 주요 구매 상품 목록을 조회합니다.
     *
     * @param companyName 조회할 고객의 상호명
     * @return 주요 구매 상품 목록 (상호명이 없거나 고객이 없는 경우 빈 목록 반환)
     */
    public List<String> getTopProducts(String companyName) {
        String name = (companyName == null) ? "" : companyName.trim();
        
        // 공백 또는 존재하지 않는 상호명인 경우에도 에러 대신 빈 추천 리스트로 응답
        if (name.isEmpty()) {
            return List.of();
        }

        Customer customer = customerMapper.findByCompanyName(name);
        if (customer == null) {
            return List.of();
        }

        List<String> products = salesMapper.listTopProductNamesByCompanyName(name);
        return products != null ? products : List.of();
    }

    /**
     * 상호명으로 고객을 조회합니다.
     *
     * @param companyName 조회할 고객의 상호명
     * @return 조회된 고객 정보 (없는 경우 null)
     */
    public Customer findByCompanyName(String companyName) {
        return customerMapper.findByCompanyName(companyName);
    }
}