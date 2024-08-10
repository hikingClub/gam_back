package com.gam.hikingclub.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gam.hikingclub.entity.*;
import com.gam.hikingclub.service.DetailService;
import com.gam.hikingclub.service.MyPageService;
import com.gam.hikingclub.service.SearchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@RestController
@RequestMapping("/detail")
public class DetailController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private DetailService detailService;

    @Autowired
    private MyPageService myPageService;


    @GetMapping("")
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
                startDate = "20100101"; // 디지털 집현전의 시작 날짜
                endDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 오늘 날짜를 YYYYMMDD 형식으로
            } else {
                // date 형식에 따라 startDate와 endDate 설정
                try {
                    if (date.length() == 8) { // YYYYMMDD 형식
                        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
                        startDate = parsedDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                        endDate = startDate; // 동일한 날짜로 설정
                    } else if (date.length() == 6) { // YYYYMM 형식
                        LocalDate parsedDate = LocalDate.parse(date + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
                        startDate = parsedDate.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                        endDate = parsedDate.withDayOfMonth(parsedDate.lengthOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    } else if (date.length() == 4) { // YYYY 형식
                        LocalDate parsedDate = LocalDate.parse(date + "0101", DateTimeFormatter.ofPattern("yyyyMMdd"));
                        startDate = parsedDate.withDayOfYear(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                        endDate = parsedDate.withDayOfYear(parsedDate.lengthOfYear()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                    } else {
                        throw new IllegalArgumentException("Invalid date format: " + date);
                    }
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid date format: " + date);
                }
            }

            pageNum = (pageNum == null || pageNum.isEmpty()) ? "1" : pageNum;
            pagePer = (pagePer == null || pagePer.isEmpty()) ? "20" : pagePer;

            // 검색 수행
            JsonNode searchResults = searchService.searchData(
                    searchKeyword,
                    startDate,
                    endDate,
                    pageNum,
                    pagePer
            );

            // Document 정보 저장 및 업데이트
            Document document = new Document();
            document.setTitle(searchKeyword);
            document.setDocId(docId);
            document.setDate(date);
            detailService.setDocument(document);

            // Document 정보 조회
            Optional<Document> docOptional = detailService.getDocumentById(docId);
            ObjectMapper objectMapper = new ObjectMapper();

            // responseNode의 타입을 ObjectNode로 변경
            ObjectNode responseNode = objectMapper.createObjectNode();

            if (searchResults.has("totalCount")) {
                int totalCount = searchResults.path("totalCount").asInt();
                System.out.println("totalCount: " + totalCount);

                if (totalCount > 0) {
                    JsonNode selectedResult;

                    if (totalCount > 1) {
                        // totalCount가 2 이상일 때, docId와 일치하는 문서만 선택
                        selectedResult = searchService.findDocById(searchResults, docId);
                        if (selectedResult == null) {
                            // 일치하는 문서가 없을 경우 기본 메시지
                            JsonNode errorResponse = objectMapper.createObjectNode()
                                    .put("message", "Document with specified docId not found")
                                    .put("totalCount", totalCount);
                            return ResponseEntity.status(404).body(errorResponse);
                        }
                    } else {
                        // totalCount가 1일 때, 첫 번째 결과 선택
                        JsonNode resultArray = searchResults.path("result");
                        selectedResult = resultArray.isArray() && resultArray.size() > 0
                                ? resultArray.get(0)
                                : objectMapper.createObjectNode();
                    }

                    // 결과에 selectedResult 추가
                    responseNode.setAll((ObjectNode) selectedResult);

                    // Document 정보 추가
                    if (docOptional.isPresent()) {
                        Document doc = docOptional.get();
                        responseNode.put("empathy", doc.getEmpathy())
                                .put("views", doc.getViews());
                    } else {
                        responseNode.put("empathy", 0)
                                .put("views", 0);
                    }

                    // totalCount 추가
                    responseNode.put("totalCount", totalCount);
                } else {
                    JsonNode errorResponse = objectMapper.createObjectNode()
                            .put("message", "No documents found")
                            .put("totalCount", 0);
                    return ResponseEntity.status(404).body(errorResponse);
                }
            } else {
                JsonNode errorResponse = objectMapper.createObjectNode()
                        .put("message", "Invalid search results format")
                        .put("totalCount", 0);
                return ResponseEntity.status(500).body(errorResponse);
            }

            // 로그인된 사용자 확인
            if (session != null) {
                Integer memberSeq = (Integer) session.getAttribute("memberSeq");
                if (memberSeq != null) {
                    // SearchHistory 객체 생성 및 설정
                    ViewHistory viewHistory = new ViewHistory();
                    viewHistory.setSeq(memberSeq);
                    viewHistory.setViewTitle(searchKeyword);
                    viewHistory.setViewTime(LocalDateTime.now());
                    viewHistory.setDate(date);
                    viewHistory.setDocId(docId);
                    // SearchHistory 저장
                    detailService.setUserViewHistory(viewHistory);
                }
            }
            // 결과 반환
            return ResponseEntity.ok(responseNode);

        } catch (Exception e) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode errorResponse = objectMapper.createObjectNode()
                    .put("message", "Error occurred: " + e.getMessage())
                    .put("totalCount", 0);

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    //공감 추가하는 부분
    @PostMapping("/setEmpathy")
    public ResponseEntity<String> setEmpathy(HttpSession session, @RequestBody Empathy empathy) {
        try {
            Integer memberSeq = (Integer) session.getAttribute("memberSeq");
            empathy.setSeq(memberSeq);
            myPageService.setEmpathy(empathy);
            return ResponseEntity.ok("성공!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("에러 이유" + e.getMessage());
        }
    }

    //공감 추가하는 부분
    @PostMapping("/deleteEmpathy")
    public ResponseEntity<String> deleteEmpathy(HttpSession session, @RequestBody Empathy empathy) {
        Integer memberSeq = (Integer) session.getAttribute("memberSeq");
        if (memberSeq == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        try {
            myPageService.deleteUserEmpathy(empathy.getIdx());
            return ResponseEntity.ok("공감 내역이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("에러 이유" + e.getMessage());
        }
    }
}
