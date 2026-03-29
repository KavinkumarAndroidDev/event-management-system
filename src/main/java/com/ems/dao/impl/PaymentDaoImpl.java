package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.ems.dao.PaymentDao;
import com.ems.enums.PaymentMethod;
import com.ems.enums.PaymentStatus;
import com.ems.enums.RegistrationStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.RegistrationResult;
import com.ems.util.DBConnectionUtil;

/*
 * Handles database operations related to payments.
 *
 * Responsibilities:
 * - Persist payment transactions for registrations
 * - Record payment method, amount, and status
 * - Handle refunds through status updates
 */
public class PaymentDaoImpl implements PaymentDao {

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
		RegistrationResult result = new RegistrationResult();
		result.setSuccess(false);

		Connection con = null;
		try {
			con = DBConnectionUtil.getConnection();
			con.setAutoCommit(false); // Start Transaction

			// Lock and validate ticket availability
			String lockTicketSql = "SELECT available_quantity FROM tickets WHERE ticket_id = ? FOR UPDATE";
			int availableQuantity = 0;
			try (PreparedStatement ps = con.prepareStatement(lockTicketSql)) {
				ps.setInt(1, ticketId);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						availableQuantity = rs.getInt("available_quantity");
					} else {
						con.rollback();
						result.setMessage("Ticket not found");
						return result;
					}
				}
			}

			if (availableQuantity < quantity) {
				con.rollback();
				result.setMessage("Insufficient tickets available");
				return result;
			}

			// Validate offer code if provided
			Integer offerId = null;
			int discount = 0;
			if (offerCode != null && !offerCode.trim().isEmpty()) {
				String offerSql = "SELECT offer_id, discount_percentage FROM offers " +
						"WHERE event_id = ? AND UPPER(code) = UPPER(?) " +
						"AND (valid_from IS NULL OR valid_from <= CURRENT_TIMESTAMP) " +
						"AND (valid_to IS NULL OR valid_to >= CURRENT_TIMESTAMP)";
				try (PreparedStatement ps = con.prepareStatement(offerSql)) {
					ps.setInt(1, eventId);
					ps.setString(2, offerCode.trim());
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							offerId = rs.getInt("offer_id");
							discount = rs.getInt("discount_percentage");
						} else {
							con.rollback();
							result.setMessage("Invalid or expired offer code");
							return result;
						}
					}
				}

				// Check if user already used this offer
				String usageSql = "SELECT 1 FROM offer_usages WHERE offer_id = ? AND user_id = ?";
				try (PreparedStatement ps = con.prepareStatement(usageSql)) {
					ps.setInt(1, offerId);
					ps.setInt(2, userId);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							con.rollback();
							result.setMessage("Offer code already used by user");
							return result;
						}
					}
				}
			}

			// Create registration record
			String regSql = "INSERT INTO registrations (user_id, event_id, registration_date, status) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
			int registrationId = 0;
			try (PreparedStatement ps = con.prepareStatement(regSql, Statement.RETURN_GENERATED_KEYS)) {
				ps.setInt(1, userId);
				ps.setInt(2, eventId);
				ps.setString(3, RegistrationStatus.CONFIRMED.name());
				ps.executeUpdate();
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						registrationId = rs.getInt(1);
					}
				}
			}

			// Link tickets to registration
			String regTicketSql = "INSERT INTO registration_tickets (registration_id, ticket_id, quantity) VALUES (?, ?, ?)";
			try (PreparedStatement ps = con.prepareStatement(regTicketSql)) {
				ps.setInt(1, registrationId);
				ps.setInt(2, ticketId);
				ps.setInt(3, quantity);
				ps.executeUpdate();
			}

			// Calculate amount and record payment
			double baseAmount = price * quantity;
			double discountAmount = (baseAmount * discount) / 100.0;
			double finalAmount = baseAmount - discountAmount;

			String paySql = "INSERT INTO payments (registration_id, amount, payment_method, payment_status, created_at, offer_id) "
					+
					"VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
			try (PreparedStatement ps = con.prepareStatement(paySql)) {
				ps.setInt(1, registrationId);
				ps.setDouble(2, finalAmount);
				ps.setString(3, paymentMethod.name());
				ps.setString(4, PaymentStatus.SUCCESS.name());
				if (offerId != null) {
					ps.setInt(5, offerId);
				} else {
					ps.setNull(5, Types.INTEGER);
				}
				ps.executeUpdate();
			}

			// Deduct ticket quantity
			String updateTicketSql = "UPDATE tickets SET available_quantity = available_quantity - ? WHERE ticket_id = ?";
			try (PreparedStatement ps = con.prepareStatement(updateTicketSql)) {
				ps.setInt(1, quantity);
				ps.setInt(2, ticketId);
				ps.executeUpdate();
			}

			// Record offer usage
			if (offerId != null) {
				String recordUsageSql = "INSERT INTO offer_usages (offer_id, user_id, registration_id, used_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
				try (PreparedStatement ps = con.prepareStatement(recordUsageSql)) {
					ps.setInt(1, offerId);
					ps.setInt(2, userId);
					ps.setInt(3, registrationId);
					ps.executeUpdate();
				}
			}

			con.commit(); // Commit
			result.setSuccess(true);
			result.setMessage("Registration successful");
			result.setRegistrationId(registrationId);
			result.setFinalAmount(finalAmount);
			return result;

		} catch (SQLException e) {
			if (con != null) {
				try {
					con.rollback();
				} catch (SQLException ex) {
					// Ignore rollback failure
				}
			}
			throw new DataAccessException("Failed to execute registration logic", e);
		} finally {
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
					// Ignore close failure
				}
			}
		}
	}

	@Override
	public void updatePaymentStatus(int registrationId)
			throws DataAccessException {

		// Marks a successful payment as refunded
		String sql = "UPDATE payments "
				+ "SET payment_status = ? "
				+ "WHERE registration_id = ? "
				+ "AND payment_status = ?";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, PaymentStatus.REFUNDED.name());
			ps.setInt(2, registrationId);
			ps.setString(3, PaymentStatus.SUCCESS.name());

			int updatedRows = ps.executeUpdate();

			if (updatedRows == 0) {
				throw new DataAccessException(
						"No successful payment found to refund for registration id: " + registrationId);
			}

		} catch (SQLException e) {
			throw new DataAccessException(
					"Database error while updating payment status for registration id: " + registrationId,
					e);
		}
	}

}