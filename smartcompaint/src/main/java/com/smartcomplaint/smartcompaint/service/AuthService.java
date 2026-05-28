package com.smartcomplaint.smartcompaint.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartcomplaint.smartcompaint.auth.AuthResponse;
import com.smartcomplaint.smartcompaint.auth.LoginRequest;
import com.smartcomplaint.smartcompaint.auth.RegisterRequest;
import com.smartcomplaint.smartcompaint.auth.UserResponse;
import com.smartcomplaint.smartcompaint.entity.AppUser;
import com.smartcomplaint.smartcompaint.enums.Role;
import com.smartcomplaint.smartcompaint.exception.BadRequestException;
import com.smartcomplaint.smartcompaint.repository.UserRepository;
import com.smartcomplaint.smartcompaint.security.JwtService;
import com.smartcomplaint.smartcompaint.util.DtoMapper;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final DtoMapper mapper;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            DtoMapper mapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.mapper = mapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already registered");
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName());
        user.setEmail(request.email().toLowerCase());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));

        boolean firstUser = userRepository.count() == 0;
        user.setRole(firstUser && request.role() != null ? request.role() : Role.USER);

        AppUser saved = userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(saved), "Bearer", mapper.toUserResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
        );
        AppUser user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        return new AuthResponse(jwtService.generateToken(user), "Bearer", mapper.toUserResponse(user));
    }

    public UserResponse me(AppUser user) {
        return mapper.toUserResponse(user);
    }
}
