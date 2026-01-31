package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Category;

public interface CategoryDao {

	Category getCategory(int categoryId)  throws DataAccessException;

	List<Category> getActiveCategories()  throws DataAccessException;
	
	void addCategory(String name) throws DataAccessException;

	void updateCategoryName(int categoryId, String name) throws DataAccessException;

	void deactivateCategory(int categoryId) throws DataAccessException;

	List<Category> getAllCategories() throws DataAccessException;

	void activateCategory(int categoryId) throws DataAccessException;

}
