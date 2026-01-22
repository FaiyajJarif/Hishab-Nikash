package com.Eqinox.store.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.web.bind.annotation.*;

import com.Eqinox.store.websocket.NotificationMessage;
import com.Eqinox.store.websocket.NotificationPublisher;

@RestController
@RequestMapping("/test")
public class WsTestController {

    private final NotificationPublisher notificationPublisher;

    public WsTestController(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    @PostMapping("/notify/{userId}")
    public void testNotify(@PathVariable Integer userId) {
        notificationPublisher.notifyUser(
                userId,
                new NotificationMessage(
                        "TEST",
                        LocalDate.now(),
                        BigDecimal.ZERO,
                        "Hello from Postman 🚀"));
    }

}
