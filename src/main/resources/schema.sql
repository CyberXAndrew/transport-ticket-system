DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS carriers;

CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_time VARCHAR (255) NOT NULL,
    user_id BIGINT,
    route_id BIGINT NOT NULL,
    price DECIMAL (10,2) NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (route_id) REFERENCES routes(id)
);
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR (255) NOT NULL UNIQUE,
    password VARCHAR (255) NOT NULL,
    full_name VARCHAR (255) NOT NULL
);

CREATE TABLE routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departure_point VARCHAR (255) NOT NULL,
    destination_point VARCHAR (255) NOT NULL,
    carrier_id BIGINT NOT NULL,
    duration_in_minutes INT NOT NULL,
    FOREIGN KEY (carrier_id) REFERENCES carriers(id)
);
CREATE TABLE carriers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR (255) NOT NULL,
    phone_number VARCHAR (20) UNIQUE
);

