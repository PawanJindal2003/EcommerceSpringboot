package com.Project.ecommerce.services.admin;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AdminEmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public AdminEmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendActivationEmail(String toEmail) throws MessagingException {

        String subject = "Account activation status";
        String body = "<p>Your account has been activated by admin</p>";

        sendEmail(toEmail, subject, body);
    }

    @Async
    public void sendDeactivationEmail(String toEmail) throws MessagingException {

        String subject = "Account activation status";
        String body = "<p>Your account has been deactivated by admin</p>";

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true); // Set the body as HTML

        emailSender.send(message);
    }

    private String generateActivationUrl(String token) {
        String baseUrl = "http://localhost:8080/api/admin/activation-status";
        return baseUrl + "?token=" + token;
    }
}
