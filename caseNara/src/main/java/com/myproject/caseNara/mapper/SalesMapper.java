package com.myproject.caseNara.mapper;

import com.myproject.caseNara.model.Sale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SalesMapper {
    int insertSale(Sale sale);
    List<String> listTopProductNamesByCompanyName(@Param("companyName") String companyName);
    
    // 판매 목록 조회 (날짜 기준)
    List<Sale> listSales(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    // 동일 날짜/고객/상품에 대한 주문 조회
    Sale findExistingSale(@Param("customerId") Long customerId, 
                         @Param("productId") Long productId, 
                         @Param("saleAt") LocalDateTime saleAt);
    
    // 기존 주문 수량 및 금액 업데이트
    int updateSaleQuantity(@Param("saleId") Long saleId, 
                          @Param("additionalQuantity") Integer additionalQuantity,
                          @Param("additionalPrice") Integer additionalPrice);
}