DROP TABLE IF EXISTS tickets;
----DROP TABLE IF EXISTS users;
--
----CREATE TABLE users (
----                id BIGINT AUTO_INCREMENT PRIMARY KEY,
----                login VARCHAR (255) NOT NULL UNIQUE,
----                password VARCHAR (255) NOT NULL,
----                name VARCHAR (255) NOT NULL,
----                surname VARCHAR (255) NOT NULL,
----                middle_name VARCHAR (255));

--CREATE TABLE routes (
--    id BIGINT PRIMARY KEY,
--    departure_point VARCHAR (255),
--    destination_point VARCHAR (255),
--    carrier_name VARCHAR (255)
--    );----

CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_time VARCHAR (255) NOT NULL,
    user_id BIGINT,
    route_id BIGINT NOT NULL,
    price DECIMAL (10,2) NOT NULL,
    seat_number VARCHAR(255) NOT NULL
--    FOREIGN KEY (route_id) REFERENCES routes(id) ----
);