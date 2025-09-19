package com.employee.system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.employee.system.jwt.JwtFilter;
import com.employee.system.jwt.JwtFilter1;



@Configuration
public class SecurityConfig {
	
	@Autowired
	private CustomUserDetailService customUserDetailService;
	@Autowired
	JwtFilter jwtFilter;
	@Autowired
	JwtFilter1 jwtFilter1;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable) // disable HTTP Basic
                .formLogin(AbstractHttpConfigurer::disable)          // disable default login form
                .authorizeHttpRequests(r -> r
                        .requestMatchers("/user/login", "/user/register","/service/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // optional for JWT
                .addFilterBefore(jwtFilter1, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider(customUserDetailService);
		authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		return authenticationProvider;
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	

}
