package com.example.URLShortner.service;

import com.example.URLShortner.dto.request.LoginRequest;
import com.example.URLShortner.dto.request.RegisterRequest;
import com.example.URLShortner.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
