package com.example.backend.Service.Impl;

import com.example.backend.Model.inmemory.JwtToken;
import com.example.backend.Repository.inmemory.JwtTokenRepository;
import com.example.backend.Service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    @Autowired
    JwtTokenRepository jwtTokenRepository;

    @Value("${inmemory.max.entry}")
    private Long maxEntry;

    @Override
    public boolean save(JwtToken jwtToken){
        try{
            if(jwtTokenRepository.count()>=maxEntry){
                jwtTokenRepository.deleteAll();
            }
            jwtTokenRepository.save(jwtToken);
            return true;
        }catch (Exception e){
            System.out.println("loc: JwtTokenServiceImpl; fn: save; "+e.getMessage());
            return false;
        }
    }


}
