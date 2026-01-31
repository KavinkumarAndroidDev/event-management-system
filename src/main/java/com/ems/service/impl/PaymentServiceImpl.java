package com.ems.service.impl;

import com.ems.dao.EventDao;
import com.ems.dao.NotificationDao;
import com.ems.dao.PaymentDao;
import com.ems.enums.NotificationType;
import com.ems.enums.PaymentMethod;
import com.ems.exception.DataAccessException;
import com.ems.model.RegistrationResult;
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

	private final PaymentDao paymentDao;
	private final NotificationDao notificationDao;
	private final SystemLogService systemLogService;


	public PaymentServiceImpl(PaymentDao paymentDao,
			NotificationDao notificationDao, EventDao eventDao, SystemLogService systemLogService) {
		this.paymentDao = paymentDao;
		this.notificationDao = notificationDao;
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
			RegistrationResult result = paymentDao.registerForEvent(userId, eventId, ticketId, quantity, price, selectedMethod, offerCode);
			

			if (result.isSuccess()) {
			    systemLogService.log(
	    		        userId,
	    		        "REGISTER",
	    		        "EVENT",
	    		        eventId,
	    		        "User registered for event using ticket " + ticketId +
	    		        (offerCode != null ? " with offer code " + offerCode : "")
	    		    );

			    notificationDao.sendNotification(
			        userId,
			        "Registration confirmed. Amount paid: ₹" + result.getFinalAmount(),
			        NotificationType.EVENT.name()
			    );
			} else {
			    systemLogService.log(
			        userId,
			        "REGISTER_FAILED",
			        "EVENT",
			        eventId,
			        result.getMessage()
			    );
			}
			return result.isSuccess();

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
}
