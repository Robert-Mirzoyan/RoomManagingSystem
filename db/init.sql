DROP TABLE IF EXISTS booking_manager CASCADE;
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
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE timeslot (
    id INT NOT NULL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    CHECK (end_time > start_time)
);

CREATE TABLE booking (
    id INT NOT NULL PRIMARY KEY,
    user_id INT NOT NULL ,
    room_id INT NOT NULL ,
    timeslot_id INT NOT NULL ,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELED')),
    FOREIGN KEY (user_id) REFERENCES "user"(id),
    FOREIGN KEY (room_id)  REFERENCES room(id),
    FOREIGN KEY (timeslot_id) REFERENCES timeslot(id),
    UNIQUE (timeslot_id)
);

-- Many-to-Many: Booking User
CREATE TABLE booking_manager (
    booking_id INT,
    user_id INT,
    PRIMARY KEY (booking_id, user_id),
    FOREIGN KEY (booking_id) REFERENCES booking(id),
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);