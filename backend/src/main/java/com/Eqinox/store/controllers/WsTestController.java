package com.Eqinox.store.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.Eqinox.store.websocket.NotificationMessage;
import com.Eqinox.store.websocket.NotificationPublisher;
import com.Eqinox.store.services.AuthUserService;

@RestController
@RequestMapping("/test")
public class WsTestController {

    private final NotificationPublisher notificationPublisher;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthUserService authUserService;

    public WsTestController(
            NotificationPublisher notificationPublisher,
            SimpMessagingTemplate simpMessagingTemplate,
            AuthUserService authUserService
    ) {
        this.notificationPublisher = notificationPublisher;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.authUserService = authUserService;
    }

    @PostMapping("/notify/{userId}")
    public void testNotify(@PathVariable Integer userId) {
        notificationPublisher.notifyUser(
                userId,
                new NotificationMessage(
                        "TEST",
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        "Hello from Postman 🚀"
                )
        );
    }

    @GetMapping("/debug/ws")
    public void testWs(@RequestHeader("Authorization") String auth) {
        Integer uid = authUserService.requireUserId(auth);

        simpMessagingTemplate.convertAndSend(
                "/topic/daily",
                Map.of(
                        "type", "DEBUG",
                        "userId", uid,
                        "time", System.currentTimeMillis()
                )
        );
    }
}
