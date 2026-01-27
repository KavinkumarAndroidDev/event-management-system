package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ems.dao.PaymentDao;
import com.ems.exception.DataAccessException;
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
}