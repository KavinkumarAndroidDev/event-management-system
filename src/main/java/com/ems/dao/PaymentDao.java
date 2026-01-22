package com.ems.dao;

import com.ems.exception.DataAccessException;

public interface PaymentDao {

	boolean processPayment(int regId, double totalAmount, String string) throws DataAccessException;

}
