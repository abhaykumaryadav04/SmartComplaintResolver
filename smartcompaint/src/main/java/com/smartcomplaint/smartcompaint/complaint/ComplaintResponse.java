package com.smartcomplaint.smartcompaint.complaint;



import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;
import com.smartcomplaint.smartcompaint.enums.ComplaintPriority;
import com.smartcomplaint.smartcompaint.enums.ComplaintStatus;

public record ComplaintResponse(
        UUID id,
        String title,
        String description,
        ComplaintCategory category,
        ComplaintPriority priority,
        ComplaintStatus status,
        String location,
        String department,
        String imageUrl,
        UserSummary createdBy,
        UserSummary assignedTo,
        Instant createdAt,
        Instant updatedAt,
        List<CommentResponse> comments,
        List<HistoryResponse> history
) {
}
