package com.gam.hikingclub.service;

import com.gam.hikingclub.dto.MemberRecommendDTO;
import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.RecommendedField;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.RecommendFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Member getMember(Integer seq) throws Exception {
        Optional<Member> optionalMember = memberRepository.findBySeq(seq);
        if (optionalMember.isEmpty()) {
            throw new Exception("존재하지 않는 멤버 번호입니다.");
        }
        return optionalMember.get();
    }


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

    public void setRecIndexes(Member member) throws Exception {
        Optional<Member> optionalMember = memberRepository.findBySeq(member.getSeq());
        if (optionalMember.isPresent()) {
            Member updateMember = optionalMember.get();
            updateMember.setRecIndexes(member.getRecIndexes());
            memberRepository.save(updateMember);
        } else {
            throw new Exception("존재하지 않는 멤버입니다.");
        }
    }

    public MemberRecommendDTO getRecommendedSetting(Integer seq) throws Exception {
        Optional<Member> optionalMember = memberRepository.findBySeq(seq);
        if (optionalMember.isEmpty()) {
            throw new Exception("존재하지 않는 멤버 번호입니다.");
        }
        return new MemberRecommendDTO(optionalMember.get().getInterest(), optionalMember.get().getJobRange(), optionalMember.get().getAgeRange());
    }

    public void setInterest(Member member) throws Exception {
        Optional<Member> optionalMember = memberRepository.findBySeq(member.getSeq());
        if (optionalMember.isPresent()) {
            Member updateMember = optionalMember.get();
            updateMember.setRecIndexes(member.getRecIndexes());
            memberRepository.save(updateMember);
        } else {
            throw new Exception("존재하지 않는 멤버입니다.");
        }
    }
}

