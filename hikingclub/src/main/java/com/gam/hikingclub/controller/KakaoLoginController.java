package com.gam.hikingclub.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        log.info("Authorization code received: {}", code);
        // 추가 로직을 이곳에 작성할 수 있습니다.
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
