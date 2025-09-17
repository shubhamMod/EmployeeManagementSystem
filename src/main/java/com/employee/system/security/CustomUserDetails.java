package com.employee.system.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.employee.system.model.SignUp;


public class CustomUserDetails implements UserDetails {

	
	private static final long serialVersionUID = 1L;
	
	SignUp signUp;
	 public SignUp getSignUp() {
	        return signUp;
	    }

	public CustomUserDetails(SignUp byEmailIgnoreCase) {
		this.signUp=byEmailIgnoreCase;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    return signUp.getRoles()
	                 .stream()
	                 .map(role -> new SimpleGrantedAuthority(role.getName())) 
	                 .collect(Collectors.toList());
	}


	@Override
	public String getPassword() {
		return signUp.getPassword();
	}

	@Override
	public String getUsername() {
		return signUp.getEmail();
	}

}
