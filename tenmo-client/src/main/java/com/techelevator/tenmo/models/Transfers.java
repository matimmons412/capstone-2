package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Transfers {

	private Long id;
	private String type;
	private String status;
	private String accountFrom;
	private String accountTo;
	private Long toUserId;
	private BigDecimal amount;

	public Long getToUserId() {
		return toUserId;
	}
	public void setToUserId(Long toUserId) {
		this.toUserId = toUserId;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAccountFrom() {
		return accountFrom;
	}

	public void setAccountFrom(String accountFrom) {
		this.accountFrom = accountFrom;
	}

	public String getAccountTo() {
		return accountTo;
	}

	public void setAccountTo(String accountTo) {
		this.accountTo = accountTo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		 
		return "Id: " + id + "\nFrom: " + accountFrom + "\nTo: " + accountTo + "\nType: " + type + 
				"\nStatus: " + status +  "\nAmount: " + amount;
	}
}