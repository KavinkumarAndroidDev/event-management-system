package com.ems.dao;

import com.ems.enums.PaymentMethod;
import com.ems.exception.DataAccessException;
import com.ems.model.RegistrationResult;

public interface PaymentDao {

	boolean processPayment(int regId, double totalAmount, String string, int offerId) throws DataAccessException;

	void updatePaymentStatus(int registrationId) throws DataAccessException;

	boolean processPayment(int regId, double totalAmount, String paymentMethod) throws DataAccessException;

	RegistrationResult registerForEvent(int userId, int eventId, int ticketId, int quantity, double price,
			PaymentMethod paymentMethod, String offerCode) throws DataAccessException;

}
