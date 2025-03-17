-- Test tickets to test TicketServiceImplIntegrationTest.java
INSERT INTO tickets (date_time, user_id, route_id, price, seat_number) VALUES
('2025-03-17T10:20:30.123456789', NULL, 3, 10.0, '1A'),
('2025-05-19T15:25:10.123456789', NULL, 5, 10.0, '2B'),
('2025-05-19T15:25:10.123456789', NULL, 3, 10.0, '2B'),
('2025-04-18T20:10:30.123456789', 1, 4, 11.0, '1A');
