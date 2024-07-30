package com.gam.hikingclub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchResultDTO {
    private String searchKeyword;
    private String startDate;
    private String endDate;
    private String pageNum;
    private String pagePer;
}
