package com.ems.model;

public class RegistrationTicket {
	private int id;
	private int registrationId;
	private int ticketId;
	private int quantity;
	/**
	 * @param id
	 * @param registrationId
	 * @param ticketId
	 * @param quantity
	 */
	public RegistrationTicket(int id, int registrationId, int ticketId, int quantity) {
		this.id = id;
		this.registrationId = registrationId;
		this.ticketId = ticketId;
		this.quantity = quantity;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}
	public int getTicketId() {
		return ticketId;
	}
	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
}
