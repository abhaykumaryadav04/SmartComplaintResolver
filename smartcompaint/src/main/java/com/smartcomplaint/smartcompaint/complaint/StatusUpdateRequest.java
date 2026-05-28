package com.smartcomplaint.smartcompaint.complaint;

import com.smartcomplaint.smartcompaint.enums.ComplaintStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusUpdateRequest(
        @NotNull ComplaintStatus status,
        @Size(max = 1000) String note
) {
}
