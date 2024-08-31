package com.example.backend.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@Component
public class JwtUtils {

    public static final SecretKey JwtSecret=Jwts.SIG.HS256.key().build();

    @Value("${jwt.expiry}")
    private long jwtExpiryMs;

    public String generateJwtToken(UserDetails userDetails){
         String username=userDetails.getUsername();
         Date currentDate=new Date();

         return Jwts.builder()
                 .subject(username)
                 .issuedAt(currentDate)
                 .expiration(new Date((currentDate).getTime()+jwtExpiryMs))
                 .signWith(JwtSecret)
                 .compact();
    }

    public String getUsernameFromJwtToken(String token){
        return Jwts
                .parser()
                .verifyWith(JwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(JwtSecret)
                    .build()
                    .parseSignedClaims(token);
            System.out.println("Token validated");
            return true;
        }catch(MalformedJwtException e){
            System.out.println("Invalid JWT token: "+e.getMessage());
        }catch (ExpiredJwtException e){
            System.out.println("JWT token has expired: "+e.getMessage());
        }catch (UnsupportedJwtException e){
            System.out.println("JWT token is unsupported: "+e.getMessage());
        }catch (IllegalArgumentException e){
            System.out.println("JWT claims string is empty: "+e.getMessage());
        }catch (Exception e){
            throw new AuthenticationCredentialsNotFoundException("JWT expired");
        }
        return false;
    }
}
