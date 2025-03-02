package com.apigateway.demo.util;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JWTTokenDTO {

	    private String username;

	    private Set<String> roles;

	    private Long userId;
	    
	    private Long issuedAt;
	    
	    private Long expiredAt;
	    
	    private String sessionId;
	    
	    public JWTTokenDTO setUsername(String username) {
	        this.username = username;
	        return this;
	    }

	    public JWTTokenDTO setRoles(Set<String> roles) {
	        this.roles = roles;
	        return this;
	    }

	    public JWTTokenDTO setUserId(Long userId) {
	        this.userId = userId;
	        return this;
	    }

	}

