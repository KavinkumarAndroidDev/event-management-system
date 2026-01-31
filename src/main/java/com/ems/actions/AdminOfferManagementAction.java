package com.ems.actions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.model.Offer;
import com.ems.service.OfferService;
import com.ems.util.ApplicationUtil;

public class AdminOfferManagementAction {
    private final OfferService offerService;

    public AdminOfferManagementAction() {
        this.offerService = ApplicationUtil.offerService();
    }

    public List<Offer> getAllOffers() {
        return offerService.getAllOffers();
    }

    public int createOffer(int eventId, String code, int discount, LocalDateTime from, LocalDateTime to) {
        return offerService.createOffer(eventId, code, discount, from, to);
    }

    public void toggleOfferStatus(int offerId, LocalDateTime newValidTo) {
        offerService.toggleOfferStatus(offerId, newValidTo);
    }

    public Map<String, Integer> getOfferUsageReport() {
        return offerService.getOfferUsageReport();
    }
}