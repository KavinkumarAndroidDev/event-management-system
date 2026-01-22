package com.ems.model;

import java.time.LocalDateTime;

public class Venue {
	private int venueId;
	private String name;
	private String street;
	private String city;
	private String state;
	private String pincode;
	private int maxCapacity;
	private LocalDateTime createdAt;
	private LocalDateTime updateAt;
	/**
	 * @param venueId
	 * @param name
	 * @param street
	 * @param city
	 * @param state
	 * @param pincode
	 * @param maxCapacity
	 * @param createdAt
	 * @param updateAt
	 */
	public Venue(int venueId, String name, String street, String city, String state, String pincode, int maxCapacity,
			LocalDateTime createdAt, LocalDateTime updateAt) {
		this.venueId = venueId;
		this.name = name;
		this.street = street;
		this.city = city;
		this.state = state;
		this.pincode = pincode;
		this.maxCapacity = maxCapacity;
		this.createdAt = createdAt;
		this.updateAt = updateAt;
	}
	public Venue() {
		// TODO Auto-generated constructor stub
	}
	public int getVenueId() {
		return venueId;
	}
	public void setVenueId(int venueId) {
		this.venueId = venueId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public int getMaxCapacity() {
		return maxCapacity;
	}
	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}
	
	
}
