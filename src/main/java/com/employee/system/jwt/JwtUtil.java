package com.employee.system.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	
	@Value("${spring.jpa.properties.jwt.secret}")
    private String key;

    @Value("${spring.jpa.properties.jwt.expiration}")
    private long expiration;


    private SecretKey getKey() {
	        try {
	            if (key == null || key.isBlank()) {
	                KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
	                SecretKey secretKey = keyGen.generateKey();
	                key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
	            }
	            byte[] keyBytes = Decoders.BASE64.decode(key); 
	            return Keys.hmacShaKeyFor(keyBytes);
	        } catch (Exception e) {
	            throw new RuntimeException("Invalid Secret Key: " + e.getMessage(), e);
	        }
	    } 
	 
	 public String generateToken(String username) {
		 Map<String , String>mp=new HashMap<String, String>();
		 mp.put("username", username);
		 return Jwts.builder()

				 .subject(username)
				 .issuedAt(new Date(System.currentTimeMillis()))
				 .expiration(new Date(System.currentTimeMillis()+expiration))
//				 .and()
				 .signWith(getKey())
				 .header()
				 .type("JWT")
				 .and()
				 .compact();
	 }
	 

	    public String extractUsername(String token) {
	        return extractClaims(token).getSubject();
	    }

	    public boolean valideToken(String token, UserDetails userDetails) {
	        final String username = extractUsername(token);
	        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	    }

	    private boolean isTokenExpired(String token) {
	        return extractClaims(token).getExpiration().before(new Date());
	    }

	    public Claims extractClaims(String token) {
	        return Jwts.parser()
	                .verifyWith(getKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	    }
	
	
	

}
