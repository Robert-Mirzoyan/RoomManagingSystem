INSERT INTO "user" (id, name, email, role)
VALUES
    (1, 'Robert', 'robert_mirzoyan@edu.aua.am', 'Student'),
    (2, 'Ruben', 'ruben@edu.aua.am', 'Student'),
    (3, 'Ashot', 'ashot@edu.aua.am', 'Student'),
    (4, 'Peto', 'peto@edu.aua.am', 'Student');

INSERT INTO room (id, name, type, capacity)
VALUES
    (1, 'Room A', 'Lab', 20),
    (2, 'Room B', 'Auditorium', 30);

INSERT INTO timeslot (id, start_time, end_time)
VALUES
    (1, '2025-06-03 12:30', '2025-06-03 14:00'),
    (2, '2025-06-02 12:30', '2025-06-02 14:00'),
    (3, '2025-06-04 12:30', '2025-06-04 14:00'),
    (4, '2025-06-01 12:30', '2025-06-01 14:00'),
    (5, '2025-06-02 16:30', '2025-06-02 18:00');

INSERT INTO booking (id, user_id, room_id, timeslot_id, status)
VALUES
    (1, 1, 1, 1, 'PENDING'),
    (2, 2, 1, 2, 'PENDING'),
    (3, 1, 1, 3, 'PENDING'),
    (4, 2, 2, 4, 'PENDING'),
    (5, 1, 2, 5, 'PENDING');

INSERT INTO booking_participant (booking_id, user_id)
VALUES
    (1, 4),
    (2, 3),
    (2, 4),
    (3, 3),
    (4, 3),
    (5, 4);


-- CRUD
SELECT * FROM booking WHERE id = 1;

UPDATE booking
SET status = 'APPROVED'
WHERE id = 1;

DELETE FROM booking WHERE id = 6;

-- Search query with dynamic filters, pagination and sorting
SELECT
    b.id AS booking_id,
    u.name AS booked_by,
    r.name AS room_name,
    t.start_time,
    t.end_time,
    b.status
FROM booking b
    JOIN "user" u ON b.user_id = u.id
    JOIN room r ON b.room_id = r.id
    JOIN timeslot t ON b.timeslot_id = t.id
WHERE b.status = 'PENDING'
ORDER BY t.start_time ASC
LIMIT 10;

-- Search query with joined data for your use-cases
-- All bookings where Ashot is participant
SELECT
    b.id AS booking_id,
    u.name AS booked_by,
    r.name AS room_name,
    t.start_time,
    t.end_time,
    b.status
FROM booking_participant bp
JOIN booking b ON bp.booking_id = b.id
JOIN "user" u ON b.user_id = u.id
JOIN room r ON b.room_id = r.id
JOIN timeslot t ON b.timeslot_id = t.id
WHERE bp.user_id = 3
ORDER BY t.start_time;

-- Statistic query
SELECT
    r.name AS room_name, number_of_booking
FROM room as r
JOIN (
    SELECT COUNT(room_id) AS number_of_booking, room_id
    FROM booking
    GROUP BY room_id
) AS b
ON b.room_id = r.id
ORDER BY number_of_booking ASC;

-- Top 1 user with bookings
SELECT
    u.name AS user_name, number_of_booking
FROM "user" as u
LEFT JOIN (
    SELECT COUNT(user_id) AS number_of_booking, user_id
    FROM booking
    GROUP BY user_id
) AS b
ON u.id = b.user_id
ORDER BY number_of_booking DESC
LIMIT 1;