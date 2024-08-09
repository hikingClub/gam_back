package com.gam.hikingclub.dto;

import com.gam.hikingclub.entity.Member;
import lombok.Data;

@Data
public class LoginResponseDto {
    public boolean loginSuccess;
    public Member member;
}
