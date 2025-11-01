package com.myproject.caseNara.model;

import java.time.OffsetDateTime;
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
    private OffsetDateTime saleAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Integer deleted;
}