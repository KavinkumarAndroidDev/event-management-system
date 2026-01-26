package com.ems.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.model.Offer;

public interface OfferService {

    List<Offer> getAllOffers();

    int createOffer(
    	    int eventId,
    	    String code,
    	    Integer discount,
    	    LocalDateTime from,
    	    LocalDateTime to
    	);

    void assignOfferToEvent(int offerId, int eventId);

    void toggleOfferStatus(int offerId, LocalDateTime validDate);

    Map<String, Integer> getOfferUsageReport();
}

