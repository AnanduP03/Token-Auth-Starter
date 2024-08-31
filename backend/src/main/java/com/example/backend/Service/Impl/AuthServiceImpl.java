package com.example.backend.Service.Impl;

import com.example.backend.Dto.JwtResponse;
import com.example.backend.Dto.TokenRefreshRequestDto;
import com.example.backend.Dto.TokenRefreshResponse;
import com.example.backend.Dto.UserDto;
import com.example.backend.Exception.EmailExistsException;
import com.example.backend.Exception.TokenExpireException;
import com.example.backend.Exception.UsernameExistsException;
import com.example.backend.Model.inmemory.JwtToken;
import com.example.backend.Model.persistent.RefreshToken;
import com.example.backend.Model.persistent.User;
import com.example.backend.Repository.inmemory.JwtTokenRepository;
import com.example.backend.Repository.persistent.RefreshTokenRepository;
import com.example.backend.Repository.persistent.UserRepository;
import com.example.backend.Service.AuthService;
import com.example.backend.Service.JwtTokenService;
import com.example.backend.Service.RefreshTokenService;
import com.example.backend.jwt.JwtUtils;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtTokenRepository jwtTokenRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;


    public void setContext(UserDto user,UserDetails userDetails) throws Exception{
        try{
            System.out.println("Inside setContext");
            Authentication authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Context set");
        }catch (BadCredentialsException e){
            throw new BadCredentialsException(e.getMessage());
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }


    @Override
    public JwtResponse login(UserDto userDto) throws Exception {
        try{
            UserDetails userDetails=userDetailsService.loadUserByUsername(userDto.getUsername());
            if(userRepository.existsByUsername(userDto.getUsername())){
                User user=userRepository.findByUsername(userDto.getUsername());
                Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(),userDto.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt=jwtUtils.generateJwtToken(userDetails);

                JwtToken jwtToken=setJwtToken(jwt,user);
                if(jwtToken!=null){
                    if(jwtTokenService.save(jwtToken)){
                        if(refreshTokenRepository.existsByUser(user)){
                            String token=null;
                            try{
                                RefreshToken refreshToken=refreshTokenService.verifyExpiration(refreshTokenRepository.findByUser(user));
                                token=refreshToken.getToken();
                            }catch (TokenExpireException e){
                                token=refreshTokenService.createRefreshToken(user.getId()).getToken();
                            }

                            setContext(userDto,userDetails);
                            return JwtResponse
                                    .builder()
                                    .username(user.getUsername())
                                    .accessToken(jwt)
                                    .refreshToken(token)
                                    .email(user.getEmail())
                                    .build();
                        }else{
                            RefreshToken refreshToken=refreshTokenService.createRefreshToken(user.getId());

                            setContext(userDto,userDetails);

                            return JwtResponse
                                .builder()
                                .username(user.getUsername())
                                .accessToken(jwt)
                                .refreshToken(refreshToken.getToken())
                                .email(user.getEmail())
                                .build();
                        }
                    }else{
                        throw new RuntimeException("Couldn't set the token");
                    }
                }else{
                    throw new RuntimeException("Couldn't set the token");
                }
            }else {
                throw new UsernameNotFoundException("User not found with this username");
            }

        }catch (Exception e){
            System.out.println("Some error occured in AuthService; loc: AuthServiceImpl; fn: authenticate; errorMessage: "+e.getMessage());
            throw new Exception(e.getMessage());
        }

    }


    @Override
    public boolean register(UserDto userDto) throws UsernameExistsException, EmailExistsException {
        try {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                System.out.println("Username already exists: " + userDto.getUsername());
                throw new UsernameExistsException("Username already exists: " + userDto.getUsername());

            }
            if (userRepository.existsByEmail(userDto.getEmail())){
                System.out.println("Email already exists: "+userDto.getEmail());
                throw new EmailExistsException("Email already exists: "+ userDto.getEmail());
            }

            User user=new User();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            if(passwordValidate(userDto.getPassword())){
                user.setPassword(encoder.encode(userDto.getPassword()));
                userRepository.save(user);
                return true;
            }else{
                System.out.println("Password validation failed");
                return false;
            }

        }catch (Exception e){
            System.out.println("loc:AuthServiceImpl; fn:register; "+e.getMessage());
            return false;
        }
    }

    public boolean passwordValidate(String password) throws Exception {
        try {
            PasswordValidator validator = new PasswordValidator(
                    new LengthRule(8, 16),
                    new WhitespaceRule(),
                    new CharacterRule(EnglishCharacterData.UpperCase, 1),
                    new CharacterRule(EnglishCharacterData.Digit, 1),
                    new CharacterRule(EnglishCharacterData.Special, 1)
            );
            PasswordData passwordData = new PasswordData(password);
            RuleResult result = validator.validate(passwordData);
            if (result.isValid()) {
                System.out.println("Password validated.");
                return true;
            } else {
                System.out.println("Invalid Password: " + validator.getMessages(result));
                return false;
            }
        } catch (Exception e) {
            System.out.println("loc:AuthServiceImpl; fn:passwordValidate; " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


    public TokenRefreshResponse refreshToken(TokenRefreshRequestDto tokenRefreshRequestDto) throws Exception {
            String requestRefreshToken = tokenRefreshRequestDto.getRefreshToken();

                return refreshTokenService.findByToken(requestRefreshToken)
                        .map(refreshToken -> {
                            try{
                                return refreshTokenService.verifyExpiration(refreshToken);
                            }catch (TokenExpireException e){
                                // Incase if u want to create a new refresh token uncomment this
                                // Optional<RefreshToken> token=refreshTokenRepository.findByToken(requestRefreshToken);
                                // return refreshTokenService.createRefreshToken(token.get().getUser().getId());
                                throw new TokenExpireException(e.getMessage());
                            }catch (Exception e){
                                throw new RuntimeException(e.getMessage());
                            }
                        })
                        .map(RefreshToken::getUser)
                        .map(user -> {
                            String token = jwtUtils.generateJwtToken(user);
                            JwtToken jwtToken=setJwtToken(token,user);
                            if(jwtToken!=null){
                                if(jwtTokenService.save(jwtToken)) {
                                    return TokenRefreshResponse
                                            .builder()
                                            .username(user.getUsername())
                                            .accessToken(token)
                                            .refreshToken(requestRefreshToken)
                                            .build();
                                }else{
                                    throw new RuntimeException("Couldn't update the token");
                                }
                            }else{
                                throw new RuntimeException("Couldn't update the token");
                            }
                        })
                        .orElseThrow(() -> new TokenExpireException("Refresh token is not in database!"));
    }

    public JwtToken setJwtToken(String jwt,User user){
        try{
            if(jwtTokenRepository.existsByUsername(user.getUsername())){
                JwtToken jwtToken=jwtTokenRepository.findByUsername(user.getUsername());
                jwtToken.setToken(jwt);
                return jwtToken;
            }else {
                JwtToken jwtToken = JwtToken
                        .builder()
                        .token(jwt)
                        .username(user.getUsername())
                        .build();
                return jwtToken;
            }
        }catch (Exception e){
            System.out.println("loc: AuthServiceImpl; fn: setJwtToken; "+e.getMessage());
            return null;
        }
    }


    @Override
    public boolean isAuthorized() throws Exception{
        try{
            System.out.println("Inside isAuthorized");
            Authentication auth=SecurityContextHolder.getContext().getAuthentication();
            System.out.println(
                    auth!=null &&
                            !(auth instanceof AnonymousAuthenticationToken)&&
                            auth.isAuthenticated()
            );
            return(
                    auth!=null &&
                            !(auth instanceof AnonymousAuthenticationToken)&&
                            auth.isAuthenticated()
                    );
        }catch (Exception e){
            System.out.println("loc:AuthServiceImpl; fn:isAuthorized; " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }




}
