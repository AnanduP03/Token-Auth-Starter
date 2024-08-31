package com.example.backend.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/test")
    public ResponseEntity<?> authTest(){
        System.out.println("Auth test successfull");
        Map<String,String> res=new HashMap<>();
        res.put("message","Auth test successful");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
