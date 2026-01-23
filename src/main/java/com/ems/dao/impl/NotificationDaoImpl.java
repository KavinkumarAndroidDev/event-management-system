package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.model.Notification;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

public class NotificationDaoImpl implements NotificationDao {
	
	// sends notification to all active users
	@Override
	public void sendSystemWideNotification(String message, String notificationType) {
		String sql = "insert into notifications (user_id, message, type,"
				+ " created_at, read_status) select u.user_id"
				+ ", ? , ?, utc_timestamp(), false from users u"
				+ " where u.status = 'ACTIVE' ";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, message);
			ps.setString(2, notificationType);
			ps.executeUpdate();
		} catch (SQLException e) {
		        System.out.println("Database error while sending system notification: " + e.getMessage());
		} catch (Exception e) {
		        System.out.println("Unexpected error while sending system notification: " + e.getMessage());
		}
	}
	
	// gets all unread notifications for user
	@Override
	public List<Notification> getUnreadNotifications(int userId) {
		List<Notification> notifications = new ArrayList<>();
		String sql = "select * from notifications where user_id = ? and read_status = FALSE order by created_at desc";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				Notification notification = new Notification();
				notification.setNotificationId(rs.getInt("notification_id"));
				notification.setUserId(rs.getInt("user_id"));
				notification.setMessage(rs.getString("message"));
				notification.setType(rs.getString("type"));
				Instant created_at = rs.getTimestamp("created_at").toInstant();
				notification.setCreatedAt(
				    DateTimeUtil.convertUtcToLocal(created_at).toLocalDateTime()
				);
				notification.setReadStatus(rs.getBoolean("read_status"));
				notifications.add(notification);
			}
			rs.close();
			
		} catch (SQLException e) {
			System.out.println("Database error while reading notification: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Database error while reading notification: " + e.getMessage());
		}
		return notifications;
	}
	
	// marks single notification as read
	@Override
	public void markAsRead(int notificationId) {
		String sql = "update notifications set read_status = 1 where notification_id = ?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, notificationId);
			ps.executeUpdate();
		} catch (SQLException e) {
		        System.out.println("Database error while sending system notification: " + e.getMessage());
		} catch (Exception e) {
		        System.out.println("Unexpected error while sending system notification: " + e.getMessage());
		}
		
	}
	
	// gets all notifications for user
	@Override
	public List<Notification> getAllNotifications(int userId) {
		List<Notification> notifications = new ArrayList<>();
		String sql = "select * from notifications where user_id = ? order by created_at desc";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				Notification notification = new Notification();
				notification.setNotificationId(rs.getInt("notification_id"));
				notification.setUserId(rs.getInt("user_id"));
				notification.setMessage(rs.getString("message"));
				notification.setType(rs.getString("type"));
				Instant created_at = rs.getTimestamp("created_at").toInstant();
				notification.setCreatedAt(
				    DateTimeUtil.convertUtcToLocal(created_at).toLocalDateTime()
				);
				notification.setReadStatus(rs.getBoolean("read_status"));
				notifications.add(notification);
			}
			rs.close();
			
		} catch (SQLException e) {
			System.out.println("Database error while reading notification: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Database error while reading notification: " + e.getMessage());
		}
		return notifications;
	}	
	
	// marks all unread notifications as read
	@Override
	public void markAllAsRead(int userId) {
	    String sql =
	        "UPDATE notifications " +
	        "SET read_status = TRUE " +
	        "WHERE user_id = ? AND read_status = FALSE";

	    try (
	        Connection con = DBConnectionUtil.getConnection();
	        PreparedStatement ps = con.prepareStatement(sql)
	    ) {
	        ps.setInt(1, userId);
	        ps.executeUpdate();
	    } catch (SQLException e) {
	    	System.out.println("Database error while updating notification: " + e.getMessage());
	    } catch (Exception e) {
	    	System.out.println("Database error while updating notification: " + e.getMessage());
		}
	}
	
	// sends notification to a specific user
	@Override
	public void sendNotification(int userId, String message, String notificationType) {
		String sql = "insert into notifications (user_id, message, type,"
				+ " created_at, read_status) values (?"
				+ ", ? , ?, ?, ?)";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.setString(2, message);
			ps.setString(3, notificationType);
			ps.setTimestamp(4, Timestamp.from(DateTimeUtil.getCurrentUtc()));
			ps.setBoolean(5, false);
			int affectedRows = ps.executeUpdate();
			if(affectedRows == 0) {
				System.out.println("No user found with the user id: " + userId);
			}
		} catch (SQLException e) {
		        System.out.println("Database error while sending system notification: " + e.getMessage());
		} catch (Exception e) {
		        System.out.println("Unexpected error while sending system notification: " + e.getMessage());
		}
	}

	// sends notification based on user role
	@Override
	public void sendNotificationByRole(String message, String notificationType, String role) {
		String sql = "insert into notifications (user_id, message, type,created_at, read_status) "
				+ "select u.user_id, ? , ?, utc_timestamp(), false from users u "
				+ "inner join roles r on u.role_id = r.role_id "
				+ "where u.status = 'ACTIVE' and role_name = ?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, message);
			ps.setString(2, notificationType);
			ps.setString(3, role.toUpperCase());
			ps.executeUpdate();
		} catch (SQLException e) {
		        System.out.println("Database error while sending system notification: " + e.getMessage());
		} catch (Exception e) {
		        System.out.println("Unexpected error while sending system notification: " + e.getMessage());
		}
	}
}
