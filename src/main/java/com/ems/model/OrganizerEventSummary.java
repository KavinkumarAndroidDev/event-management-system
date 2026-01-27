package com.ems.model;

public class OrganizerEventSummary {
	private int eventId;
	private String title;
	private String status;
	private int totalTickets;
	private int bookedTickets;

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTotalTickets() {
		return totalTickets;
	}

	public void setTotalTickets(int totalTickets) {
		this.totalTickets = totalTickets;
	}

	public int getBookedTickets() {
		return bookedTickets;
	}

	public void setBookedTickets(int bookedTickets) {
		this.bookedTickets = bookedTickets;
	}

}
