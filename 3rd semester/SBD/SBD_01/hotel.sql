-- =====================================================
--  HOTEL DATABASE - practice data for SQL tasks (MySQL 8)
--  Run the whole file once. You can run it again any
--  time to reset everything back to the original data.
-- =====================================================

CREATE DATABASE IF NOT EXISTS hotel;
USE hotel;

-- Delete old tables first (children before parents,
-- because of the foreign keys)
DROP TABLE IF EXISTS Reservation;
DROP TABLE IF EXISTS Room;
DROP TABLE IF EXISTS Guest;
DROP TABLE IF EXISTS Category;

-- -----------------------------------------------------
--  TABLES
-- -----------------------------------------------------

CREATE TABLE Category (
    IdCategory  INT           PRIMARY KEY,
    Name        VARCHAR(50)   NOT NULL,      -- fixed: was "number" in the PDF
    Price       DECIMAL(8,2)  NOT NULL
);

CREATE TABLE Room (
    RoomNumber          INT  PRIMARY KEY,
    Capacity            INT  NOT NULL,       -- number of beds
    Category_IdCategory INT  NOT NULL,
    FOREIGN KEY (Category_IdCategory) REFERENCES Category(IdCategory)
);

CREATE TABLE Guest (
    IdGuest   INT           PRIMARY KEY,
    Name      VARCHAR(50)   NOT NULL,        -- first name
    Surname   VARCHAR(50)   NOT NULL,        -- last name
    Discount  DECIMAL(5,2)  NULL             -- percent, can be NULL
);

CREATE TABLE Reservation (
    IdReservation    INT       PRIMARY KEY,
    DateStart        DATE      NOT NULL,
    DateEnd          DATE      NOT NULL,
    Paid             SMALLINT  NOT NULL,     -- 1 = paid, 0 = not paid
    Guest_IdGuest    INT       NOT NULL,
    Room_RoomNumber  INT       NOT NULL,
    FOREIGN KEY (Guest_IdGuest)   REFERENCES Guest(IdGuest),
    FOREIGN KEY (Room_RoomNumber) REFERENCES Room(RoomNumber),
    CHECK (Paid IN (0, 1)),
    CHECK (DateEnd > DateStart)
);

-- -----------------------------------------------------
--  DATA
-- -----------------------------------------------------

INSERT INTO Category (IdCategory, Name, Price) VALUES
(1, 'Standard', 150.00),
(2, 'Luxury',   400.00),
(3, 'Family',   250.00),
(4, 'Economy',   90.00);

INSERT INTO Room (RoomNumber, Capacity, Category_IdCategory) VALUES
-- Standard (category 1)
(105, 2, 1),
(101, 3, 1),
(112, 2, 1),
(108, 4, 1),
(120, 3, 1),
-- Luxury (category 2)
(210, 1, 2),
(202, 2, 2),
(215, 3, 2),
(205, 4, 2),
-- Family (category 3)
(310, 4, 3),
(303, 5, 3),
(307, 6, 3),
-- Economy (category 4)
(402, 1, 4),
(401, 2, 4);

INSERT INTO Guest (IdGuest, Name, Surname, Discount) VALUES
(1,  'Anna',      'Kowalska',    10.00),
(2,  'Bartosz',   'Nowak',        0.00),
(3,  'Katarzyna', 'Wisniewska',   NULL),
(4,  'Piotr',     'Wojcik',       5.00),
(5,  'Barbara',   'Kaminska',    15.00),
(6,  'Tomasz',    'Lewandowski',  NULL),
(7,  'Magdalena', 'Zielinska',   20.00),
(8,  'Jan',       'Borkowski',    0.00),
(9,  'Ewa',       'Dabrowska',    NULL),
(10, 'Marek',     'Kozlowski',    5.00);

INSERT INTO Reservation (IdReservation, DateStart, DateEnd, Paid, Guest_IdGuest, Room_RoomNumber) VALUES
-- Anna
(1,  '2025-01-10', '2025-01-15', 1, 1, 101),
(2,  '2025-03-05', '2025-03-12', 1, 1, 303),
(3,  '2025-06-20', '2025-06-22', 1, 1, 105),
(4,  '2025-09-01', '2025-09-04', 0, 1, 307),
-- Bartosz
(5,  '2025-02-14', '2025-02-16', 1, 2, 112),
(6,  '2025-07-10', '2025-07-20', 0, 2, 310),
(7,  '2025-11-03', '2025-11-05', 0, 2, 101),
-- Katarzyna
(8,  '2025-04-01', '2025-04-08', 1, 3, 303),
(9,  '2025-08-15', '2025-08-18', 1, 3, 307),
-- Piotr
(10, '2025-01-20', '2025-01-21', 1, 4, 108),
(11, '2025-03-15', '2025-03-18', 0, 4, 202),
(12, '2025-05-05', '2025-05-10', 1, 4, 105),
(13, '2025-10-10', '2025-10-12', 0, 4, 215),
(14, '2026-01-02', '2026-01-04', 0, 4, 112),
-- Barbara
(15, '2025-02-01', '2025-02-05', 0, 5, 205),
(16, '2025-06-01', '2025-06-03', 0, 5, 202),
(17, '2025-12-24', '2025-12-27', 0, 5, 215),
-- Tomasz
(18, '2025-08-08', '2025-08-09', 0, 6, 108),
-- Magdalena
(19, '2025-04-12', '2025-04-14', 1, 7, 101),
(20, '2025-07-25', '2025-07-30', 1, 7, 310),
(21, '2026-02-10', '2026-02-13', 0, 7, 205),
-- Jan
(22, '2025-05-18', '2025-05-21', 1, 8, 303),
(23, '2025-09-09', '2025-09-10', 0, 8, 402);

-- -----------------------------------------------------
--  QUICK CHECK - run these to see the data
-- -----------------------------------------------------
SELECT * FROM Category;
SELECT * FROM Room;
SELECT * FROM Guest;
SELECT * FROM Reservation;
