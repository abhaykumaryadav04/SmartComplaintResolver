package com.smartcomplaint.smartcompaint.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartcomplaint.smartcompaint.entity.AppUser;
import com.smartcomplaint.smartcompaint.entity.Complaint;
import com.smartcomplaint.smartcompaint.entity.Notification;
import com.smartcomplaint.smartcompaint.enums.NotificationType;
import com.smartcomplaint.smartcompaint.notification.NotificationResponse;
import com.smartcomplaint.smartcompaint.repository.NotificationRepository;
import com.smartcomplaint.smartcompaint.util.DtoMapper;
import com.smartcomplaint.smartcompaint.websocket.WebSocketPublisher;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DtoMapper mapper;
    private final WebSocketPublisher webSocketPublisher;

    public NotificationService(NotificationRepository notificationRepository, DtoMapper mapper, WebSocketPublisher webSocketPublisher) {
        this.notificationRepository = notificationRepository;
        this.mapper = mapper;
        this.webSocketPublisher = webSocketPublisher;
    }

    @Transactional
    public Notification notify(AppUser recipient, Complaint complaint, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setComplaint(complaint);
        notification.setType(type);
        notification.setMessage(message);
        Notification saved = notificationRepository.save(notification);
        webSocketPublisher.userNotification(recipient.getId(), type.name(), complaint == null ? null : complaint.getId(), message);
        return saved;
    }

    public List<NotificationResponse> forUser(AppUser user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(mapper::toNotificationResponse)
                .toList();
    }
}
