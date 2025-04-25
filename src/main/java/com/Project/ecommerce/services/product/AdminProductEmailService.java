package com.Project.ecommerce.services.product;

import com.Project.ecommerce.entities.product.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AdminProductEmailService {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public AdminProductEmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendProductActivationEmail(String toEmail, Product product) throws MessagingException {

        String subject = "Product activation status";
        String body = "<p>Your product has been activated by admin</p>" +
                "<h3>Product Details:</h3>" +
                "<ul>" +
                "<li><strong>Name:</strong> " + product.getName() + "</li>" +
                "<li><strong>Brand:</strong> " + product.getBrand() + "</li>" +
                "<li><strong>Category:</strong> " + product.getCategory().getName() + "</li>" +
                "<li><strong>Description:</strong> " + product.getDescription() + "</li>" +
                "</ul>";

        sendEmail(toEmail, subject, body);
    }

    @Async
    public void sendProductDeactivationEmail(String toEmail, Product product) throws MessagingException {

        String subject = "Account activation status";
        String body = "<p>Your product has been deactivated by admin</p>" +
                "<h3>Product Details:</h3>" +
                "<ul>" +
                "<li><strong>Name:</strong> " + product.getName() + "</li>" +
                "<li><strong>Brand:</strong> " + product.getBrand() + "</li>" +
                "<li><strong>Category:</strong> " + product.getCategory().getName() + "</li>" +
                "<li><strong>Description:</strong> " + product.getDescription() + "</li>" +
                "</ul>";

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
