package com.gam.hikingclub.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;


@Service
public class DetailService {

    private JsonNode findDocById(JsonNode searchResults, String docId) {
        if (searchResults.has("documents")) {
            Iterator<JsonNode> elements = searchResults.get("documents").elements();
            while (elements.hasNext()) {
                JsonNode doc = elements.next();
                if (doc.has("doc_id") && doc.get("doc_id").asText().equals(docId)) {
                    return doc;
                }
            }
        }
        return null; // doc_id를 찾지 못한 경우
    }
}
