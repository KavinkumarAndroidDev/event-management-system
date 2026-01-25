package com.ems.service;

import com.ems.enums.PaymentMethod;

public interface PaymentService {

    boolean processRegistration(
            int userId,
            int eventId,
            int ticketId,
            int quantity,
            double price,
            PaymentMethod selectedMethod
    );
}
