package com.gam.hikingclub.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gam.hikingclub.entity.SearchHistory;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/detail")
public class DetailController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/")
    public ResponseEntity<JsonNode> getDetailData(
            @RequestParam(value = "title") String searchKeyword,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "pageNum", required = false) String pageNum,
            @RequestParam(value = "pagePer", required = false) String pagePer,
            @RequestParam(value = "docId") String docId,
            HttpSession session) {
        try {
            String startDate;
            String endDate;
            // 기본값 설정
            if (date == null || date.isEmpty()) {
                LocalDate now = LocalDate.now();
                startDate = "2010-01-01"; // 디지털 집현전의 시작 날짜
                endDate = now.toString();
            } else {
                // date 형식에 따라 startDate와 endDate 설정
                try {
                    LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    startDate = parsedDate.toString();
                    endDate = parsedDate.toString();
                } catch (DateTimeParseException e1) {
                    try {
                        LocalDate parsedDate = LocalDate.parse(date + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        startDate = parsedDate.withDayOfMonth(1).toString();
                        endDate = parsedDate.withDayOfMonth(parsedDate.lengthOfMonth()).toString();
                    } catch (DateTimeParseException e2) {
                        try {
                            LocalDate parsedDate = LocalDate.parse(date + "-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            startDate = parsedDate.withDayOfYear(1).toString();
                            endDate = parsedDate.withDayOfYear(parsedDate.lengthOfYear()).toString();
                        } catch (DateTimeParseException e3) {
                            throw new IllegalArgumentException("Invalid date format: " + date);
                        }
                    }
                }
            }
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
                Integer memberSeq = (Integer) session.getAttribute("memberSeq");
                if (memberSeq != null) {
                    // SearchHistory 객체 생성 및 설정
                    SearchHistory searchHistory = new SearchHistory();
                    searchHistory.setSeq(memberSeq);
                    searchHistory.setKeyword(searchKeyword);
                    searchHistory.setKeywordTime(LocalDateTime.now());

                    // SearchHistory 저장
                    searchService.setUserSearchHistory(searchHistory);
                }
            }

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
