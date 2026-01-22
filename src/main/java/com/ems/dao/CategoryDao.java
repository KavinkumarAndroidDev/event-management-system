package com.ems.dao;

import java.util.Map;

import com.ems.exception.DataAccessException;

public interface CategoryDao {

	String getCategory(int categoryId)  throws DataAccessException;

	void listAllCategory() throws DataAccessException;

	Map<Integer, String> getAllCategories()  throws DataAccessException;
}
