package com.smartcomplaint.smartcompaint.websocket;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.smartcomplaint.smartcompaint.notification.WebSocketEvent;

import java.time.Instant;
import java.util.UUID;

@Component
public class WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void complaintEvent(UUID complaintId, String event, String message) {
        WebSocketEvent payload = new WebSocketEvent(event, complaintId, message, Instant.now());
        messagingTemplate.convertAndSend("/topic/complaints/" + complaintId, payload);
        messagingTemplate.convertAndSend("/topic/complaints", payload);
    }

    public void userNotification(UUID userId, String event, UUID complaintId, String message) {
        WebSocketEvent payload = new WebSocketEvent(event, complaintId, message, Instant.now());
        messagingTemplate.convertAndSend("/topic/users/" + userId + "/notifications", payload);
    }
}
