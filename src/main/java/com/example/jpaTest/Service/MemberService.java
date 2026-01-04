package com.example.jpaTest.Service;

import com.example.jpaTest.Entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    @PersistenceContext
    EntityManager em;

    @Transactional
    public Long saveMember(Member member) {
        em.persist(member);

        return member.getId();
    }

    @Transactional
    public void updateMember(Long id, String name) {
        Member member = em.find(Member.class, id);
        String name1 = member.getName();
        System.out.println("저장한 이름:" + name1);

        member.setName(name);
    }

    @Transactional
    public String findMember(Long id) {
        Member member = em.find(Member.class, id);

        return member.getName();
    }
}
