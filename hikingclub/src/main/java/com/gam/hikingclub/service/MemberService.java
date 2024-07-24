package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.repository.MemberRepository;
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

        // 회원 정보 저장
        memberRepository.save(member);
    }

    public Member login(Member member) throws Exception {
        // 아이디 중복체크
        Optional<Member> optionalMember = memberRepository.findById(member.getUid());
        System.out.println("1241" + optionalMember);
        if (optionalMember.isEmpty()) {
            throw new Exception("존재하지 않는 아이디입니다.");
        }
        System.out.println("아이디 체크 함");
        // 비밀번호 중복체크
        Member existingMember = optionalMember.get();
        if (!passwordEncoder.matches(member.getPassword(), existingMember.getPassword())) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }
        System.out.println("비번 체크도 함");

        return existingMember;
    }
}
