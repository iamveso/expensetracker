package com.simon.expensetracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simon.expensetracker.dto.request.LoginRequest;
import com.simon.expensetracker.dto.request.RegisterRequest;
import com.simon.expensetracker.dto.response.AuthResponse;
import com.simon.expensetracker.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse>register(@Valid @RequestBody RegisterRequest request) throws Exception{
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse>login(@Valid @RequestBody LoginRequest request) throws Exception {
        return ResponseEntity.ok().body(authService.login(request));
    }
}
