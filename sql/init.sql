DROP TABLE IF EXISTS booking_participant CASCADE;
DROP TABLE IF EXISTS booking CASCADE;
DROP TABLE IF EXISTS timeslot CASCADE;
DROP TABLE IF EXISTS room CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;

CREATE TABLE "user" (
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('Student', 'Admin', 'FacultyManager'))
);

CREATE TABLE room (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE timeslot (
    id SERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    CHECK (end_time > start_time)
);

CREATE TABLE booking (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL ,
    room_id INT NOT NULL ,
    timeslot_id INT NOT NULL ,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    FOREIGN KEY (user_id) REFERENCES "user"(id),
    FOREIGN KEY (room_id)  REFERENCES room(id),
    FOREIGN KEY (timeslot_id) REFERENCES timeslot(id)
);

-- Many-to-Many: Booking User
CREATE TABLE booking_participant (
    booking_id INT,
    user_id INT,
    PRIMARY KEY (booking_id, user_id),
    FOREIGN KEY (booking_id) REFERENCES booking(id),
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);

INSERT INTO "user" (id, name, email, role)
VALUES
    (101, 'Robert', 'robert_mirzoyan@edu.aua.am', 'Student'),
    (102, 'Ruben', 'ruben@edu.aua.am', 'Student'),
    (103, 'Ashot', 'ashot@edu.aua.am', 'Student'),
    (104, 'Petros', 'petros@edu.aua.am', 'Student'),
    (201, 'Suren', 'skachat@aua.am', 'FacultyManager'),
    (301, 'Artur', 'artur@edu.aua.am', 'Admin');

INSERT INTO room (name, type, capacity)
VALUES
    ('Room A', 'Lab', 20),
    ('Room B', 'Auditorium', 30);

INSERT INTO timeslot (start_time, end_time)
VALUES
    ('2025-06-03 12:30', '2025-06-03 14:00'),
    ('2025-06-02 12:30', '2025-06-02 14:00'),
    ('2025-06-04 12:30', '2025-06-04 14:00'),
    ('2025-06-01 12:30', '2025-06-01 14:00'),
    ('2025-06-02 16:30', '2025-06-02 18:00'),
    ('2025-06-07 16:30', '2025-06-07 18:00');

INSERT INTO booking (user_id, room_id, timeslot_id, status)
VALUES
    (101, 1, 1, 'PENDING'),
    (102, 1, 2, 'PENDING'),
    (101, 1, 3, 'PENDING'),
    (102, 2, 4, 'PENDING'),
    (101, 2, 5, 'PENDING');

INSERT INTO booking_participant (booking_id, user_id)
VALUES
    (1, 104),
    (2, 103),
    (2, 104),
    (3, 103),
    (4, 103),
    (5, 104);