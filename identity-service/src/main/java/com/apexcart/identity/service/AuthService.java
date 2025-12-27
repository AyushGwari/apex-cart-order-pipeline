package com.apexcart.identity.service;

import com.apexcart.identity.entity.UserCredential;
import com.apexcart.identity.exception.UserAlreadyExistsException;
import com.apexcart.identity.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String saveUser(UserCredential userCredential){
        if (repository.findByUsername(userCredential.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username '" + userCredential.getUsername() + "' is already taken.");
        }
        if (repository.findByEmail(userCredential.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email '" + userCredential.getEmail() + "' is already registered.");
        }
        userCredential.setPassword(passwordEncoder.encode(userCredential.getPassword()));
        repository.save(userCredential);
        return "User Generated Successfully";
    }
    public String generateToken(String username){
        return jwtService.generateToken(username);
    }
}
