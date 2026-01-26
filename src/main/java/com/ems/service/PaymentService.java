package com.ems.service;

import com.ems.enums.PaymentMethod;

public interface PaymentService {

    // payment & registration
	public boolean processRegistration(
		    int userId,
		    int eventId,
		    int ticketId,
		    int quantity,
		    double price,
		    PaymentMethod selectedMethod,
		    String offerCode
		);

}
