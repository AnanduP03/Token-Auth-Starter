package com.example.backend.Dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshResponse {
    private String username;
    private String accessToken;
    private String refreshToken;
}
