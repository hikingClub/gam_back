package com.gam.hikingclub.controller;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // 회원가입 요청 처리
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Member member) {
        try {
            memberService.signup(member);
            return ResponseEntity.ok("회원가입 성공! 이메일 인증을 완료해주세요.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원가입 실패 사유: " + e.getMessage());
        }
    }

    // 이메일 인증 코드 확인 요청 처리
    @PostMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = memberService.checkVerificationCode(email, code);
        if (isVerified) {
            return ResponseEntity.ok("이메일 인증 성공!");
        } else {
            return ResponseEntity.badRequest().body("이메일 인증 실패: 잘못된 인증 코드입니다.");
        }
    }

    // 로그인 요청 처리
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Member member, HttpSession session) {
        try {
            Member loggedIn = memberService.login(member);
            // 세션에 유저 시퀀스 추가
            session.setAttribute("memberSeq", loggedIn.getSeq());
            return ResponseEntity.ok("로그인 성공! 세션 SEQ: " + session.getAttribute("memberSeq"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("로그인 실패 사유: " + e.getMessage());
        }
    }

    // 로그아웃 요청 처리
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // 로그아웃 시 세션을 비움
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공!");
    }
}
