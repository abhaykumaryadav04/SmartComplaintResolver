package com.smartcomplaint.smartcompaint.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.smartcomplaint.smartcompaint.entity.Complaint;
import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;
import com.smartcomplaint.smartcompaint.enums.ComplaintStatus;

import java.util.List;
import java.util.UUID;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID>, JpaSpecificationExecutor<Complaint> {
    long countByStatus(ComplaintStatus status);

    long countByCategory(ComplaintCategory category);

    long countByDepartment(String department);

    List<Complaint> findTop20ByDepartmentOrderByCreatedAtDesc(String department);
}
