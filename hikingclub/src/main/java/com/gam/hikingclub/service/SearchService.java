package com.gam.hikingclub.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SearchService {

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
}
