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
public class Bill {
    private Long billId;        // PK: bill_id
    private Long customerId;    // customer_id
    private Integer totalCost;  // total_cost
    private Integer remainCost; // remain_cost
    private Integer status;     // status
    private LocalDateTime createdAt; // create_at
}