package com.smartcomplaint.smartcompaint.complaint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank @Size(min = 2, max = 2000) String message
) {
}
