package com.myproject.caseNara.mapper;

import com.myproject.caseNara.model.Bill;
import com.myproject.caseNara.model.BillWithSales;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BillMapper {
    int insertBill(Bill bill);
    List<Bill> listBillsByCustomerId(@Param("customerId") Long customerId);
    int updateRemainCost(@Param("billId") Long billId, @Param("remainCost") Integer remainCost);
    int updateStatus(@Param("billId") Long billId, @Param("status") Integer status);
    int insertBillSales(@Param("billId") Long billId, @Param("salesIds") List<Long> salesIds);
    List<BillWithSales> listBillsWithSales();
    List<Long> listSalesIdsByBillId(@Param("billId") Long billId);
    List<Map<String, Object>> listBills(@Param("startDate") String startDate, @Param("endDate") String endDate);
    int applyReceive(@Param("billId") Long billId, @Param("amount") Integer amount);
    int settleBill(@Param("billId") Long billId);
    int rollbackBill(@Param("billId") Long billId);
    Long findBillIdBySaleId(Long saleId);
    void deleteBillByBillId(Long billId);
    void deleteBillBySaleId(Long saleId);
    void deleteBillSalesByBillId(@Param("billId") Long billId);
}
