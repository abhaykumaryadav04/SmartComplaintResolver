package com.smartcomplaint.smartcompaint.util;


import org.springframework.stereotype.Component;

import com.smartcomplaint.smartcompaint.auth.UserResponse;
import com.smartcomplaint.smartcompaint.complaint.CommentResponse;
import com.smartcomplaint.smartcompaint.complaint.ComplaintResponse;
import com.smartcomplaint.smartcompaint.complaint.HistoryResponse;
import com.smartcomplaint.smartcompaint.complaint.UserSummary;
import com.smartcomplaint.smartcompaint.entity.AppUser;
import com.smartcomplaint.smartcompaint.entity.Complaint;
import com.smartcomplaint.smartcompaint.entity.ComplaintComment;
import com.smartcomplaint.smartcompaint.entity.ComplaintHistory;
import com.smartcomplaint.smartcompaint.entity.Notification;
import com.smartcomplaint.smartcompaint.notification.NotificationResponse;

import java.util.List;

@Component
public class DtoMapper {

    public UserResponse toUserResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getRole(), user.getCreatedAt());
    }

    public UserSummary toUserSummary(AppUser user) {
        if (user == null) {
            return null;
        }
        return new UserSummary(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }

    public ComplaintResponse toComplaintResponse(
            Complaint complaint,
            List<ComplaintComment> comments,
            List<ComplaintHistory> history
    ) {
        return new ComplaintResponse(
                complaint.getId(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getCategory(),
                complaint.getPriority(),
                complaint.getStatus(),
                complaint.getLocation(),
                complaint.getDepartment(),
                complaint.getImageUrl(),
                toUserSummary(complaint.getCreatedBy()),
                toUserSummary(complaint.getAssignedTo()),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt(),
                comments.stream().map(this::toCommentResponse).toList(),
                history.stream().map(this::toHistoryResponse).toList()
        );
    }

    public CommentResponse toCommentResponse(ComplaintComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getMessage(),
                toUserSummary(comment.getAuthor()),
                comment.getCreatedAt()
        );
    }

    public HistoryResponse toHistoryResponse(ComplaintHistory history) {
        return new HistoryResponse(
                history.getId(),
                history.getFromStatus(),
                history.getToStatus(),
                history.getNote(),
                toUserSummary(history.getChangedBy()),
                history.getCreatedAt()
        );
    }

    public NotificationResponse toNotificationResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getComplaint() == null ? null : notification.getComplaint().getId(),
                notification.getType(),
                notification.getMessage(),
                notification.isReadFlag(),
                notification.getCreatedAt()
        );
    }
}
