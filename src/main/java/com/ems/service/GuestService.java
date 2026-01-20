package com.ems.service;

import java.util.Scanner;

import com.ems.dao.UserDao;
import com.ems.dao.impl.UserDaoImpl;
import com.ems.util.InputValidationUtil;

public class GuestService {
	private static UserDao userDao = new UserDaoImpl();
}
