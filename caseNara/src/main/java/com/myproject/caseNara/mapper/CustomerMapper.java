package com.myproject.caseNara.mapper;

import com.myproject.caseNara.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {
    List<Customer> getAllCustomers();
    
    Customer getCustomerById(Long customerId);
    
    Customer findByCompanyName(String companyName);
    
    int insertCustomer(Customer customer);
    
    int updateCustomer(Customer customer);
    
    int deleteCustomer(Long customerId);
    
    List<String> listCompanyNamesLike(@Param("query") String query);
    
    List<String> listAllCompanyNames();
}