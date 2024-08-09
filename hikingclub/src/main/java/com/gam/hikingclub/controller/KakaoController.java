package com.gam.hikingclub.controller;

import com.gam.hikingclub.dto.LoginResponseDto;
import com.gam.hikingclub.dto.KakaoTokenDto;
import com.gam.hikingclub.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class KakaoController {

    private final AuthService authService;

    @Autowired
    public KakaoController(AuthService authService) {
        this.authService = authService;
    }

    // 카카오톡 로그인시 엔드포인트
    @GetMapping("/login/oauth2/callback/kakao")
    public ResponseEntity<String> kakaoLogin(HttpServletRequest request, HttpSession session) {
        try {
            String code = request.getParameter("code");
            KakaoTokenDto kakaoTokenDto = authService.getKakaoAccessToken(code);
            String kakaoAccessToken = kakaoTokenDto.getAccess_token();
            ResponseEntity<LoginResponseDto> responseEntity = authService.kakaoLogin(kakaoAccessToken);
            LoginResponseDto loginResponse = responseEntity.getBody();  // ResponseEntity에서 body 추출

            if (loginResponse.isLoginSuccess()) {

                // 세션에 사용자 정보 저장
                session.setAttribute("memberSeq", loginResponse.getMember().getSeq());
                // 성공 메시지 반환
                return ResponseEntity.ok("로그인 성공! 세션 SEQ: " + session.getAttribute("memberSeq"));
            } else {
                // 로그인 실패 시
                return ResponseEntity.status(401).body("로그인 실패: 사용자 정보를 가져올 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("로그인 실패 사유: " + e.getMessage());
        }
    }
}
