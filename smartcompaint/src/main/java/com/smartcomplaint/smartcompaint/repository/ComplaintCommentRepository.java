package com.smartcomplaint.smartcompaint.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcomplaint.smartcompaint.entity.Complaint;
import com.smartcomplaint.smartcompaint.entity.ComplaintComment;

import java.util.List;
import java.util.UUID;

public interface ComplaintCommentRepository extends JpaRepository<ComplaintComment, UUID> {
    List<ComplaintComment> findByComplaintOrderByCreatedAtAsc(Complaint complaint);
}
