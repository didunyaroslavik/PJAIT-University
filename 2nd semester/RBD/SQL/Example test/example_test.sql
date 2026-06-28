-- 1.
SELECT h1.ID, h1.Name, h1.BecameHeroDate
FROM Hero h1
WHERE h1.BecameHeroDate = (
  SELECT MIN(h2.BecameHeroDate)
  FROM Hero h2
  WHERE h1.TEAM_ID = h2.TEAM_ID
);

-- 2.
SELECT t.ID, t.Name, COUNT(m.ID) AS "Missions Performed"
FROM Team t
JOIN Team_Mission tm ON tm.Team_ID = t.ID
JOIN Mission m ON tm.Mission_ID = m.ID
GROUP BY t.ID, t.Name
HAVING COUNT(m.ID) >= 2;

-- 3.
SELECT l.City
FROM Location l
JOIN Mission m ON m.Location_ID = l.ID
GROUP BY l.City
HAVING COUNT(m.ID) = (
  SELECT MAX(COUNT(m2.ID))
  FROM Mission m2
  GROUP BY m2.Location_ID
);

-- 4.
SELECT m.DateEnd - m.DateStart AS "Mission Time", m.ShortDescription, l.City
FROM Mission m
JOIN Location l ON l.ID = m.Location_ID;

-- 5.
SELECT t.ID, t.Name
FROM Team t
WHERE NOT EXISTS(
  SELECT 1
  FROM Team_Mission tm
  WHERE tm.Team_ID = t.ID
);

-- 6.
SELECT h.Name, s.Name AS "Power", t.Name as "Team"
FROM Superpower s
JOIN Hero h ON s.ID = h.Superpower_ID
JOIN Team t ON t.ID = h.TEAM_ID
ORDER BY t.Name DESC, h.Name ASC;

-- 7.
SELECT m.ID, m.ShortDescription, m.Difficulty
From Mission m
WHERE m.Difficulty > (
  SELECT MIN(m2.Difficulty)
  FROM Mission m2
  JOIN Location l ON l.ID = m2.Location_ID
  WHERE l.City = 'New York'
);

-- 8.
SELECT m1.ID, m1.ShortDescription, m1.Difficulty
From Mission m1
WHERE 3 > (
  SELECT COUNT(m2.ID)
  FROM Mission m2
  WHERE m2.Difficulty > m1.Difficulty
);

-- 9.
SELECT h.Name, COUNT(m.ID) AS "Number of missions"
FROM Hero h
JOIN Team t ON t.ID = h.team_ID
JOIN Team_Mission tm ON t.ID = tm.Team_ID
JOIN Mission m ON m.ID = tm.Mission_ID
WHERE h.Name NOT LIKE 'B%'
GROUP BY h.Name;

-- 10.
SELECT t.ID, t.Name
FROM Team t
JOIN Team_Mission tm ON t.ID = tm.Team_ID
JOIN Mission m ON m.ID = tm.Mission_ID
WHERE m.Difficulty = 1
  
INTERSECT
  
SELECT t.ID, t.Name
FROM Team t
JOIN Team_Mission tm ON t.ID = tm.Team_ID
JOIN Mission m ON m.ID = tm.Mission_ID
WHERE m.Difficulty = 4;
