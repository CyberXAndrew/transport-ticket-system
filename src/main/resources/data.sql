INSERT INTO carriers (name, phone_number) VALUES
('Java Airlines', '123789'),
('J7', '000111222'),
('Defeat', '888777666'),
('Seaflot', '123321'),
('Hyperloop', '777666555');

INSERT INTO routes (departure_point, destination_point, carrier_id, duration) VALUES
('Saint Petersburg', 'Moscow', 1, 120),
('Vienna', 'Saint Petersburg', 1, 260),
('Saint Petersburg', 'Vienna', 2, 260),
('Moscow', 'Saint Petersburg', 3, 110),
('Saint Petersburg', 'Moscow', 2, 420),
('Saint Petersburg', 'Moscow', 2, 410),
('Tokyo', 'Moscow', 2, 420),
('Moscow', 'Vienna', 3, 190);

INSERT INTO users (login, password, full_name, role) VALUES
('login1', 'password1', 'Medvedeva Alisa Ivanovna', 'CUSTOMER'),
('login2', 'password2', 'Rodionova Sophia Dmitrievna', 'CUSTOMER'),
('login3', 'password3', 'Romanov Andrey Vladimirovich', 'CUSTOMER'),
('test', '$2a$10$dm.JAkD.U.zcdRRQbyPnDeDCLSGooNspzAkp2o.j2b4xTeJ67YEie', 'Administrator Test Entity', 'ADMIN'),
('user', '$2a$10$dm.JAkD.U.zcdRRQbyPnDeDCLSGooNspzAkp2o.j2b4xTeJ67YEie', 'Customer Test Entity', 'CUSTOMER');

INSERT INTO tickets (date_time, user_id, route_id, price, seat_number) VALUES
('2025-03-17T10:20:30.123456789', NULL, 5, 10.0, '1A'),
('2025-05-19T15:25:10.123456789', NULL, 5, 10.0, '2B'),
('2025-05-19T15:25:10.123456789', NULL, 3, 10.0, '2B'),
('2025-04-18T20:10:30.123456789', 1, 4, 11.0, '1A');
