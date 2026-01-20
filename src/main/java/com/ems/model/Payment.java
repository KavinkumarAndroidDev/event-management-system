package com.ems.model;

import java.time.LocalDateTime;

public class Payment {
	private int paymentId;
	private int registrationId;
	private double amount;
	private String paymentMethod;
	private String paymentStatus;
	private LocalDateTime createdAt;
	/**
	 * @param paymentId
	 * @param registrationId
	 * @param amount
	 * @param paymentMethod
	 * @param paymentStatus
	 * @param createdAt
	 */
	public Payment(int paymentId, int registrationId, double amount, String paymentMethod, String paymentStatus,
			LocalDateTime createdAt) {
		this.paymentId = paymentId;
		this.registrationId = registrationId;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
		this.createdAt = createdAt;
	}
	public int getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}
	public int getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
}
