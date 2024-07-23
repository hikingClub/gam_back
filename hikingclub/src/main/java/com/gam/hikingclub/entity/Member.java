package com.gam.hikingclub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    private String nickname;
    private String id;
    private String email;
    private String password;
    private String interest;
    private String ageRange;
    private String jobRange;
    private Integer alarmCheck;
    private String interestKeyword;

    
}