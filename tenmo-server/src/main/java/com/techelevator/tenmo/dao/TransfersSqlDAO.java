package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Accounts;
import com.techelevator.tenmo.model.Transfers;

@Component
public class TransfersSqlDAO implements TransfersDAO {

	private JdbcTemplate jdbcTemplate;
	
	
	public TransfersSqlDAO(JdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}

	
	public Transfers createSendTransfer(Long fromUserAcctId, Long toUserAcctId, BigDecimal amount) {

		if(updateSenderBalance(fromUserAcctId, amount) == false) {
			
			return null;
		} else {
			updateReceiverBalance(toUserAcctId, amount); 
			
			String sqlCreateTransfer = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
					"VALUES ((SELECT transfer_type_id FROM transfer_types WHERE transfer_type_desc ILIKE '%Send'), " + // Get the transfer_type_id of send 
					"(SELECT transfer_status_id FROM transfer_statuses WHERE transfer_status_desc ILIKE '%Approved'), " + // Get the transfer_status_id of pending
					"?, ?, ?)";
			 

			jdbcTemplate.update(sqlCreateTransfer, fromUserAcctId, toUserAcctId, amount);
		
			
			String sqlGetCreatedTransfer = "SELECT sender.username AS account_from, receiver.username AS account_to, transfers.transfer_id, transfer_types.transfer_type_desc, transfer_statuses.transfer_status_desc, transfers.amount " + 
					"FROM transfers " + 
					"JOIN transfer_types USING (transfer_type_id) " + 
					"JOIN transfer_statuses USING (transfer_status_id) " + 
					"JOIN accounts AS senderAcct ON transfers.account_from = senderAcct.account_id " + // sender
					"JOIN accounts AS receiverAcct ON transfers.account_to = receiverAcct.account_id " + // receiver
					"JOIN users AS sender ON senderAcct.user_id = sender.user_id " + 
					"JOIN users AS receiver ON receiverAcct.user_id = receiver.user_id " + 
					"WHERE receiverAcct.user_id = ?";
			
			
			SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetCreatedTransfer, toUserAcctId);
			Transfers newTransfer = new Transfers();
			while (results.next()) {
				newTransfer = mapRowToTransfer(results);

			}

			return newTransfer;
		}
		
	}

	@Override
	public List<Transfers> listTransfers(Long userId) {
		String sqlGetTransferList = "SELECT sender.username AS account_from, receiver.username AS account_to, transfers.transfer_id, transfer_types.transfer_type_desc, transfer_statuses.transfer_status_desc, transfers.amount " + 
				"FROM transfers " + 
				"JOIN transfer_types USING (transfer_type_id) " + 
				"JOIN transfer_statuses USING (transfer_status_id) " + 
				"JOIN accounts AS senderAcct ON transfers.account_from = senderAcct.account_id " + // sender
				"JOIN accounts AS receiverAcct ON transfers.account_to = receiverAcct.account_id " + // receiver
				"JOIN users AS sender ON senderAcct.user_id = sender.user_id " + 
				"JOIN users AS receiver ON receiverAcct.user_id = receiver.user_id " + 
				"WHERE senderAcct.user_id = ? OR receiverAcct.user_id = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetTransferList, userId, userId);
		
		List<Transfers> transferList = new ArrayList<>();
		Transfers transfer = new Transfers();
		while (results.next()) {
			transfer = mapRowToTransfer(results);
			transferList.add(transfer);
		}
		return transferList;
	}

	@Override
	public Transfers getTransferById(Long transferID, Long userId) {
		String sqlGetTransferById = "SELECT sender.username AS account_from, receiver.username AS account_to, transfers.transfer_id, transfer_types.transfer_type_desc, transfer_statuses.transfer_status_desc, transfers.amount " + 
				"FROM transfers " + 
				"JOIN transfer_types USING (transfer_type_id) " + 
				"JOIN transfer_statuses USING (transfer_status_id) " + 
				"JOIN accounts AS senderAcct ON transfers.account_from = senderAcct.account_id " + // sender
				"JOIN accounts AS receiverAcct ON transfers.account_to = receiverAcct.account_id " + // receiver
				"JOIN users AS sender ON senderAcct.user_id = sender.user_id " + 
				"JOIN users AS receiver ON receiverAcct.user_id = receiver.user_id " +  
				"WHERE transfers.transfer_id = ? AND (sender.user_id = ? OR receiver.user_id = ?)";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetTransferById, transferID, userId, userId);
		
		Transfers newTransfer = new Transfers();
		while (results.next()) {
			newTransfer = mapRowToTransfer(results);

		}

		return newTransfer;
	}
	
	public boolean updateSenderBalance(Long fromUserAcctId, BigDecimal amount) {
		String sqlGetSenderAccount = "SELECT * FROM accounts WHERE account_id = ?";

		SqlRowSet senderAccountResults = jdbcTemplate.queryForRowSet(sqlGetSenderAccount, fromUserAcctId);

		Accounts senderAccount = new Accounts();
		while (senderAccountResults.next()) {
			senderAccount = mapRowToAccounts(senderAccountResults);
		}

		BigDecimal newSenderBalance = null;
		if (amount.min(senderAccount.getBalance()) == amount) {
			newSenderBalance = senderAccount.getBalance().subtract(amount);
			
		} else {
			return false;
			// Maybe create and throw an insufficient funds exception?
			// System.out.println("Insufficient funds");
		}
		
		String updateFromBalance = "UPDATE accounts SET balance = ? WHERE account_id = ?";

		jdbcTemplate.update(updateFromBalance, newSenderBalance, fromUserAcctId);
		
		return true;
	}
	
	public boolean updateReceiverBalance(Long toUserAcctId, BigDecimal amount) {
		String sqlGetReceiverAccount = "SELECT * FROM accounts WHERE account_id = ?";

		SqlRowSet receiverAccountResults = jdbcTemplate.queryForRowSet(sqlGetReceiverAccount, toUserAcctId);
		
		Accounts receiverAccount = new Accounts();
		while (receiverAccountResults.next()) {
			receiverAccount = mapRowToAccounts(receiverAccountResults);
		}

		BigDecimal newReceiverBalance = receiverAccount.getBalance().add(amount);

		String updateToBalance = "UPDATE accounts SET balance = ? WHERE account_id = ?";

		jdbcTemplate.update(updateToBalance, newReceiverBalance, toUserAcctId);
		
		return true;
	}

	private Transfers mapRowToTransfer(SqlRowSet rs) {
		Transfers transfer = new Transfers();
		transfer.setId(rs.getLong("transfer_id"));
		transfer.setType(rs.getString("transfer_type_desc"));
		transfer.setStatus(rs.getString("transfer_status_desc"));
		transfer.setAccountFrom(rs.getString("account_from"));
		transfer.setAccountTo(rs.getString("account_to"));
		transfer.setAmount(rs.getBigDecimal("amount"));
		return transfer;
	}

	private Accounts mapRowToAccounts(SqlRowSet rs) {
        Accounts account = new Accounts();
        account.setId(rs.getLong("account_id"));
        account.setUserId(rs.getLong("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}