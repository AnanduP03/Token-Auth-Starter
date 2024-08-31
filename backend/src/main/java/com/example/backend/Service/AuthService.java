package com.example.backend.Service;

import com.example.backend.Dto.JwtResponse;
import com.example.backend.Dto.TokenRefreshRequestDto;
import com.example.backend.Dto.TokenRefreshResponse;
import com.example.backend.Dto.UserDto;
import com.example.backend.Exception.EmailExistsException;
import com.example.backend.Exception.UsernameExistsException;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    JwtResponse login(UserDto userDto) throws Exception;
    boolean register(UserDto userDto) throws UsernameExistsException, EmailExistsException;
    TokenRefreshResponse refreshToken(TokenRefreshRequestDto tokenRefreshRequestDto) throws Exception;
    boolean isAuthorized() throws Exception;
}
