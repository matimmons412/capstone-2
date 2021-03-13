package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.util.List;

import com.techelevator.tenmo.model.Transfers;

public interface TransfersDAO {

	Transfers createSendTransfer(Long fromUserId, Long toUserId, BigDecimal amount);
	
//	Transfers createReceiveTransfer(Long fromUserId, Long toUserId, Long amount);
	
	List<Transfers> listTransfers(Long userId);
	
	Transfers getTransferById(Long transferID, Long userId);

	
}
