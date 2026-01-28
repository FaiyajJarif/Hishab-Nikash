package com.Eqinox.store.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.Eqinox.store.dtos.alerts.AlertEventDto;

@Slf4j
@Service
public class UserWsNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public UserWsNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // requires Spring Security websocket user principal mapping to work fully.
    // For now it's optional; you can still broadcast to family topic.
    public void notifyUserByEmail(String email, Object payload) {
        messagingTemplate.convertAndSendToUser(email, "/queue/family-invites", payload);
    }
    public void broadcastToUser(Integer userId, Object payload) {
        log.info("📤 SENDING ALERT TO USER {}", userId);
        messagingTemplate.convertAndSendToUser(
            userId.toString(),   // 🔑 MUST match Principal name
            "/queue/alerts",     // 🔑 MUST match frontend subscribe
            payload
        );
        System.out.println("📤 SENDING ALERT TO USER " + userId);
    }
}
