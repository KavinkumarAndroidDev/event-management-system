package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.UserDao;
import com.ems.model.User;
import com.ems.util.DBConnectionUtil;

public class UserDaoImpl implements UserDao{

	@Override
	public void createUser(User user){
		String sql = "insert into users(full_name"
				+ ", email, phone, password_hash, role_id, created_at, status, "
				+ "updated_at) values (?,?,?,?,?,?,?,?)";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, user.getFullName());
			ps.setString(2, user.getEmail().toLowerCase());
			ps.setString(3, user.getPhone());
			ps.setString(4, user.getPasswordHash());
			ps.setInt(5, user.getRoleId());
			Timestamp ts = Timestamp.valueOf(user.getCreatedAt());
			ps.setTimestamp(6, ts);
			ps.setString(7, "ACTIVE");
			ps.setTimestamp(8, ts);
			ps.execute();
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}


	}

	@Override
	public User findByEmail(String email) {
		User user = null;
		String sql = "select * from users where email = ?";
		try (Connection con = DBConnectionUtil.getConnection();
	             PreparedStatement ps = con.prepareStatement(sql)) {
	            ps.setString(1, email.toLowerCase());
	            try(ResultSet rs = ps.executeQuery()){
	            	if(rs.next()) {
	            		 user = new User();
	                     user.setUserId(rs.getInt("user_id"));
	                     user.setFullName(rs.getString("full_name"));
	                     user.setEmail(rs.getString("email"));
	                     user.setPhone(rs.getString("phone"));
	                     user.setPasswordHash(rs.getString("password_hash"));
	                     user.setRoleId(rs.getInt("role_id"));
	                     user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
	                     user.setStatus(rs.getString("status"));
	                     if(rs.getTimestamp("updated_at") != null) {
	                    	 user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
	                     }else {
	                    	 user.setUpdatedAt(null); 
	                     }
	            	}
	            }
		} catch (SQLException e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		return user;
	}

	@Override
	public void updateUserStatus(int userId, String status) {
		String sql = "update users set status = ? where user_id = ?";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
	        ps.setString(1, status);
	        ps.setInt(2, userId);

	        int rowsUpdated = ps.executeUpdate();

	        if (rowsUpdated == 0) {
	            System.out.println("No user found with ID: " + userId);
	        }else {
	        	System.out.println("Status of the User account: " + userId + " has been changed to: " + status);
	        }
	    } catch (SQLException e) {
	        System.out.println("Database error: " + e.getMessage());
	    } catch (Exception e) {
	        System.out.println("Unexpected error: " + e.getMessage());
	    }
	}


	@Override
	public List<User> findAllUsers() {
		String sql = "select * from users";
		List<User> users= new ArrayList<>();
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery()){
			while (rs.next()) {
	            User user = new User();
	            user.setUserId(rs.getInt("user_id"));
	            user.setFullName(rs.getString("full_name"));
	            user.setEmail(rs.getString("email"));
	            user.setPhone(rs.getString("phone"));
	            user.setPasswordHash(rs.getString("password_hash"));
	            user.setRoleId(rs.getInt("role_id"));
	            user.setStatus(rs.getString("status"));
	            if (rs.getTimestamp("created_at") != null) {
	                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
	            }
	            if (rs.getTimestamp("updated_at") != null) {
	                user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
	            } else {
	                user.setUpdatedAt(null);
	            }
	            
	            users.add(user); 
	        }
            
		} catch (SQLException e) {			
			System.out.println("Unexpected error occured: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected error occured: " + e.getMessage());
		}
		return users;
	}

	@Override
	public int getRole(User user) {
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
	        System.err.println("Error fetching role: " + e.getMessage());
	        return 0; 
	    } catch (Exception e1) {
			System.out.println("Unexpected error occured: " + e1.getMessage());
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
	public List<User> findAllOrganizers() {
		// TODO Auto-generated method stub
		return null;
	}


}
