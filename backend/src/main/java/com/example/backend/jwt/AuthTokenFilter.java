package com.example.backend.jwt;

import com.example.backend.Repository.inmemory.JwtTokenRepository;
import com.example.backend.Service.Impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    private String parseJwt(HttpServletRequest request){
        String headerAuth=request.getHeader("Authorization");

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }

        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String jwt=parseJwt(request);
            if(jwtTokenRepository.existsByToken(jwt)){
                if(jwt!=null && jwtUtils.validateToken(jwt)){
                    String username=jwtUtils.getUsernameFromJwtToken(jwt);

                    UserDetails userDetails=userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }else {
                    System.out.println("Token not valid");
                }
            }
        }catch (Exception e){
            System.out.println("Cannot set user authentication: "+e.getMessage());
        }

        filterChain.doFilter(request,response);
    }


}
