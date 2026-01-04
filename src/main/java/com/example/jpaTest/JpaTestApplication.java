package com.example.jpaTest;

import com.example.jpaTest.Entity.Member;
import com.example.jpaTest.Service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpaTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaTestApplication.class, args);
	}


//	@Bean
//	public CommandLineRunner run(MemberService memberService) {
//		return args -> {
//			Member member = new Member();
//			member.setName("실시간_확인용");
//			memberService.saveMember(member);
//			System.out.println("---- 데이터 저장 완료! 이제 H2 콘솔 보세요! ----");
//		};
//	}
}
