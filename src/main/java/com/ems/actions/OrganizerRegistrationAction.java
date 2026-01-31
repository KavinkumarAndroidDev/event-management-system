package com.ems.actions;

import java.util.List;
import java.util.Map;

import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

/**
 * Action class for organizer registration management operations.
 * Delegates business logic to appropriate services.
 */
public class OrganizerRegistrationAction {

    private final OrganizerService organizerService;

    public OrganizerRegistrationAction() {
        this.organizerService = ApplicationUtil.organizerService();
    }

    /**
     * Views the total number of registrations for a specific event.
     * 
     * @param eventId the ID of the event
     * @return the count of registrations
     */
    public int viewEventRegistrations(int eventId) {
        return organizerService.viewEventRegistrations(eventId);
    }

    /**
     * Views all registered users for a specific event.
     * 
     * @param eventId the ID of the event
     * @return list of maps containing user information (userId, name, email)
     */
    public List<Map<String, Object>> viewRegisteredUsers(int eventId) {
        return organizerService.viewRegisteredUsers(eventId);
    }
}
