package com.ems.actions;

import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;

public class FeedbackAction {
    private final EventService eventService;

    public FeedbackAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    public void submitRating(int userId, int eventId, int rating, String comments) {
        eventService.submitRating(userId, eventId, rating, comments);
    }
}