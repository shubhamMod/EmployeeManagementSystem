package com.employee.system.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RoleController {
    
	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/home")
    public String home(){
        return "Hello World";
    }
}
