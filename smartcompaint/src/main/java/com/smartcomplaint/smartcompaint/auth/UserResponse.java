package com.smartcomplaint.smartcompaint.auth;


import java.time.Instant;
import java.util.UUID;

import com.smartcomplaint.smartcompaint.enums.Role;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        Role role,
        Instant createdAt
) {
}
