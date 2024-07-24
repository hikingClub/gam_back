package com.gam.hikingclub.controller;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    private Member getLoggedInUser(HttpSession session) throws Exception {
        Integer memberSeq = (Integer) session.getAttribute("memberSeq");
        if (memberSeq == null) {
            throw new Exception("로그인시 마이페이지에 접근이 가능합니다.");
        }
        return memberRepository.findBySeq(memberSeq)
                .orElseThrow(() -> new Exception("유저 정보를 찾을 수 없습니다."));
    }


}
