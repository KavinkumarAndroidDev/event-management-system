package com.ems.model;

import java.time.LocalDateTime;

public class EventRegistrationReport {
    private String eventTitle;
    private String userName;
    private String ticketType;
    private int quantity;
    private LocalDateTime registrationDate;
	public String getEventTitle() {
		return eventTitle;
	}
	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTicketType() {
		return ticketType;
	}
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}
	@Override
	public String toString() {
	    return
	        "Event        : " + eventTitle + "\n" +
	        "User         : " + userName + "\n" +
	        "Ticket Type  : " + ticketType + "\n" +
	        "Quantity     : " + quantity + "\n" +
	        "Registered On: " +
	        com.ems.util.DateTimeUtil.formatDateTime(registrationDate) + "\n" +
	        "------------------------------------------";
	}

	
}
