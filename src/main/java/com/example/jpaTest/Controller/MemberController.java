package com.example.jpaTest.Controller;

import com.example.jpaTest.Entity.Member;
import com.example.jpaTest.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {
    @Autowired
    MemberService memberService;

    @GetMapping("/test")
    public String saveMember(Member member) {
        memberService.saveMember(member);

        return "success";
    }
}
