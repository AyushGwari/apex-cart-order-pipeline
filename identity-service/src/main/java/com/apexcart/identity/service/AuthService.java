package com.apexcart.identity.service;

import com.apexcart.identity.entity.Role;
import com.apexcart.identity.entity.UserCredential;
import com.apexcart.identity.exception.UserAlreadyExistsException;
import com.apexcart.identity.repository.RoleRepository;
import com.apexcart.identity.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    @Transactional
    public String saveUser(UserCredential userCredential){
        if (repository.findByUsername(userCredential.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username '" + userCredential.getUsername() + "' is already taken.");
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Default Role not found."));
        if (repository.findByEmail(userCredential.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email '" + userCredential.getEmail() + "' is already registered.");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        userCredential.setRoles(roles);
//        userCredential.setRoles(Collections.singleton(userRole)); // Set default role
        userCredential.setPassword(passwordEncoder.encode(userCredential.getPassword()));
        repository.save(userCredential);
        return "User Generated Successfully";
    }
    public String generateToken(String username){
        UserCredential user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Pass the Set<Role> to JWT Service to be converted into Claims
        return jwtService.generateToken(username, user.getRoles());    }
}
