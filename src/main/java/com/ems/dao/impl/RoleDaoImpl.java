package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.RoleDao;
import com.ems.model.Role;
import com.ems.util.DBConnectionUtil;

public class RoleDaoImpl implements RoleDao{

	
	//get the list of available roles and its id
	@Override
	public List<Role> getRoles() {
		List<Role> roles = new ArrayList<>();

        String query = "SELECT role_id, role_name, created_at FROM Roles";

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
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e1) {
			e1.printStackTrace();
		}
		return roles;
	}

}
