package com.ems.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.ems.dao.PaymentDao;
import com.ems.enums.PaymentMethod;
import com.ems.exception.DataAccessException;
import com.ems.model.RegistrationResult;
import com.ems.util.DBConnectionUtil;

/*
 * Handles database operations related to payments.
 *
 * Responsibilities:
 * - Persist payment transactions for registrations
 * - Record payment method, amount, and status
 */
public class PaymentDaoImpl implements PaymentDao {
	@Override
	public boolean processPayment(int regId, double totalAmount, String paymentMethod) 
			throws DataAccessException {
		
		String sql = "insert into payments (registration_id, amount, payment_method, payment_status, created_at) " +
		             "values(?, ?, ?, 'SUCCESS', utc_timestamp())";
		
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, regId);
			ps.setDouble(2, totalAmount);
			ps.setString(3, paymentMethod);
		
			
			int updatedRows = ps.executeUpdate();
			
			if (updatedRows == 0) {
				throw new DataAccessException("Error while updating payment!");
			}
			
			return true;
			
		} catch (SQLException e) {
			throw new DataAccessException("Database error while processing payment");
		}
	}
	
	@Override
	public boolean processPayment(int regId, double totalAmount, String paymentMethod, int offerId) 
			throws DataAccessException {
		
		String sql = "insert into payments (registration_id, amount, payment_method, payment_status, created_at, offer_id) " +
		             "values(?, ?, ?, 'SUCCESS', utc_timestamp(), ?)";
		
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			
			ps.setInt(1, regId);
			ps.setDouble(2, totalAmount);
			ps.setString(3, paymentMethod);
			ps.setInt(4, offerId);
			
			int updatedRows = ps.executeUpdate();
			
			if (updatedRows == 0) {
				throw new DataAccessException("Error while updating payment!");
			}
			
			return true;
			
		} catch (SQLException e) {
			throw new DataAccessException("Database error while processing payment");
		}
	}
	
	@Override
	public RegistrationResult registerForEvent(
	        int userId,
	        int eventId,
	        int ticketId,
	        int quantity,
	        double price,
	        PaymentMethod paymentMethod,
	        String offerCode
	) throws DataAccessException {

	    String sql = "{ CALL sp_register_for_event(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

	    RegistrationResult result = new RegistrationResult();

	    try (Connection con = DBConnectionUtil.getConnection();
	         CallableStatement cs = con.prepareCall(sql)) {

	        // IN params
	        cs.setInt(1, userId);
	        cs.setInt(2, eventId);
	        cs.setInt(3, ticketId);
	        cs.setInt(4, quantity);
	        cs.setDouble(5, price);
	        cs.setString(6, paymentMethod.name());
	        cs.setString(7, offerCode);

	        // OUT params
	        cs.registerOutParameter(8, Types.BOOLEAN);     // o_success
	        cs.registerOutParameter(9, Types.VARCHAR);     // o_message
	        cs.registerOutParameter(10, Types.INTEGER);    // o_registration_id
	        cs.registerOutParameter(11, Types.DECIMAL);    // o_final_amount

	        cs.execute();

	        result.setSuccess(cs.getBoolean(8));
	        result.setMessage(cs.getString(9));
	        result.setRegistrationId(cs.getInt(10));
	        result.setFinalAmount(cs.getDouble(11));

	        return result;

	    } catch (SQLException e) {
	        throw new DataAccessException("Failed to execute registration procedure", e);
	    }
	}

	
	@Override
	public void updatePaymentStatus(int registrationId) throws DataAccessException {

	    String sql = "UPDATE payments "
	               + "SET payment_status = ? "
	               + "WHERE registration_id = ? "
	               + "AND payment_status = ?";

	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, "REFUNDED");
	        ps.setInt(2, registrationId);
	        ps.setString(3, "SUCCESS");

	        int updatedRows = ps.executeUpdate();

	        if (updatedRows == 0) {
	            throw new DataAccessException(
	                "No successful payment found to refund for registration id: " + registrationId
	            );
	        }

	    } catch (SQLException e) {
	        throw new DataAccessException(
	            "Database error while updating payment status for registration id: " + registrationId,
	            e
	        );
	    }
	}

}