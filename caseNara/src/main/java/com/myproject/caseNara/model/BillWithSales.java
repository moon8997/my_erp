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
public class BillWithSales {
    private Long billId;        // bills.bill_id
    private Long customerId;    // bills.customer_id
    private Integer totalCost;  // bills.total_cost
    private Integer remainCost; // bills.remain_cost
    private Integer status;     // bills.status
    private LocalDateTime createdAt; // bills.created_at
    private Long salesId;       // bills_sales.sales_id
}