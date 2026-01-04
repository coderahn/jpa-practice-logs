package com.example.jpaTest.Service;

import com.example.jpaTest.Entity.Coupon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    @PersistenceContext
    private EntityManager em;

    //상품저장

    //쿠폰저장
    @Transactional
    public void saveCoupon(Coupon coupon) {
        em.persist(coupon);
    }

    //조회
    @Transactional
    public Coupon getCoupon(String couponId) {
        Coupon coupon = em.find(Coupon.class, couponId);
        return coupon;
    }
}
