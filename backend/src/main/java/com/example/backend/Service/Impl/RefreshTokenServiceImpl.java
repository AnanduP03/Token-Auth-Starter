package com.example.backend.Service.Impl;

import com.example.backend.Exception.TokenExpireException;
import com.example.backend.Model.persistent.RefreshToken;
import com.example.backend.Model.persistent.User;
import com.example.backend.Repository.persistent.RefreshTokenRepository;
import com.example.backend.Repository.persistent.UserRepository;
import com.example.backend.Service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${refreshToken.expiry}")
    private long refreshTokenExpiryMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(long userId){
        if(refreshTokenRepository.existsById(userId)){
            String username=SecurityContextHolder.getContext().getAuthentication().getName();
            User user=userRepository.findByUsername(username);
            RefreshToken refreshToken=refreshTokenRepository.findByUser(user);
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiryMs));
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }else{
            RefreshToken refreshToken=RefreshToken
                .builder()
                .user(userRepository.findById(userId).get())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiryMs))
                .token(UUID.randomUUID().toString())
                .build();
            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }
    }

    public RefreshToken verifyExpiration(RefreshToken token) throws Exception {
        try{
            if(token.getExpiryDate().compareTo(Instant.now())<0){
                refreshTokenRepository.delete(token);
                throw new TokenExpireException("Refresh Token Expired");
            }
            return token;
        }catch (TokenExpireException e){
            System.out.println("loc:RefreshTokenServiceImpl; fn:verfiyExpiration; "+e.getMessage());
            throw new TokenExpireException(e.getMessage());
        }catch (Exception e){
            System.out.println("loc:RefreshTokenServiceImpl; fn:verfiyExpiration; "+e.getMessage());
            throw new Exception(e.getMessage());
        }

    }

    @Transactional
    public void deleteByUsername(String username){
        if(userRepository.existsByUsername(username)){
            refreshTokenRepository.deleteByUser(userRepository.findByUsername(username));
        }
    }


}
