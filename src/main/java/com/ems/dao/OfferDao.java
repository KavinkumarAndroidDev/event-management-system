package com.ems.dao;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.ems.exception.DataAccessException;
import com.ems.model.Offer;

public interface OfferDao {

    List<Offer> getAllOffers() throws DataAccessException;

    int createOffer(Offer offer) throws DataAccessException;

    void assignOfferToEvent(int offerId, int eventId) throws DataAccessException;

    void updateOfferActiveStatus(int offerId,  Instant validDate) throws DataAccessException;

    Map<String, Integer> getOfferUsageReport() throws DataAccessException;

}

