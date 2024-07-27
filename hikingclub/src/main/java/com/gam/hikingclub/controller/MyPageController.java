package com.gam.hikingclub.controller;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.service.MemberService;
import com.gam.hikingclub.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MyPageService myPageService;
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

    @PostMapping("/getRecommendField")
    public ResponseEntity<Map<String, Object>> getRecommendField(@RequestBody Member member) {
        try {
            List<String> recommendFieldList = myPageService.getRecommendFieldName(member.getSeq());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "성공");
            response.put("recommendFieldList", recommendFieldList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "오류 메세지: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/insertRecIndexes")
    public ResponseEntity<String> insertRecIndexes(@RequestBody Member member) {
        try {
            myPageService.setRecIndexes(member);
            return ResponseEntity.ok("성공!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("에러 이유" + e.getMessage());
        }
    }

    @PostMapping("/getRecommendSetting")
    public ResponseEntity<String> getRecommendSetting(@RequestBody Member member) {
        try {
            myPageService.setRecIndexes(member);
            return ResponseEntity.ok("성공!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("에러 이유" + e.getMessage());
        }
    }
}
