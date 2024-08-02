package com.gam.hikingclub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;
    private String recIndexes;
    private String nickname;
    private String uid;
    private String email;
    private String password;
    private String interest;
    private String ageRange;
    private String jobRange;
    private Integer alarmCheck;
    private String interestKeyword;
    private boolean verified;
}
