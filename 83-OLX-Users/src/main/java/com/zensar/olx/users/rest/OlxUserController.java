package com.zensar.olx.users.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.zensar.olx.users.bean.LoginResponse;
import com.zensar.olx.users.bean.LoginUser;
import com.zensar.olx.users.bean.OLXUser;
import com.zensar.olx.users.db.TokenStorage;
import com.zensar.olx.users.service.OlxUserService;
import com.zensar.olx.users.util.jwtutil.JwtUtil;

@RestController
public class OlxUserController {

	@Autowired
	OlxUserService olxUserService;

	@Autowired
	AuthenticationManager manager;
	
	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * This is the REST specification 1 according to documentation. Authentication
	 * using auth token.
	 * 
	 * @param user
	 * @return
	 */
	@PostMapping("/user/authenticate")
	public LoginResponse login(@RequestBody LoginUser user) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				user.getUserName(), user.getPassword());

		System.out.println(user.getUserName());
		System.out.println(user.getPassword());

		try {
			//Authentication authenticate=this.manager.authenticate(authenticationToken);
			//String token = jwtUtil.generateToken(user.getUserName(),authenticate.getAuthorities());
			String token = jwtUtil.generateToken(user.getUserName());
			TokenStorage.storeToken(token, token);
			LoginResponse userResponse = new LoginResponse();
			userResponse.setJwt(token);
			return userResponse;
		} catch (Exception e) {
			throw e;
		}
	}
	

	@PostMapping(value = "/user")
	public OLXUser addOlxUser(@RequestBody OLXUser user) {
		return this.olxUserService.addOLXUser(user);
	}

	/**
	 * This is the URL 4
	 * using auth token.
	 * 
	 * @param user
	 * @return
	 */
	@GetMapping(value = "/user/{id}")
	public OLXUser findPerson(@PathVariable(name = "id") int id) {
		return this.olxUserService.findById(id);
	}

	@GetMapping("/user/find/{userName}")
	public OLXUser findOlxUserByName(@PathVariable(name = "userName") String name) {
		return this.olxUserService.findOlxUserByName(name);
	}
	
	
	/**
	 * REST 5th endpoint
	 * @param authToken
	 * @return
	 */
	@GetMapping("/token/validate")
	public ResponseEntity<Boolean> isValidateUser(@RequestHeader("Authorization") String authToken) {
		try {
			String validatedToken=jwtUtil.validateToken(authToken.substring(7));
			return new ResponseEntity<Boolean>(true,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/user/logout")
	public ResponseEntity<Boolean> logout(@RequestHeader("Authorization") String authToken){
		String validatedToken=authToken.substring(7);
		try {
			TokenStorage.removeToken(validatedToken);
			return new ResponseEntity<Boolean>(true,HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
		}
	}

}
