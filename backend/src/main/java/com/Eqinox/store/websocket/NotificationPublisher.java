package com.Eqinox.store.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationPublisher.class);

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyUser(Integer userId, NotificationMessage message) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                message
        );

        log.info("📢 WS sent to user {} -> {}", userId, message);
    }
}
