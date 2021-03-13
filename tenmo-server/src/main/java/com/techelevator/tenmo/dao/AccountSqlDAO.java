package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Accounts;

@Component
public class AccountSqlDAO implements AccountDAO{
	
	private JdbcTemplate jdbcTemplate;

	public AccountSqlDAO(JdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}


	@Override
	public BigDecimal getBalance(Long userId) {
		String sqlGetBalance = "SELECT * FROM accounts WHERE user_id = ?"; 
//				"(SELECT user_id FROM users WHERE username = ?)"; 
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetBalance, userId);
		Accounts account = new Accounts();
		while (results.next()) {
			account = mapRowToAccounts(results);
		}
		
		return account.getBalance();
	}

	
	private Accounts mapRowToAccounts(SqlRowSet rs) {
        Accounts account = new Accounts();
        account.setId(rs.getLong("account_id"));
        account.setUserId(rs.getLong("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }


}
