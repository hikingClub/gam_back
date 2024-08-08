package com.gam.hikingclub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                                .requestMatchers("/detail/**").permitAll() // /detail 경로도 인증 없이 접근 가능
                                .anyRequest().authenticated() // 나머지 경로는 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/loginSuccess") // OAuth2 로그인 성공 후 리디렉션될 URL
                        .failureUrl("/loginFailure") // 로그인 실패 시 리디렉션될 URL
                )
                .formLogin(form -> form.disable()); // 폼 로그인 비활성화 (추후 JWT, OAuth2 토큰 기반인증으로 전환 예정)
        return http.build();
    }
}
