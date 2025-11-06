package com.myproject.caseNara.mapper;

import com.myproject.caseNara.model.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BillMapper {
    int insertBill(Bill bill);
    List<Bill> listBillsByCustomerId(@Param("customerId") Long customerId);
    int updateRemainCost(@Param("billId") Long billId, @Param("remainCost") Integer remainCost);
    int updateStatus(@Param("billId") Long billId, @Param("status") Integer status);
}