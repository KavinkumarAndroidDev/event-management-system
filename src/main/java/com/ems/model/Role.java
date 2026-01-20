package com.ems.model;

import java.time.LocalDateTime;

public class Role {
	private int roleId;
	private String roleName;
	private LocalDateTime createdAt;
	/**
	 * @param roleId
	 * @param roleName
	 * @param createdAt
	 */
	public Role(int roleId, String roleName, LocalDateTime createdAt) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.createdAt = createdAt;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
}
