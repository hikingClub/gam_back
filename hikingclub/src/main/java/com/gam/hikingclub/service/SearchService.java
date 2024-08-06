package com.gam.hikingclub.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.repository.MemberRepository;
import com.gam.hikingclub.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Service
public class SearchService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    private static final String API_KEY = "nn15pgmriohhrqmm4cc8nil9y7f0y23q4yz1sido747gquojm4vz2vje6iq3ei5d";

    public JsonNode searchData(String searchKeyword, String startDate, String endDate, String pageNum, String pagePer) throws Exception {
        String jsonInputString = String.format(
                "{\"searchKeyword\": \"%s\", \"startDate\": \"%s\", \"endDate\": \"%s\", \"pageNum\": \"%s\", \"pagePer\": \"%s\"}",
                searchKeyword, startDate, endDate, pageNum, pagePer
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://metalink.k-knowledge.kr/search/openapi/search"))
                .header("Content-Type", "application/json")
                .header("api_key", API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // JSON 응답을 JsonNode로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response.body());
        } else {
            throw new RuntimeException("오류 사유 : " + response.statusCode());
        }
    }

   //  유저의 검색 기록 저장하는 메서드
    public void setUserSearchHistory(SearchHistory searchHistory) throws Exception {
        Optional<Member> optionalMember = memberRepository.findBySeq(searchHistory.getSeq());
        if (optionalMember.isPresent()) {
            Integer memberSeq = searchHistory.getSeq();
            int maxRecords = 50;
            // 현재 유저의 검색 기록 수를 가져옴
            Integer searchHistoryCount = searchHistoryRepository.countBySeq(memberSeq);
            System.out.println("검색된 아이디 갯수 : " + searchHistoryCount);
            if (searchHistoryCount >= maxRecords) {
                // 오래된 기록을 삭제해야 하는 경우
                int recordsToDelete = (int) (searchHistoryCount - maxRecords + 1); // 삭제해야 할 레코드 수 계산
                System.out.println("지워야 할 갯수 : " + recordsToDelete);
                Pageable pageable = PageRequest.of(0, recordsToDelete, Sort.by(Sort.Direction.ASC, "keywordTime"));
                List<SearchHistory> oldestHistories = searchHistoryRepository.findBySeq(memberSeq, pageable);
                // 오래된 기록 삭제
                searchHistoryRepository.deleteAll(oldestHistories);
                System.out.println("지우고 난 뒤의 갯수 : " + searchHistoryRepository.countBySeq(memberSeq));
            }
            searchHistoryRepository.save(searchHistory);
        } else {
            throw new Exception("seq값이 존재하지 않는 멤버입니다.");
        }
    }
}
