package com.smartcomplaint.smartcompaint.complaint;

import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;
import com.smartcomplaint.smartcompaint.enums.ComplaintPriority;

public record AiCategoryResponse(
        ComplaintCategory suggestedCategory,
        ComplaintPriority priority,
        String department,
        double confidence
) {
}
