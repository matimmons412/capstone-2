package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.client.RestClientResponseException;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfers;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AccountServiceException;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.TransfersServiceException;
import com.techelevator.view.ConsoleService;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";

	private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN,
			MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS,
			MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS,
			MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	BigDecimal currentBalance = null;

	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;

	public static void main(String[] args) {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");

		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while (true) {
			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				try {
					viewCurrentBalance();
				} catch (AccountServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() throws AccountServiceException {
		// TODO Auto-generated method stub
		AccountService accountService = new AccountService(API_BASE_URL, currentUser);
		try {
			currentBalance = accountService.getUserBalance();
			System.out.println("Your current account balance is: $" + currentBalance);
		} catch (RestClientResponseException rcre) {
			System.out.println("App FAIL");
			throw new AccountServiceException(rcre.getRawStatusCode() + " : " + rcre.getResponseBodyAsString());

		}
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		TransferService transferService = new TransferService(API_BASE_URL, currentUser);
		List<Long> validIds = new ArrayList<>();
		String choice = "";
		
		System.out.println("------------------------------------------------");
		System.out.println("Transfers                                       ");
		System.out.println("ID\t\t   From/To\t\tAmount");
		System.out.println("------------------------------------------------");
		try {
			for (Transfers transfer : transferService.listTransferHistory()) {
				validIds.add(transfer.getId());
				if (transfer.getAccountFrom().equalsIgnoreCase(currentUser.getUser().getUsername())) {
					System.out.println(
							transfer.getId() + "\t\t To: " + transfer.getAccountTo() + "\t\t$ " + transfer.getAmount());
				} else {
					System.out.println(transfer.getId() + "\t\t From: " + transfer.getAccountFrom() + "\t\t$ "
							+ transfer.getAmount());
				}
				
			}
			if (transferService.listTransferHistory().length == 0) {
				console.getUserInput("** You haven't sent any transfers yet, please press ENTER to return to Main Menu **");
				return;
			}
			choice = console.getUserInput("\nPlease enter transfer ID to view details (0 to cancel)");
			Long transferId = Long.parseLong(choice);
			if (transferId == 0) {
				return;
			} else if (!validIds.contains(transferId)) {
				System.out.println("*** " + transferId + " is not a valid ID ***");
				return;
			}
			// NumberFormatException needed?
			Transfers thisTransfer = transferService.getTransferDetails(transferId);
			// Return Transfer Details based on user choice

			System.out.println("--------------------------------------------");
			System.out.println("Transfer Details");
			System.out.println("--------------------------------------------");
			System.out.println(thisTransfer.toString());
		} catch (TransfersServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException nfe) {
			System.out.println("*** " + choice + " is not a valid option ***");
		}
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub

	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		// Print list of users
		// User selection goes into path for transfer
		AccountService accountService = new AccountService(API_BASE_URL, currentUser);
		List<Long> validIds = new ArrayList<>();
		
		System.out.println("-------------------------------------------");
		System.out.println(" User                                      ");
		System.out.println("  ID\t\t\t\tUsername       ");
		System.out.println("-------------------------------------------");
		try {
			for (User user : accountService.userList()) {
				System.out.println(user.getId()+"\t\t\t\t"+user.getUsername());
				validIds.add(user.getId().longValue());
				
			}
			try {
				String userIdChoice = console.getUserInput("\nEnter ID of user you are sending to (0 to cancel)");
				Long toUserId = Long.parseLong(userIdChoice);
				if (toUserId == 0) {
					return;
				} else if(!validIds.contains(toUserId)) {
					System.out.println("*** Invalid User ID ***");
					return;
				} else if(toUserId.intValue() == currentUser.getUser().getId()) {
					System.out.println("*** Unable to send money to yourself ***");
					return;
				}

					String amountChoice = console.getUserInput("\nEnter amount");
					BigDecimal amount = new BigDecimal(amountChoice);

					try {
						currentBalance = accountService.getUserBalance();
						
					} catch (RestClientResponseException rcre) {
						
						throw new AccountServiceException(rcre.getRawStatusCode() + " : " + rcre.getResponseBodyAsString());

					}
					if (currentBalance.compareTo(amount) < 0) {
						System.out.println("*** Insufficient funds ***");
					} else {

						TransferService transferService = new TransferService(API_BASE_URL, currentUser);

						transferService.sendTransfer(toUserId, amount);

						System.out.println("**********************");
						System.out.println("* Transfer Complete! *");
						System.out.println("**********************");
						
					}
				

			} catch (NumberFormatException nfe) {
				System.out.println("*** Please enter a valid number ***");
			}
		} catch (AccountServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransfersServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void requestBucks() {
		// TODO Auto-generated method stub

	}

	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
		while (!isRegistered) // will keep looping until user is registered
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) // will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
				System.out.println("\n" + currentUser.getUser().getUsername() + " is now logged in!");
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
