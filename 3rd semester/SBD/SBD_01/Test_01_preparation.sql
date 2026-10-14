-- =====================================================================
--  SQL TEST PREPARATION (MySQL 8)
--  Databases needed:
--    * HOTEL     -> run hotel.sql first  (Guest, Reservation, Room, Category)
--    * EMPLOYEES -> run emp.sql first    (Emp, Dept, Salgrade, Proj, Emp_Proj)
--  Note: the test itself used MS SQL Server. Main syntax differences:
--    LIMIT -> TOP, DATEDIFF(end, start) -> DATEDIFF(day, start, end),
--    IFNULL -> ISNULL, `name` -> [name], CHAR_LENGTH -> LEN
-- =====================================================================


-- #####################################################################
--  PART 1: HOTEL
-- #####################################################################

-- Extra data for Task 2 (Jack Blue). Run once after hotel.sql.
INSERT INTO Guest VALUES (11, 'Jack', 'Blue', 5.00);

INSERT INTO Reservation VALUES
(24, '2024-01-05', '2024-01-08', 1, 11, 101),
(25, '2024-03-10', '2024-03-12', 1, 11, 101),
(26, '2024-06-01', '2024-06-04', 0, 11, 101),
(27, '2024-02-14', '2024-02-16', 1, 11, 303),
(28, '2024-08-20', '2024-08-25', 1, 11, 303),
(29, '2024-11-11', '2024-11-12', 0, 11, 402),
(30, '2024-04-01', '2024-04-03', 1, 11, 205),
(31, '2024-09-09', '2024-09-10', 1, 11, 205);


-- 1. List guests who have never rented a luxurious room.
--    "Never" -> list A (all guests) EXCEPT list B (guests who DID rent Luxury).

SELECT g.IdGuest, g.Name, g.Surname
FROM Guest g

EXCEPT

SELECT g.IdGuest, g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name = 'Luxury';

-- Same with NOT EXISTS
SELECT g.Name, g.Surname
FROM Guest g
WHERE NOT EXISTS (
    SELECT 1
    FROM Reservation r
    JOIN Room ro ON r.Room_RoomNumber = ro.RoomNumber
    JOIN Category c ON ro.Category_IdCategory = c.IdCategory
    WHERE c.Name = 'Luxury'
      AND r.Guest_IdGuest = g.IdGuest
);

-- 1 (practice). List guests who have never had an unpaid reservation.

SELECT g.IdGuest, g.Name, g.Surname
FROM Guest g

EXCEPT

SELECT g.IdGuest, g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
WHERE re.Paid = 0;



-- 2. List how many times each room was rented by Jack Blue. The result should be in the form:
--    RoomNumber, number. Sort in descending order by number. Do not list luxurious rooms.
--    "Don't count these rows" -> WHERE (here <> 'Luxury' is correct).

SELECT ro.RoomNumber, COUNT(*) AS number
FROM Reservation r
JOIN Guest g    ON g.IdGuest = r.Guest_IdGuest
JOIN Room ro    ON ro.RoomNumber = r.Room_RoomNumber
JOIN Category c ON c.IdCategory = ro.Category_IdCategory
WHERE g.Name = 'Jack' AND g.Surname = 'Blue'
  AND c.Name <> 'Luxury'
GROUP BY ro.RoomNumber
ORDER BY number DESC;

-- 2 (practice). For Anna Kowalska, show the total number of nights she spent in each room.
--               Do not include Family rooms. Show RoomNumber and nights, sorted from the most nights.
--               "Total" -> SUM (COUNT would count reservations, not nights).

SELECT ro.RoomNumber, SUM(DATEDIFF(r.DateEnd, r.DateStart)) AS nights
FROM Reservation r
JOIN Guest g    ON g.IdGuest = r.Guest_IdGuest
JOIN Room ro    ON ro.RoomNumber = r.Room_RoomNumber
JOIN Category c ON c.IdCategory = ro.Category_IdCategory
WHERE g.Name = 'Anna' AND g.Surname = 'Kowalska'
  AND c.Name <> 'Family'
GROUP BY ro.RoomNumber
ORDER BY nights DESC;



-- 3. List the number of paid reservations for each guest.
--    Do not list guests who have made fewer than two reservations or reservations from category ID 3.
--    Count condition -> HAVING. "Guests who ever did X" -> NOT IN list.
--    SUM(Paid) keeps guests with 0 paid reservations.

