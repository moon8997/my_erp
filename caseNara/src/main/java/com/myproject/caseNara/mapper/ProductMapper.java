package com.myproject.caseNara.mapper;

import com.myproject.caseNara.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    int insertProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(Long productId);
    Product getProductByName(String productName);
    List<String> listProductNamesLike(@Param("query") String query);
    List<String> listAllProductNames();
    List<String> listAllImageUrls();
    int deleteProduct(Long productId);
    int updateProduct(Product product);
}