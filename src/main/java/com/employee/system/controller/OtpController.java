package com.employee.system.controller;

import com.employee.system.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/sending")
    public String otpSender(@RequestParam String email) {

        boolean sendOtp = otpService.sendOtp(email);
        if(sendOtp) {
            return "otp send to this mail :"+" "+email;
        }else {
            return "otp fail to this mail :"+" "+email;
        }

    }

}
