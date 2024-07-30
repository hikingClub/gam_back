package com.gam.hikingclub.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gam.hikingclub.dto.SearchResultDTO;
import com.gam.hikingclub.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/keyword")
    public ResponseEntity<JsonNode> searchData(@RequestBody SearchResultDTO requestData) {
        try {
            // 기본값 설정
            String startDate = (requestData.getStartDate() == null || requestData.getStartDate().isEmpty()) ?
                    LocalDate.now().minusYears(2).toString() : requestData.getStartDate();
            String endDate = (requestData.getEndDate() == null || requestData.getEndDate().isEmpty()) ?
                    LocalDate.now().toString() : requestData.getEndDate();
            String pageNum = (requestData.getPageNum() == null || requestData.getPageNum().isEmpty()) ?
                    "1" : requestData.getPageNum();
            String pagePer = (requestData.getPagePer() == null || requestData.getPagePer().isEmpty()) ?
                    "20" : requestData.getPagePer();

            JsonNode result = searchService.searchData(
                    requestData.getSearchKeyword(),
                    startDate,
                    endDate,
                    pageNum,
                    pagePer
            );

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
