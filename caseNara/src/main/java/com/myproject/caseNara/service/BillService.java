package com.myproject.caseNara.service;

import com.myproject.caseNara.mapper.BillMapper;
import com.myproject.caseNara.model.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillMapper billMapper;

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
        return billMapper.insertBill(bill);
    }

    public List<Bill> listByCustomerId(Long customerId) {
        return billMapper.listBillsByCustomerId(customerId);
    }

    /**
     * 요청 DTO: 프런트에서 전달받는 필드
     */
    public static class BillRequest {
        public Long customerId;
        public Integer totalCost;
        public Integer remainCost; // 옵션: 지정 없으면 totalCost
        public Integer status;     // 옵션: 지정 없으면 0
    }
}