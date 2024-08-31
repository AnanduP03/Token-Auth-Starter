package com.example.backend.Controller;

import com.example.backend.Dto.JwtResponse;
import com.example.backend.Dto.TokenRefreshRequestDto;
import com.example.backend.Dto.TokenRefreshResponse;
import com.example.backend.Dto.UserDto;
import com.example.backend.Exception.EmailExistsException;
import com.example.backend.Exception.TokenExpireException;
import com.example.backend.Exception.UsernameExistsException;
import com.example.backend.Model.persistent.User;
import com.example.backend.Repository.inmemory.JwtTokenRepository;
import com.example.backend.Repository.persistent.UserRepository;
import com.example.backend.Service.AuthService;
import com.example.backend.Service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtTokenRepository jwtTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto){
        try{
            JwtResponse response=authService.login(userDto);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (UsernameNotFoundException e){
            System.out.println("Username not found: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto){
        try{
            if(authService.register(userDto)){
                return new ResponseEntity<>("User added succesfully",HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Couldn't add user",HttpStatus.BAD_REQUEST);
            }
        }catch (UsernameExistsException | EmailExistsException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequestDto tokenRefreshRequestDto){
            try{
                TokenRefreshResponse tokenRefreshResponse=authService.refreshToken(tokenRefreshRequestDto);
                return new ResponseEntity<>(tokenRefreshResponse,HttpStatus.OK);
            }catch (TokenExpireException e){
                return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
            }catch (Exception e){
                return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

    @GetMapping("/authorizationCheck")
    public ResponseEntity<?> checkAuthorization(){
        try{
            if(authService.isAuthorized()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> userLogout(@RequestHeader("Authorization") String authorizationHeader){
        try{
            String token=authorizationHeader.substring(7);
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            String username=authentication.getName();
            User user=userRepository.findByUsername(username);

            refreshTokenService.deleteByUsername(user.getUsername());
            jwtTokenRepository.deleteByToken(token);
            SecurityContextHolder.clearContext();
            System.out.println("User logout successful");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
