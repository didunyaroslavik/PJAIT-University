-- 1. Provide the room with the lowest number in its room category.

SELECT r.RoomNumber, r.Capacity, r.Category_IdCategory
FROM Room r
WHERE r.RoomNumber = (
    SELECT MIN(r2.RoomNumber)
    FROM Room r2
    WHERE r2.Category_IdCategory = r.Category_IdCategory
);

-- 1 (practice). For each guest, show their earliest reservation (the one with the smallest DateStart). Show: IdReservation, Guest_IdGuest, DateStart.

SELECT r.IdReservation, r.Guest_IdGuest, r.DateStart AS EarliestDate
FROM Reservation r
WHERE r.DateStart = (
    SELECT MIN(r2.DateStart)
    FROM Reservation r2
    WHERE r2.Guest_IdGuest = r.Guest_IdGuest
);



-- 2. List the number of paid reservations for each guest. Do not list guests who have made fewer than 3 reservations.

SELECT g.Name, g.Surname, SUM(r.Paid) AS PaidReservations
FROM Guest g
JOIN Reservation r ON g.IdGuest = r.Guest_IdGuest
GROUP BY g.IdGuest, g.Name, g.Surname
HAVING COUNT(*) >= 3;

-- 2 (practice). For each room, show how many unpaid reservations it has. Show only rooms that have at least 3 reservations in total.

SELECT Room_RoomNumber, SUM(1 - Paid) AS UnpaidReservations
FROM Reservation
GROUP BY Room_RoomNumber
HAVING COUNT(*) >= 3;



-- 3. Find the category with the most rooms.

SELECT Category_IdCategory, COUNT(*) AS RoomCount
FROM Room
GROUP BY Category_IdCategory
HAVING COUNT(*) >= ALL (
    SELECT COUNT(*)
    FROM Room
    GROUP BY Category_IdCategory
);

-- 3 (practice). Find the room (or rooms!) with the most reservations. Show the room number and the number of reservations.

SELECT Room_RoomNumber, COUNT(*) AS ReservationCount
FROM Reservation
GROUP BY Room_RoomNumber
HAVING COUNT(*) >= ALL (
    SELECT COUNT(*)
    FROM Reservation
    GROUP BY Room_RoomNumber
);



-- 4. List without repetition the guests who booked rooms in both category 3 and category 1.

SELECT g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
WHERE ro.Category_IdCategory = 1

INTERSECT

SELECT g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
WHERE ro.Category_IdCategory = 3;

-- 4 (practice). List without repetition the guests who booked a room in category 1, but never in category 3.

SELECT g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
WHERE ro.Category_IdCategory = 1

EXCEPT

SELECT g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
WHERE ro.Category_IdCategory = 3;




-- 5. List the duration of all reservations. Provide the customer's first and last name, and name the column with the time “Reservation time.”

SELECT g.Name, g.Surname, DATEDIFF(re.DateEnd, re.DateStart) AS 'Reservation time'
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest;

-- 5 (practice). For each guest, show the total number of nights they booked (all their reservations added together).
--               Name the column Total nights. Sort from the guest with the most nights to the least.

SELECT g.Name, g.Surname, SUM(DATEDIFF(re.DateEnd, re.DateStart)) AS 'Total nights'
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
GROUP BY g.IdGuest, g.Name, g.Surname
ORDER BY `Total nights` DESC;



-- 6. List the number of unpaid reservations for each guest. Do not list guests whose names begin with the letter ‘B’.

SELECT g.Name, g.Surname, SUM(1 - r.Paid) AS UnpaidReservations
FROM Guest g
JOIN Reservation r ON g.IdGuest = r.Guest_IdGuest
WHERE g.Name NOT LIKE 'B%'
GROUP BY g.IdGuest, g.Name, g.Surname;

-- 6 (practice). For each guest whose surname does not end with “ski” or “ska”, show how many of their reservations were longer than 2 nights.

SELECT g.Name, g.Surname,
       SUM(CASE WHEN DATEDIFF(r.DateEnd, r.DateStart) > 2 THEN 1 ELSE 0 END) AS ReservationsLongerThan2
FROM Guest g
JOIN Reservation r ON g.IdGuest = r.Guest_IdGuest
WHERE g.Surname NOT LIKE '%sk_'
GROUP BY g.IdGuest, g.Name, g.Surname;



-- 7. Find rooms that have never been booked.

SELECT RoomNumber, Capacity
FROM Room
WHERE RoomNumber NOT IN (
    SELECT Room_RoomNumber
    FROM Reservation
);

-- 7 (practice). Find rooms that were never booked by a guest whose first name starts with “B”. Show room number and capacity.

SELECT RoomNumber, Capacity
FROM Room
WHERE RoomNumber NOT IN (
    SELECT r.Room_RoomNumber
    FROM Reservation r
    JOIN Guest g ON g.IdGuest = r.Guest_IdGuest
    WHERE g.Name LIKE 'B%'
);

-- or

SELECT ro.RoomNumber, ro.Capacity
FROM Room ro
WHERE NOT EXISTS (
    SELECT 1
    FROM Reservation re
    JOIN Guest g ON g.IdGuest = re.Guest_IdGuest
    WHERE g.Name LIKE 'B%' AND re.Room_RoomNumber = ro.RoomNumber
);



-- 8. List guests who have never rented any room (name, surname).

SELECT g.IdGuest, g.Name, g.Surname, r.IdReservation, r.DateStart
FROM Guest g
LEFT JOIN Reservation r ON g.IdGuest = r.Guest_IdGuest
WHERE r.IdReservation IS NULL;

