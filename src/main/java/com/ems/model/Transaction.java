package com.ems.model;

import java.time.LocalDateTime;

public class Transaction {
	private int transactionId;
	private int paymentId;
	private String transactionRef;
	private String transactionStatus;
	private LocalDateTime transactionTime;
	/**
	 * @param transactionId
	 * @param paymentId
	 * @param transactionRef
	 * @param transactionStatus
	 * @param transactionTime
	 */
	public Transaction(int transactionId, int paymentId, String transactionRef, String transactionStatus,
			LocalDateTime transactionTime) {
		this.transactionId = transactionId;
		this.paymentId = paymentId;
		this.transactionRef = transactionRef;
		this.transactionStatus = transactionStatus;
		this.transactionTime = transactionTime;
	}
	public int getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}
	public int getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}
	public String getTransactionRef() {
		return transactionRef;
	}
	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}
	public String getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	public LocalDateTime getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(LocalDateTime transactionTime) {
		this.transactionTime = transactionTime;
	}
	
	
}
