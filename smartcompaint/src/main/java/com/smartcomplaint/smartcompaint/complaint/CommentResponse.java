package com.smartcomplaint.smartcompaint.complaint;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String message,
        UserSummary author,
        Instant createdAt
) {
}
