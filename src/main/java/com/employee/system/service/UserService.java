package com.employee.system.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.employee.system.dto.Login;
import com.employee.system.exception.ProfileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.employee.system.model.EmployeeProfile;
import com.employee.system.model.SignUp;
import com.employee.system.repo.EmployeeRepo;
import com.employee.system.repo.Profile;
import com.employee.system.security.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {
	
	private final EmployeeRepo employeeRepo;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	Profile profile;
	

	public UserService(EmployeeRepo employeeRepo) {
		this.employeeRepo = employeeRepo;
	}


BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
	public SignUp saveData(SignUp signUp) {

		 LocalDateTime now = LocalDateTime.now();
	        signUp.setR_cre_time(now);
	        signUp.setR_mod_time(now);
	        signUp.setR_created_by("SYSTEM");
	        signUp.setR_modified_by("SYSTEM");
	        signUp.setPassword(bCryptPasswordEncoder.encode(signUp.getPassword()));
//	        List<Role> userRoles = new ArrayList<>();
//	        if (signUp.getRoles() != null) {
//	            for (Role role : new ArrayList<>(signUp.getRoles())) {
//	                Role existingRole = roleRepo.findByName(role.getName())
//	                        .orElseGet(() -> {
//	                            role.setR_cre_time(now);
//	                            role.setR_mod_time(now);
//	                            role.setR_created_by("SYSTEM");
//	                            role.setR_modified_by("SYSTEM");
//	                            return roleRepo.save(role);
//	                        });
//	                userRoles.add(existingRole);
//	            }
//	        }
//	        signUp.setRoles(userRoles);
	        return employeeRepo.save(signUp);
	}

    // Service
    public SignUp getLogin(Login login, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        login.getEmail(),
                        login.getPassword()
                )
        );
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(120);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            session.setAttribute("userId", userDetails.getUsername());
            return userDetails.getSignUp();
        }
        return null;
    }



    public EmployeeProfile addEmployee(Long adminId, EmployeeProfile employeeProfile) {
	        SignUp admin = employeeRepo.findById(adminId)
	                .orElseThrow(() -> new RuntimeException("Admin not found"));
	        LocalDateTime now = LocalDateTime.now();
        String lastId = profile.findLastEmployeeId();
        int nextNumber = 100; // start from EMP100

        if (lastId != null && lastId.startsWith("EMP")) {
            nextNumber = Integer.parseInt(lastId.replace("EMP", "")) + 1;
        }

        String newId = "EMP" + nextNumber;
        employeeProfile.setId(newId);
	        employeeProfile.setR_cre_time(now);
	        employeeProfile.setR_mod_time(now);
	        employeeProfile.setR_created_by(admin.getFirstname()+"  "+admin.getLastname());
	        employeeProfile.setR_modified_by(admin.getFirstname()+"  "+admin.getLastname());
	        employeeProfile.setUser(admin);

	        return profile.save(employeeProfile);
	    }

//        @Cacheable(value = "EmployeeProfile",key = "'Data'")
    public Page<EmployeeProfile> getAllEmployee(int page, int size, String field, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(field).ascending()
                : Sort.by(field).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return profile.findAll(pageable);
    }

//public Page<EmployeeProfile> getAllEmployee(int page, int size, String field, String direction) {
//    Sort sort = direction.equalsIgnoreCase("asc")
//            ? Sort.by(field).ascending()
//            : Sort.by(field).descending();
//    Pageable pageable = PageRequest.of(page, size, sort);
//    return profile.findByEnabledTrue(pageable);
//}


//    @Cacheable(value = "EmployeeProfile",key = "#id")
public EmployeeProfile fetchingProfile(String id) {
    return profile.findById(id)
            .filter(EmployeeProfile::isEnabled)
            .orElseThrow(() -> new ProfileNotFoundException("Profile not found or not enabled"));
}
		
		// Update
        public EmployeeProfile updateProfile(String employeeId, Long adminId, EmployeeProfile employeeProfile) {
            SignUp admin = employeeRepo.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            EmployeeProfile existingProfile = profile.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            existingProfile.setFirstname(employeeProfile.getFirstname());
            existingProfile.setLastname(employeeProfile.getLastname());
            existingProfile.setDepartment(employeeProfile.getDepartment());
            existingProfile.setDesignation(employeeProfile.getDesignation());
            existingProfile.setPhone(employeeProfile.getPhone());
            existingProfile.setAddress(employeeProfile.getAddress());
            existingProfile.setR_mod_time(LocalDateTime.now());
            existingProfile.setR_modified_by(admin.getFirstname() + " " + admin.getLastname());

            return profile.save(existingProfile);
        }





    // Soft Delete

//    @CacheEvict(value = "employee", key = "#id")
    public boolean deleteProfile(String id) {
        Optional<EmployeeProfile> byId = profile.findById(id);

        if (byId.isPresent()) {
            EmployeeProfile employeeProfile = byId.get();
            if (employeeProfile.isEnabled()) { 
                employeeProfile.setR_mod_time(LocalDateTime.now());
                employeeProfile.setEnabled(false);
                profile.save(employeeProfile);
                return true;
            }
        }
        return false;
    }

    public boolean enableProfile(String id) {
        Optional<EmployeeProfile> byId = profile.findById(id);
        if (byId.isPresent()) {
            EmployeeProfile employeeProfile = byId.get();
            if (!employeeProfile.isEnabled()) {
                employeeProfile.setR_mod_time(LocalDateTime.now());
                employeeProfile.setEnabled(true);
                profile.save(employeeProfile);
                return true;
            }
        }
        return false;
    }






}
