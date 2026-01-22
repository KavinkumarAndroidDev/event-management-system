package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ems.dao.PaymentDao;
import com.ems.exception.DataAccessException;
import com.ems.util.DBConnectionUtil;

public class PaymentDaoImpl implements PaymentDao{

	@Override
	public boolean processPayment(int regId, double totalAmount, String paymentMethod) throws DataAccessException{
		String sql = "insert into payments (registration_id, amount, payment_method, payment_status, created_at) values(?, ?, ?, 'SUCCESS', now())";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, regId);
			ps.setDouble(2, totalAmount);
			ps.setString(3, paymentMethod);
			int updatedRows = ps.executeUpdate();
			if(updatedRows == 0) {
				throw new DataAccessException("Error while updating payment!: " );
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}


//public class PaymentDaoImpl implements PaymentDao {
//
//    @Override
//    public void save(Payment payment) {
//        String sql = "INSERT INTO payments (registration_id, amount, payment_method, payment_status, created_at) VALUES (?, ?, ?, ?, ?)";
//        try (Connection con = DBConnectionUtil.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, payment.getRegistrationId());
//            ps.setBigDecimal(2, payment.getAmount());
//            ps.setString(3, payment.getPaymentMethod());
//            ps.setString(4, payment.getPaymentStatus());
//            ps.setTimestamp(5, Timestamp.valueOf(payment.getCreatedAt()));
//
//            ps.executeUpdate();
//
//        } catch (SQLException e) {
//            throw new DataAccessException("Payment save failed", e);
//        }
//    }
//}
