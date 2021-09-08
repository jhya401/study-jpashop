package jpabook.jpashop.api;

import jpabook.jpashop.MapperUtil;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MemberApiController {

    private final MemberService memberService;

    // 나쁜 조회
    @GetMapping("/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    // 좋은 조회
    @GetMapping("/v2/members")
    public Result membersV2(){
        List<Member> members = memberService.findMembers();
        List<MemberDto> collect = members.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    @PostMapping("/v1/members")
    public CreateMemeberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemeberResponse(id);
    }

    @PostMapping("/v2/members")
    public CreateMemeberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        // api통신은 DTO로하고, 백엔드에서 엔티티로 변환해준다.
        Member member = MapperUtil.toObject(request, Member.class);
        Long id = memberService.join(member);
        return new CreateMemeberResponse(id);
    }

    @PutMapping("/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member member = memberService.findOne(id);

        return new UpdateMemberResponse(member.getId(), member.getName());
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class CreateMemeberResponse {
        private Long id;
    }
}
