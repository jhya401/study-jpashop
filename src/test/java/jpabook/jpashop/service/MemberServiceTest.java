package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional // 같은 트랜젝션 = 같은 영속성 관리가 된다. = 같은 타입의 엔티티는 동등하다.
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @Rollback(value = false)
    void 회원가입() {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long saveId = memberService.join(member);

        //then
        assertEquals(member, memberRepository.findOne(saveId)); // 서비스에서 조회한값, 리파지토리에서 조회한 값 동일한지 테스트 하는 것임
    }

    @Test
    void 회원조회() {
        //given
        String name = "Lee";
        Member member1 = new Member();
        member1.setName(name);

        //when
        Long id = memberService.join(member1);
        Member member = memberRepository.findOne(id);

        //then
        Assertions.assertEquals(member.getId(), id);
        Assertions.assertEquals(name, member.getName());
    }

    @DisplayName("중복 회원 검증")
    @Test
    void 중복_회원_검증() {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);

        //then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });
    }

}