package com.ems.service.impl;

import com.ems.dao.EventDao;
import com.ems.dao.NotificationDao;
import com.ems.dao.PaymentDao;
import com.ems.dao.RegistrationDao;
import com.ems.dao.TicketDao;
import com.ems.enums.NotificationType;
import com.ems.enums.PaymentMethod;
import com.ems.model.Ticket;
import com.ems.service.PaymentService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class PaymentServiceImpl implements PaymentService {

    private final RegistrationDao registrationDao;
    private final TicketDao ticketDao;
    private final PaymentDao paymentDao;
    private final NotificationDao notificationDao;
    private final EventDao eventDao;

    public PaymentServiceImpl(
            RegistrationDao registrationDao,
            TicketDao ticketDao,
            PaymentDao paymentDao,
            NotificationDao notificationDao,
            EventDao eventDao
    ) {
        this.registrationDao = registrationDao;
        this.ticketDao = ticketDao;
        this.paymentDao = paymentDao;
        this.notificationDao = notificationDao;
        this.eventDao = eventDao;
    }

    @Override
    public boolean processRegistration(
            int userId,
            int eventId,
            int ticketId,
            int quantity,
            double price,
            PaymentMethod selectedMethod
    ) {
        try {
            Ticket ticket = ticketDao.getTicketById(ticketId);
            if (ticket.getAvailableQuantity() < quantity) {
            	System.out.println("\nQuantity is more than available tickets");
                return false;
            }

            int regId = registrationDao.createRegistration(userId, eventId);
            registrationDao.addRegistrationTickets(regId, ticketId, quantity);

            double totalAmount = price * quantity;

            boolean paymentSuccess =
                paymentDao.processPayment(
                    regId,
                    totalAmount,
                    selectedMethod.toString()
                );

            if (!paymentSuccess) {
                registrationDao.removeRegistrations(regId);
                registrationDao.removeRegistrationTickets(regId, ticketId);
                return false;
            }

            ticketDao.updateAvailableQuantity(ticketId, -quantity);

            String notificationMessage =
                "your registration for "
              + eventDao.getEventName(eventId)
              + " is confirmed. ";

            notificationDao.sendNotification(
                userId,
                notificationMessage,
                NotificationType.EVENT.toString()
            );

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Transaction failed: " + e.getMessage());
            return false;
        }
    }
}
