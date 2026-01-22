package com.Eqinox.store.services;

import com.Eqinox.store.dtos.FamilyEventDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FamilyNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public FamilyNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 🧵 Async broadcast to family topic
     * Subscribed clients: /topic/family/{familyId}
     */
    @Async("familyExecutor")
    public void broadcast(Integer familyId, FamilyEventDto payload) {
        messagingTemplate.convertAndSend("/topic/family/" + familyId, payload);
        System.out.println("👨‍👩‍👧‍👦 FAMILY WS SEND -> /topic/family/" + familyId + " | " + payload.getType());
    }
}
