package com.ems.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ems.dao.CategoryDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.util.DBConnectionUtil;


/**
 * Category DAO implementation - Database operations ONLY
 */
public class CategoryDaoImpl implements CategoryDao {
    
    @Override
    public Category getCategory(int categoryId) throws DataAccessException {
        String sql = "select * from categories where category_id=?";
        try (Connection con = DBConnectionUtil.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                	return new Category(rs.getInt("category_id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while fetching category: " + e.getMessage());
        }
        return null;
    } 
    @Override
    public List<Category> getAllCategories() throws DataAccessException {
        String sql = "select * from categories where is_active = 1 order by name";
        List<Category> categories = new ArrayList<>();
        
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category category = new Category(rs.getInt("category_id"), rs.getString("name"));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while fetching categories: " + e.getMessage());
        }
        return categories;
    }
    @Override
    public void addCategory(String name) throws DataAccessException {
        String sql =
            "insert into categories (name,is_active) values (?,?)";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, 1);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Unable to add category", e);
        }
    }

    @Override
    public void updateCategoryName(int categoryId, String name)
            throws DataAccessException {

        String sql =
            "update categories set name=? where category_id=?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, categoryId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Unable to update category", e);
        }
    }

    @Override
    public void deactivateCategory(int categoryId)
            throws DataAccessException {

        String sql =
            "update categories set is_active=0 where category_id=?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DataAccessException("Unable to delete category", e);
        }
    }

}