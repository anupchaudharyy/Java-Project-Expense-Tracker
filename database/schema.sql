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

CREATE TABLE income_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE incomes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    category_id INT,
    amount DECIMAL(10,2) NOT NULL,
    description TEXT,
    income_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES income_categories(id)
);

-- Sample data
INSERT INTO expense_categories (name, description) VALUES
('Inventory', 'Cost of goods sold'),
('Marketing', 'Advertising and promotional expenses'),
('Utilities', 'Electricity, water, internet, etc.'),
('Rent', 'Office or storage space rent'),
('Transport', 'Fuel, public transport, etc.'),
('Equipment', 'Purchase of new equipment'),
('Packaging', 'Packaging materials for products'),
('Staff Salary', 'Salaries for employees'),
('Software', 'Software subscriptions and licenses'),
('Legal/Tax', 'Legal and tax consultation fees'),
('Food', 'Meals and groceries'),
('Education', 'Courses, books, and training'),
('Health', 'Medical expenses and insurance'),
('Entertainment', 'Movies, concerts, etc.'),
('Subscription', 'Recurring subscription fees');

INSERT INTO income_categories (name, description) VALUES
('Product Sales', 'Revenue from selling products'),
('Online Sales', 'Revenue from online channels'),
('Service Income', 'Revenue from services rendered'),
('Commission Earnings', 'Commissions from sales'),
('Rental Income', 'Income from rental properties'),
('Sponsorship Revenue', 'Revenue from sponsorships'),
('Training Fees', 'Fees for providing training'),
('Freelance Payments', 'Payments for freelance work'),
('Investment Returns', 'Dividends, interest, etc.'),
('Refunded Costs', 'Refunds for returned items or services'),
('Salary Income', 'Regular salary from employment'),
('Performance Bonus', 'Bonuses for good performance'),
('Monetary Gifts', 'Gifts received in the form of money'),
('Earnings from Referrals', 'Income from referral programs'),
('Royalties from Published Work', 'Royalties from books, music, etc.');

INSERT INTO users (username, password, role) VALUES 
('anup', 'anup123', 'ADMIN'),
('staff1', 'staff123', 'STAFF');