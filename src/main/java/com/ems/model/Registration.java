package com.ems.model;

import java.time.LocalDateTime;

public class Registration {
	private int registrationId;
	private int userId;
	private int eventId;
	private LocalDateTime registratinoDate;
	private String status;
	/**
	 * @param registrationId
	 * @param userId
	 * @param eventId
	 * @param registratinoDate
	 * @param status
	 */
	public Registration(int registrationId, int userId, int eventId, LocalDateTime registratinoDate, String status) {
		this.registrationId = registrationId;
		this.userId = userId;
		this.eventId = eventId;
		this.registratinoDate = registratinoDate;
		this.status = status;
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
	public LocalDateTime getRegistratinoDate() {
		return registratinoDate;
	}
	public void setRegistratinoDate(LocalDateTime registratinoDate) {
		this.registratinoDate = registratinoDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
