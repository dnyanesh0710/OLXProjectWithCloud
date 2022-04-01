package com.zensar.olx.users.security.jwt.filter;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.zensar.olx.users.db.TokenStorage;
import com.zensar.olx.users.util.jwtutil.JwtUtil;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

	private String authHeader = "Authorization";
	private final String BEARER="Bearer ";
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		JwtUtil jwtUtil=new JwtUtil();

		System.out.println("IN do filter internal");
		String authorizationHeaderValue = request.getHeader(authHeader);
		if(authorizationHeaderValue==null || !authorizationHeaderValue.startsWith(BEARER)) {
			chain.doFilter(request, response);
			return;
		}
		
		if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith(BEARER)) {
			String token = authorizationHeaderValue.substring(7).trim();
			String tokenExists=TokenStorage.getToken(token);
			if(tokenExists==null) {
				chain.doFilter(request, response);
				return;
			}
			/*if (token != null) {
				System.out.println("AUTH HEADER: " + authorizationHeaderValue);
				System.out.println("TOKEN: " + token);
				
				*/
				try {
					String encodedPayload=jwtUtil.validateToken(token);
					
					String payload=new String(Base64.getDecoder().decode(encodedPayload));
					
					JsonParser jsonParser=JsonParserFactory.getJsonParser();
					Map<String,Object> parseMap=jsonParser.parseMap(payload);
					String username=(String)parseMap.get("username");

					
					UsernamePasswordAuthenticationToken authenticationToken
						=new UsernamePasswordAuthenticationToken(username,
								null,
								AuthorityUtils.createAuthorityList("ROLE_USER"));
					
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}

		chain.doFilter(request, response);
		}
	}


