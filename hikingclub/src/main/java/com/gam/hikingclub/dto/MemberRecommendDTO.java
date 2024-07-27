package com.gam.hikingclub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberRecommendDTO {
    private String interest;
    private String jobRange;
    private String ageRange;
}
