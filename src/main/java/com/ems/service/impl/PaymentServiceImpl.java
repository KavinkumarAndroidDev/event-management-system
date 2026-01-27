package com.ems.service.impl;

import com.ems.dao.EventDao;
import com.ems.dao.NotificationDao;
import com.ems.dao.OfferDao;
import com.ems.dao.PaymentDao;
import com.ems.dao.RegistrationDao;
import com.ems.dao.TicketDao;
import com.ems.enums.NotificationType;
import com.ems.enums.PaymentMethod;
import com.ems.model.Offer;
import com.ems.model.Ticket;
import com.ems.service.PaymentService;
import com.ems.service.SystemLogService;

/*
 * Handles payment and registration processing.
 *
 * Responsibilities:
 * - Coordinate event registration and ticket allocation
 * - Process payments and update ticket availability
 * - Trigger notifications upon successful registration
 */
public class PaymentServiceImpl implements PaymentService {

	private final RegistrationDao registrationDao;
	private final TicketDao ticketDao;
	private final PaymentDao paymentDao;
	private final NotificationDao notificationDao;
	private final EventDao eventDao;
	private final OfferDao offerDao;
	private final SystemLogService systemLogService;


	public PaymentServiceImpl(RegistrationDao registrationDao, TicketDao ticketDao, PaymentDao paymentDao,
			NotificationDao notificationDao, EventDao eventDao, OfferDao offerDao, SystemLogService systemLogService) {
		this.registrationDao = registrationDao;
		this.ticketDao = ticketDao;
		this.paymentDao = paymentDao;
		this.notificationDao = notificationDao;
		this.eventDao = eventDao;
		this.offerDao = offerDao;
		this.systemLogService = systemLogService;
	}

	/*
	 * Processes complete event registration including payment.
	 *
	 * Flow: - Validate ticket availability - Create registration and reserve
	 * tickets - Process payment - Confirm registration and notify user
	 *
	 * Rules: - Registration fails if requested quantity exceeds availability -
	 * Payment failure triggers registration rollback
	 */
	@Override
	public boolean processRegistration(int userId, int eventId, int ticketId, int quantity, double price,
			PaymentMethod selectedMethod, String offerCode) {
		try {
			Ticket ticket = ticketDao.getTicketById(ticketId);

			// Prevent overbooking beyond available ticket quantity
			if (ticket.getAvailableQuantity() < quantity) {
				System.out.println("\nQuantity is more than available tickets");
				return false;
			}
			
			Offer offer = null;
			double discountPercentage = 0;

			if (offerCode != null && !offerCode.isBlank()) {
			    offer = offerDao.getValidOfferForEvent(eventId, offerCode);
			    if (offer == null) {
			        System.out.println("Invalid or expired offer code");
			        return false;
			    }
			    discountPercentage = offer.getDiscountPercentage() != null
			            ? offer.getDiscountPercentage()
			            : 0;

			}



			
			int regId = registrationDao.createRegistration(userId, eventId);
			registrationDao.addRegistrationTickets(regId, ticketId, quantity);

			double baseAmount = price * quantity;
			double discountAmount = (baseAmount * discountPercentage) / 100;
			double finalAmount = baseAmount - discountAmount;

			boolean paymentSuccess = paymentDao.processPayment(regId, finalAmount, selectedMethod.toString(), offer != null ? offer.getOfferId() : null);

			// Rollback registration if payment fails
			if (!paymentSuccess) {
				registrationDao.removeRegistrationTickets(regId, ticketId);
				registrationDao.removeRegistrations(regId);
				systemLogService.log(
					    userId,
					    "PAYMENT_FAILED",
					    "EVENT",
					    eventId,
					    "Payment failed for registration ID " + regId
					);

				return false;
			}
			if (offer != null) {
			    offerDao.recordOfferUsage(
			        offer.getOfferId(),
			        userId,
			        regId
			    );
			}

			// Deduct confirmed ticket quantity after successful payment
			ticketDao.updateAvailableQuantity(ticketId, -quantity);

			String notificationMessage =
				    "Your registration for " +
				    eventDao.getEventName(eventId) +
				    " is confirmed. Amount paid: ₹" +
				    finalAmount;

			notificationDao.sendNotification(userId, notificationMessage, NotificationType.EVENT.toString());
			
			systemLogService.log(
				    userId,
				    "REGISTER_SUCCESS",
				    "EVENT",
				    eventId,
				    "Registered successfully. Amount paid: ₹" + finalAmount +
				    (offer != null ? ", Offer used: " + offer.getCode() : "")
				);

			
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Transaction failed: " + e.getMessage());
			return false;
		}
	}
}
