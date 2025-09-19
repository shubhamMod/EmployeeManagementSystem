package com.employee.system.controller;

import com.employee.system.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/service")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/sending")
    public String otpSender(@RequestParam String email) {
        boolean sendOtp = otpService.sendOtp(email);
        if(sendOtp) {
            return "OTP sent to: " + email;
        } else {
            return "Failed to send OTP to: " + email;
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        String isValid = otpService.verifyOtp(email, otp);

        Map<String, Object> response = new HashMap<>();

        if (isValid!=null) {
            otpService.clearOtp(email); // clear OTP after successful verification
            response.put("status", "success");
            response.put("message", "OTP verified successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Invalid OTP");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @PutMapping("/reset-password")
    public String updatePassword(@RequestBody Map<String, String> request){
        String email = request.get("email");
        String password = request.get("password");

       boolean f= otpService.setPassword(password,email);
       if(f){
           return "Success";
       }else{
           return "Failed";
       }
    }





}
