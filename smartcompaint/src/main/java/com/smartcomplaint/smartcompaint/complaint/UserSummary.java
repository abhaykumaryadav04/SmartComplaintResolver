package com.smartcomplaint.smartcompaint.complaint;

import java.util.UUID;

import com.smartcomplaint.smartcompaint.enums.Role;

public record UserSummary(
        UUID id,
        String fullName,
        String email,
        Role role
) {
}
