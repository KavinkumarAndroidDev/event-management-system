package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ems.dao.CategoryDao;
import com.ems.exception.DataAccessException;
import com.ems.util.DBConnectionUtil;

/**
 * Implements Category DAO. Methods used to create, read, update and delete categories
 */
public class CategoryDaoImpl implements CategoryDao{
	
	
	//to get the category using the category id
	@Override
	public String getCategory(int categoryId) throws DataAccessException{
		String sql = "select name from categories where category_id=?";
		try(Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, categoryId);
			try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("name");
	            }
	        }
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching category: " + e.getMessage());
		}
		return null;
	}
	
	//get all catgeories
	@Override
	public void listAllCategory() throws DataAccessException{
		
		String sql = "select * from categories order by category_id";
		try(Connection con = DBConnectionUtil.getConnection();
				Statement ps = con.createStatement()){
			try (ResultSet rs = ps.executeQuery(sql)) {
	            while (rs.next()) {
	            	System.out.println("category id: " + rs.getInt("category_id") + ", category name:" + rs.getString("name"));
	            }
	        }
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching categories: " + e.getMessage());
		}
	}
	
	@Override
	public Map<Integer, String> getAllCategories() throws DataAccessException{
		String sql = "select category_id, name from categories order by name";
		Map<Integer, String> categories = new HashMap<>();
		try(Connection con =  DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				categories.put(rs.getInt("category_id"), rs.getString("name"));
			}
		}catch (SQLException e) {
			throw new DataAccessException("Error while fetching categories: " + e.getMessage());
		}
		return categories;
	}
}
