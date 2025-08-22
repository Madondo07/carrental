package com.clinic.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    public void sendAppointmentReceipt(String to, String patientName, String studentId, String email, String phone,
            String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("CPUT Clinic Appointment Receipt");
        StringBuilder sb = new StringBuilder();
        sb.append("Thank you for booking your appointment at the CPUT Clinic.\n\n");
        sb.append("--- Appointment Receipt ---\n");
        sb.append("Name: ").append(patientName).append("\n");
        sb.append("Student ID: ").append(studentId).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Phone: ").append(phone).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("\nPlease arrive 10 minutes before your scheduled time.\n");
        sb.append("If you have any questions, contact the clinic.\n");
        sb.append("\n---\nCPUT Clinic Management System");
        message.setText(sb.toString());
        mailSender.send(message);
    }
}
