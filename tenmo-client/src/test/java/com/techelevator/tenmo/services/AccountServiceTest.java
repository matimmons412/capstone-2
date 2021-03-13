package com.techelevator.tenmo.services;

import org.junit.*;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.UserCredentials;


public class AccountServiceTest {
	
	
	private static final String API_BASE_URL = "http://localhost:8080/";
	private AuthenticatedUser currentUser;
    private AuthenticationService authenticationService;
    private UserCredentials credentials;
    private AccountService accountService;
	
    @BeforeClass
    
    @AfterClass
    
    @Before
	public void setup() {
    	authenticationService = new AuthenticationService(API_BASE_URL);
    	credentials.setUsername("Matt");
    	credentials.setPassword("Matt");
    	try {
			currentUser = authenticationService.login(credentials);
		} catch (AuthenticationServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	accountService = new AccountService(API_BASE_URL, currentUser);
    }
	
	@After
	
	@Test
	public void getUserBalace_returns_double() throws AccountServiceException {
		// ARRANGE
		
		accountService.getUserBalance();
	
	}
	
}
