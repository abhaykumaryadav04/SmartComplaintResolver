package com.smartcomplaint.smartcompaint.auth;

import com.smartcomplaint.smartcompaint.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 2, max = 120) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Pattern(regexp = "^[0-9+\\-\\s]{8,20}$") String phone,
        Role role
) {
}
