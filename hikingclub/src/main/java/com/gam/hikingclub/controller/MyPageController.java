package com.gam.hikingclub.controller;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.service.MyPageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    private Member getLoggedInUser(HttpSession session) throws Exception {
        Integer memberSeq = (Integer) session.getAttribute("memberSeq");
        if (memberSeq == null) {
            throw new Exception("로그인시 마이페이지에 접근이 가능합니다.");
        }
        return myPageService.getMemberBySeq(memberSeq);
    }

    @GetMapping("/kwdhistory")
    public ResponseEntity<?> getUserHistory(HttpSession session) {
        try {
            Member member = getLoggedInUser(session);
            List<SearchHistory> history = myPageService.getUserSearchHistory(member.getSeq());
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("조회 실패 사유: " + e.getMessage());
        }
    }
}
