package com.Eqinox.store.services;

import com.Eqinox.store.dtos.alerts.OverspendAlertDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    private final SimpMessagingTemplate messaging;

    public AlertService(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    public void sendToUser(Integer userId, OverspendAlertDto alert) {
        // frontend subscribes to: /topic/alerts/{userId}
        messaging.convertAndSend("/topic/alerts/" + userId, alert);
    }
}
