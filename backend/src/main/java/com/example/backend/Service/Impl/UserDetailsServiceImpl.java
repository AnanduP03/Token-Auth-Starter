package com.example.backend.Service.Impl;

import com.example.backend.Model.persistent.User;
import com.example.backend.Repository.persistent.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        if(userRepository.existsByUsername(username)){
            User user=userRepository.findByUsername(username);

            Set<GrantedAuthority> authorities=new HashSet<>();
            user.setAuthorities(authorities);
            return user;
        }else{
            throw new UsernameNotFoundException("User not found with username: "+username);
        }
    }
}
