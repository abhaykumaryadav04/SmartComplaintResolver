package com.smartcomplaint.smartcompaint.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcomplaint.smartcompaint.entity.AppUser;
import com.smartcomplaint.smartcompaint.entity.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(AppUser recipient);
}
