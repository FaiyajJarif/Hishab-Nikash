package com.Eqinox.store.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
}
