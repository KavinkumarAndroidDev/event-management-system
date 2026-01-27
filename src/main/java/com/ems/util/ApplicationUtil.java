package com.ems.util;

import com.ems.dao.impl.CategoryDaoImpl;
import com.ems.dao.impl.EventDaoImpl;
import com.ems.dao.impl.FeedbackDaoImpl;
import com.ems.dao.impl.NotificationDaoImpl;
import com.ems.dao.impl.OfferDaoImpl;
import com.ems.dao.impl.PaymentDaoImpl;
import com.ems.dao.impl.RegistrationDaoImpl;
import com.ems.dao.impl.RoleDaoImpl;
import com.ems.dao.impl.SystemLogDaoImpl;
import com.ems.dao.impl.TicketDaoImpl;
import com.ems.dao.impl.UserDaoImpl;
import com.ems.dao.impl.VenueDaoImpl;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.service.OfferService;
import com.ems.service.OrganizerService;
import com.ems.service.PaymentService;
import com.ems.service.SystemLogService;
import com.ems.service.UserService;
import com.ems.service.impl.*;

/*
 * Centralized factory for application-wide service instances.
 *
 * Responsibilities:
 * - Create and wire DAO and Service dependencies
 * - Provide shared singleton service objects
 * - Act as a lightweight dependency container
 *
 * This avoids repeated object creation and keeps
 * service initialization consistent across menus.
 */
public final class ApplicationUtil {

    private static final EventService eventService;
    private static final NotificationService notificationService;
    private static final PaymentService paymentService;
    private static final UserService userService;
    private static final AdminService adminService;
    private static final OfferService offerService;
    private static final SystemLogService systemLogService;
    private static final OrganizerService organizerService;

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
        OfferDaoImpl offerDao = new OfferDaoImpl();
        SystemLogDaoImpl systemLogDao = new SystemLogDaoImpl();
        
        systemLogService = new SystemLogServiceImpl(systemLogDao);
        notificationService =
            new NotificationServiceImpl(notificationDao, registrationDao, systemLogService);
        organizerService = new OrganizerServiceImpl(eventDao, ticketDao, registrationDao, notificationService, systemLogService);


        paymentService =
            new PaymentServiceImpl(
                registrationDao,
                ticketDao,
                paymentDao,
                notificationDao,
                eventDao,
                offerDao,
                systemLogService
            );

        eventService =
            new EventServiceImpl(
                eventDao,
                categoryDao,
                venueDao,
                ticketDao,
                paymentService,
                feedbackDao,
                systemLogService
            );

        userService =
            new UserServiceImpl(
                userDao,
                roleDao,
                systemLogService
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
                systemLogService
            );
        offerService = 
        		new OfferServiceImpl(
        				offerDao,systemLogService);
        
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
    public static OfferService offerService() {
    	return offerService;
    }

	public static OrganizerService organizerService() {
		return organizerService;
	}
	public static SystemLogService systemLogService() {
		return systemLogService;
	}
}
