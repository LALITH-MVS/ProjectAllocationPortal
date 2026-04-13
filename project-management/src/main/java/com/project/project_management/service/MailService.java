package com.project.project_management.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendIdeaSubmissionMail(String toEmail, String ideaTitle, String studentName) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Idea Submitted");

        message.setText(
                "A new idea has been submitted.\n\n" +
                        "Student: " + studentName + "\n" +
                        "Title: " + ideaTitle + "\n\n" +
                        "Please review and approve it."
        );

        mailSender.send(message);
    }
}