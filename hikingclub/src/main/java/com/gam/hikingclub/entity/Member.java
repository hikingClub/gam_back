package com.gam.hikingclub.entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 생성 전략 추가
    private Integer seq;
    private String nickname;
    private String uid;
    private String email;
    private String password;
    private String interest;
    private String ageRange;
    private String jobRange;
    private Integer alarmCheck;
    private String interestKeyword;
}