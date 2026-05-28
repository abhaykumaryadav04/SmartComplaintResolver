package com.smartcomplaint.smartcompaint.service;


import org.springframework.stereotype.Service;

import com.smartcomplaint.smartcompaint.analytics.AnalyticsResponse;
import com.smartcomplaint.smartcompaint.entity.Complaint;
import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;
import com.smartcomplaint.smartcompaint.enums.ComplaintStatus;
import com.smartcomplaint.smartcompaint.repository.ComplaintRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final ComplaintRepository complaintRepository;

    public AnalyticsService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public AnalyticsResponse dashboard() {
        long total = complaintRepository.count();
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED)
                + complaintRepository.countByStatus(ComplaintStatus.CLOSED);
        Map<ComplaintStatus, Long> byStatus = Arrays.stream(ComplaintStatus.values())
                .collect(Collectors.toMap(status -> status, complaintRepository::countByStatus));
        Map<ComplaintCategory, Long> byCategory = Arrays.stream(ComplaintCategory.values())
                .collect(Collectors.toMap(category -> category, complaintRepository::countByCategory));
        Map<String, Long> byDepartment = complaintRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Complaint::getDepartment, Collectors.counting()));
        return new AnalyticsResponse(total, total - resolved, resolved, byStatus, byCategory, byDepartment);
    }
}
