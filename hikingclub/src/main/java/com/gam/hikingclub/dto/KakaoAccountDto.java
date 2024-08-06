package com.gam.hikingclub.dto;

import lombok.Data;

@Data
public class KakaoAccountDto {
    private Long id;
    private KakaoProfile profile;
    private String email;

    @Data
    public static class KakaoProfile {
        private String nickname;
        private String profile_image_url;
        private String thumbnail_image_url;
    }
}
