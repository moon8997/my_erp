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
    public static record OrderRequest(String customerName, String saleDate, List<CreateOrderItem> items) {}

    /**
     * 새로운 주문을 생성합니다.
     *
     * @param request 주문 생성 요청 정보 (고객명, 판매일자, 주문 상품 목록)
     * @return 생성된 주문 항목 수
     * @throws IllegalArgumentException 주문 정보가 없거나, 고객 또는 상품을 찾을 수 없는 경우
     */
    public int createOrder(OrderRequest request) {
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

            // 동일 날짜/고객/상품에 대한 기존 주문 확인
            Sale existingSale = salesMapper.findExistingSale(
                customer.getCustomerId(),
                product.getProductId(),
                saleAt
            );

            if (existingSale != null) {
                // 기존 주문이 있는 경우 수량과 금액 업데이트
                int additionalPrice = product.getSalePrice() * item.quantity();
                inserted += salesMapper.updateSaleQuantity(
                    existingSale.getSaleId(),
                    product.getProductId(),
                    item.quantity(),
                    additionalPrice
                );
            } else {
                // 새로운 주문 생성
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

    public List<Sale>  findSalesById(Long saleId) {
        return salesMapper.findSalesById(saleId);
    }

    /**
     * 기존 주문을 수정합니다. 목록에서 제외된 상품은 삭제 처리하고,
     * 수량이 변경된 상품은 증감하여 반영하며, 신규 상품은 추가합니다.
     * 또한 주문일자 변경 시 동일 SALE_ID의 모든 항목의 날짜를 업데이트합니다.
     */
    public void updateOrder(Long saleId, OrderRequest request) {
        if (saleId == null) {
            throw new IllegalArgumentException("saleId가 필요합니다.");
        }
        if (request == null || request.items() == null) {
            throw new IllegalArgumentException("요청 본문이 올바르지 않습니다.");
        }

        Customer customer = customerMapper.findByCompanyName(request.customerName());
        if (customer == null) {
            throw new IllegalArgumentException("상호명을 찾을 수 없습니다.");
        }

        LocalDateTime saleAt = LocalDateTime.parse(request.saleDate() + "T00:00:00");

        // 현재 저장된 항목 조회 (삭제되지 않은 항목만)
        List<Sale> currentSales = salesMapper.findSalesById(saleId);

        // productId -> 현재 Sale 매핑
        java.util.Map<Long, Sale> currentByProduct = new java.util.HashMap<>();
        for (Sale s : currentSales) {
            currentByProduct.put(s.getProductId(), s);
        }

        // 요청 항목을 productId로 변환 및 수량 맵 구성
        java.util.Map<Long, Integer> requestedQty = new java.util.HashMap<>();
        java.util.Map<Long, Integer> productSalePrice = new java.util.HashMap<>();
        for (CreateOrderItem item : request.items()) {
            if (item == null || item.productName() == null || item.productName().isBlank()) {
                continue;
            }
            Product product = productMapper.getProductByName(item.productName());
            if (product == null) {
                throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + item.productName());
            }
            requestedQty.put(product.getProductId(), item.quantity());
            productSalePrice.put(product.getProductId(), product.getSalePrice());
        }

        // 삭제: 현재에는 있지만 요청에는 없는 상품
        for (Long pid : currentByProduct.keySet()) {
            if (!requestedQty.containsKey(pid)) {
                // 업데이트 전 중복 병합 후 삭제 처리
                mergeDuplicateItems(saleId, pid);
                salesMapper.deleteSaleItem(saleId, pid);
            }
        }

        // 업데이트/추가 처리
        for (java.util.Map.Entry<Long, Integer> entry : requestedQty.entrySet()) {
            Long pid = entry.getKey();
            Integer newQty = entry.getValue();
            Integer salePrice = productSalePrice.get(pid);
            if (newQty == null || newQty <= 0) {
                // 0 이하 수량은 삭제로 간주 (중복 병합 후 삭제)
                mergeDuplicateItems(saleId, pid);
                salesMapper.deleteSaleItem(saleId, pid);
                continue;
            }

            if (currentByProduct.containsKey(pid)) {
                // 기존 항목의 수량과 비교하여 증감 적용
                Sale curr = currentByProduct.get(pid);
                int deltaQty = newQty - curr.getQuantity();
                if (deltaQty != 0) {
                    int deltaPrice = salePrice * deltaQty;
                    salesMapper.updateSaleQuantity(saleId, pid, deltaQty, deltaPrice);
                }
            } else {
                // 신규 항목 추가 (기존 saleId 유지)
                Sale newSale = Sale.builder()
                        .saleId(saleId)
                        .customerId(customer.getCustomerId())
                        .productId(pid)
                        .quantity(newQty)
                        .unitPrice(salePrice * newQty)
                        .saleAt(saleAt)
                        .deleted(0)
                        .build();
                salesMapper.insertSaleWithId(newSale);
            }
        }

        // 주문일자 변경 반영 (SALE_ID 전체 항목에 적용)
        salesMapper.updateSaleDateBySaleId(saleId, saleAt);
    }

    /**
     * 주문에서 특정 상품을 삭제(소프트 삭제)합니다.
     */
    public void deleteOrderItem(Long saleId, Long productId) {
        if (saleId == null || productId == null) {
            throw new IllegalArgumentException("saleId와 productId가 필요합니다.");
        }
        // 삭제 전에 중복을 병합하여 유니크 제약 충돌을 방지
        mergeDuplicateItems(saleId, productId);
        // 활성 레코드만 삭제 상태로 변경
        salesMapper.deleteSaleItem(saleId, productId);
    }

    /**
     * 중복 레코드 병합: 동일한 (SALE_ID, PRODUCT_ID) 활성 레코드를 하나로 합치고
     * 나머지 활성/삭제 레코드는 하드 삭제합니다.
     */
    public void mergeDuplicateItems(Long saleId, Long productId) {
        // 대표 활성 행에 합계 반영 (MYSQL 호환 방식)
        java.util.Map<String, java.math.BigDecimal> sums = salesMapper.sumActiveSaleProduct(saleId, productId);
        java.math.BigDecimal qtyBd = (sums != null) ? sums.get("totalQty") : java.math.BigDecimal.ZERO;
        java.math.BigDecimal priceBd = (sums != null) ? sums.get("totalPrice") : java.math.BigDecimal.ZERO;
        Integer totalQty = (qtyBd != null) ? qtyBd.intValue() : 0;
        Integer totalPrice = (priceBd != null) ? priceBd.intValue() : 0;
        salesMapper.updateCanonicalRowWithAggregates(saleId, productId, totalQty, totalPrice);
        // 대표 행 외 활성 중복 하드 삭제
        salesMapper.deleteOtherActiveDuplicates(saleId, productId);
        // 삭제된 레코드도 모두 제거하여 향후 제약 충돌 방지
        salesMapper.deleteAllDeletedRowsForSaleProduct(saleId, productId);
    }
}