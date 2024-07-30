package com.gam.hikingclub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchResultItemDTO {
    private String date;
    private String summary;
    private String typeName;
    private List<String> mapPath;
    private String authorAffiliation;
    private String toc;
    private String title;
    private String docId;
    private String abstAlt;
    private String tocAlt;
    private String url;
    private String abst;
    private String orgnCode;
    private String titleAlt;
    private String orgnName;
    private String companyName;
    private String publisher;
    private String summaryAlt;
}
