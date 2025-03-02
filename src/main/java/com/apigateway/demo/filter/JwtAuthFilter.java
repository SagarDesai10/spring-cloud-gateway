package com.apigateway.demo.filter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.apigateway.demo.util.JWTTokenDTO;
import com.apigateway.demo.util.JwtUtils;


@Component
@Order(0)
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {
    
    private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtAuthFilter.class);
	
    @Autowired
    private JwtUtils jwtUtils;

	public JwtAuthFilter() {
		super(Config.class);
	}
	
	@Override
	public GatewayFilter apply(Config config) {
	    return (exchange, chain) -> {
	        ServerHttpRequest updatedRequest = null;

	        // Check header contains token or not
	        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
	            throw new RuntimeException("Not authorized");
	        }

	        String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            authHeader = authHeader.substring(7);
	        }

	        try {
	            if (!jwtUtils.validateToken(authHeader)) {
	                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid Token");
	            }
	            JWTTokenDTO jwtToken = jwtUtils.getJWTToken(authHeader);
	            logger.info("client Ip captured at gateway from the incoming request: {}", 
	                        exchange.getRequest().getHeaders().getFirst("X-Forwarded-For"));

	            String rolesString = String.join(",", jwtToken.getRoles());
	            String sessionId = jwtToken.getSessionId();

	            updatedRequest = exchange.getRequest()
	                    .mutate()
	                    .header("X-User-Id", jwtToken.getUserId().toString())
	                    .header("roles", rolesString)
	                    .header("X-User", jwtToken.getUsername())
	                    .header("X-Session-Id", sessionId)
	                    .header("X-External-Request", "true")
	                    .build();

	        } catch (Exception e) {
	            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to the application", e);
	        }

	        return chain.filter(exchange.mutate().request(updatedRequest).build());
	    };
	}

	
	public static class Config {
		
	}

}
