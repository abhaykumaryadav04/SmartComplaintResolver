package com.smartcomplaint.smartcompaint.analytics;



import java.util.Map;

import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;
import com.smartcomplaint.smartcompaint.enums.ComplaintStatus;

public record AnalyticsResponse(
        long totalComplaints,
        long openComplaints,
        long resolvedComplaints,
        Map<ComplaintStatus, Long> byStatus,
        Map<ComplaintCategory, Long> byCategory,
        Map<String, Long> byDepartment
) {
}
