package com.apigateway.demo.util;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	
	@Value("${JWT_SECRET_KEY}")
	private String jwtSecret;
	
	
	private Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }
	
	public JWTTokenDTO getJWTToken(String token) throws JsonMappingException, JsonProcessingException {
		Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
		JWTTokenDTO jwtToken = new ObjectMapper().readValue(claims.getSubject(), JWTTokenDTO.class);
		return jwtToken;
	}
    
	 public boolean validateToken(String token) {
	        try{
	            Jwts.parserBuilder()
	                    .setSigningKey(key())
	                    .build()
	                    .parse(token);
	            return true;
	        } catch (Exception e) {
	        }
	        return false;
	    }
}
