package com.smartcomplaint.smartcompaint.complaint;



import java.time.Instant;
import java.util.UUID;

import com.smartcomplaint.smartcompaint.enums.ComplaintStatus;

public record HistoryResponse(
        UUID id,
        ComplaintStatus fromStatus,
        ComplaintStatus toStatus,
        String note,
        UserSummary changedBy,
        Instant createdAt
) {
}
