package com.gam.hikingclub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gam.hikingclub.dto.KakaoAccountDto;
import com.gam.hikingclub.dto.KakaoTokenDto;
import com.gam.hikingclub.dto.LoginResponseDto;
import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public LoginResponseDto kakaoLogin(String code, HttpSession session) {
        KakaoTokenDto kakaoTokenDto = getKakaoAccessToken(code);
        KakaoAccountDto kakaoAccountDto = getKakaoUserInfo(kakaoTokenDto.getAccess_token());

        Member member = findOrCreateMember(kakaoAccountDto);
        session.setAttribute("memberSeq", member.getSeq());

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setLoginSuccess(true);
        responseDto.setMessage("로그인 성공!");

        return responseDto;
    }

    private KakaoTokenDto getKakaoAccessToken(String code) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "2acd6511c9ba3a8086adbbf0e5322117");
        params.add("redirect_uri", "https://cdn.kyujanggak.com/auto/kakao/callback");
        params.add("code", code);
        params.add("client_secret", "R39gyqJZ5U3tfdIsYJLtIFkc0PWF57tA");

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoTokenDto kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kakaoTokenDto;
    }

    private KakaoAccountDto getKakaoUserInfo(String accessToken) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> profileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoAccountDto kakaoAccountDto = null;
        try {
            kakaoAccountDto = objectMapper.readValue(profileResponse.getBody(), KakaoAccountDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kakaoAccountDto;
    }

    private Member findOrCreateMember(KakaoAccountDto kakaoAccountDto) {
        Optional<Member> optionalMember = memberRepository.findByUid(kakaoAccountDto.getId().toString());
        Member member;

        if (optionalMember.isPresent()) {
            member = optionalMember.get();
        } else {
            member = new Member();
            member.setUid(kakaoAccountDto.getId().toString());
            member.setNickname(kakaoAccountDto.getProfile().getNickname());
            member.setEmail(kakaoAccountDto.getEmail());
            member.setPassword(""); // 비밀번호 필드는 빈 값으로 설정
            member.setAlarmCheck(0); // 알람 체크 기본값 설정
            member.setVerified(true); // 이메일 인증을 강제로 통과시키는 경우
            memberRepository.save(member);
        }

        return member;
    }
}
