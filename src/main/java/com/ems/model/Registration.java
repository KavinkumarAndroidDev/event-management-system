package com.ems.model;

import java.time.LocalDateTime;

public class Registration {
	private int registrationId;
	private int userId;
	private int eventId;
	private LocalDateTime registrationDate;
	private LocalDateTime eventStartDate;
	private LocalDateTime eventEndDate;
	private String status;
	private String fullName;
	private String email;
	private String eventTitle;
	
	/**
	 * @param registrationId
	 * @param userId
	 * @param eventId
	 * @param registrationDate
	 * @param status
	 */
	public Registration(int registrationId, int userId, int eventId, LocalDateTime registrationDate, String status) {
		this.registrationId = registrationId;
		this.userId = userId;
		this.eventId = eventId;
		this.registrationDate = registrationDate;
		this.status = status;
	}
	public Registration() {
		// TODO Auto-generated constructor stub
	}
	public int getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public LocalDateTime getEventStartDate() {
		return eventStartDate;
	}
	public void setEventStartDate(LocalDateTime eventStartDate) {
		this.eventStartDate = eventStartDate;
	}
	public LocalDateTime getEventEndDate() {
		return eventEndDate;
	}
	public void setEventEndDate(LocalDateTime eventEndDate) {
		this.eventEndDate = eventEndDate;
	}
	public String getEventTitle() {
		return eventTitle;
	}
	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}
	
}
