package com.ems.model;

import java.time.LocalDateTime;

import com.ems.util.DateTimeUtil;

public class Notification {
	private int notificationId;
	private int userId;
	private String message;
	private String type;
	private LocalDateTime createdAt;
	private boolean readStatus;
	/**
	 * @param notificationId
	 * @param userId
	 * @param message
	 * @param type
	 * @param createdAt
	 * @param readStatus
	 */
	public Notification(int notificationId, int userId, String message, String type, LocalDateTime createdAt,
			boolean readStatus) {
		this.notificationId = notificationId;
		this.userId = userId;
		this.message = message;
		this.type = type;
		this.createdAt = createdAt;
		this.readStatus = readStatus;
	}
	public Notification() {
		// TODO Auto-generated constructor stub
	}
	public int getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public boolean isReadStatus() {
		return readStatus;
	}
	public void setReadStatus(boolean readStatus) {
		this.readStatus = readStatus;
	}
	@Override
	public String toString() {
	    return "[" + type + "] " + message + " (" + DateTimeUtil.formatDateTime(createdAt) + ")";
	}

	
	
}
