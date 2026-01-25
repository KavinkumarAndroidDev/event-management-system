package com.ems.util;

import com.ems.dao.impl.*;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.service.PaymentService;
import com.ems.service.UserService;
import com.ems.service.impl.*;

public final class ApplicationUtil {
	//So basically application util class helps to share the service 
    private static final EventService eventService;
    private static final NotificationService notificationService;
    private static final PaymentService paymentService;
    private static final UserService userService;
    private static final AdminService adminService;

    static {
        NotificationDaoImpl notificationDao = new NotificationDaoImpl();
        EventDaoImpl eventDao = new EventDaoImpl();
        TicketDaoImpl ticketDao = new TicketDaoImpl();
        CategoryDaoImpl categoryDao = new CategoryDaoImpl();
        VenueDaoImpl venueDao = new VenueDaoImpl();
        RegistrationDaoImpl registrationDao = new RegistrationDaoImpl();
        PaymentDaoImpl paymentDao = new PaymentDaoImpl();
        UserDaoImpl userDao = new UserDaoImpl();
        RoleDaoImpl roleDao = new RoleDaoImpl();
        FeedbackDaoImpl feedbackDao = new FeedbackDaoImpl();

        notificationService =
            new NotificationServiceImpl(notificationDao, registrationDao);

        paymentService =
            new PaymentServiceImpl(
                registrationDao,
                ticketDao,
                paymentDao,
                notificationDao,
                eventDao
            );

        eventService =
            new EventServiceImpl(
                eventDao,
                categoryDao,
                venueDao,
                ticketDao,
                paymentService,
                feedbackDao
            );

        userService =
            new UserServiceImpl(
                userDao,
                roleDao,
                eventService
            );

        adminService =
            new AdminServiceImpl(
                userDao,
                eventDao,
                notificationDao,
                registrationDao,
                categoryDao,
                venueDao,
                notificationService,
                eventService
            );
    }

    public static AdminService adminService() {
        return adminService;
    }

    public static EventService eventService() {
        return eventService;
    }

    public static UserService userService() {
        return userService;
    }
    
    public static NotificationService notificationService() {
    	return notificationService;
    }
}
