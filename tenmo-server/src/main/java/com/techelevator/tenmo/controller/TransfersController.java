package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.techelevator.tenmo.dao.TransfersDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Transfers;


@PreAuthorize("isAuthenticated()")
@RestController
public class TransfersController {
	

	private TransfersDAO dao;
	private UserDAO userDao;
	
	

	public TransfersController(TransfersDAO dao, UserDAO userDao) {

		this.dao = dao;
		this.userDao = userDao;
	}

	// listTransfers()

	@RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
	public List<Transfers> list(@PathVariable Long id) {
		return dao.listTransfers(id);
	}

	// sendTransfer() from user
	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(path = "/transfers/{id}/send", method = RequestMethod.POST)
	// Principal obj = user																						part of Spring - look into JWT without getting attached
	public Transfers sendTransfers(@Valid @RequestBody Transfers transfer, @PathVariable("id") Long fromUserId, Principal p) {

		if (fromUserId == userDao.findIdByUsername(p.getName())) {
			// data validation
		 
		BigDecimal amount = transfer.getAmount();
		Long toUserId = transfer.getToUserId();
		return dao.createSendTransfer(fromUserId, toUserId, amount);
		} else {
			// throw exception
			return null;
		}
	}

	
	// listTransferDetails()

	@RequestMapping(path = "/transfers/{id}/details/{transferID}", method = RequestMethod.GET)
	public Transfers transferDetails(@Valid @PathVariable("id") Long UserID, @PathVariable Long transferID) {

		return dao.getTransferById(transferID, UserID);
	}

}
