CREATE SCHEMA `event_management_db`;
USE `event_management_db`;

CREATE TABLE `categories` (
  `category_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`category_id`),
  UNIQUE INDEX `name` (`name` ASC) VISIBLE
) ENGINE=InnoDB;

CREATE TABLE `roles` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(50) NOT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT '1',
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE INDEX `role_name` (`role_name` ASC) VISIBLE
) ENGINE=InnoDB;

CREATE TABLE `users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(15) DEFAULT NULL,
  `gender` VARCHAR(15) DEFAULT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `role_id` INT NOT NULL,
  `status` ENUM('ACTIVE','SUSPENDED') NOT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  `failed_attempts` INT DEFAULT '0',
  `last_login` DATETIME DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `email` (`email` ASC) VISIBLE,
  INDEX `role_id` (`role_id` ASC) VISIBLE,
  CONSTRAINT `users_ibfk_1`
    FOREIGN KEY (`role_id`)
    REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB;

CREATE TABLE `venues` (
  `venue_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `street` VARCHAR(100) NOT NULL,
  `city` VARCHAR(50) NOT NULL,
  `state` VARCHAR(50) NOT NULL,
  `pincode` VARCHAR(10) NOT NULL,
  `max_capacity` INT NOT NULL,
  `is_active` TINYINT(1) NOT NULL DEFAULT '1',
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`venue_id`)
) ENGINE=InnoDB;

CREATE TABLE `events` (
  `event_id` INT NOT NULL AUTO_INCREMENT,
  `organizer_id` INT NOT NULL,
  `title` VARCHAR(150) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `category_id` INT NOT NULL,
  `venue_id` INT NOT NULL,
  `start_datetime` DATETIME NOT NULL,
  `end_datetime` DATETIME NOT NULL,
  `capacity` INT NOT NULL,
  `status` ENUM('DRAFT','PUBLISHED','CANCELLED','COMPLETED') NOT NULL,
  `approved_by` INT DEFAULT NULL,
  `approved_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`event_id`),
  INDEX `organizer_id` (`organizer_id` ASC) VISIBLE,
  INDEX `category_id` (`category_id` ASC) VISIBLE,
  INDEX `venue_id` (`venue_id` ASC) VISIBLE,
  INDEX `approved_by` (`approved_by` ASC) VISIBLE,
  CONSTRAINT `events_ibfk_1`
    FOREIGN KEY (`organizer_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `events_ibfk_2`
    FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`),
  CONSTRAINT `events_ibfk_3`
    FOREIGN KEY (`venue_id`) REFERENCES `venues` (`venue_id`),
  CONSTRAINT `events_ibfk_4`
    FOREIGN KEY (`approved_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB;

CREATE TABLE `feedback` (
  `feedback_id` INT NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `rating` INT DEFAULT NULL,
  `comments` TEXT DEFAULT NULL,
  `submitted_at` DATETIME NOT NULL,
  PRIMARY KEY (`feedback_id`),
  INDEX `event_id` (`event_id` ASC) VISIBLE,
  INDEX `user_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `feedback_ibfk_1`
    FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`),
  CONSTRAINT `feedback_ibfk_2`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB;

CREATE TABLE `notifications` (
  `notification_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `message` TEXT NOT NULL,
  `type` VARCHAR(30) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `read_status` TINYINT(1) NOT NULL,
  PRIMARY KEY (`notification_id`),
  INDEX `user_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `notifications_ibfk_1`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB;

CREATE TABLE `offers` (
  `offer_id` INT NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `code` VARCHAR(30) NOT NULL,
  `discount_percentage` INT DEFAULT NULL,
  `valid_from` DATETIME DEFAULT NULL,
  `valid_to` DATETIME DEFAULT NULL,
  PRIMARY KEY (`offer_id`),
  UNIQUE INDEX `code` (`code` ASC) VISIBLE,
  INDEX `event_id` (`event_id` ASC) VISIBLE,
  CONSTRAINT `offers_ibfk_1`
    FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`)
) ENGINE=InnoDB;

CREATE TABLE `registrations` (
  `registration_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `event_id` INT NOT NULL,
  `registration_date` DATETIME NOT NULL,
  `status` ENUM('CONFIRMED','CANCELLED') NOT NULL,
  PRIMARY KEY (`registration_id`),
  INDEX `user_id` (`user_id` ASC) VISIBLE,
  INDEX `event_id` (`event_id` ASC) VISIBLE,
  CONSTRAINT `registrations_ibfk_1`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `registrations_ibfk_2`
    FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`)
) ENGINE=InnoDB;

CREATE TABLE `offer_usages` (
  `offer_usage_id` INT NOT NULL AUTO_INCREMENT,
  `offer_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `registration_id` INT NOT NULL,
  `used_at` DATETIME NOT NULL,
  PRIMARY KEY (`offer_usage_id`),
  UNIQUE INDEX `offer_id` (`offer_id` ASC, `user_id` ASC) VISIBLE,
  INDEX `user_id` (`user_id` ASC) VISIBLE,
  INDEX `registration_id` (`registration_id` ASC) VISIBLE,
  CONSTRAINT `offer_usages_ibfk_1`
    FOREIGN KEY (`offer_id`) REFERENCES `offers` (`offer_id`),
  CONSTRAINT `offer_usages_ibfk_2`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `offer_usages_ibfk_3`
    FOREIGN KEY (`registration_id`) REFERENCES `registrations` (`registration_id`)
) ENGINE=InnoDB;

CREATE TABLE `payments` (
  `payment_id` INT NOT NULL AUTO_INCREMENT,
  `registration_id` INT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `payment_method` VARCHAR(30) NOT NULL,
  `payment_status` VARCHAR(30) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `offer_id` INT DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  INDEX `registration_id` (`registration_id` ASC) VISIBLE,
  CONSTRAINT `payments_ibfk_1`
    FOREIGN KEY (`registration_id`) REFERENCES `registrations` (`registration_id`)
) ENGINE=InnoDB;


CREATE TABLE `tickets` (
  `ticket_id` INT NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `ticket_type` VARCHAR(50) NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `total_quantity` INT NOT NULL,
  `available_quantity` INT NOT NULL,
  PRIMARY KEY (`ticket_id`),
  INDEX `event_id` (`event_id` ASC) VISIBLE,
  CONSTRAINT `tickets_ibfk_1`
    FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`)
) ENGINE=InnoDB;

CREATE TABLE `registration_tickets` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `registration_id` INT NOT NULL,
  `ticket_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `registration_id` (`registration_id` ASC) VISIBLE,
  INDEX `ticket_id` (`ticket_id` ASC) VISIBLE,
  CONSTRAINT `registration_tickets_ibfk_1`
    FOREIGN KEY (`registration_id`) REFERENCES `registrations` (`registration_id`),
  CONSTRAINT `registration_tickets_ibfk_2`
    FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`ticket_id`)
) ENGINE=InnoDB;

CREATE TABLE `system_logs` (
  `log_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT DEFAULT NULL,
  `action` VARCHAR(50) NOT NULL,
  `entity` VARCHAR(50) NOT NULL,
  `entity_id` INT DEFAULT NULL,
  `message` TEXT NOT NULL,
  `created_at` DATETIME NOT NULL,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB;