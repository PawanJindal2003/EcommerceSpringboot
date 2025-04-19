package com.Project.ecommerce.services.product.seller;

import com.Project.ecommerce.entities.product.Product;
import com.Project.ecommerce.entities.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SellerProductEmailService {
    @Value("${spring.mail.username}")
    private String adminEmail;

    private final JavaMailSender emailSender;


    public SellerProductEmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }
    @Async
    public void sendNewProductActivationEmail(String fromEmail, Product product, User seller) throws MessagingException {


        String subject = "New Product Awaiting Activation";

        String body = "<html>" +
                "<body>" +
                "<p>Dear Admin,</p>" +
                "<p>A new product has been submitted and is awaiting your activation.</p>" +

                "<h3>Product added by: </h3>" +
                "<ul>" +
                "<li><strong>Seller Name:</strong> " + seller.getFirstName() + " " + seller.getLastName() + "</li>" +
                "</ul>" +

                "<h3>Product Details:</h3>" +
                "<ul>" +
                "<li><strong>Name:</strong> " + product.getName() + "</li>" +
                "<li><strong>Brand:</strong> " + product.getBrand() + "</li>" +
                "<li><strong>IS_CANCELLABLE:</strong> " + product.getIsCancellable() + "</li>" +
                "<li><strong>IS_RETURNABLE:</strong> " + product.getIsReturnable() + "</li>" +
                "<li><strong>Description:</strong> " + product.getDescription() + "</li>" +
                "<li><strong>Category:</strong> " + product.getCategory().getName() + "</li>" +
                "</ul>" +
                "<p>Please log in to the admin panel to review and activate the product.</p>" +
                "<br>" +
                "<p>Best regards,<br>Your eCommerce Team</p>" +
                "</body>" +
                "</html>";

        sendEmail(fromEmail, subject, body);
    }


    private void sendEmail(String fromEmail, String subject, String body) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(adminEmail);
        helper.setSubject(subject);
        helper.setText(body, true); // Set the body as HTML

        emailSender.send(message);
    }
}

