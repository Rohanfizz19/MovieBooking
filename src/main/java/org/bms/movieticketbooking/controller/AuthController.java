package org.bms.movieticketbooking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bms.movieticketbooking.dto.request.LoginRequest;
import org.bms.movieticketbooking.dto.request.RegisterRequest;
import org.bms.movieticketbooking.dto.response.ApiResponse;
import org.bms.movieticketbooking.dto.response.AuthResponse;
import org.bms.movieticketbooking.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
