package com.ems.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnectionUtil {

	private DBConnectionUtil() {}
	public static Connection getConnection() throws Exception{
		Properties prop = new Properties();
		try(InputStream is = DBConnectionUtil.class.getClassLoader().getResourceAsStream("db.properties")){
			prop.load(is);
			
		}
		return DriverManager.getConnection(
				prop.getProperty("db.url"),
				prop.getProperty("db.username"),
				prop.getProperty("db.password")
		);
	}
}
