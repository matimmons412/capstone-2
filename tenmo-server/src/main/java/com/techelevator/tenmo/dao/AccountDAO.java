package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

import com.techelevator.tenmo.model.User;

public interface AccountDAO {
	
	BigDecimal getBalance(Long id);
	

}
