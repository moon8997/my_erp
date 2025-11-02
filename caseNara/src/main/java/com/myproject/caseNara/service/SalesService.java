package com.myproject.caseNara.service;

import com.myproject.caseNara.mapper.CustomerMapper;
import com.myproject.caseNara.mapper.ProductMapper;
import com.myproject.caseNara.mapper.SalesMapper;
import com.myproject.caseNara.model.Customer;
import com.myproject.caseNara.model.Product;
import com.myproject.caseNara.model.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class SalesService {

    @Autowired
    private SalesMapper salesMapper;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private ProductMapper productMapper;

    /**
     * 주문 상품 항목을 나타내는 레코드 클래스입니다.
     */
    public static record CreateOrderItem(String productName, Integer quantity) {}

    /**
     * 주문 생성 요청을 나타내는 레코드 클래스입니다.
     */
    public static record CreateOrderRequest(String customerName, String saleDate, List<CreateOrderItem> items) {}

    /**
     * 새로운 주문을 생성합니다.
     *
     * @param request 주문 생성 요청 정보 (고객명, 판매일자, 주문 상품 목록)
     * @return 생성된 주문 항목 수
     * @throws IllegalArgumentException 주문 정보가 없거나, 고객 또는 상품을 찾을 수 없는 경우
     */
    public int createOrder(CreateOrderRequest request) {
        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("상품 항목이 필요합니다.");
        }

        Customer customer = customerMapper.findByCompanyName(request.customerName());
        if (customer == null) {
            throw new IllegalArgumentException("상호명을 찾을 수 없습니다.");
        }

        LocalDateTime saleAt = LocalDateTime.parse(request.saleDate() + "T00:00:00");

        int inserted = 0;
        for (CreateOrderItem item : request.items()) {
            Product product = productMapper.getProductByName(item.productName());
            if (product == null) {
                throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + item.productName());
            }

            Sale sale = Sale.builder()
                    .customerId(customer.getCustomerId())
                    .productId(product.getProductId())
                    .quantity(item.quantity())
                    .unitPrice(product.getSalePrice() * item.quantity())
                    .saleAt(saleAt)
                    .deleted(0)
                    .build();

            inserted += salesMapper.insertSale(sale);
        }

        return inserted;
    }

    /**
     * 특정 고객의 주요 구매 상품명 목록을 조회합니다.
     *
     * @param companyName 조회할 고객의 상호명
     * @return 주요 구매 상품명 목록
     */
    public List<String> getTopProductsByCompanyName(String companyName) {
        return salesMapper.listTopProductNamesByCompanyName(companyName);
    }
    
    /**
     * 판매 목록을 조회합니다.
     * 날짜가 지정되지 않으면 전체 목록을, 지정된 경우 해당 기간의 목록을 반환합니다.
     *
     * @param startDate 시작 날짜 (선택)
     * @param endDate 종료 날짜 (선택)
     * @return 판매 목록
     */
    public List<Sale> listSales(String startDate, String endDate) {
        return salesMapper.listSales(startDate, endDate);
    }
}