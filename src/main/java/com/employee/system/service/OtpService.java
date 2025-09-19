package com.employee.system.service;

import com.employee.system.model.SignUp;
import com.employee.system.repo.EmployeeRepo;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmployeeRepo employeeRepo;

    // Store OTP + expiry time
    private final Map<String, OtpData> otpStorage = new HashMap<>();



    // Inner class
    private static class OtpData {
        private final String otp;
        private final Instant expiryTime;

        public OtpData(String otp, Instant expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
        public String getOtp() { return otp; }
        public Instant getExpiryTime() { return expiryTime; }
    }

    private String generateOtp() {
        SecureRandom secureRandom = new SecureRandom();
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    public boolean sendOtp(String email) {
        String otp = generateOtp();
        String cleanEmail = email.trim().replaceAll("\\s+", "");

        try {
            saveOtp(cleanEmail, otp, 5); // 5 mins
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
                + "\n\nThis OTP will expire in 5 minutes.\n\nRegards,\nBank Support");
        message.setFrom("no-reply@yourbank.com");

        javaMailSender.send(message);
    }

    public void saveOtp(String email, String otp, int minutesValid) {
        Instant expiry = Instant.now().plusSeconds(minutesValid * 60L);
        otpStorage.put(email, new OtpData(otp, expiry));
    }

    // ✅ Now returns a string status instead of boolean
    public String verifyOtp(String email, String otp) {
        if (!otpStorage.containsKey(email)) {
            return "OTP not found";
        }

        OtpData otpData = otpStorage.get(email);

        if (Instant.now().isAfter(otpData.getExpiryTime())) {
            otpStorage.remove(email);
            return "OTP expired";
        }

        if (!otpData.getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        return "VALID";
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
    }

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public boolean setPassword(String password, String email) {
        SignUp user = employeeRepo.findByEmailIgnoreCase(email);
        if (user == null) {
            return false;
        }
        user.setPassword(bCryptPasswordEncoder.encode(password));
        employeeRepo.save(user);
        return true;
    }



}
