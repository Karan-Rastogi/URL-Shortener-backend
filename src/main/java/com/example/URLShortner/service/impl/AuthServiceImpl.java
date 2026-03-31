package com.example.URLShortner.service.impl;

import com.example.URLShortner.dto.request.LoginRequest;
import com.example.URLShortner.dto.request.RegisterRequest;
import com.example.URLShortner.dto.response.AuthResponse;
import com.example.URLShortner.model.User;
import com.example.URLShortner.repository.UserRepository;
import com.example.URLShortner.security.JwtUtil;
import com.example.URLShortner.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Check email not already taken
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already registered");
        }

        // 2. Build and save new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .is_Enabled(true)
                .build();

        // 3. Generate token and return response
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Registration successfull")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        //verify password
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid email or password");
        }

        // 3. Check account is active
        if(!user.getIsEnabled()){
            throw new RuntimeException("Account is disabled");
        }

        // 4. Generate token and return response
        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }

}
