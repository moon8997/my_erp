package com.myproject.caseNara.mapper;

import com.myproject.caseNara.model.Sale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SalesMapper {
    int insertSale(Sale sale);
    List<String> listTopProductNamesByCompanyName(@Param("companyName") String companyName);
    
    // 판매 목록 조회 (날짜 기준)
    List<Sale> listSales(@Param("startDate") String startDate, @Param("endDate") String endDate);
}