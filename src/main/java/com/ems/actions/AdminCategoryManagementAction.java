package com.ems.actions;

import java.util.List;

import com.ems.model.Category;
import com.ems.service.AdminService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class AdminCategoryManagementAction {
    private final AdminService adminService;

    public AdminCategoryManagementAction() {
        this.adminService = ApplicationUtil.adminService();
    }
    
    /*
     * Prints all available categories
     */
    public void listAllCategories() {
        List<Category> categories = adminService.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }
        System.out.println("\n-------------------------------------------");
        System.out.printf("%-5s %-25s %-10s%n", "No", "Name", "Status");
        System.out.println("-------------------------------------------");

        int index = 1;
        for (Category c : categories) {
            String status = (c.getIsActive() == 1 ? "ACTIVE" : "INACTIVE");
            
            System.out.printf(
                "%-5d %-25s %-10s%n",
                index, 
                c.getName(), 
                status
            );
            index++;
        }
        System.out.println("-------------------------------------------");
    }

    
    /*
     * Gets user input and add a new category
     */
    public void addCategory() {
    	String name = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter category name: ");
        adminService.addCategory(name.trim());
        System.out.println("Category added successfully.");
    }
    /*
     * It prints the existing categories
     * user select a category to update
     */
    public void updateCategory() {
    	
    	Category selectedCategory = selectCategory();
		if (selectedCategory == null)
			return;

		if(selectedCategory.getIsActive() == 1) {
			String newName = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(),
					"Enter new category name: ");
			adminService.updateCategory(selectedCategory.getCategoryId(), newName);

		}else {
			char confirm = InputValidationUtil.readChar(ScannerUtil.getScanner(),
					"Are you sure you want to activate this category (Y/N): ");

			if (Character.toUpperCase(confirm) != 'Y') {
				System.out.println("Category updation cancelled.");
				return;
				
			}
			adminService.updateCategory(selectedCategory.getCategoryId());
		}
		System.out.println("Category updated successfully");
        
    }
    /*
     * Prints all active category and admin select any 
     * and the category is deactivated, not completely delted
     */
    public void deleteCategory() {
    	
		Category selectedCategory = selectCategory();
		if (selectedCategory == null)
			return;

		if(selectedCategory.getIsActive() == 1) {
			char confirm = InputValidationUtil.readChar(ScannerUtil.getScanner(),
					"Are you sure you want to delete this category (Y/N): ");

			if (Character.toUpperCase(confirm) != 'Y') {
				System.out.println("Category deletion cancelled.");
				return;
			}
	        adminService.deleteCategory(selectedCategory.getCategoryId());
			System.out.println("Category deleted successfully");
		}else {
			System.out.println("The category is already deleted");
		}

    }
    
   /*
    * It returns the list of all available categories
    */
    public List<Category> getAllCategories(){
    	List<Category> categories = adminService.getAllCategories();
    	return categories;
    }
    
    /*
     * Helper function to display the categories
     */
    private Category selectCategory() {

		List<Category> categories = getAllCategories();

		if (categories.isEmpty()) {
			System.out.println("No categories found.");
			return null;
		}

        System.out.println("\n-------------------------------------------");
        System.out.printf("%-5s %-25s %-10s%n", "No", "Name", "Status");
        System.out.println("-------------------------------------------");

        int index = 1;
        for (Category c : categories) {
            String status = (c.getIsActive() == 1 ? "ACTIVE" : "INACTIVE");
            
            System.out.printf(
                "%-5d %-25s %-10s%n",
                index, 
                c.getName(), 
                status
            );
            index++;
        }
        System.out.println("-------------------------------------------");

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select a category (1-" + categories.size() + "): ");

		while (choice < 1 || choice > categories.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		return categories.get(choice - 1);
	}
}