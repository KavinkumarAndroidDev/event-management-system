package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Role;

public interface RoleDao {
	
	List<Role> getRoles()  throws DataAccessException;
}
