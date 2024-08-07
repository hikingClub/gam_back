package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.util.VerificationStore;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private final MemberRepository memberRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final MailService mailService;

    private static final long EXPIRY_TIME = 5 * 60 * 1000; // 인증 코드의 유효 시간: 5분

    // 이메일 인증 요청 메소드
    public void sendVerificationMail(String email) throws Exception {
        String token = MailService.createToken();
        VerificationStore.addCode(email, token, EXPIRY_TIME);
        mailService.sendMail(email, token);
    }

    // 이메일 인증 코드 확인 메소드
    public boolean checkVerificationCode(String email, String inputCode) {
        VerificationStore.VerificationInfo info = VerificationStore.getCode(email);
        if (info != null && info.getCode().equals(inputCode)) {
            VerificationStore.removeCode(email);
            VerificationStore.addVerifiedEmail(email);
            return true;
        }
        return false;
    }

    // 이메일 인증 여부 확인 메소드
    public boolean isEmailVerified(String email) {
        return VerificationStore.isEmailVerified(email);
    }

    // 이메일 중복 체크 메소드
    public boolean isEmailDuplicate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    // 아이디 중복 체크 메소드
    public boolean isUidDuplicate(String uid) {
        return memberRepository.findByUid(uid).isPresent();
    }

    // 회원가입 메소드
    public void signup(Member member) throws Exception {
        if (!isEmailVerified(member.getEmail())) {
            throw new Exception("이메일 인증이 필요합니다.");
        }
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new Exception("중복된 이메일입니다.");
        }
        if (memberRepository.findByUid(member.getUid()).isPresent()) {
            throw new Exception("중복된 아이디입니다.");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setVerified(true);
        memberRepository.save(member);
    }

    // 로그인 메소드
    public Member login(Member member) throws Exception {
        Optional<Member> optionalMember = memberRepository.findByUid(member.getUid());
        if (optionalMember.isEmpty()) {
            throw new Exception("존재하지 않는 아이디입니다.");
        }
        Member existingMember = optionalMember.get();
        if (!passwordEncoder.matches(member.getPassword(), existingMember.getPassword())) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }
        if (!existingMember.isVerified()) {
            throw new Exception("이메일 인증이 완료되지 않았습니다.");
        }
        return existingMember;
    }

    public Integer create(Member member) {
        memberRepository.save(member);
        return member.getSeq();
    }

    public Integer findByUid(String uid) {
        Optional<Member> member = memberRepository.findByUid(uid);
        return member.map(Member::getSeq).orElse(null);
    }
}
