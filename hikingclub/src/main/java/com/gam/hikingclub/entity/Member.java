package com.gam.hikingclub.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private String interestKeyword; // 알람 키워드 설정
    private boolean verified; // 이메일 인증 여부
    private boolean temporaryPasswordUsed; // 임시비밀번호 사용여부
}
