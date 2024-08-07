package com.gam.hikingclub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gam.hikingclub.security.OAuth2SuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(@Lazy OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (개발 환경에서만)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/member/**").permitAll() // /member 경로는 인증 없이 접근 가능
                                .requestMatchers("/mypage/**").permitAll() // /mypage 경로도 인증 없이 접근 가능
                                .requestMatchers("/search/**").permitAll() // /search 경로도 인증 없이 접근 가능
                                .anyRequest().authenticated() // 나머지 경로는 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler) // 로그인 성공 시 OAuth2SuccessHandler 실행
                        .failureHandler(new AuthenticationFailureHandler() {
                            @Override
                            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                // JSON 응답 생성
                                Map<String, Object> responseData = new HashMap<>();
                                responseData.put("message", "login_failure");
                                responseData.put("error", exception.getMessage());

                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));
                            }
                        }) // 로그인 실패 시 JSON 응답
                );
        return http.build();
    }
}
