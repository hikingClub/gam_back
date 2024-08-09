package com.gam.hikingclub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gam.hikingclub.dto.KakaoAccountDto;
import com.gam.hikingclub.dto.KakaoTokenDto;
import com.gam.hikingclub.dto.LoginResponseDto;
import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthService {

    private static final String KAKAO_TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_REDIRECT_URI = "http://localhost:5173/login/oauth2/callback/kakao";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    private final MemberRepository memberRepository;

    @Transactional
    public KakaoTokenDto getKakaoAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // Http Response Body 객체 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);
        params.add("client_secret", KAKAO_CLIENT_SECRET);

        // 헤더와 바디를 합치기 위해 HttpEntity 객체 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // 카카오에서 Access token 받아오기
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                KAKAO_TOKEN_URI,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON Parsing (-> KakaoTokenDto)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoTokenDto kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);

            // 토큰 정보 출력 (디버깅용)
            System.out.println("Access Token: " + kakaoTokenDto.getAccess_token());
            System.out.println("Token Type: " + kakaoTokenDto.getToken_type());
            System.out.println("Expires In: " + kakaoTokenDto.getExpires_in());
            System.out.println("Refresh Token: " + kakaoTokenDto.getRefresh_token());
            System.out.println("ID Token: " + kakaoTokenDto.getId_token());
            System.out.println("Scope: " + kakaoTokenDto.getScope());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoTokenDto;
    }

    public ResponseEntity<LoginResponseDto> kakaoLogin(String kakaoAccessToken) {
        Member member = getKakaoInfo(kakaoAccessToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setLoginSuccess(true);
        loginResponseDto.setMember(member);

        HttpHeaders headers = new HttpHeaders(); // 추가된 헤더 객체 선언

        Member existOwner = memberRepository.findByUid(member.getUid()).orElse(null);
        try {
            if (existOwner == null) {
                System.out.println("처음 로그인 하는 회원입니다.");
                memberRepository.save(member);
            }
            loginResponseDto.setLoginSuccess(true);

            return ResponseEntity.ok().headers(headers).body(loginResponseDto);
        } catch (Exception e) {
            loginResponseDto.setLoginSuccess(false);
            return ResponseEntity.badRequest().body(loginResponseDto);
        }
    }

    // getKakaoInfo 메서드 구현 필요
    public AuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member getKakaoInfo(String kakaoAccessToken) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        System.out.println("Sending request with headers: " + headers.toString());

        // POST 방식으로 API 서버에 요청 후 response 받아옴
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                KAKAO_USER_INFO_URI,
                HttpMethod.GET,
                accountInfoRequest,
                String.class
        );

        System.out.println("Received response: " + accountInfoResponse.getBody());

        // JSON Parsing (-> KakaoAccountDto)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoAccountDto kakaoAccountDto = null;
        try {
            kakaoAccountDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountDto.class);
            System.out.println("Parsed KakaoAccountDto: " + kakaoAccountDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 회원가입 처리하기
        String kakaoUid = String.valueOf(kakaoAccountDto.getId());
        Member existOwner = memberRepository.findByUid(kakaoUid).orElse(null);

        if (existOwner != null) {
            System.out.println("Existing member found: " + existOwner);
            return existOwner;
        } else {
            Member newMember = new Member();
            newMember.setUid(kakaoUid);
            newMember.setEmail(kakaoAccountDto.getKakaoAccount().getEmail());
            newMember.setNickname(kakaoAccountDto.getKakaoAccount().getProfile().getNickname());
            newMember.setAlarmCheck(0);
            newMember.setVerified(true);

            Member savedMember = memberRepository.save(newMember);
            System.out.println("New member saved: " + savedMember);
            return savedMember;
        }
    }

}
