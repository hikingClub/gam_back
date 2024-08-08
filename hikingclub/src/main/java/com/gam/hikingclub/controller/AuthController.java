package com.gam.hikingclub.controller;

import com.gam.hikingclub.security.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) {
        try {
            String tokenUri = "https://kauth.kakao.com/oauth/token";
            String userInfoUri = "https://kapi.kakao.com/v2/user/me";

            // 엑세스 토큰 요청
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", "authorization_code");
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("redirect_uri", redirectUri);
            params.put("code", code);

            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUri, params, Map.class);

            if (tokenResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> tokenMap = tokenResponse.getBody();
                String accessToken = (String) tokenMap.get("access_token");

                // 사용자 정보 요청
                Map<String, String> userInfoHeaders = new HashMap<>();
                userInfoHeaders.put("Authorization", "Bearer " + accessToken);

                ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUri, Map.class, userInfoHeaders);

                if (userInfoResponse.getStatusCode().is2xxSuccessful()) {
                    OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(), userInfoResponse.getBody(), "id");
                    oAuth2SuccessHandler.onAuthenticationSuccess(request, response, new OAuth2AuthenticationToken(oAuth2User, Collections.emptyList(), "kakao"));

                    return ResponseEntity.ok().build();
                }
            }

            return ResponseEntity.status(401).body("카카오 로그인 실패");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }
}
