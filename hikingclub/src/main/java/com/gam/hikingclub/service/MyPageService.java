package com.gam.hikingclub.service;


import com.gam.hikingclub.dto.MemberRecommendDTO;
import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.RecommendedField;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.RecommendFieldRepository;
import com.gam.hikingclub.entity.SearchHistory;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.repository.MemberRepository;

import com.gam.hikingclub.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class MyPageService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RecommendFieldRepository recommendFieldRepository;

    // 숫자로 되어있는 RecIndexes를 RecIndex에 대입해서 가져옴
    public List<String> getRecommendFieldName(Integer seq) throws Exception {
        List<String> recommendList = new ArrayList<>();
        List<Integer> recIndexList = new ArrayList<>();

        Optional<Member> optionalMember = memberRepository.findBySeq(seq);
        if (optionalMember.isPresent()) {

            // recIndexes가 null인 경우 빈 리스트를 반환
            if (optionalMember.get().getRecIndexes() == null) {
                System.out.println("recIndexes 값이 비어있습니다.");
                return recommendList;
            }

            String recIndexes = optionalMember.get().getRecIndexes();
            String[] splitRecIndexes = recIndexes.split(",");

            // 각 문자열을 Integer로 변환
            for (String index : splitRecIndexes) {
                try {
                    recIndexList.add(Integer.parseInt(index.trim()));  // 공백 제거 후 Integer로 변환
                } catch (NumberFormatException e) {
                    // 숫자가 아닐 경우
                    System.out.println("잘못된 값이 들어있습니다 : " + index);
                }
            }
        } else {
            throw new Exception("존재하지 않는 멤버입니다.");
        }

        for (Integer index : recIndexList) {
            Optional<RecommendedField> optionalRecommendedField = recommendFieldRepository.findByRecIndex(index);
            if (optionalRecommendedField.isPresent()) {
                recommendList.add(optionalRecommendedField.get().getCategory1() + " > " + optionalRecommendedField.get().getCategory2());
            } else {
                throw new Exception("존재하지 않는 recIndex 번호");
            }
        }
        return recommendList;
    }

    //DTO를 이용해서 관심분야, 나이, 직업을 불러옴
    public MemberRecommendDTO getRecommendedSetting(Integer seq) throws Exception {
        Optional<Member> optionalMember = memberRepository.findBySeq(seq);
        if (optionalMember.isEmpty()) {
            throw new Exception("존재하지 않는 멤버 번호입니다.");
        }
        return new MemberRecommendDTO(optionalMember.get().getInterest(), optionalMember.get().getJobRange(), optionalMember.get().getAgeRange());
    }

    // 추천설정 업데이트
    public void setRecommendSetting(Member member) throws Exception {
        Optional<Member> optionalMember = memberRepository.findBySeq(member.getSeq());
        if (optionalMember.isPresent()) {
            Member updateMember = optionalMember.get();
            updateMember.setRecIndexes(member.getRecIndexes());
            updateMember.setInterest(member.getInterest());
            updateMember.setJobRange(member.getJobRange());
            updateMember.setAgeRange(member.getAgeRange());
            memberRepository.save(updateMember);
        } else {
            throw new Exception("존재하지 않는 멤버입니다.");
        }
    }
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

