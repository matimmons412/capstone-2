package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.User;



@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(path = "user")
public class AccountController {
	
	private AccountDAO dao;
	private UserDAO userDAO;

	public AccountController(AccountDAO dao, UserDAO userDAO) {
		this.dao = dao;
		this.userDAO = userDAO;
	}

	
	@RequestMapping(path = "/{id}/balance", method = RequestMethod.GET)
	public BigDecimal getUserBalance(@PathVariable Long id) {
		return dao.getBalance(id);
	}
	
	
	@RequestMapping(path = "/listUsers", method = RequestMethod.GET)
	public List<User> userList() {

			List<User> users = new ArrayList<>();
			for(User user : userDAO.findAll()) {
				User updatedUser = new User();
				updatedUser.setId(user.getId());
				updatedUser.setUsername(user.getUsername());
				
				users.add(updatedUser);
			}
			return users;
		}
	
}