SELECT g.Name, g.Surname, SUM(r.Paid) AS PaidReservations
FROM Guest g
JOIN Reservation r ON g.IdGuest = r.Guest_IdGuest
WHERE g.IdGuest NOT IN (
    SELECT r2.Guest_IdGuest
    FROM Reservation r2
    JOIN Room ro2 ON r2.Room_RoomNumber = ro2.RoomNumber
    WHERE ro2.Category_IdCategory = 3
)
GROUP BY g.IdGuest, g.Name, g.Surname
HAVING COUNT(*) >= 2;

-- 3 (practice). For each guest, show the total number of nights.
--               Do not list guests who have ever rented a Luxury room or who made fewer than 3 reservations.

SELECT g.Name, g.Surname, SUM(DATEDIFF(re.DateEnd, re.DateStart)) AS TotalNights
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
WHERE g.IdGuest NOT IN (
    SELECT re2.Guest_IdGuest
    FROM Reservation re2
    JOIN Room ro2 ON re2.Room_RoomNumber = ro2.RoomNumber
    JOIN Category c2 ON ro2.Category_IdCategory = c2.IdCategory
    WHERE c2.Name = 'Luxury'
)
GROUP BY g.IdGuest, g.Name, g.Surname
HAVING COUNT(*) >= 3;



-- 4. List standard rooms that have fewer beds than the maximum number of beds in a room of category 2.
--    Regular subquery: runs once, gives one number.

SELECT ro.RoomNumber, ro.Capacity AS Beds
FROM Room ro
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name = 'Standard'
  AND ro.Capacity < (
      SELECT MAX(ro2.Capacity)
      FROM Room ro2
      WHERE ro2.Category_IdCategory = 2
  );

-- Same with ANY  (< ANY = < MAX)
SELECT ro.RoomNumber, ro.Capacity AS Beds
FROM Room ro
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name = 'Standard'
  AND ro.Capacity < ANY (
      SELECT ro2.Capacity
      FROM Room ro2
      WHERE ro2.Category_IdCategory = 2
  );

-- 4 (practice). List all rooms that have fewer beds than every Family room.

-- With ALL  (< ALL = < MIN)
SELECT ro.RoomNumber, ro.Capacity AS Beds
FROM Room ro
WHERE ro.Capacity < ALL (
    SELECT ro2.Capacity
    FROM Room ro2
    JOIN Category c2 ON ro2.Category_IdCategory = c2.IdCategory
    WHERE c2.Name = 'Family'
);

-- With a group function instead
SELECT ro.RoomNumber, ro.Capacity AS Beds
FROM Room ro
WHERE ro.Capacity < (
    SELECT MIN(ro2.Capacity)
    FROM Room ro2
    JOIN Category c2 ON ro2.Category_IdCategory = c2.IdCategory
    WHERE c2.Name = 'Family'
);



-- 5. List the duration of all reservations. Enter the customer's first and last name,
--    and name the column with the time "Reservation time".
--    MySQL: DATEDIFF(end, start). Names with spaces -> backticks.

SELECT g.Name, g.Surname, DATEDIFF(re.DateEnd, re.DateStart) AS `Reservation time`
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest;

-- 5 (practice). For each guest (also those without reservations), show their longest reservation in nights.
--               Name the column `Longest stay`. Guests with no reservations should show 0, not NULL.

SELECT g.Name, g.Surname, IFNULL(MAX(DATEDIFF(re.DateEnd, re.DateStart)), 0) AS `Longest stay`
FROM Guest g
LEFT JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
GROUP BY g.IdGuest, g.Name, g.Surname;



-- 6. Find customers who have used the hotel's services twice.

SELECT g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
GROUP BY g.IdGuest, g.Name, g.Surname
HAVING COUNT(*) = 2;

-- Same with a correlated COUNT (also works for 0)
SELECT g.Name, g.Surname
FROM Guest g
WHERE (
    SELECT COUNT(*)
    FROM Reservation r
    WHERE r.Guest_IdGuest = g.IdGuest
) = 2;

-- 6 (practice). Find guests who used the hotel at least 3 times, but never had a reservation that started in 2026.

SELECT g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
WHERE g.IdGuest NOT IN (
    SELECT re2.Guest_IdGuest
    FROM Reservation re2
    WHERE YEAR(re2.DateStart) = 2026
)
GROUP BY g.IdGuest, g.Name, g.Surname
HAVING COUNT(*) >= 3;



