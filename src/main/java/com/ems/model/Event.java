package com.ems.model;

import java.time.LocalDateTime;

// TODO: Implement the Comparable<Event> interface to enable natural sorting of events.
// The compareTo method should be implemented to sort events based on their StartDateTime.
// This will allow lists of events to be easily sorted using Collections.sort().
public class Event {
	private int eventId;
	private int organizerId;
	private String title;
	private String description;
	private int categoryId;
	private int venueId;
	private LocalDateTime StartDateTime;
	private LocalDateTime endDateTime;
	private int capacity;
	private String status;
	private int approvedBy;
	private LocalDateTime approvedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	/**
	 * @param eventId
	 * @param organizerId
	 * @param title
	 * @param description
	 * @param categoryId
	 * @param venueId
	 * @param startDateTime
	 * @param endDateTime
	 * @param capacity
	 * @param status
	 * @param approvedBy
	 * @param approvedAt
	 * @param createdAt
	 * @param updatedAt
	 */
	public Event(int eventId, int organizerId, String title, String description, int categoryId, int venueId,
			LocalDateTime startDateTime, LocalDateTime endDateTime, int capacity, String status, int approvedBy,
			LocalDateTime approvedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.eventId = eventId;
		this.organizerId = organizerId;
		this.title = title;
		this.description = description;
		this.categoryId = categoryId;
		this.venueId = venueId;
		StartDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.capacity = capacity;
		this.status = status;
		this.approvedBy = approvedBy;
		this.approvedAt = approvedAt;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public Event() {
		// TODO Auto-generated constructor stub
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public int getOrganizerId() {
		return organizerId;
	}
	public void setOrganizerId(int organizerId) {
		this.organizerId = organizerId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public int getVenueId() {
		return venueId;
	}
	public void setVenueId(int venueId) {
		this.venueId = venueId;
	}
	public LocalDateTime getStartDateTime() {
		return StartDateTime;
	}
	public void setStartDateTime(LocalDateTime startDateTime) {
		StartDateTime = startDateTime;
	}
	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(int approvedBy) {
		this.approvedBy = approvedBy;
	}
	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}
	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	@Override
	public String toString() {
		return "Event [eventId=" + eventId + ", organizerId=" + organizerId + ", title=" + title + ", description="
				+ description + ", categoryId=" + categoryId + ", venueId=" + venueId + ", StartDateTime="
				+ StartDateTime + ", endDateTime=" + endDateTime + ", capacity=" + capacity + ", status=" + status
				+ ", approvedBy=" + approvedBy + ", approvedAt=" + approvedAt + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}
	
	
}
