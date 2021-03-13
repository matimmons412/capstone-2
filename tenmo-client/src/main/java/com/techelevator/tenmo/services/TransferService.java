package com.techelevator.tenmo.services;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfers;

public class TransferService {

	private final String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	private AuthenticatedUser authUser;

	public TransferService(String url, AuthenticatedUser authUser) {
		super();
		BASE_URL = url;
		this.authUser = authUser;	}
	
	public Transfers[] listTransferHistory() throws TransfersServiceException {
		Transfers[] transfersArray = null;
		
		try {
			transfersArray = restTemplate.exchange(BASE_URL + "/transfers/" + authUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), Transfers[].class).getBody();
		} catch (RestClientResponseException rcre) {
			System.out.println("LIST TRANSFER HISTORY FAILED");

			throw new TransfersServiceException(rcre.getRawStatusCode() + " : " + rcre.getResponseBodyAsString());
		}
		return transfersArray;
	}
	
	public Transfers getTransferDetails(Long transferId) throws TransfersServiceException {
		Transfers transfer = new Transfers();
		
		try {
			transfer = restTemplate.exchange(BASE_URL + "/transfers/" + authUser.getUser().getId() + "/details/" + transferId , HttpMethod.GET, makeAuthEntity(), Transfers.class).getBody();
		} catch (RestClientResponseException rcre) {
			System.out.println("GET TRANSFER DETAILS FAILED");
			throw new TransfersServiceException(rcre.getRawStatusCode() + " : " + rcre.getResponseBodyAsString());
		}
		return transfer;
	}
	
	public Transfers sendTransfer(Long toUserId, BigDecimal amount) throws TransfersServiceException {
		Transfers transfer = new Transfers();
		Transfers sendTransfer = new Transfers();
		sendTransfer.setToUserId(toUserId);
		sendTransfer.setAmount(amount);
		try {
			transfer = restTemplate.exchange(BASE_URL + "/transfers/" + authUser.getUser().getId() + "/send", HttpMethod.POST, makeAuthEntityWithBody(sendTransfer), Transfers.class).getBody();
		} catch (RestClientResponseException rcre) {
			System.out.println("*** SEND TRANSFER FAILED ***");
			System.out.println("We are currently unable to process your transfer. Please try again.");
			//throw new TransfersServiceException(rcre.getRawStatusCode() + " : " + rcre.getResponseBodyAsString());
		}
		return transfer;
	}
	
	private HttpEntity makeAuthEntity() {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(authUser.getToken());
	    HttpEntity entity = new HttpEntity<>(headers);
	    return entity;
	  }
	
	private HttpEntity<Transfers> makeAuthEntityWithBody(Transfers transfer) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(authUser.getToken());
	    HttpEntity<Transfers> entity = new HttpEntity<>(transfer, headers);
	    return entity;
	  }
}