-- 7. List 2009 bookings of customers whose last name begins with the letter "B" or has 5 letters.
--    Give first name, last name, date of booking, room number and category name.
--    (Our data has no 2009 bookings, so 2025 is used.)
--    AND + OR together -> ALWAYS brackets around the OR.

SELECT g.Name, g.Surname, re.DateStart, ro.RoomNumber, c.Name AS CategoryName
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE YEAR(re.DateStart) = 2025
  AND (g.Surname LIKE 'B%' OR g.Surname LIKE '_____');

-- 7 (practice). List bookings from 2024 or 2026 of guests whose surname has at least 6 letters,
--               in Luxury or Standard rooms. Show first name, surname, start date, room number and category name.

SELECT g.Name, g.Surname, re.DateStart, ro.RoomNumber, c.Name AS CategoryName
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE YEAR(re.DateStart) IN (2024, 2026)
  AND g.Surname LIKE '______%'
  AND c.Name IN ('Luxury', 'Standard');



-- 8. List without repetition the guests who booked only standard rooms, and never tourist rooms.
--    "Only X" = did X AND never did anything that is not X.

SELECT g.IdGuest, g.Name, g.Surname
FROM Guest g
JOIN Reservation r ON g.IdGuest = r.Guest_IdGuest
JOIN Room ro ON r.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name = 'Standard'

EXCEPT

SELECT g.IdGuest, g.Name, g.Surname
FROM Guest g
JOIN Reservation r ON g.IdGuest = r.Guest_IdGuest
JOIN Room ro ON r.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
WHERE c.Name <> 'Standard';

-- Same with GROUP BY + HAVING (count the "bad" rows)
SELECT g.Name, g.Surname
FROM Guest g
JOIN Reservation r ON g.IdGuest = r.Guest_IdGuest
JOIN Room ro ON r.Room_RoomNumber = ro.RoomNumber
JOIN Category c ON ro.Category_IdCategory = c.IdCategory
GROUP BY g.IdGuest, g.Name, g.Surname
HAVING SUM(CASE WHEN c.Name <> 'Standard' THEN 1 ELSE 0 END) = 0;

-- 8 (practice). List guests who booked only rooms with at least 3 beds.

SELECT g.IdGuest, g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
WHERE ro.Capacity >= 3

EXCEPT

SELECT g.IdGuest, g.Name, g.Surname
FROM Guest g
JOIN Reservation re ON g.IdGuest = re.Guest_IdGuest
JOIN Room ro ON re.Room_RoomNumber = ro.RoomNumber
WHERE ro.Capacity < 3;



-- 9. Find the category with the fewest rooms.
--    Tie-safe: <= ALL. Inner GROUP BY must make the same groups as the outer one.

SELECT c.Name, COUNT(*) AS RoomCount
FROM Category c
JOIN Room ro ON c.IdCategory = ro.Category_IdCategory
GROUP BY c.IdCategory, c.Name
HAVING COUNT(*) <= ALL (
    SELECT COUNT(*)
    FROM Room
    GROUP BY Category_IdCategory
);

-- Safer version: also finds a category with 0 rooms (LEFT JOIN + COUNT(column))
SELECT c.Name, COUNT(ro.RoomNumber) AS RoomCount
FROM Category c
LEFT JOIN Room ro ON ro.Category_IdCategory = c.IdCategory
GROUP BY c.IdCategory, c.Name
HAVING COUNT(ro.RoomNumber) <= ALL (
    SELECT COUNT(ro2.RoomNumber)
    FROM Category c2
    LEFT JOIN Room ro2 ON ro2.Category_IdCategory = c2.IdCategory
    GROUP BY c2.IdCategory
);

-- 9 (practice). Find the room (or rooms) with the fewest reservations. Include rooms that were never booked.

SELECT ro.RoomNumber, COUNT(re.IdReservation) AS Reservations
FROM Room ro
LEFT JOIN Reservation re ON ro.RoomNumber = re.Room_RoomNumber
GROUP BY ro.RoomNumber
HAVING COUNT(re.IdReservation) <= ALL (
    SELECT COUNT(re2.IdReservation)
    FROM Room ro2
    LEFT JOIN Reservation re2 ON ro2.RoomNumber = re2.Room_RoomNumber
    GROUP BY ro2.RoomNumber
);



-- 10. List the three rooms with the largest number of beds in the hotel.
--     Sort them by number and number of beds.

