package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ems.dao.OfferDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Offer;
import com.ems.service.OfferService;
import com.ems.service.SystemLogService;
import com.ems.util.DateTimeUtil;

/*
 * Handles offer and promotion related business operations.
 *
 * Responsibilities:
 * - Create and manage promotional offers
 * - Control offer activation and validity
 * - Generate offer usage reports
 */
public class OfferServiceImpl implements OfferService {

	private final OfferDao offerDao;
	private final SystemLogService systemLogService;


	/*
	 * Initializes offer service with required data access dependency.
	 */
	public OfferServiceImpl(OfferDao offerDao, SystemLogService systemLogService) {
		this.offerDao = offerDao;
		this.systemLogService = systemLogService;
	}

	/*
	 * Retrieves all offers available in the system.
	 *
	 * Used by admin for offer management and reporting.
	 */
	@Override
	public List<Offer> getAllOffers() {
		try {
			return offerDao.getAllOffers();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());;
		}
		return new ArrayList<>();
	}

	/*
	 * Creates a new promotional offer for an event.
	 *
	 * Rules: - Offer validity is defined by from and to dates - Discount is applied
	 * as a percentage
	 */
	@Override
	public int createOffer(int eventId, String code, Integer discount, LocalDateTime from, LocalDateTime to) {
		Offer offer = new Offer();
		offer.setEventId(eventId);
		offer.setCode(code);
		offer.setDiscountPercentage(discount);
		offer.setValidFrom(from);
		offer.setValidTo(to);

		try {
			int offerId = offerDao.createOffer(offer);

			systemLogService.log(
			    null,
			    "CREATE",
			    "OFFER",
			    offerId,
			    "Offer created with code: " + code
			);

			return offerId;

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	/*
	 * Activates or deactivates an offer by updating its validity.
	 *
	 * Rule: - Valid date is stored in UTC for consistency
	 */
	@Override
	public void toggleOfferStatus(int offerId, LocalDateTime validDate) {
		try {
			offerDao.updateOfferActiveStatus(
				    offerId,
				    DateTimeUtil.convertLocalDefaultToUtc(validDate)
				);

				systemLogService.log(
				    null,
				    "UPDATE",
				    "OFFER",
				    offerId,
				    "Offer validity updated. New valid_to: " + validDate
				);


		} catch (DataAccessException e) {
			System.out.println(e.getMessage());;
		}
	}

	/*
	 * Generates a usage report for all offers.
	 *
	 * Used to analyze offer effectiveness.
	 */
	@Override
	public Map<String, Integer> getOfferUsageReport() {
		try {
			return offerDao.getOfferUsageReport();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());;
		}
		return new HashMap<>();
	}
}
