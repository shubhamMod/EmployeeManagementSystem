package com.employee.system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.employee.system.model.SignUp;
import com.employee.system.repo.EmployeeRepo;

@Service
public class CustomUserDetailService implements UserDetailsService {
	
	@Autowired
	EmployeeRepo userRepo;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SignUp byEmailIgnoreCase = userRepo.findByEmailIgnoreCase(username);
		
		return new CustomUserDetails(byEmailIgnoreCase);
	}

}
