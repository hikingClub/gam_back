package com.gam.hikingclub;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getJobRange() {
        return jobRange;
    }

    public void setJobRange(String jobRange) {
        this.jobRange = jobRange;
    }

    public Integer getAlarmCheck() {
        return alarmCheck;
    }

    public void setAlarmCheck(Integer alarmCheck) {
        this.alarmCheck = alarmCheck;
    }

    public String getInterestKeyword() {
        return interestKeyword;
    }

    public void setInterestKeyword(String interestKeyword) {
        this.interestKeyword = interestKeyword;
    }
}