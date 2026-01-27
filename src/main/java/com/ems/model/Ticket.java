package com.ems.model;


public class Ticket {
	private int ticketId;
	private int eventId;
	private String ticketType;
	private double price;
	private int totalQuantity;
	private int availableQuantity;
	/**
	 * @param ticketId
	 * @param eventId
	 * @param ticketType
	 * @param price
	 * @param totalQuantity
	 * @param availableQuantity
	 */
	public Ticket(int ticketId, int eventId, String ticketType, double price, int totalQuantity,
			int availableQuantity) {
		this.ticketId = ticketId;
		this.eventId = eventId;
		this.ticketType = ticketType;
		this.price = price;
		this.totalQuantity = totalQuantity;
		this.availableQuantity = availableQuantity;
	}
	public Ticket() {
		// TODO Auto-generated constructor stub
	}
	public int getTicketId() {
		return ticketId;
	}
	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public String getTicketType() {
		return ticketType;
	}
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public int getAvailableQuantity() {
		return availableQuantity;
	}
	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}
	@Override
	public String toString() {
	    return String.format("Ticket [%d] %s | Price: %.2f | Available: %d/%d", 
	        ticketId, ticketType, price, availableQuantity, totalQuantity);
	}

	
}
