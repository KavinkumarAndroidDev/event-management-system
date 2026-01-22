package com.ems.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.ems.exception.DataAccessException;

public class DBConnectionUtil {

	private DBConnectionUtil() {}
	public static Connection getConnection() throws DataAccessException{
		Properties prop = new Properties();
		try(InputStream is = DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties")){
			prop.load(is);
			return DriverManager.getConnection(
					prop.getProperty("db.url"),
					prop.getProperty("db.username"),
					prop.getProperty("db.password")
			);
			
		}catch (SQLException | IOException e) {
		    throw new DataAccessException("Failed to get DB connection", e);
		}
	}
}
