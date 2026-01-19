package com.example.jpaTest.Service;

import com.example.jpaTest.Entity.Coupon;
import com.example.jpaTest.Entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CouponService {

    @PersistenceContext
    private EntityManager em;

    /**
     * 쿠폰 저장
     * @param coupon
     */
    @Transactional
    public void saveCoupon(Coupon coupon) {
        em.persist(coupon);
    }

    /**
     * 쿠폰아이디에 따른 개별 조회
     * @param couponId
     * @return
     */
    @Transactional
    public Coupon getCoupon(String couponId) {
        Coupon coupon = em.find(Coupon.class, couponId);
        return coupon;
    }

    /**
     * 전체 쿠폰 조회
     * @return
     */
    @Transactional
    public List<Coupon> getAllCoupon() {
        return em.createQuery("select c from Coupon c", Coupon.class).getResultList();
    }

    /**
     * 전체 쿠폰 조회(fetch join)
     * @return
     */
    @Transactional
    public List<Coupon> getAllCouponByFetchJoin() {
        return em.createQuery("select c from Coupon c join fetch c.product", Coupon.class).getResultList();
    }

    @Transactional
    public void getTeamProxy() {
        List<Coupon> couponList = em.createQuery("select c from Coupon c", Coupon.class).getResultList();

        for (Coupon coupon : couponList) {
            Product product = coupon.getProduct();
            Product productProxy = em.getReference(Product.class, coupon.getProduct().getProductId());

            //프록시명 출력
            System.out.println("productProxy: " + productProxy.getClass().getName());

            //product, productProxy는 같은 메모리 참조 객체(프록시는 영속성 컨텍스트 내에서 같은ID에 대해 오직 하나의 프록시만 존재)
            System.out.println("isSameProxy: " + (product == productProxy));

            //product 프록시 초기화
            System.out.println("productName: " + productProxy.getProductName());
        }
    }
}
