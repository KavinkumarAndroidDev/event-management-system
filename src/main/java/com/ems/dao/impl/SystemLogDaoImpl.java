package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ems.dao.SystemLogDao;
import com.ems.exception.DataAccessException;
import com.ems.util.DBConnectionUtil;

public class SystemLogDaoImpl implements SystemLogDao {

	@Override
	public void log(
			Integer userId,
			String action,
			String entity,
			Integer entityId,
			String message) throws DataAccessException {

		String sql =
			"insert into system_logs " +
			"(user_id, action, entity, entity_id, message, created_at) " +
			"values (?, ?, ?, ?, ?, utc_timestamp())";

		try (Connection con = DBConnectionUtil.getConnection();
		     PreparedStatement ps = con.prepareStatement(sql)) {

			if (userId != null) {
				ps.setInt(1, userId);
			} else {
				ps.setNull(1, java.sql.Types.INTEGER);
			}

			ps.setString(2, action);
			ps.setString(3, entity);

			if (entityId != null) {
				ps.setInt(4, entityId);
			} else {
				ps.setNull(4, java.sql.Types.INTEGER);
			}

			ps.setString(5, message);

			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException(
				"Error while inserting system log"
			);
		}
	}
}
