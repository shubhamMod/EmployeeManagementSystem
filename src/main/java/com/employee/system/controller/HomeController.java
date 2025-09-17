package com.employee.system.controller;

import com.employee.system.dto.PageResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.employee.system.dto.Login;
import com.employee.system.dto.SignUpDTO;
import com.employee.system.jwt.JwtUtil;
import com.employee.system.model.EmployeeProfile;
import com.employee.system.model.SignUp;
import com.employee.system.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

//@Slf4j
@RestController
@RequestMapping("/user")
//@CrossOrigin(value = "http:/localhost:3000",allowCredentials = "true")
public class HomeController {
	
	private final UserService userService;
	@Autowired
	JwtUtil jwtUtil;
	
	public HomeController(UserService userService) {
		this.userService = userService;
	}



	@GetMapping("/home")
	public String home() {
		return "Internal Project";
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<String> setData(@RequestBody SignUp signUp) {
         SignUp saveData = userService.saveData(signUp);
         if(saveData!=null) {
        	 return ResponseEntity.ok("Data Register SuccessFully");
         }else {
        	 return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Data not Register");
         }
	}

    // Controller
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Login login,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        SignUp user = userService.getLogin(login, request);
        if (user != null) {
            SignUpDTO dto = new SignUpDTO();
            dto.setId(user.getId());
            dto.setFirstname(user.getFirstname());
            dto.setLastname(user.getLastname());
            dto.setEmail(user.getEmail());
            String token = jwtUtil.generateToken(user.getEmail());
            request.getSession().invalidate();
            HttpSession newSession = request.getSession(true);  // true = create new session
            newSession.setAttribute("USER", user);
            ResponseCookie authCookie = ResponseCookie.from("AUTH-TOKEN", token)
                    .httpOnly(true)
                    .secure(false)        // true in production
                    .path("/")
                    .maxAge(120)// 2 minutes expiry
                    .sameSite("Strict")
                    .build();


//            HttpSession session = request.getSession(true);
//            ResponseCookie sessionCookie = ResponseCookie.from("JSESSIONID", session.getId())
//                    .httpOnly(true)
//                    .secure(false)
//                    .path("/")
//                    .maxAge(120)
//                     .sameSite("Lax")
//                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, authCookie.toString());
//            response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie.toString());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }




    @PostMapping("/employee/add/{adminId}")
    public ResponseEntity<Object> addEmployee(@PathVariable Long adminId, @RequestBody EmployeeProfile employeeProfile) {
        try {
            EmployeeProfile savedEmployee = userService.addEmployee(adminId, employeeProfile);
            return ResponseEntity.ok(savedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Object> allData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String field,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Page<EmployeeProfile> allEmployee = userService.getAllEmployee(page, size, field, direction);

        if (allEmployee != null && !allEmployee.isEmpty()) {
            return ResponseEntity.ok(new PageResponse(allEmployee));
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Data not Found");
        }
    }

    
    @GetMapping("/fetching/{id}")
    public ResponseEntity<Object> fetchingData(@PathVariable String id) {
    	 EmployeeProfile fetchingProfile = userService.fetchingProfile(id);
    	 if(fetchingProfile!=null) {
    		 return ResponseEntity.ok(fetchingProfile);
    	 }else {
    		 return   ResponseEntity.status(HttpStatus.NO_CONTENT).body("Data not Found");
    	 }
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteData(@PathVariable String id) {
    	 boolean deleteProfile = userService.deleteProfile(id);
    	 if(deleteProfile) {
    		 return ResponseEntity.ok("Employee Deleted");
    	 }else {
    		 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not Found");
    	 }
    }
    @PutMapping ("/enable/{id}")
    public ResponseEntity<String> enableData(@PathVariable String id) {
    	 boolean enableProfile = userService.enableProfile(id);
    	 if(enableProfile) {
    		 return ResponseEntity.ok("Employee Enabled");
    	 }else {
    		 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not Found");
    	 }
    }

    @PutMapping("/update/{adminId}")
    public ResponseEntity<Object> updateData(@PathVariable Long adminId, @RequestBody EmployeeProfile employeeProfile) {
        EmployeeProfile updatedProfile = userService.updateProfile(adminId, employeeProfile);
        if (updatedProfile != null) {
            return ResponseEntity.ok(updatedProfile);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Data not Found");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // Kill server-side session
        request.getSession().invalidate();

        // Expire JSESSIONID
        ResponseCookie jsessionCookie = ResponseCookie.from("JSESSIONID", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)  // expire immediately
                .sameSite("Strict")
                .build();

        // Expire AUTH-TOKEN
        ResponseCookie authTokenCookie = ResponseCookie.from("AUTH-TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)  // expire immediately
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jsessionCookie.toString())
                .header(HttpHeaders.SET_COOKIE, authTokenCookie.toString())
                .build();
    }





}
