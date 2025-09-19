package com.employee.system.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpService {

    @Autowired
   private JavaMailSender javaMailSender;


    private String generateOtp(){
        SecureRandom secureRandom=new SecureRandom();

        int otp=100000+secureRandom.nextInt(900000);

        return String.valueOf(otp);
    }

    public boolean sendOtp(String email) {
        String otp = generateOtp();
        String cleanEmail = email.trim().replaceAll("\\s+", "");

        try {
            sendOtpToEmail(cleanEmail, otp);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendOtpToEmail(String email, String otp) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Bank OTP Verification");
        message.setText("Hello, \n\nYour OTP is: " + otp
                + "\n\nPlease do not share this OTP with anyone.\n\nRegards,\nBank Support");
        message.setFrom("no-reply@yourbank.com"); // better use a no-reply address

        javaMailSender.send(message);
    }

}
