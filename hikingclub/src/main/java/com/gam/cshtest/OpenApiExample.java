package com.gam.cshtest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

public class OpenApiExample {
    public static void main(String[] args) {
        String apiKey = "nn15pgmriohhrqmm4cc8nil9y7f0y23q4yz1sido747gquojm4vz2vje6iq3ei5d";
        String searchKeyword = "정연주"; // 검색 키워드
        String startDate = "20220701"; // 시작 날짜
        String endDate = "20230701"; // 종료 날짜
        String pageNum = "3"; // 요청 페이지 번호
        String pagePer = "20"; // 페이지당 목록 수

        String jsonInputString = String.format(
                "{\"searchKeyword\": \"%s\", \"startDate\": \"%s\", \"endDate\": \"%s\", \"pageNum\": \"%s\", \"pagePer\": \"%s\"}",
                searchKeyword, startDate, endDate, pageNum, pagePer
        );

        try {
            // HttpClient 생성
            HttpClient client = HttpClient.newHttpClient();

            // HttpRequest 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://metalink.k-knowledge.kr/search/openapi/search"))
                    .header("Content-Type", "application/json")
                    .header("api_key", apiKey)
                    .POST(BodyPublishers.ofString(jsonInputString))
                    .build();

            // HttpResponse 받기
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 코드 및 내용 출력
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
