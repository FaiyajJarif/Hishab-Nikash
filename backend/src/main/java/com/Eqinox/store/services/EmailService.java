package com.Eqinox.store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("faiyaj.jarif01@gmail.com"); // same as spring.mail.username
        message.setSubject("Verify your Hishab Nikash account");
        message.setText(
                "Welcome to Hishab Nikash!\n\n" +
                        "Please click the link below to verify your email:\n" +
                        verificationLink + "\n\n" +
                        "If you did not sign up, you can ignore this email.");

        System.out.println("Sending verification email to " + toEmail);
        mailSender.send(message);
        System.out.println("Verification email sent to " + toEmail);
    }

    public void sendBillEmail(String toEmail,
            String billName,
            String amount,
            String date) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("faiyaj.jarif01@gmail.com");
        message.setSubject("💸 Bill Paid: " + billName);
        message.setText(
                "Your recurring bill was paid successfully.\n\n" +
                        "Bill: " + billName + "\n" +
                        "Amount: ৳" + amount + "\n" +
                        "Date: " + date + "\n\n" +
                        "— Hishab Nikash");

        mailSender.send(message);
    }
    public void sendPlainEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);

        System.out.println("📧 EMAIL SENT → " + to);
    }
}
