package com.ems.actions;

import java.util.Comparator;
import java.util.List;

import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;

public class AdminUserManagementAction {
    private final AdminService adminService;

    public AdminUserManagementAction() {
        this.adminService = ApplicationUtil.adminService();
    }

    public List<User> getUsersByRole(String role) {
        return adminService.getUsersList(role);
    }
    
    public void listUsersByRole(String role) {
    	List<User> users = getUsersByRole(role);
		if (users.isEmpty()) {
			System.out.println("No "+role+" found at the moment.");
		} else {
			MenuHelper.displayUsers(users);
		}
    }
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    public void changeUserStatus(String status) {
    	
    	List<User> users = getAllUsers();

		if (users.isEmpty()) {
			System.out.println("No users available");
			return;
		}

		users.sort(Comparator.comparing(User::getFullName));
		MenuHelper.displayUsers(users);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select a user (1-" + users.size() + "): ");

		while (choice < 1 || choice > users.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		User selectedUser = users.get(choice - 1);
		if(status.equals(selectedUser.getStatus())) {
			System.out.println(selectedUser.getFullName() + "'s status has already set as: " + status);
			return;
		}
		char approveChoice = InputValidationUtil.readChar(ScannerUtil.getScanner(),
				"Do you want to change the status to " + status + " for user " + selectedUser.getFullName() + " (Y/N)\n");
		if (approveChoice == 'Y' || approveChoice == 'y') {
			boolean isSuccess = adminService.changeStatus(status, selectedUser.getUserId());
			if (isSuccess) {
				System.out.println("User status updated successfully.");
			} else {
				System.out.println("Unable to update user status. Please try again.");
			}
		} else {
			System.out.println("Action cancelled by user.");
		} 	
    }
}