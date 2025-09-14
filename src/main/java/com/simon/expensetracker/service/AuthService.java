package com.simon.expensetracker.service;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.simon.expensetracker.dto.request.LoginRequest;
import com.simon.expensetracker.dto.request.RegisterRequest;
import com.simon.expensetracker.dto.response.AuthResponse;
import com.simon.expensetracker.entity.User;
import com.simon.expensetracker.enums.Roles;
import com.simon.expensetracker.exception.CustomException;
import com.simon.expensetracker.repository.UserRepository;
import com.simon.expensetracker.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

// TODO: login make two calls to db potentially. Optimize later

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) throws Exception{
        if (userRepository.existsByEmail(request.getEmail())){
            throw new CustomException("Email already in use", HttpStatus.NOT_FOUND);
        }

        User user = new User();
        user.setUserName(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Roles.USER);

        
        String token = jwtTokenProvider.generateToken(new org.springframework.security.core.userdetails.User(
            user.getEmail(), 
            user.getPassword(), 
            Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString()))
            ));
            
        userRepository.save(user);
            return new AuthResponse(token, user.getUserName(), user.getRole().toString());
    }

    public AuthResponse login(LoginRequest request) throws Exception {
        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        String token = jwtTokenProvider.generateToken((UserDetails)authentication.getPrincipal());
        return new AuthResponse(token, user.getUserName(), user.getRole().toString());
    }
}
