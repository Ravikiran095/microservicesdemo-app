package com.appsdeveloperblog.photoapp.api.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.net.HttpHeaders;

import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.config> {

	@Autowired
	Environment env;
	
	     public AuthorizationHeaderFilter() {
	    	 super(config.class);
	     }
	
           public static class config {
        	   
           }

		@Override
		public GatewayFilter apply(config config) {
			return (exchange, chain)-> {
		
				ServerHttpRequest request = exchange.getRequest(); 
		if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
			return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
		}
			
		String  authorizationHeader = request.getHeaders().get("Authorization").get(0);
		String jwt = authorizationHeader.replace("Bearer", "");
		
		if(!isJwtvalid(jwt)) {
			return onError(exchange, "Jwt is not valid", HttpStatus.UNAUTHORIZED);
		}
		
		
		return chain.filter(exchange);
		};
}

		private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
              
			ServerHttpResponse response = exchange.getResponse();
			response.setStatusCode(httpStatus);
			return response.setComplete();
		}
		
		private boolean isJwtvalid(String jwt) {
			boolean returnValue = true;
			
			String subject = Jwts.parser()
			.setSigningKey(env.getProperty("token.secret"))
			.parseClaimsJws(jwt)
			.getBody()
			.getSubject();
			
			if(subject ==null || subject.isEmpty()) {
				returnValue = false;
			}
			return returnValue;
		}
		
}
