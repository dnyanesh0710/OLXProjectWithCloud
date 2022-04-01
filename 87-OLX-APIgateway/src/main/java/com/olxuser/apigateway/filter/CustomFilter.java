package com.olxuser.apigateway.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class CustomFilter implements GlobalFilter{

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		System.out.println("In filter->Doing preprocessing");
		// after successful pre-processing this method must call filter() on chain obj

		/**
		 * 
		 */
		ServerHttpRequest request = exchange.getRequest();
		HttpHeaders headers = request.getHeaders();

		List<String> list = headers.get("Authorization");

		if (list != null) {

			String authorizationHeaderValue = list.get(0);

			if (authorizationHeaderValue == null) {
				/**
				 * Preprocessing logic fails then do not allow request to proceed
				 */
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}
		} else {
			ServerHttpResponse response = exchange.getResponse();
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return response.setComplete();
		}
		return chain.filter(exchange);
	}

}
