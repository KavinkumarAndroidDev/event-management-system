package com.ems.dao;

import java.util.Map;

public interface CategoryDao {

	String getCategory(int categoryId);

	void listAllCategory();

	Map<Integer, String> getAllCategories();

}