-- With LIMIT: take the top 3 by beds, then sort the result by room number and beds.
-- (Loses ties: rooms 108, 205 and 310 all have 4 beds, only one of them is kept.)
SELECT t.RoomNumber, t.Capacity
FROM (
    SELECT RoomNumber, Capacity
    FROM Room
    ORDER BY Capacity DESC, RoomNumber
    LIMIT 3
) AS t
ORDER BY t.RoomNumber, t.Capacity;

-- Tie-safe: keep a room if fewer than 3 rooms have MORE beds than it.
SELECT ro.RoomNumber, ro.Capacity
FROM Room ro
WHERE (
    SELECT COUNT(*)
    FROM Room ro2
    WHERE ro2.Capacity > ro.Capacity
) < 3
ORDER BY ro.RoomNumber, ro.Capacity;



-- #####################################################################
--  PART 2: EMPLOYEES
-- #####################################################################

-- 1. Show the average salary of employees in the second salary group.
--    No FK between Emp and Salgrade -> join by salary RANGE (non-equi join).

SELECT AVG(e.sal) AS AvgSalary
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN s.losal AND s.hisal
WHERE s.grade = 2;

-- Same with subqueries for the range limits
SELECT AVG(sal) AS AvgSalary
FROM Emp
WHERE sal BETWEEN (SELECT losal FROM Salgrade WHERE grade = 2)
              AND (SELECT hisal FROM Salgrade WHERE grade = 2);

-- 1 (practice). For each salary grade, show the number of employees and their average salary
--               (rounded to 2 decimals). Sort by grade.

SELECT s.grade, COUNT(e.empno) AS NumOfEmployees, ROUND(AVG(e.sal), 2) AS AvgSalary
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN s.losal AND s.hisal
GROUP BY s.grade
ORDER BY s.grade;



-- 2. Find all employees working in the same position as BLAKE.
--    IN is safer than = (works even if the subquery returns more than one row).

SELECT ename, job
FROM Emp
WHERE job IN (
    SELECT job
    FROM Emp
    WHERE ename = 'BLAKE'
)
  AND ename <> 'BLAKE';

-- Same with a self-join
SELECT e.ename, e.job
FROM Emp e
JOIN Emp b ON e.job = b.job
WHERE b.ename = 'BLAKE'
  AND e.ename <> 'BLAKE';

-- 2 (practice). Find employees with the same job as TURNER who earn less than him.

SELECT e.ename, e.sal, e.job
FROM Emp e
WHERE e.job = (
    SELECT e2.job
    FROM Emp e2
    WHERE e2.ename = 'TURNER'
)
  AND e.sal < (
    SELECT e3.sal
    FROM Emp e3
    WHERE e3.ename = 'TURNER'
);



-- 3. Find the position with the highest average earnings.
--    Earnings = sal + comm. NULL + number = NULL, so IFNULL(comm, 0).
--    MySQL does not allow MAX(AVG(...)) -> use >= ALL.

SELECT job, AVG(sal + IFNULL(comm, 0)) AS AvgEarnings
FROM Emp
GROUP BY job
HAVING AVG(sal + IFNULL(comm, 0)) >= ALL (
    SELECT AVG(sal + IFNULL(comm, 0))
    FROM Emp
    GROUP BY job
);

-- 3 (practice). Find the job (or jobs) with the most employees.

SELECT e.job, COUNT(e.empno) AS NumOfEmployees
FROM Emp e
GROUP BY e.job
HAVING COUNT(e.empno) >= ALL (
    SELECT COUNT(e2.empno)
    FROM Emp e2
    GROUP BY e2.job
);



-- 4. Using operations on result sets, list the items (jobs) that appear in section (department) 10
--    and do not appear in section 30.
--    EXCEPT = Oracle's MINUS. Select ONLY the compared column.

SELECT job
FROM Emp
WHERE deptno = 10

EXCEPT

SELECT job
FROM Emp
WHERE deptno = 30;

-- 4 (practice). Using set operations, list jobs that appear in department 10 or 20, but not in department 30.

SELECT job
FROM Emp
WHERE deptno IN (10, 20)

EXCEPT

SELECT job
FROM Emp
WHERE deptno = 30;

-- Pure set operations version (brackets: UNION first)
(SELECT job FROM Emp WHERE deptno = 10
 UNION
 SELECT job FROM Emp WHERE deptno = 20)

EXCEPT

SELECT job FROM Emp WHERE deptno = 30;



-- 5. Find the employees who earn the most in their departments.
--    "Biggest value in each group" -> correlated subquery.

