package com.example.jpaTest.repository;

import com.example.jpaTest.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    //WHERE product_name AND price
    List<Product> findByProductNameAndPrice(String productName, int price);

    //쿼리 결과 가격 내림차순으로 상위 3개
    List<Product> findTop3ByOrderByPriceDesc();
}
