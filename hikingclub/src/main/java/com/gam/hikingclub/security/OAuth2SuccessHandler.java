package com.gam.hikingclub.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;

    public OAuth2SuccessHandler(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String uid = attributes.get("id").toString();
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = properties.get("nickname").toString();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = kakaoAccount.get("email").toString();

        Member kakaoInfo = new Member();
        kakaoInfo.setUid(uid);
        kakaoInfo.setNickname(nickname);
        kakaoInfo.setEmail(email);
        kakaoInfo.setPassword(""); // 비밀번호 필드는 빈 값으로 설정
        kakaoInfo.setAlarmCheck(0); // 알람 체크 기본값 설정
        kakaoInfo.setVerified(true); // 이메일 인증을 강제로 통과시키는 경우

        Integer seq = memberService.findByUid(uid);
        boolean isNewMember = false;
        if (seq == null) {
            seq = memberService.create(kakaoInfo);
            isNewMember = true;
        } else {
            kakaoInfo.setSeq(seq);
        }
        request.getSession().setAttribute("memberSeq", seq);

        // JSON 응답 생성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", isNewMember ? "signup_success" : "login_success");
        responseData.put("member", kakaoInfo);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));
    }
}
