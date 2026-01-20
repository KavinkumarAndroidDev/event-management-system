package com.ems.model;

import java.time.LocalDateTime;

public class User {
	private int userId;
	private String fullName;
	private String email;
	private String phone;
	private String passwordHash;
	private int roleId;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	/**
	 * @param userId
	 * @param fullName
	 * @param email
	 * @param phone
	 * @param passwordHash
	 * @param roleId
	 * @param status
	 * @param createdAt
	 * @param updatedAt
	 */
	public User(String fullName, String email, String phone, String passwordHash, int roleId, String status,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.passwordHash = passwordHash;
		this.roleId = roleId;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	/**
	 * @param userId
	 * @param fullName
	 * @param email
	 * @param passwordHash
	 * @param roleId
	 * @param status
	 * @param createdAt
	 * @param updatedAt
	 */
	public User(String fullName, String email, String passwordHash, int roleId, String status,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.fullName = fullName;
		this.email = email;
		this.phone = null;
		this.passwordHash = passwordHash;
		this.roleId = roleId;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public User() {
		// TODO Auto-generated constructor stub
	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
		return "User [userId=" + userId + ", fullName=" + fullName + ", email=" + email + ", phone=" + phone
				+ ", passwordHash=" + passwordHash + ", roleId=" + roleId + ", status=" + status + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}
	
	
}
