package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyPageService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // memberSeq로 유저 정보를 가져오는 메서드
    public Member getMemberBySeq(int seq) throws Exception {
        return memberRepository.findBySeq(seq)
                .orElseThrow(() -> new Exception("유저 정보를 찾을 수 없습니다."));
    }

    // 유저의 검색 기록을 가져오는 메서드
    public List<SearchHistory> getUserSearchHistory(int seq) {
        return searchHistoryRepository.findBySeq(seq);
    }

    // 알람 설정을 업데이트하는 메서드
    public void updateAlarmCheck(int seq, int alarmCheck) throws Exception {
        Member member = getMemberBySeq(seq);
        member.setAlarmCheck(alarmCheck);
        memberRepository.save(member);
    }

    // 비밀번호를 업데이트하는 메서드
    public void updatePassword(int seq, String oldPassword, String newPassword) throws Exception {
        Member member = getMemberBySeq(seq);

        // 기존 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new Exception("기존 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호를 암호화하여 저장
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    // 회원을 삭제하는 메소드
    public void deleteUser(int seq) throws Exception {
        Member member = getMemberBySeq(seq);

        // 회원의 검색 기록 디비 삭제 더 추가해야할듯 검색기록말고 다른 디비에 존재하는 모두
        searchHistoryRepository.deleteBySeq(seq);

        // 회원 삭제
        memberRepository.delete(member);
    }
}
