package com.example.backend.Service;

import com.example.backend.Model.persistent.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface RefreshTokenService {
    public Optional<RefreshToken> findByToken(String token);
    public RefreshToken createRefreshToken(long userId);
    public RefreshToken verifyExpiration(RefreshToken token) throws Exception;
    public void deleteByUsername(String username);
}
