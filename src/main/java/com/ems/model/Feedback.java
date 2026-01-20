package com.ems.model;

import java.time.LocalDateTime;

public class Feedback {
	private int feedbackId;
	private int eventId;
	private int userId;
	private int rating;
	private String comments;
	private LocalDateTime submittedAt;
	/**
	 * @param feedbackId
	 * @param eventId
	 * @param userId
	 * @param rating
	 * @param comments
	 * @param submittedAt
	 */
	public Feedback(int feedbackId, int eventId, int userId, int rating, String comments, LocalDateTime submittedAt) {
		this.feedbackId = feedbackId;
		this.eventId = eventId;
		this.userId = userId;
		this.rating = rating;
		this.comments = comments;
		this.submittedAt = submittedAt;
	}
	public int getFeedbackId() {
		return feedbackId;
	}
	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}
	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}
	
	
}
