-- Очистка существующих данных
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM accounts;
DELETE FROM categories;
DELETE FROM transactions;
DELETE FROM reports;

-- Сброс автоинкремента
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE accounts ALTER COLUMN account_id RESTART WITH 1;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 1;
ALTER TABLE transactions ALTER COLUMN transaction_id RESTART WITH 1;
ALTER TABLE reports ALTER COLUMN id RESTART WITH 1;

-- Тестовые пользователи
INSERT INTO users (username, password, first_name, last_name, created_at) VALUES
                                                                              ('admin', '$2a$10$8dAvS.dJoxo1WywuZ0JQY.1bL9kQ3n8Q3n8Q3n8Q3n8Q3n8Q3n8Q3n', 'System', 'Administrator', CURRENT_DATE),
                                                                              ('testuser', '$2a$10$8dAvS.dJoxo1WywuZ0JQY.1bL9kQ3n8Q3n8Q3n8Q3n8Q3n8Q3n8Q3n', 'Test', 'User', CURRENT_DATE);

-- Роли пользователей
INSERT INTO user_roles (user_id, role) VALUES
                                           (1, 'ADMIN'),
                                           (1, 'USER'),
                                           (2, 'USER');

-- Тестовые счета
INSERT INTO accounts (name, type, initial_balance, current_balance, currency) VALUES
                                                                                  ('Основной счет', 'CHECKING', 10000.00, 10000.00, 'RUB'),
                                                                                  ('Сберегательный счет', 'SAVINGS', 50000.00, 50000.00, 'RUB');

-- Тестовые категории
INSERT INTO categories (name, description, type, color, is_active, display_order) VALUES
                                                                                      ('Продукты', 'Покупка продуктов', 'expense', '#FF6B6B', true, 1),
                                                                                      ('Транспорт', 'Транспортные расходы', 'expense', '#4ECDC4', true, 2),
                                                                                      ('Зарплата', 'Заработная плата', 'income', '#45B7D1', true, 3),
                                                                                      ('Инвестиции', 'Инвестиционный доход', 'income', '#96CEB4', true, 4);

-- Тестовые транзакции
INSERT INTO transactions (category_id, account_id, amount, date, description) VALUES
                                                                                  (1, 1, -1500.50, CURRENT_TIMESTAMP, 'Покупка продуктов в супермаркете'),
                                                                                  (3, 1, 50000.00, CURRENT_TIMESTAMP, 'Зарплата за месяц');

-- Тестовые отчеты
INSERT INTO reports (status, content) VALUES
    ('COMPLETED', '<html><body><h1>Тестовый отчет</h1></body></html>');