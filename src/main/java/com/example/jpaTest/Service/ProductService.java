package com.example.jpaTest.Service;

import com.example.jpaTest.Entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    @PersistenceContext
    private EntityManager em;

    //상품 저장
    @Transactional
    public int saveProduct(Product product) {
        em.persist(product);

        return product.getProductId();
    }
}
