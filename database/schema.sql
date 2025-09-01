CREATE DATABASE expense_tracker;
USE expense_tracker;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('STAFF', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE expense_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE expenses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    category_id INT,
    amount DECIMAL(10,2) NOT NULL,
    description TEXT,
    expense_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES expense_categories(id)
);

-- Sample data
INSERT INTO expense_categories (name, description) VALUES 
('Marketing', 'Advertising and promotional expenses'),
('Operations', 'Day-to-day operational costs'),
('Travel', 'Business travel and transportation'),
('Salaries', 'Employee compensation'),
('Office', 'Office supplies and utilities');

INSERT INTO users (username, password, role) VALUES 
('anup', 'anup123', 'ADMIN'),
('staff1', 'staff123', 'STAFF');