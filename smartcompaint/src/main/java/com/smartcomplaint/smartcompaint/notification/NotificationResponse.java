package com.smartcomplaint.smartcompaint.notification;



import java.time.Instant;
import java.util.UUID;

import com.smartcomplaint.smartcompaint.enums.NotificationType;

public record NotificationResponse(
        UUID id,
        UUID complaintId,
        NotificationType type,
        String message,
        boolean read,
        Instant createdAt
) {
}
