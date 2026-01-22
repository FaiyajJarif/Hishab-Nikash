package com.Eqinox.store.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FamilyInviteEmailService {

    private final EmailService emailService;

    @Value("${app.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    public FamilyInviteEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendInviteEmail(String toEmail, String familyName, String inviterName, String token) {
        // frontend deep link (later you will build pages)
        String acceptUrl = frontendBaseUrl + "/invitations/accept?token=" + token;
        String rejectUrl = frontendBaseUrl + "/invitations/reject?token=" + token;

        String subject = "You’re invited to join a family budget: " + familyName;

        String body = ""
                + "Hi,\n\n"
                + inviterName + " invited you to join \"" + familyName + "\".\n\n"
                + "Accept: " + acceptUrl + "\n"
                + "Reject: " + rejectUrl + "\n\n"
                + "If you did not expect this, you can ignore this email.\n";

        // If your EmailService has a generic send method, use it.
        // If it only has sendVerificationEmail, add a generic method there.
        emailService.sendPlainEmail(toEmail, subject, body);
    }
}
