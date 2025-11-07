package com.myproject.caseNara.service;

import com.myproject.caseNara.mapper.BillMapper;
import com.myproject.caseNara.model.Bill;
import com.myproject.caseNara.model.BillWithSales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BillService {

    @Autowired
    private BillMapper billMapper;
    @Autowired
    private SalesService salesService;

    /**
     * 수금 준비 시 프런트에서 고객별 합계(totalCost)를 전달하면 Bill 레코드를 생성합니다.
     * remainCost는 초기값으로 totalCost를 동일하게 설정하고, status는 0(예: 미수)로 기본 설정합니다.
     */
    public int createBills(List<BillRequest> requests) {
        int inserted = 0;
        if (requests == null || requests.isEmpty()) return inserted;
        for (BillRequest req : requests) {
            inserted += createBill(req);
        }
        return inserted;
    }

    public int createBill(BillRequest req) {
        if (req == null || req.customerId == null || req.totalCost == null) {
            throw new IllegalArgumentException("customerId와 totalCost가 필요합니다");
        }
        Bill bill = Bill.builder()
                .customerId(req.customerId)
                .totalCost(req.totalCost)
                .remainCost(req.remainCost != null ? req.remainCost : req.totalCost)
                .status(req.status != null ? req.status : 0)
                .createdAt(LocalDateTime.now())
                .build();
        int inserted = billMapper.insertBill(bill);
        // bills_sales 매핑 저장: 전달된 salesIds가 있으면 bill_id와 함께 일괄 insert
        if (inserted > 0 && bill.getBillId() != null && req.salesIds != null && !req.salesIds.isEmpty()) {
            // null 제거 및 중복 제거
            java.util.List<Long> distinctIds = req.salesIds.stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            if (!distinctIds.isEmpty()) {
                salesService.updateBillSatus(req.salesIds);
                billMapper.insertBillSales(bill.getBillId(), distinctIds);
            }
        }
        return inserted;
    }

    public List<Bill> listByCustomerId(Long customerId) {
        return billMapper.listBillsByCustomerId(customerId);
    }

    /**
     * 상태=0인 청구서와 연결된 판매(SALES_ID) 목록을 조회합니다.
     */
    public List<BillWithSales> listBillsWithSales() {
        return billMapper.listBillsWithSales();
    }

    /**
     * 요청 DTO: 프런트에서 전달받는 필드
     */
    public static class BillRequest {
        public Long customerId;
        public Integer totalCost;
        public Integer remainCost; // 옵션: 지정 없으면 totalCost
        public Integer status;     // 옵션: 지정 없으면 0
        public java.util.List<Long> salesIds; // bills_sales 매핑용: 이 Bill에 포함된 sales_id 목록
    }
}
