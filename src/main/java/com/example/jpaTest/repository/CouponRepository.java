package com.example.jpaTest.repository;

import com.example.jpaTest.Entity.Coupon;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {

    /**
     * [실제 쿼리 콘솔로그]
     *    select
     *         c1_0.coupon_id,
     *         c1_0.coupon_name,
     *         c1_0.coupon_price,
     *         p1_0.product_id,
     *         p1_0.price,
     *         p1_0.product_name,
     *         c1_0.sll_pocy_no
     *     from
     *         coupon c1_0
     *     join
     *         product p1_0
     *             on p1_0.product_id=c1_0.product_id
     *     where
     *         p1_0.product_name=?
     */
    @Query("select c from Coupon c join fetch c.product p where p.productName = :productName")
    List<Coupon> findByProductName(@Param("productName") String productName);

    /**
     * EntityGraph: fetch join을 스프링데이터JPA로 하는 방식
     */
    @EntityGraph(attributePaths = {"product"})
    List<Coupon> findAll();

    @EntityGraph(attributePaths = {"product"})
    List<Coupon> findByCouponId(String couponId);

}
