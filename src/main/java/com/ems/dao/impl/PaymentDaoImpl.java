package com.ems.dao.impl;

public class PaymentDaoImpl {

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
