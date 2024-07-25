package com.gam.hikingclub.service;

import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyPageService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    public Member getMemberBySeq(int seq) throws Exception {
        return memberRepository.findBySeq(seq)
                .orElseThrow(() -> new Exception("유저 정보를 찾을 수 없습니다."));
    }

    public List<SearchHistory> getUserSearchHistory(int seq) {
        return searchHistoryRepository.findBySeq(seq);
    }
}
