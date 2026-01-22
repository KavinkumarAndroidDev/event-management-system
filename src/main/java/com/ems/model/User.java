package com.ems.model;

import java.time.LocalDateTime;

// TODO: Implement the Comparable<User> interface to enable natural sorting of users.
// The compareTo method should be implemented to sort users based on their fullName.
public class User {

    private final int userId;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String passwordHash;
    private final int roleId;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String gender;

    public User(
            int userId,
            String fullName,
            String email,
            String phone,
            String passwordHash,
            int roleId,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String gender
    ) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.gender = gender;
    }

    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPasswordHash() { return passwordHash; }
    public int getRoleId() { return roleId; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getGender() { return gender; }
    @Override public String toString() { return "User{" + "id=" + userId + ", name='" + fullName + '\'' + ", gender='" + gender + '\'' + ", email='" + email + '\'' + ", phone='" + phone + '\'' + ", status='" + status + '\'' + '}'; }
}
