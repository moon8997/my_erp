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
                          @Param("productId") Long productId,
                          @Param("additionalQuantity") Integer additionalQuantity,
                          @Param("additionalPrice") Integer additionalPrice);
                          
    List<Sale> findSalesById(Long saleId);

    // 기존 SALE_ID로 신규 항목 추가
    int insertSaleWithId(Sale sale);

    // 특정 상품을 주문에서 삭제(소프트 삭제)
    int deleteSaleItem(@Param("saleId") Long saleId, @Param("productId") Long productId);

    // 동일 SALE_ID의 모든 항목 주문일자 업데이트
    int updateSaleDateBySaleId(@Param("saleId") Long saleId, @Param("saleAt") LocalDateTime saleAt);

    // 중복 병합: 활성 레코드 집계 후 대표 행 업데이트
    int mergeActiveCanonicalRow(@Param("saleId") Long saleId, @Param("productId") Long productId);

    // 중복 활성 레코드 하드 삭제
    int deleteOtherActiveDuplicates(@Param("saleId") Long saleId, @Param("productId") Long productId);

    // 삭제된 레코드 하드 삭제
    int deleteAllDeletedRowsForSaleProduct(@Param("saleId") Long saleId, @Param("productId") Long productId);
}