package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.RoleDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Role;
import com.ems.util.DBConnectionUtil;

/*
 * Handles database operations related to roles.
 *
 * Responsibilities:
 * - Retrieve active roles from the system
 */
public class RoleDaoImpl implements RoleDao {

	
	@Override
	public List<Role> getRoles() throws DataAccessException {
		List<Role> roles = new ArrayList<>();
		String query = "SELECT role_id, role_name, created_at FROM Roles WHERE is_active = true";
		
		try (Connection conn = DBConnectionUtil.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(query)) {
			
			while (rs.next()) {                
				Role role = new Role(
					rs.getInt("role_id"),
					rs.getString("role_name"),
					rs.getTimestamp("created_at").toLocalDateTime()
				);
				roles.add(role);
			}
			
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching roles: " + e.getMessage());
		}
		
		return roles;
	}
}