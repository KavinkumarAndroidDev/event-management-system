package com.ems.model;

public class Category {
	private int categoryId;
	private String name;
	private int isActive;
	/**
	 * @param name 
	 * @param categoryId 
	 */
	public Category(int categoryId, String name) {
		this.categoryId = categoryId;
		this.name = name;
	}
	public Category(int categoryId, String name, int isActive) {
		this.categoryId = categoryId;
		this.name = name;
		this.isActive = isActive;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	
}