-- 8 (practice). For each guest (all 10, also those with no reservations!), show the number of reservations.
--               Sort from the most to the least. Ewa and Marek must appear with 0.

SELECT g.IdGuest, g.Name, g.Surname, COUNT(r.IdReservation) AS NumOfReservations
FROM Guest g
LEFT JOIN Reservation r ON g.IdGuest = r.Guest_IdGuest
GROUP BY g.IdGuest, g.Name, g.Surname
ORDER BY NumOfReservations DESC;



-- 9. List the booking data in the form: first name, last name, room number, category name. Sort by category name and descending by last name.

SELECT g.Name AS FirstName, g.Surname AS LastName, ro.RoomNumber, c.Name AS CategoryName
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
ORDER BY c.Name, g.Surname DESC;

-- 9 (practice). List all reservations that started in 2025 in Luxury or Family rooms.
--               Show: guest’s last name, room number, category name, and number of nights (column name: Nights).
--               Sort by category name A → Z, and inside each category, by nights from the most to the least.

SELECT g.Surname AS LastName, ro.RoomNumber, c.Name AS CategoryName, DATEDIFF(re.DateEnd, re.DateStart) AS Nights
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE YEAR(re.DateStart) = 2025 AND c.Name IN ('Luxury', 'Family')
ORDER BY c.Name, Nights;



-- 10. List luxury rooms that have more beds than the minimum number of beds in a standard room.

SELECT ro.RoomNumber, ro.Capacity
FROM Room ro
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name = 'Luxury'
AND ro.Capacity > (
    SELECT MIN(ro2.Capacity)
    FROM Room ro2
    JOIN Category c2 ON ro2.Category_IdCategory = c2.IdCategory
    WHERE c2.Name = 'Standard'
    );

-- 10 (practice). List Family rooms that have more beds than every Standard room.

SELECT ro.RoomNumber, ro.Capacity
FROM Room ro
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name = 'Family'
AND ro.Capacity > (
    SELECT MAX(ro2.Capacity)
    FROM Room ro2
    JOIN Category c2 ON ro2.Category_IdCategory = c2.IdCategory
    WHERE c2.Name = 'Standard'
    );

-- or

SELECT ro.RoomNumber, ro.Capacity
FROM Room ro
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name = 'Family'
AND ro.Capacity > ALL(
    SELECT ro2.Capacity
    FROM Room ro2
    JOIN Category c2 ON ro2.Category_IdCategory = c2.IdCategory
    WHERE c2.Name = 'Standard'
    );



-- Mini Test

-- 1. List guests whose first name ends with “a”. Show first name, surname and discount.
--    If the discount is empty, show 0. Sort by discount from the biggest to the smallest.

SELECT Name, Surname, IFNULL(Discount, 0) AS Discount
FROM Guest
WHERE Name LIKE '%a'
ORDER BY Discount DESC;

-- 2. For each category (show the category name), show the number of rooms and the total number of beds in all its rooms.

SELECT c.Name, COUNT(*) AS RoomCount, SUM(ro.Capacity) AS TotalBeds
FROM Room ro
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
GROUP BY c.IdCategory, c.Name;

-- 3. List reservations that lasted longer than the average reservation (in nights).
--    Show the guest’s surname, room number and number of nights.

SELECT g.Surname, re.Room_RoomNumber, DATEDIFF(re.DateEnd, re.DateStart) AS NumOfNights
FROM Reservation re
JOIN Guest g ON re.Guest_IdGuest = g.IdGuest
WHERE DATEDIFF(re.DateEnd, re.DateStart) > (
    SELECT AVG(DATEDIFF(re2.DateEnd, re2.DateStart))
    FROM Reservation re2
);

-- 4. List guests (first name, surname) who booked at least one room in the Economy or Luxury category, but never booked a Standard room.

SELECT g.Name, g.Surname, c.Name
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name IN ('Luxury', 'Economy')

EXCEPT

SELECT g.Name, g.Surname, c.Name
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name = 'Standard';

-- 5. For each guest who has reservations, show the total cost of all their reservations.
--    The cost of one reservation = category price × number of nights.
--    Show first name, surname and the total cost (column name: Total cost). Sort from the biggest cost to the smallest.

SELECT g.Name, g.Surname, SUM(DATEDIFF(re.DateEnd, re.DateStart) * c.Price) AS `Total cost`
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
GROUP BY g.IdGuest, g.Name, g.Surname
ORDER BY `Total cost` DESC;

-- 6. For each category, show the room (or rooms) with the most beds. Show the category name, room number and capacity.

SELECT c.Name, ro.RoomNumber, ro.Capacity
FROM Category c
JOIN Room ro ON c.IdCategory = ro.Category_IdCategory
WHERE ro.Capacity >= ALL(
    SELECT ro2.Capacity
    FROM Room ro2
    WHERE ro2.Category_IdCategory = ro.Category_IdCategory
);

-- 7. Find the guest (or guests, if there’s a tie) who spent the most nights in total in the hotel. Show first name, surname and total nights.

SELECT g.Name, g.Surname, SUM(DATEDIFF(re.DateEnd, re.DateStart)) AS TotalNights
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
GROUP BY g.Name, g.Surname
HAVING `TotalNights` >= ALL (
    SELECT SUM(DATEDIFF(re2.DateEnd, re2.DateStart))
    FROM Reservation re2
    GROUP BY re2.Guest_IdGuest
);
