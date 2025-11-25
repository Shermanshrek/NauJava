-- Создание таблиц для тестовой БД

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     first_name VARCHAR(255) NOT NULL,
                                     last_name VARCHAR(255) NOT NULL,
                                     created_at DATE
);

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role VARCHAR(255) NOT NULL,
                                          PRIMARY KEY (user_id, role),
                                          FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS accounts (
                                        account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
                                        type VARCHAR(20) NOT NULL,
                                        initial_balance DECIMAL(15,2) DEFAULT 0.00,
                                        current_balance DECIMAL(15,2) DEFAULT 0.00,
                                        currency VARCHAR(10) DEFAULT 'RUB',
                                        is_archived BOOLEAN DEFAULT FALSE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(255),
                                          description VARCHAR(255),
                                          type VARCHAR(50),
                                          color VARCHAR(20),
                                          is_active BOOLEAN,
                                          display_order INT,
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
                                            transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            category_id BIGINT,
                                            account_id BIGINT,
                                            amount DECIMAL(15,2),
                                            date TIMESTAMP,
                                            description VARCHAR(255),
                                            location VARCHAR(255),
                                            is_recurring BOOLEAN DEFAULT FALSE,
                                            recurrence_pattern VARCHAR(255),
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reports (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       status VARCHAR(50) NOT NULL,
                                       content TEXT
);