package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.model.Notification;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

public class NotificationDaoImpl implements NotificationDao {
	
	//send the system wide notification to all users
	//notificationType - SYSTEM, EVENT, etc...
	@Override
	public void sendSystemWideNotification(String message, String notificationType) {
		String sql = "insert into notifications (user_id, message, type,"
				+ " created_at, read_status) select u.user_id"
				+ ", ? , ?, NOW(), false from users u"
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
	
	//Helps to get all unread notifications
	@Override
	public List<Notification> getUnreadNotifications(int userId) {
		List<Notification> notifications = new ArrayList<>();
		String sql = "select * from notifications where user_id = ? and read_status = 0 order by created_at desc";
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
			
		} catch (SQLException e) {
			System.out.println("Database error while reading notification: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Database error while reading notification: " + e.getMessage());
		}
		return notifications;
	}
	
	//helps to set a notification as read
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
	
	// get all notification despite of its read_state
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
			
		} catch (SQLException e) {
			System.out.println("Database error while reading notification: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Database error while reading notification: " + e.getMessage());
		}
		return notifications;
	}	
	
	//when the user opens notification tab, all notification will be marked as read
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
	
	//helps to send the notification to particular user
	@Override
	public void sendNotification(int organizerId, String message, String notificationType) {
		String sql = "insert into notifications (user_id, message, type,"
				+ " created_at, read_status) ?"
				+ ", ? , ?, NOW(), false";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, organizerId);
			ps.setString(1, message);
			ps.setString(3, notificationType);
			ps.executeUpdate();
		} catch (SQLException e) {
		        System.out.println("Database error while sending system notification: " + e.getMessage());
		} catch (Exception e) {
		        System.out.println("Unexpected error while sending system notification: " + e.getMessage());
		}
	}
	

}
