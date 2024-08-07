package com.gam.hikingclub.controller;

import com.gam.hikingclub.dto.LoginResponseDto;
import com.gam.hikingclub.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/oauth/callback/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam("code") String code, HttpSession session) {
        LoginResponseDto responseDto = authService.kakaoLogin(code, session);
        return ResponseEntity.ok(responseDto);
    }
}
