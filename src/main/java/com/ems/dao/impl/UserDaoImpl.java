package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.UserDao;
import com.ems.exception.DataAccessException;
import com.ems.model.User;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

public class UserDaoImpl implements UserDao{
	
	//Used to create a new user account
	@Override
	public void createUser(String fullName, String email, String phone, String passwordHash, int roleId, String status,
			LocalDateTime createdAt, LocalDateTime updatedAt, String gender) throws DataAccessException {
		String sql = "insert into users(full_name"
				+ ", email, phone, password_hash, role_id, created_at, status, "
				+ "updated_at, gender) values (?,?,?,?,?,?,?,?,?)";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, fullName);
			ps.setString(2, email.toLowerCase());
			ps.setString(3, phone);
			ps.setString(4, passwordHash);
			ps.setInt(5, roleId);
			Instant utcInstant = DateTimeUtil.convertLocalDefaultToUtc(createdAt);
			ps.setTimestamp(6, Timestamp.from(utcInstant));
			ps.setString(7, "ACTIVE");
			ps.setTimestamp(8, null);
			ps.setString(9, gender);
			ps.executeUpdate();

		}catch (SQLException e) {
			throw new DataAccessException("Error while creating user account: " + e.getMessage());
		}


	}
	
	//gets the user details using the email id
	@Override
	public User findByEmail(String email) throws DataAccessException{
		User user = null;
		String sql = "select * from users where email = ?";
		try (Connection con = DBConnectionUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
	            ps.setString(1, email.toLowerCase());
	            try(ResultSet rs = ps.executeQuery()){
	            	if(rs.next()) {
	            		user = new User(
	            			    rs.getInt("user_id"),
	            			    rs.getString("full_name"),
	            			    rs.getString("email"),
	            			    rs.getString("phone"),
	            			    rs.getString("password_hash"),
	            			    rs.getInt("role_id"),
	            			    rs.getString("status"),
	            			    DateTimeUtil.convertUtcToLocal(
	            			        rs.getTimestamp("created_at").toInstant()
	            			    ).toLocalDateTime(),
	            			    rs.getTimestamp("updated_at") == null
	            			        ? null
	            			        : rs.getTimestamp("updated_at").toLocalDateTime(),
	            			    rs.getString("gender")
	            			);

	            	}
	            }
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching user: " + e.getMessage());
		}
		return user;
	}
	
	//Used to set the user as Active or suspended
	@Override
	public void updateUserStatus(int userId, String status) throws DataAccessException{
		String adminCheck = "select r.role_name from roles r inner join users u on r.role_id = u.role_id where u.user_id = ?";
		String sql = "update users set status = ? where user_id = ?";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				PreparedStatement ps1 = con.prepareStatement(adminCheck)){
			
			ps1.setInt(1, userId);
			ResultSet rs = ps1.executeQuery();
			String role = "";
			if(rs.next()) {
				role = rs.getString("role_name");
			}
			if(role.trim().equalsIgnoreCase("admin")) {
				throw new DataAccessException("Admin accounts cannot be suspended!");
			}
			
	        ps.setString(1, status);
	        ps.setInt(2, userId);
	        int rowsUpdated = ps.executeUpdate();

	        if (rowsUpdated == 0) {
	            System.out.println("No user found with ID: " + userId);
	        }else {
	        	System.out.println("Status of the User account: " + userId + " has been changed to: " + status);
	        }
	    }catch (SQLException e) {
			throw new DataAccessException("Error while updating the user status: " + e.getMessage());
		}
	}

	@Override
	public List<User> findAllUsers(String userType) throws DataAccessException{
		String sql = "select u.* from users u inner join roles r on u.role_id = r.role_id"
				+ " where r.role_name = ?";
		List<User> users= new ArrayList<>();
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, userType);
		ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				User user = new User(
					    rs.getInt("user_id"),
					    rs.getString("full_name"),
					    rs.getString("email"),
					    rs.getString("phone"),
					    rs.getString("password_hash"),
					    rs.getInt("role_id"),
					    rs.getString("status"),
					    DateTimeUtil.convertUtcToLocal(
					        rs.getTimestamp("created_at").toInstant()
					    ).toLocalDateTime(),
					    rs.getTimestamp("updated_at") == null
					        ? null
					        : rs.getTimestamp("updated_at").toLocalDateTime(),
					    rs.getString("gender")
					);

	            
	            users.add(user); 
	        }
            
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching users: " + e.getMessage());
		}
		return users;
	}
	
	//Helps to get the role of the user
	@Override
	public int getRole(User user) throws DataAccessException{
	    String query = "SELECT role_name FROM Roles WHERE role_id = ?";
	    String roleName = "";

	    try (Connection conn = DBConnectionUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
	        
	        pstmt.setInt(1, user.getRoleId());
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                roleName = rs.getString("role_name").toUpperCase();
	            }
	        }
	    } catch (SQLException e) {
			throw new DataAccessException("Error while fetching role of user: " + e.getMessage());
		}
	    
	    if(roleName.equals("ADMIN")) {
	    	return 1;
	    }else if(roleName.equals("ATTENDEE")) {
	    	return 2;
	    }else if(roleName.equals("ORGANIZER")) {
	    	return 3;
	    }
	    return 0;
	}

	@Override
	public List<User> findAllUsers() throws DataAccessException{
		String sql = "select * from users order by role_id";
		List<User> users= new ArrayList<>();
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
		ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				User user = new User(
					    rs.getInt("user_id"),
					    rs.getString("full_name"),
					    rs.getString("email"),
					    rs.getString("phone"),
					    rs.getString("password_hash"),
					    rs.getInt("role_id"),
					    rs.getString("status"),
					    DateTimeUtil.convertUtcToLocal(
					        rs.getTimestamp("created_at").toInstant()
					    ).toLocalDateTime(),
					    rs.getTimestamp("updated_at") == null
					        ? null
					        : rs.getTimestamp("updated_at").toLocalDateTime(),
					    rs.getString("gender")
					);

	            
	            users.add(user); 
	        }
            
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching users: " + e.getMessage());
		}
		return users;
	}
}
