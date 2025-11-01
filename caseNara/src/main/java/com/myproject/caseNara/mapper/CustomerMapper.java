package com.myproject.caseNara.mapper;

import com.myproject.caseNara.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {
    int insertCustomer(Customer customer);
    Customer findByCompanyName(String companyName);
    List<String> listCompanyNamesLike(@Param("query") String query);
    List<String> listAllCompanyNames();
}