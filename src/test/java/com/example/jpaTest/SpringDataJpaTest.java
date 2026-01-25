package com.example.jpaTest;

import com.example.jpaTest.Entity.Coupon;
import com.example.jpaTest.Entity.Product;
import com.example.jpaTest.repository.CouponRepository;
import com.example.jpaTest.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class SpringDataJpaTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    CouponRepository couponRepository;
    @Autowired
    ProductRepository productRepository;

    @Test
    public void contextLoads() {
    }

    @Test
    @Transactional
    @Rollback(false)
    public void getCouponDataTest() {
        /**
         * [테스트할 것]
         * Spring Data JPA로 save / find 등 해보기
         */

        Coupon coupon = new Coupon();
        coupon.setCouponId("A1000");
        coupon.setCouponPrice(10000);

        /**
         * [save전에 조회 쿼리 나가는 이유]
         * -couponId를 임의 세팅해줬기 때문에 캐시에 있는지 우선 뒤져서 DB 접속포인트 줄이기 위함
         * -EAGER, LAZY 상관없음.(EAGER하면 left join문으로 나감)
         */
        couponRepository.save(coupon);

        //1차캐시에 있어서 select문 안 나가는듯
        Optional<Coupon> optionalCoupon = couponRepository.findById("A1000");

        Assertions.assertThat(optionalCoupon.get().getCouponId()).isEqualTo("A1000");
    }

    @Test
    public void queryMethodTest() {
        /**
         * [테스트할 것]
         * 쿼리메소드 익혀보기
         */

        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setProductName("TA1000" + i);
            product.setPrice(1000 + i);
            productRepository.save(product);
        }

        /**
         * [콘솔로그]
         *   select
         *         p1_0.product_id,
         *         p1_0.price,
         *         p1_0.product_name
         *     from
         *         product p1_0
         *     where
         *         p1_0.product_name=?
         *         and p1_0.price=?
         */
        List<Product> products = productRepository.findByProductNameAndPrice("TA1000", 1000);

        for (Product item : products) {
            System.out.println(item.getProductName());
        }

        //TOP3 가격 높은 순
        /**
         * [콘솔로그]
         *  select
         *         p1_0.product_id,
         *         p1_0.price,
         *         p1_0.product_name
         *     from
         *         product p1_0
         *     order by
         *         p1_0.price desc
         *     fetch
         *         first ? rows only
         * top3:TA10009
         * top3:TA10008
         * top3:TA10007
         */
        List<Product> top3ByOrderByPriceDesc = productRepository.findTop3ByOrderByPriceDesc();

        for (Product item : top3ByOrderByPriceDesc) {
            System.out.println("top3:" + item.getProductName());
        }

        /**
         * [테스트정리]
         * 쿼리메소드 사용하여 메소드명을 통해 조건 지정 가능
         * 조건이 너무 복잡하거나 길 경우 @Query 또는 QueryDSL로 넘어가야 함
         */
    }

    @Test
    @Transactional
    @Rollback(false)
    public void annotationQueryTest() {
        /**
         * [테스트할 것]
         * 쿼리메소드와 기본제공쿼리로 작성 어려울 때 @Query 사용
         */

        Coupon coupon1 = new Coupon();
        coupon1.setCouponId("A1000");
        coupon1.setCouponName("DRINK1000");
        coupon1.setCouponPrice(1000);

        Product product1 = new Product();
        product1.setProductName("TA1000");
        product1.setPrice(1000);

        coupon1.saveCouponAndProduct(product1);

        productRepository.save(product1);
        couponRepository.save(coupon1);

        Coupon coupon2 = new Coupon();
        coupon2.setCouponId("A2000");
        coupon2.setCouponName("DRINK2000");
        coupon2.setCouponPrice(1000);

        Product product2 = new Product();
        product2.setProductName("TA1000");
        product2.setPrice(1000);

        coupon2.saveCouponAndProduct(product2);

        productRepository.save(product2);
        couponRepository.save(coupon2);

        List<Coupon> couponList = couponRepository.findByProductName("TA1000");

        for (Coupon coupon : couponList) {
            System.out.println("couponName: " + coupon.getCouponName());
            System.out.println("productPrice: " + coupon.getProduct().getPrice());
        }

        /**
         * [테스트 정리]
         * 2쌍의 쿠폰,상품 데이터 저장 후 @Query를 이용하여 조회
         * LAZY로 설정하든 EAGER로 설정하든 마지막에 join쿼리가 날라가는데 Product_name 조건으로 접근해서 LAZY의 무효화
         * 초반에 2개의 select문 나가는 것은 couponId 임의 세팅으로 캐시 조회 우선을 하기 때문
         */
    }
}
