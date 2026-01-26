package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.dao.OfferDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Offer;
import com.ems.service.OfferService;
import com.ems.util.DateTimeUtil;

public class OfferServiceImpl implements OfferService {

    private final OfferDao offerDao;

    public OfferServiceImpl(OfferDao offerDao) {
    	this.offerDao = offerDao;
    }
    @Override
    public List<Offer> getAllOffers() {
        try {
            return offerDao.getAllOffers();
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int createOffer(int eventId,String code, Integer discount, LocalDateTime from, LocalDateTime to) {
    	Offer offer = new Offer();
    	offer.setEventId(eventId);
    	offer.setCode(code);
    	offer.setDiscountPercentage(discount);
    	offer.setValidFrom(from);
    	offer.setValidTo(to);


        try {
            return offerDao.createOffer(offer);
        } catch (DataAccessException e) {
            System.out.println(e);
        }
        return 0;
    }

    @Override
    public void assignOfferToEvent(int offerId, int eventId) {
        try {
            offerDao.assignOfferToEvent(offerId, eventId);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void toggleOfferStatus(int offerId, LocalDateTime validDate) {
        try {
        	offerDao.updateOfferActiveStatus(offerId, DateTimeUtil.convertLocalDefaultToUtc(validDate));
            
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Integer> getOfferUsageReport() {
        try {
            return offerDao.getOfferUsageReport();
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
