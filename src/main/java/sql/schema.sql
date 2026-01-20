create database event_management_db;
use event_management_db;
-- =====================================================
-- Event Management System Database Schema (MySQL)
-- =====================================================

-- -------------------------
-- 1. Roles
-- -------------------------
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL
);

-- -------------------------
-- 2. Users
-- -------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15),
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    status ENUM('ACTIVE','SUSPENDED') NOT NULL,
    CONSTRAINT fk_users_roles
        FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- -------------------------
-- 3. Categories
-- -------------------------
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- -------------------------
-- 4. Venues
-- -------------------------
CREATE TABLE venues (
    venue_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    street VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    max_capacity INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

-- -------------------------
-- 5. Events
-- -------------------------
CREATE TABLE events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    organizer_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    category_id INT NOT NULL,
    venue_id INT NOT NULL,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    capacity INT NOT NULL,
    status ENUM('DRAFT','PUBLISHED','CANCELLED','COMPLETED') NOT NULL,
    approved_by INT,
    approved_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_events_organizer
        FOREIGN KEY (organizer_id) REFERENCES users(user_id),
    CONSTRAINT fk_events_category
        FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT fk_events_venue
        FOREIGN KEY (venue_id) REFERENCES venues(venue_id),
    CONSTRAINT fk_events_approved_by
        FOREIGN KEY (approved_by) REFERENCES users(user_id)
);

-- -------------------------
-- 6. Tickets
-- -------------------------
CREATE TABLE tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    ticket_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    total_quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    CONSTRAINT fk_tickets_event
        FOREIGN KEY (event_id) REFERENCES events(event_id)
);

-- -------------------------
-- 7. Registrations
-- -------------------------
CREATE TABLE registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    registration_date DATETIME NOT NULL,
    status ENUM('CONFIRMED','CANCELLED') NOT NULL,
    CONSTRAINT fk_registrations_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_registrations_event
        FOREIGN KEY (event_id) REFERENCES events(event_id)
);

-- -------------------------
-- 8. Registration Tickets
-- -------------------------
CREATE TABLE registration_tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    registration_id INT NOT NULL,
    ticket_id INT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_rt_registration
        FOREIGN KEY (registration_id) REFERENCES registrations(registration_id),
    CONSTRAINT fk_rt_ticket
        FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id)
);

-- -------------------------
-- 9. Payments
-- -------------------------
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    registration_id INT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_payments_registration
        FOREIGN KEY (registration_id) REFERENCES registrations(registration_id)
);

-- -------------------------
-- 10. Transactions
-- -------------------------
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_id INT NOT NULL,
    transaction_ref VARCHAR(100),
    transaction_status VARCHAR(30),
    transaction_time DATETIME NOT NULL,
    CONSTRAINT fk_transactions_payment
        FOREIGN KEY (payment_id) REFERENCES payments(payment_id)
);

-- -------------------------
-- 11. Notifications
-- -------------------------
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    read_status TINYINT(1) NOT NULL,
    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- -------------------------
-- 12. Offers
-- -------------------------
CREATE TABLE offers (
    offer_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    discount_percentage INT,
    valid_from DATETIME,
    valid_to DATETIME,
    CONSTRAINT fk_offers_event
        FOREIGN KEY (event_id) REFERENCES events(event_id)
);

-- -------------------------
-- 13. Feedback
-- -------------------------
CREATE TABLE feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL,
    comments TEXT,
    submitted_at DATETIME NOT NULL,
    CONSTRAINT fk_feedback_event
        FOREIGN KEY (event_id) REFERENCES events(event_id),
    CONSTRAINT fk_feedback_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT chk_feedback_rating
        CHECK (rating BETWEEN 1 AND 5)
);
