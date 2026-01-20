package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ems.dao.CategoryDao;
import com.ems.util.DBConnectionUtil;

public class CategoryDaoImpl implements CategoryDao{

	@Override
	public String getCategory(int categoryId) {
		String sql = "select name from categories where category_id=?";
		try(Connection con = new DBConnectionUtil().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, categoryId);
			try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("name");
	            }
	        }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	public void listAllCategory() {
		
		String sql = "select * from categories order by category_id";
		try(Connection con = new DBConnectionUtil().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	            	System.out.println("category id: " + rs.getInt("category_id") + ", category name:" + rs.getString("name"));
	            }
	        }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
