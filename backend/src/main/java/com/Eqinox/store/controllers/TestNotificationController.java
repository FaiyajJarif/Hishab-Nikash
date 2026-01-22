package com.Eqinox.store.controllers;

import com.Eqinox.store.dtos.BillNotificationDto;
import com.Eqinox.store.services.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/test")
public class TestNotificationController {

    private final NotificationService notificationService;

    public TestNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/notify/{userId}")
    public void testNotify(@PathVariable Integer userId) {

        notificationService.notifyUser(
                userId,
                new BillNotificationDto(
                        999,
                        "Test Bill",
                        new BigDecimal("1234"),
                        LocalDate.now(),
                        "🔥 Test notification"
                )
        );
    }
}
