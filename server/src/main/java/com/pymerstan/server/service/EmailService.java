package com.pymerstan.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String senderAddress;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String senderAddress) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
    }

    public void sendVerificationOtp(String recipientEmail, String otp) {
        log.debug("Attempting to send OTP email to {}", recipientEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderAddress);
            message.setTo(recipientEmail);
            message.setSubject("Verify Your Pymerstan Account");

            String emailBody = String.format(
                    "Hello,\n\nYour account verification One-Time Password (OTP) is: %s\n\n" +
                            "This code is valid for the next 15 minutes.\n\n" +
                            "Thank you,\nPymerstan Support",
                    otp
            );
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Successfully sent OTP email to {}", recipientEmail);

        } catch (MailException e) {
            log.error("Failed to send OTP email to {}: {}", recipientEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send the OTP email. Please try again later.", e);
        }
    }
}