package com.Project.ecommerce.services.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


@Service
public class CustomerEmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public CustomerEmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendActivationEmail(String toEmail, String activationToken) throws MessagingException {
        String activationUrl = generateActivationUrl(activationToken);

        String subject = "Activate Your Account";
        String body = "<p>Welcome! To activate your account, please click the link below:</p>"
                + "<p><a href='" + activationUrl + "'>Activate Account</a></p>";

        sendEmail(toEmail, subject, body);
    }

    public void sendConfirmationEmail(String toEmail) throws MessagingException {
        String subject = "Account activation confirmation";
        String body = "<p>Congratulation Your Account has been activated.</p>";

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
        String baseUrl = "http://localhost:8080/api/auth/customer/activate";
        return baseUrl + "?token=" + token;
    }
}
