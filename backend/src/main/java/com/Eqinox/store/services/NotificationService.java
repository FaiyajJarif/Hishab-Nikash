package com.Eqinox.store.services;

import com.Eqinox.store.dtos.BillNotificationDto;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final JavaMailSender mailSender;

    public NotificationService(
            SimpMessagingTemplate messagingTemplate,
            JavaMailSender mailSender) {

        this.messagingTemplate = messagingTemplate;
        this.mailSender = mailSender;
    }

    // 🔔 WebSocket notification (already working)
    public void notifyUser(Integer userId, BillNotificationDto payload) {
        messagingTemplate.convertAndSend(
                "/topic/bills/" + userId,
                payload
        );
        System.out.println("📡 WS SEND → user " + userId);
    }

    // 📧 Email notification
    public void sendBillEmail(
            String email,
            BillNotificationDto payload) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("💸 Bill Due: " + payload.getBillName());

            helper.setText("""
                Hello,

                This is a reminder that your bill is due.

                Bill: %s
                Amount: ৳%s
                Due Date: %s

                %s

                — Hishab Nikash
                """.formatted(
                    payload.getBillName(),
                    payload.getAmount(),
                    payload.getDate(),
                    payload.getMessage()
            ));

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace(); // do NOT crash scheduler
        }
    }
}
