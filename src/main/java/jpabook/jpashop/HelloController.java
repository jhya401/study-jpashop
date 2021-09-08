package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HelloController {

    private final MemberRepository memberRepository;

    @GetMapping(value = "hello")
    public String hello(Model model) {
        log.trace("Trace Level 테스트");
        log.debug("DEBUG Level 테스트");
        log.info("INFO Level 테스트");
        log.warn("Warn Level 테스트");
        log.error("ERROR Level 테스트");

        memberRepository.findOne(1L);

        model.addAttribute("data", "hello!!");
        return "hello"; // resource/templates/hello.html 뷰 호출함
    }
}
