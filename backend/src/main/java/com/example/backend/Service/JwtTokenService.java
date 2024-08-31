package com.example.backend.Service;

import com.example.backend.Model.inmemory.JwtToken;
import org.springframework.stereotype.Service;

@Service
public interface JwtTokenService {
    boolean save(JwtToken jwtToken);

}
