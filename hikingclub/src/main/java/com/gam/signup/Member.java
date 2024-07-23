package com.gam.signup;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "MEMBER")
public class Member {

    //이 Id 애노테이션 밑에 있는 필드가 기본 키임
    @Id
    //자동으로 기존의 seq값보다 1 높은 값으로 넣게 해주는 코드
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seq;

    private int recIndex;
    private String nickname;
    private String id;
    private String email;
    private String password;
    private String interest;
    private String ageRange;
    private String jobRange;
    private int alarmCheck;
    private String interestKeyword;



}
