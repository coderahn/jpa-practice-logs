package com.example.jpaTest;

import com.example.jpaTest.Entity.Coupon;
import com.example.jpaTest.Entity.Member;
import com.example.jpaTest.Entity.Product;
import com.example.jpaTest.Service.CouponService;
import com.example.jpaTest.Service.MemberService;
import com.example.jpaTest.Service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class JpaTestApplicationTests {
	@PersistenceContext
	private EntityManager em;

	@Autowired
	MemberService memberService;

	@Autowired
	CouponService couponService;

	@Autowired
	ProductService productService;

	@Test
	@Rollback(false)
	public void saveMemberAndGetMemberNameTest() {
		//저장
		Member member = new Member();
		member.setName("테스터");

		Long id = memberService.saveMember(member);

		//더티체킹 테스트
		memberService.updateMember(id, "테스터2");

		String memberName = memberService.findMember(id);

		System.out.println("변경된 이름: "  + memberName);

		/**
		 * 2025.01.03
		 * saveMember, updateMember, findMember 각각 @Transactional이 있어서 이 테스트공간에서 실행시
		 * 각각 영속성 컨텍스트는 다를 것
		 * 최초 saveMember로 영속성컨텍스트에 저장 후 트랜잭션이 끝나서 flush가 되고 id를 반환 후 트랜잭션이 종료될 것
		 * 이 때 영속성 컨텍스트도 종료될 것
		 * updateMember에서 다시 트랜잭션을 시작하여, em.find()로 DB에서 조회함(이때 1차캐시는 트랜잭션이 이전에 끝나서 적용 안 됨)
		 * em.find()로 새로운 영속성 컨텍스트에 가져온 후, setName(테스터2)를 해서 스냅샷과 비교후, 가져온 애랑 다르기 때문에 update가 나감
		 * 그러나 em.flush 코드가 없어서 실제로 안 보임
		 * 마지막으로 findMember()로 호출시 또 새로운 영속성컨텍스트가 시작되어 DB조회가 될 수 밖에 없음
		 * 테스터2가 나올 것
		 *
		 */
	}

	@Test
	@Transactional
	@Rollback(false)
	public void saveMemberAndGetMemberNameTest2() {
		//저장
		Member member = new Member();
		member.setName("테스터");

		Long id = memberService.saveMember(member);

		//더티체킹 테스트
		memberService.updateMember(id, "테스터2");

		String memberName = memberService.findMember(id);

		System.out.println("변경된 이름: "  + memberName);

		/**
		 * 2025.01.03
		 * 각각의 @Transactional이 서비스 메소드에 붙어있지만, 그 상위 메소드인 @Test메소드에 @Transactional을 붙였기에 하나의 트랜잭션으로 작동
		 * 이말은 하나의 영속성 컨텍스트를 공유한다는 의미
		 * 최초 saveMember시 영속성컨텍스트가 열려서 member객체는 영속화 되고, 1차적으로 DB저장 전 1차 캐시에 넣어둔 후 쓰기지연.
		 * updateMember시에 setName을 하는데, 일단 DB에 저장전 1차캐시 업데이트(이름 다르기 때문에 업데이트)
		 * 마지막에 findmember때도 DB가 아니라 1차캐시에서 get
		 * 테스트 트랜잭션 종료시 모든 DB DML 나감
		 */
	}

	@Test
	public void twoSideTest() {
		/**
		 * [테스트할 것]
		 * Coupon, Product N:1 관계
		 * 양방향 연관관계 설정
		 * 정상 INSERT
		 */

		Product product = new Product();
		product.setProductName("올리브영1000원권");
		product.setPrice(1000);

		productService.saveProduct(product);

		Coupon coupon = new Coupon();
		coupon.setCouponId("OL1000");

		//아래 코드는 연관관계 편의메소드로 지정하면 좋음
//		coupon.setProduct(product);
//		product.getCouponList().add(coupon);

		coupon.saveCouponAndProduct(product);

		couponService.saveCoupon(coupon);

	}

	@Description("양방향 연관관계 및 조회 테스트")
	@Test
	@Transactional
	@Rollback(false)
	public void twoSideSelectTest() {
		/**
		 * 260104
		 * [테스트할 것]
		 * Coupon, Product N:1 관계
		 * 양방향 연관관계 설정
		 * 정상 INSERT
		 * Coupon 조회시 Product도 조회되는지 여부
		 * 지연로딩 테스트
		 */

		//상품저장
		Product product = new Product();
		product.setProductName("올리브마트 1000원권");
		product.setPrice(1000);

		productService.saveProduct(product);

		//쿠폰저장
		Coupon coupon = new Coupon();
		coupon.setCouponId("OL1000");
		coupon.setCouponPrice(1000);
		coupon.saveCouponAndProduct(product); //연관관계 편의 메소드로 양방향 관계 설정

		couponService.saveCoupon(coupon);

		//select문 보려고 날림. 없으면 insert문만 나감. 아래 getCoupon시 1차캐시 뒤지기 때문
		em.flush();
		em.clear();

		Coupon couponInfo = couponService.getCoupon("OL1000");

		Assertions.assertThat(couponInfo)
				.as("쿠폰 정보 정상 조회 후 가격 확인")
				.isNotNull()
				.extracting(Coupon::getCouponPrice)
				.isEqualTo(1000);

		Assertions.assertThat(couponInfo.getProduct())
				.as("쿠폰 정보에 속한 상품 정보의 상품명을 가져와서 확인")
				.isNotNull()
				.extracting(Product::getProductName)
				.isEqualTo("올리브마트 1000원권");

		/**
		 * 260104
		 * [테스트 정리]
		 * Coupon, Product N:1 도메인으로 생성하여 엔티티 양방향 연관관계로 설정해줌
		 * Coupon에서 연관관계 편의 메소드를 만들어서 객체간의 관계 설정(안 해주면 Product가져올 때 NPE)
		 * em.flush, em.clear를 insert 처리 후 해줘야 select문 볼 수 있음(1차캐시로 인해 해줘야 볼 수 있음)
		 */

		/**
		 * [기억할 것]
		 * Coupon의 @ManyToOne으로 설정해준 Product의 pk값을 @JoinColumn으로 설정 안 해주면 JPA가 알아서 PK명 생성(product_product_id)
		 * Coupon의 @ManyToOne(fetch = FetchType.LAZY)을 설정해줘야 위 getProduct().getProductName()시에 쿼리가 나가서 불필요한 DB자원 낭비 안 함
		 * 기본적으로 LAZY로딩으로 바꾼 후, 즉시로딩이 필요하면 JPQL fetch join 사용
		 */
	}
}
