package com.techelevator.tenmo.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.User;

public class AccountService {

	public static String AUTH_TOKEN = "";
	private final String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	private AuthenticatedUser authUser;

	public AccountService(String url, AuthenticatedUser authUser) {
		super();
		BASE_URL = url;
		this.authUser = authUser;
	}
	
	public BigDecimal getUserBalance() throws AccountServiceException {
		String username = authUser.getUser().getUsername();
		int userId = authUser.getUser().getId();
		BigDecimal balance = null;
		
		try {
			balance = restTemplate.exchange(BASE_URL + "user/" + userId + "/balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
		} catch (RestClientResponseException rcre) {
			System.out.println("SERVICE FAIL");

			throw new AccountServiceException(rcre.getRawStatusCode() + " : " + rcre.getResponseBodyAsString());
		}
		return balance;
	}
	
	public User[] userList() throws AccountServiceException {
		User[] userArray;
		try {
			userArray = restTemplate.exchange(BASE_URL + "user/listUsers", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
		} catch (RestClientResponseException rcre) {
			System.out.println("SERVICE FAIL");

			throw new AccountServiceException(rcre.getRawStatusCode() + " : " + rcre.getResponseBodyAsString());
		}
		
		return userArray;
	}
	
	private HttpEntity makeAuthEntity() {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(authUser.getToken());
	    HttpEntity entity = new HttpEntity<>(headers);
	    return entity;
	  }
}
