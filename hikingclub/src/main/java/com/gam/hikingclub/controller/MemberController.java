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

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Member member) {
        try {
            memberService.signup(member);
            return ResponseEntity.ok("회원가입 성공!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원가입 실패 사유: " + e.getMessage());
        }
    }

    @PostMapping("/sendVerificationMail")
    public ResponseEntity<String> sendVerificationMail(@RequestParam String email) {
        try {
            memberService.sendVerificationMail(email);
            return ResponseEntity.ok("인증 이메일이 발송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("인증 이메일 발송 실패: " + e.getMessage());
        }
    }

    @GetMapping("/checkVerificationCode")
    public ResponseEntity<Boolean> checkVerificationCode(@RequestParam String userNumber) {
        boolean isMatch = memberService.checkVerificationCode(userNumber);
        return ResponseEntity.ok(isMatch);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Member member, @RequestParam String verificationCode, HttpSession session) {
        try {
            if (!memberService.checkVerificationCode(verificationCode)) {
                throw new Exception("이메일 인증 번호가 일치하지 않습니다.");
            }
            Member loggedIn = memberService.login(member);
            // 세션에 유저 시퀀스 추가
            session.setAttribute("memberSeq", loggedIn.getSeq());
            return ResponseEntity.ok("로그인 성공! 세션 SEQ: " + session.getAttribute("memberSeq"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("로그인 실패 사유: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // 로그아웃 시 세션을 비움
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공!");
    }
}