SELECT e.ename, e.sal, e.deptno
FROM Emp e
WHERE e.sal = (
    SELECT MAX(e2.sal)
    FROM Emp e2
    WHERE e2.deptno = e.deptno
);

-- Same with IN on pairs (MySQL only, not in MS SQL)
SELECT ename, sal, deptno
FROM Emp
WHERE (deptno, sal) IN (
    SELECT deptno, MAX(sal)
    FROM Emp
    GROUP BY deptno
);

-- 5 (practice). For each job, find the employee(s) hired most recently. Show name, job and hire date.
--               Most recent = newest date = MAX.

SELECT e.ename, e.job, e.hiredate
FROM Emp e
WHERE e.hiredate = (
    SELECT MAX(e2.hiredate)
    FROM Emp e2
    WHERE e2.job = e.job
);



-- 6. Find employees who have subordinates using the EXISTS statement.
--    The subordinate's mgr points to the boss: s.mgr = e.empno.

SELECT e.ename, e.job
FROM Emp e
WHERE EXISTS (
    SELECT 1
    FROM Emp s
    WHERE s.mgr = e.empno
);

-- 6 (practice). Find employees who have no subordinates.

-- With NOT EXISTS (always safe)
SELECT e.ename, e.job
FROM Emp e
WHERE NOT EXISTS (
    SELECT 1
    FROM Emp s
    WHERE s.mgr = e.empno
);

-- With NOT IN: KING's mgr is NULL, so remove NULLs, otherwise the result is EMPTY.
SELECT e.ename, e.job
FROM Emp e
WHERE e.empno NOT IN (
    SELECT s.mgr
    FROM Emp s
    WHERE s.mgr IS NOT NULL
);



-- 7. Find employees earning minimum wage in their salary groups.
--    Sort the records obtained by descending salary groups.
--    "Same group" link = same salary range.

SELECT e.ename, e.sal, s.grade
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN s.losal AND s.hisal
WHERE e.sal = (
    SELECT MIN(e2.sal)
    FROM Emp e2
    WHERE e2.sal BETWEEN s.losal AND s.hisal
)
ORDER BY s.grade DESC;

-- 7 (practice). In each salary group, find the employee(s) hired earliest.
--               Show name, grade and hire date. Sort by grade. Earliest = oldest date = MIN.

SELECT e.ename, e.hiredate, s.grade
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN s.losal AND s.hisal
WHERE e.hiredate = (
    SELECT MIN(e2.hiredate)
    FROM Emp e2
    WHERE e2.sal BETWEEN s.losal AND s.hisal
)
ORDER BY s.grade;



-- 8. Identify the three highest paid employees in the company. Give their names and salaries.

-- With LIMIT (loses ties: SCOTT and FORD both earn 3000)
SELECT ename, sal
FROM Emp
ORDER BY sal DESC
LIMIT 3;

-- Tie-safe: keep an employee if fewer than 3 people earn MORE than them.
SELECT e.ename, e.sal
FROM Emp e
WHERE (
    SELECT COUNT(*)
    FROM Emp e2
    WHERE e2.sal > e.sal
) < 3
ORDER BY e.sal DESC;



-- #####################################################################
--  PART 3: CONCAT (gluing text)
--  MySQL: use CONCAT, NOT || (in MySQL || means OR).
--  Any NULL piece -> whole result NULL. Use IFNULL or CONCAT_WS.
-- #####################################################################

-- Two columns in one
SELECT CONCAT(ename, ' - ', job) AS EMPLOYEES
FROM Emp;

-- Annual salary and name in one column
SELECT CONCAT(ename, ' earns ', sal * 12, ' per year') AS EMPLOYEES
FROM Emp;

-- NULL trap and two fixes
SELECT CONCAT(ename, ' commission: ', comm)             AS broken,      -- NULL for most rows
       CONCAT(ename, ' commission: ', IFNULL(comm, 0))  AS fix_ifnull,
       CONCAT_WS(' | ', ename, job, comm)               AS fix_concat_ws -- skips NULLs
FROM Emp;

-- Practice: one column, e.g. "KING works as PRESIDENT in department 10"
SELECT CONCAT(ename, ' works as ', job, ' in department ', deptno) AS EMPLOYEES
FROM Emp;

-- Practice: one column per project, e.g. "ATLAS: 2022-09-01 - still running"
SELECT CONCAT(pname, ': ', startdate, ' - ', IFNULL(enddate, 'still running')) AS PROJECT
FROM Proj;