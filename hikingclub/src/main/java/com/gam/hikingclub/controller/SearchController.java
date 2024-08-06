package com.gam.hikingclub.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gam.hikingclub.entity.Member;
import com.gam.hikingclub.entity.SearchHistory;
import com.gam.hikingclub.service.MyPageService;
import com.gam.hikingclub.service.SearchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;
    @Autowired
    private MyPageService myPageService;


    @GetMapping("/keyword")
    public ResponseEntity<JsonNode> searchData(
            @RequestParam(value = "searchKeyword") String searchKeyword,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "pageNum", required = false) String pageNum,
            @RequestParam(value = "pagePer", required = false) String pagePer,
            HttpSession session) {
        try {
            // 기본값 설정
            startDate = (startDate == null || startDate.isEmpty()) ?
                    LocalDate.now().minusYears(2).toString() : startDate;
            endDate = (endDate == null || endDate.isEmpty()) ?
                    LocalDate.now().toString() : endDate;
            pageNum = (pageNum == null || pageNum.isEmpty()) ?
                    "1" : pageNum;
            pagePer = (pagePer == null || pagePer.isEmpty()) ?
                    "20" : pagePer;

            // 검색 수행
            JsonNode result = searchService.searchData(
                    searchKeyword,
                    startDate,
                    endDate,
                    pageNum,
                    pagePer
            );

            // 로그인된 사용자 확인
            if (session != null) {
                // Integer memberSeq = (Integer) session.getAttribute("memberSeq");
                Integer memberSeq = 4;
                if (memberSeq != null) {
                    System.out.println("memberSeq null아닐때");
                    // SearchHistory 객체 생성 및 설정
                    SearchHistory searchHistory = new SearchHistory();
                    searchHistory.setSeq(memberSeq);
                    searchHistory.setKeyword(searchKeyword);
                    searchHistory.setKeywordTime(LocalDateTime.now());

                    // SearchHistory 저장
                    searchService.setUserSearchHistory(searchHistory);
                }
            }
            System.out.println("그냥 검색 끝");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 에러 발생 시 JSON 형식으로 응답
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode errorResponse = objectMapper.createObjectNode()
                    .put("message", "에러 사유: " + e.getMessage())
                    .put("totalCount", 0);

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
