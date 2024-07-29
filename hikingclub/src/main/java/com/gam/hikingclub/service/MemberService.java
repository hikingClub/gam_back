package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.util.VerificationStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

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
}
