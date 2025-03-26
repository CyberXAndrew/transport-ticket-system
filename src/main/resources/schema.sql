DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS carriers;

CREATE TABLE carriers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR (255) NOT NULL,
    phone_number VARCHAR (20) UNIQUE
);

CREATE TABLE routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departure_point VARCHAR (255) NOT NULL,
    destination_point VARCHAR (255) NOT NULL,
    carrier_id BIGINT NOT NULL,
    duration_in_minutes INT NOT NULL,
    FOREIGN KEY (carrier_id) REFERENCES carriers(id)
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR (255) NOT NULL UNIQUE,
    password VARCHAR (255) NOT NULL,
    full_name VARCHAR (255) NOT NULL
);

CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_time VARCHAR (255) NOT NULL,
    user_id BIGINT,
    route_id BIGINT NOT NULL,
    price DECIMAL (10,2) NOT NULL,
    seat_number VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (route_id) REFERENCES routes(id)
);

INSERT INTO carriers (name, phone_number) VALUES
('Java Airlines', '123789'),
('J7', '000111222'),
('Defeat', '888777666'),
('Seaflot', '123321'),
('Hyperloop', '777666555');

INSERT INTO routes (departure_point, destination_point, carrier_id, duration_in_minutes) VALUES
('Saints-Petersburg', 'Moscow', 1, 120),
('Vienna', 'Saints-Petersburg', 1, 260),
('Saints-Petersburg', 'Vienna', 2, 260),
('Moscow', 'Saints-Petersburg', 3, 110),
('Saints-Petersburg', 'Moscow', 2, 420),
('Saints-Petersburg', 'Moscow', 2, 410),
('Tokyo', 'Moscow', 2, 420),
('Moscow', 'Vienna', 3, 190);

INSERT INTO users (login, password, full_name) VALUES
('login1', 'password1', 'Medvedeva Alisa Ivanovna'),
('login2', 'password2', 'Rodionova Sophia Dmitrievna'),
('login3', 'password3', 'Romanov Andrey Vladimirovich');

INSERT INTO tickets (date_time, user_id, route_id, price, seat_number) VALUES
('2025-03-17T10:20:30.123456789', NULL, 5, 10.0, '1A'),
('2025-05-19T15:25:10.123456789', NULL, 5, 10.0, '2B'),
('2025-05-19T15:25:10.123456789', NULL, 3, 10.0, '2B'),
('2025-04-18T20:10:30.123456789', 1, 4, 11.0, '1A');


