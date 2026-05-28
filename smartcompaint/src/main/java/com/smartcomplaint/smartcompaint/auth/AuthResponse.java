package com.smartcomplaint.smartcompaint.auth;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}
