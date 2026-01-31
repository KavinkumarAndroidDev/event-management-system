package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ems.dao.OfferDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Offer;
import com.ems.util.DBConnectionUtil;

/*
 * Handles database operations related to offers.
 *
 * Responsibilities:
 * - Retrieve available offers
 * - Persist offer creation and updates
 * - Associate offers with events
 * - Generate offer usage reports
 */
public class OfferDaoImpl implements OfferDao {


    @Override
    public List<Offer> getAllOffers() throws DataAccessException {
        List<Offer> offers = new ArrayList<>();

        String sql = "select offer_id, event_id, code, discount_percentage, valid_from, valid_to from offers";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Offer offer = new Offer();
                offer.setOfferId(rs.getInt("offer_id"));
                offer.setEventId(rs.getInt("event_id"));
                offer.setCode(rs.getString("code"));
                offer.setDiscountPercentage(rs.getInt("discount_percentage"));
                offer.setValidFrom(rs.getTimestamp("valid_from") != null
                        ? rs.getTimestamp("valid_from").toLocalDateTime()
                        : null);
                offer.setValidTo(rs.getTimestamp("valid_to") != null
                        ? rs.getTimestamp("valid_to").toLocalDateTime()
                        : null);
                if(offer.getValidTo().isAfter(LocalDateTime.now()) ) {
                	offers.add(offer);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to fetch offers");
        }

        return offers;
    }

    @Override
    public int createOffer(Offer offer) throws DataAccessException {
        String sql =
            "insert into offers (code, discount_percentage, valid_from, valid_to, event_id) " +
            "values (?, ?, ?, ?, ?)";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, offer.getCode().toUpperCase());
            ps.setObject(2, offer.getDiscountPercentage());
            ps.setTimestamp(3,
                offer.getValidFrom() != null ? Timestamp.valueOf(offer.getValidFrom()) : null);
            ps.setTimestamp(4,
                offer.getValidTo() != null ? Timestamp.valueOf(offer.getValidTo()) : null);
            ps.setInt(5, offer.getEventId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;

        } catch (Exception e) {
            throw new DataAccessException("Failed to create offer");
        }
    }

    @Override
    public void assignOfferToEvent(int offerId, int eventId) throws DataAccessException {
        String sql = "update offers set event_id = ? where offer_id = ?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, offerId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Failed to assign offer to event");
        }
    }

    @Override
    public void updateOfferActiveStatus(int offerId, Instant validDate) throws DataAccessException {
        String sql = "update offers set valid_to = ? where offer_id = ?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	
        	ps.setTimestamp(1, Timestamp.from(validDate));
            ps.setInt(2, offerId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Failed to update offer status");
        }
    }

    @Override
    public Map<String, Integer> getOfferUsageReport() throws DataAccessException {
        Map<String, Integer> report = new HashMap<>();

        String sql =
            "select o.code, count(ou.offer_usage_id) as usage_count " +
            "from offers o left join offer_usages ou on o.offer_id = ou.offer_id " +
            "group by o.offer_id, o.code";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                report.put(
                    rs.getString("code"),
                    rs.getInt("usage_count")
                );
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to fetch offer usage report");
        }

        return report;
    }

	@Override
	public Offer getValidOfferForEvent(int eventId, String code) throws DataAccessException {
	
	    String sql =
	        "SELECT * FROM offers " +
	        "WHERE event_id = ? " +
	        "AND UPPER(code) = ? " +
	        "AND (valid_from IS NULL OR valid_from <= NOW()) " +
	        "AND (valid_to IS NULL OR valid_to >= NOW())";
	
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	
	        ps.setInt(1, eventId);
	        ps.setString(2, code.trim().toUpperCase());
	
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                Offer offer = new Offer();
	                offer.setOfferId(rs.getInt("offer_id"));
	                offer.setEventId(rs.getInt("event_id"));
	                offer.setCode(rs.getString("code"));
	                offer.setDiscountPercentage(rs.getInt("discount_percentage"));
	
	                if (rs.getTimestamp("valid_from") != null) {
	                    offer.setValidFrom(
	                        rs.getTimestamp("valid_from").toLocalDateTime()
	                    );
	                }
	
	                if (rs.getTimestamp("valid_to") != null) {
	                    offer.setValidTo(
	                        rs.getTimestamp("valid_to").toLocalDateTime()
	                    );
	                }
	
	                return offer;
	            }
	        }
	
	    } catch (SQLException e) {
	        throw new DataAccessException("Failed to fetch offer for the event");
	    }
	
	    return null;
	}


	@Override
	public void recordOfferUsage(
	        int offerId,
	        int userId,
	        int registrationId
	) throws DataAccessException {

	    String sql =
	        "INSERT INTO offer_usages (offer_id, user_id, registration_id, used_at) " +
	        "VALUES (?, ?, ?, utc_timestamp())";
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, offerId);
	        ps.setInt(2, userId);
	        ps.setInt(3, registrationId);

	        ps.executeUpdate();

	    } catch (SQLException e) {
	        throw new DataAccessException("Failed to record offer usage");
	    }
	}

	@Override
	public boolean hasUserUsedOfferCode(int userId, int offerId) throws DataAccessException {
		String sql = "select * from offer_usages where user_id = ? and offer_id = ?";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, userId);
			ps.setInt(2, offerId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return false;
			}else {
				return true;
			}
		}catch(SQLException e) {
			throw new DataAccessException("Failed to fetch offer code usage");
		}
	}

}
