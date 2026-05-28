package com.smartcomplaint.smartcompaint.notification;

import java.time.Instant;
import java.util.UUID;

public record WebSocketEvent(
        String event,
        UUID complaintId,
        String message,
        Instant timestamp
) {
}
