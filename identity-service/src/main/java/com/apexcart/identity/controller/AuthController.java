package com.apexcart.identity.controller;

import com.apexcart.identity.dto.AuthRequest;
import com.apexcart.identity.dto.UserRequest;
import com.apexcart.identity.entity.UserCredential;
import com.apexcart.identity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserRequest user){
        UserCredential usercred = UserCredential.builder().username(user.getUsername()).email(user.getEmail()).password(user.getPassword()).build();
        return authService.saveUser(usercred);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()));
        if(authenticate.isAuthenticated()){
            return authService.generateToken(authRequest.getUsername());
        }
        else{
            throw new RuntimeException("Invalid Access - check credentials");
        }
    }
}
