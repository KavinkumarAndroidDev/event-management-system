package com.ems.service;

public interface PaymentService {

    boolean processRegistration(
            int userId,
            int eventId,
            int ticketId,
            int quantity,
            double price
    );
}
