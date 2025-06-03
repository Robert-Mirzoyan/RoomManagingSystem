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