package com.myproject.caseNara.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {
    private Long saleId;
    private Long customerId;
    private Long productId;
    private Integer quantity;
    private Integer unitPrice; // NUMBER(18,2)지만 정수 가격 컬럼과 맞추어 Integer 사용
    private LocalDateTime saleAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
    private Integer billStatus;

    // 주문 목록 표시를 위한 추가 필드
    private String customerName;    // 고객사 이름
    private String productName;     // 상품명
    private Integer productPrice;   // 상품 가격
}