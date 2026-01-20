package com.ems.model;

import java.time.LocalDateTime;

public class Offer {
	private int offerId;
	private int eventId;
	private String code;
	private Integer discountPercentage;
	private LocalDateTime validFrom;
	private LocalDateTime validTo;
	/**
	 * @param offerId
	 * @param eventId
	 * @param code
	 * @param discountPercentage
	 * @param validFrom
	 * @param validTo
	 */
	public Offer(int offerId, int eventId, String code, Integer discountPercentage, LocalDateTime validFrom,
			LocalDateTime validTo) {
		this.offerId = offerId;
		this.eventId = eventId;
		this.code = code;
		this.discountPercentage = discountPercentage;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}
	public int getOfferId() {
		return offerId;
	}
	public void setOfferId(int offerId) {
		this.offerId = offerId;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(Integer discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	public LocalDateTime getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(LocalDateTime validFrom) {
		this.validFrom = validFrom;
	}
	public LocalDateTime getValidTo() {
		return validTo;
	}
	public void setValidTo(LocalDateTime validTo) {
		this.validTo = validTo;
	}
	
	
}
