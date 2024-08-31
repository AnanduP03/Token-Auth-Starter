package com.example.backend.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequestDto {
    private String refreshToken;
}
