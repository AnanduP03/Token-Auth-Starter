package com.example.backend.Dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String username;
    private String accessToken;
    private String refreshToken;
    private String email;
}
