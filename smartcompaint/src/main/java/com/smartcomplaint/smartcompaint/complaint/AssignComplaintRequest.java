package com.smartcomplaint.smartcompaint.complaint;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AssignComplaintRequest(
        @NotBlank String department,
        UUID staffId
) {
}
