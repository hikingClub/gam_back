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

    private static final long EXPIRY_TIME = 5 * 60 * 1000; // 5분

    // 회원가입 처리
    public void signup(Member member) throws Exception {
        // 이메일 중복 체크
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new Exception("중복된 이메일입니다.");
        }

        // 아이디 중복 체크
        if (memberRepository.findByUid(member.getUid()).isPresent()) {
            throw new Exception("중복된 아이디입니다.");
        }

        // 비밀번호 암호화
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setVerified(false); // 이메일 인증 여부를 false로 설정

        // 회원 정보 저장
        memberRepository.save(member);

        // 이메일 인증 코드 발송
        String token = MailService.createToken();
        VerificationStore.addCode(member.getEmail(), token, EXPIRY_TIME);

        mailService.sendMail(member.getEmail(), token);
    }

    // 이메일 인증 코드 확인
    public boolean checkVerificationCode(String email, String inputCode) {
        VerificationStore.VerificationInfo info = VerificationStore.getCode(email);
        if (info != null && info.getCode().equals(inputCode)) {
            VerificationStore.removeCode(email);

            // 이메일로 회원 찾기
            Optional<Member> optionalMember = memberRepository.findByEmail(email);
            if (optionalMember.isPresent()) {
                Member member = optionalMember.get();
                member.setVerified(true); // 이메일 인증 완료
                memberRepository.save(member);
                return true;
            }
        }
        return false;
    }

    // 로그인 처리
    public Member login(Member member) throws Exception {
        // 아이디 체크
        Optional<Member> optionalMember = memberRepository.findByUid(member.getUid());
        if (optionalMember.isEmpty()) {
            throw new Exception("존재하지 않는 아이디입니다.");
        }

        // 비밀번호 체크
        Member existingMember = optionalMember.get();
        if (!passwordEncoder.matches(member.getPassword(), existingMember.getPassword())) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        // 이메일 인증 여부 체크
        if (!existingMember.isVerified()) {
            throw new Exception("이메일 인증이 완료되지 않았습니다.");
        }

        return existingMember;
    }

    // 인증 이메일 발송
    public void sendVerificationMail(String email) throws Exception {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            String token = MailService.createToken();
            VerificationStore.addCode(email, token, EXPIRY_TIME);

            mailService.sendMail(email, token);
        } else {
            throw new Exception("존재하지 않는 이메일입니다.");
        }
    }
}
