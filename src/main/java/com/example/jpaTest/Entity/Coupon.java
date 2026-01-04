package com.example.jpaTest.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

@Entity
public class Coupon {
    @Id
    private String couponId;

    private int couponPrice;

    private int sllPocyNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // FK 컬럼명 명시 (권장)
    private Product product;


    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public int getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(int couponPrice) {
        this.couponPrice = couponPrice;
    }

    public int getSllPocyNo() {
        return sllPocyNo;
    }

    public void setSllPocyNo(int sllPocyNo) {
        this.sllPocyNo = sllPocyNo;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    //연관관계 편의 메소드
    public void saveCouponAndProduct(Product product) {
        this.product = product;
        product.getCouponList().add(this);
    }
}
