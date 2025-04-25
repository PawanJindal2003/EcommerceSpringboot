package com.Project.ecommerce.services.user.forgetPassword;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ForgetPasswordEmailService {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public ForgetPasswordEmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendResetPasswordEmail(String toEmail, String forgetPasswordToken) throws MessagingException {
        String subject = "Link to reset your password";
        String body = "<p>To reset your password, please use the activation token :</p>"
                + "<p>" + forgetPasswordToken + "</p>";
        sendEmail(toEmail, subject, body);
    }

    @Async
    public void sendSuccessResetPasswordEmail(String toEmail) throws MessagingException {

        String subject = "Reset password request";
        String body = "<p>Your password has been successfully changed</p>";

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
}
