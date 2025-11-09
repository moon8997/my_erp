package com.myproject.caseNara.service;

import com.myproject.caseNara.mapper.BillMapper;
import com.myproject.caseNara.model.Bill;
import com.myproject.caseNara.model.BillWithSales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Map;

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
     * BILL_ID로 매핑된 SALES_ID 목록을 조회합니다. 상태와 관계없이 반환합니다.
     */
    public List<Long> listSalesIdsByBillId(Long billId) {
        if (billId == null) {
            throw new IllegalArgumentException("billId가 필요합니다");
        }
        return billMapper.listSalesIdsByBillId(billId);
    }

    /**
     * 전체 Bill 목록 조회 (옵션: 날짜 범위)
     */
    public List<Map<String, Object>> listBills(String startDate, String endDate) {
        return billMapper.listBills(startDate, endDate);
    }

    /**
     * 받은 금액을 반영합니다. 남은 금액을 차감하고 상태를 동시에 업데이트합니다.
     */
    public void applyReceive(Long billId, Integer amount) {
        if (billId == null || amount == null || amount <= 0) {
            throw new IllegalArgumentException("billId와 양수의 받은 금액(amount)이 필요합니다");
        }
        billMapper.applyReceive(billId, amount);
    }

    /**
     * 완납 처리: 남은 금액을 0으로 만들고 상태를 2로 설정합니다.
     */
    public void settleBill(Long billId) {
        if (billId == null) {
            throw new IllegalArgumentException("billId가 필요합니다");
        }
        billMapper.settleBill(billId);
    }

    /**
     * 롤백 처리: 받은 금액을 0으로 되돌립니다. 남은 금액을 총액으로 설정하고 상태를 0으로 초기화합니다.
     */
    public void rollbackBill(Long billId) {
        if (billId == null) {
            throw new IllegalArgumentException("billId가 필요합니다");
        }
        billMapper.rollbackBill(billId);
    }

    
    public void deleteBillBySaleId(Long saleId) {
        if (saleId == null) {
            throw new IllegalArgumentException("saleId가 필요합니다");
        }
        Long billId = billMapper.findBillIdBySaleId(saleId);
        List<Long> salesIds = billMapper.listSalesIdsByBillId(billId);
        if (salesIds != null) {
            for (Long sid : salesIds) {
                if (sid != null) {
                    salesService.resetBillStatusForSaleId(sid);
                }
            }
        }
        // 해당 청구서의 모든 매핑을 billId 기준으로 제거한 뒤 청구서 삭제
        billMapper.deleteBillSalesByBillId(billId);
        billMapper.deleteBillByBillId(billId);
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
