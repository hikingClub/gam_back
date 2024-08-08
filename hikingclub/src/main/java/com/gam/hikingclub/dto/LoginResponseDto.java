package com.gam.hikingclub.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private boolean loginSuccess;
    private String message;
    private boolean isNewUser;
}
