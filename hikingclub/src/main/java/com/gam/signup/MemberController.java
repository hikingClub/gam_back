package com.gam.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//JSON 또는 XML 형식의 HTTP 응답을 생성
@RestController
// 이 클래스의 모든 핸들러 메서드들이 '/members' 경로에 매핑됨
// '/members' 경로로 들어오는 요청을 처리함!!
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // /members/signup 경로로 들어오는 HTTP POST 요청을 처리함
    @PostMapping("/signup")
    public Member signup(@RequestBody Member member) {
        return memberService.saveMember(member);
    }
}
