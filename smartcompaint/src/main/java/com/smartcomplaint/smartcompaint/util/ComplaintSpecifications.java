package com.smartcomplaint.smartcompaint.util;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.smartcomplaint.smartcompaint.entity.Complaint;
import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;
import com.smartcomplaint.smartcompaint.enums.ComplaintStatus;

public final class ComplaintSpecifications {

    private ComplaintSpecifications() {
    }

    public static Specification<Complaint> search(String keyword, ComplaintStatus status, ComplaintCategory category, String department) {
        return Specification.where(keyword(keyword))
                .and(status(status))
                .and(category(category))
                .and(department(department));
    }

    private static Specification<Complaint> keyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) {
                return cb.conjunction();
            }
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like),
                    cb.like(cb.lower(root.get("location")), like)
            );
        };
    }

    private static Specification<Complaint> status(ComplaintStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    private static Specification<Complaint> category(ComplaintCategory category) {
        return (root, query, cb) -> category == null ? cb.conjunction() : cb.equal(root.get("category"), category);
    }

    private static Specification<Complaint> department(String department) {
        return (root, query, cb) -> StringUtils.hasText(department) ? cb.equal(root.get("department"), department) : cb.conjunction();
    }
}
