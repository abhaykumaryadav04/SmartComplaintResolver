package com.smartcomplaint.smartcompaint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcomplaint.smartcompaint.entity.Complaint;
import com.smartcomplaint.smartcompaint.entity.ComplaintHistory;

import java.util.List;
import java.util.UUID;

public interface ComplaintHistoryRepository extends JpaRepository<ComplaintHistory, UUID> {
    List<ComplaintHistory> findByComplaintOrderByCreatedAtAsc(Complaint complaint);
}
