package com.Eqinox.store.services;

import com.Eqinox.store.websocket.NotificationMessage;
import com.Eqinox.store.websocket.NotificationPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class OverspendAlertService {

    private final AnalyticsService analyticsService;
    private final NotificationPublisher publisher;

    public OverspendAlertService(
            AnalyticsService analyticsService,
            NotificationPublisher publisher) {
        this.analyticsService = analyticsService;
        this.publisher = publisher;
    }

    public void checkDailyOverspend(Integer userId, LocalDate date) {

        var alert = analyticsService.getDailyOverspendAlert(userId, date);

        if (!alert.isOverspent()) return;

        NotificationMessage msg = new NotificationMessage(
                "DAILY_OVERSPEND",
                date,
                alert.getDifference(),
                "You overspent today by " + alert.getDifference()
        );

        publisher.notifyUser(userId, msg);
    }
}
