-- =======================================================
-- DQL (DATA QUERY LANGUAGE) - SELECT STATEMENTS
-- =======================================================



-- -------------------------------------------------------
-- REQUIREMENT 1: 2 SELECT statements with at least two tables and a WHERE clause
-- -------------------------------------------------------

-- Query 1.1: Shows the profile name and the main account email for profiles set to 'Adult' maturity.
SELECT p.profile_name, a.email AS account_email
FROM Profile p
JOIN Account a ON p.Account_ID = a.ID
WHERE p.maturity_level = 'Adult';

-- Query 1.2: Lists the titles and durations of all episodes belonging to the series 'Dark'.
SELECT s.title AS series_title, e.season_number, e.episode_number, e.duration_mins
FROM Series s
JOIN Episode e ON s.ID = e.Series_ID
WHERE s.title = 'Dark';


-- -------------------------------------------------------
-- REQUIREMENT 2: 2 SELECT statements with aggregate functions and GROUP BY/HAVING
-- -------------------------------------------------------

-- Query 2.1: Counts the total number of movies in each genre, showing only genres that have at least 1 movie.
SELECT g.name AS genre_name, COUNT(m.ID) AS total_movies
FROM Genre g
JOIN Movie m ON g.ID = m.Genre_ID
GROUP BY g.name
HAVING COUNT(m.ID) > 0;

-- Query 2.2: Calculates the average episode duration for each series, showing only series where the average episode is over 30 minutes.
SELECT s.title AS series_title, AVG(e.duration_mins) AS avg_episode_length
FROM Series s
JOIN Episode e ON s.ID = e.Series_ID
GROUP BY s.title
HAVING AVG(e.duration_mins) > 30;


-- -------------------------------------------------------
-- REQUIREMENT 3: 2 SELECT statements with a standard subquery
-- -------------------------------------------------------

-- Query 3.1: Finds all movies that are longer than the overall average movie duration.
SELECT title, duration_mins
FROM Movie
WHERE duration_mins > (
    SELECT AVG(duration_mins) 
    FROM Movie
);

-- Query 3.2: Finds the emails of all accounts that are subscribed to the 'Premium' plan.
SELECT email, created_at
FROM Account
WHERE Plan_ID = (
    SELECT ID 
    FROM Plan 
    WHERE name = 'Premium'
);


-- -------------------------------------------------------
-- REQUIREMENT 4: 1 SELECT statement with a correlated subquery
-- -------------------------------------------------------

-- Query 4.1: Lists all series titles and counts how many episodes they have. 
SELECT s.title,
       (SELECT COUNT(*) 
        FROM Episode e 
        WHERE e.Series_ID = s.ID) AS total_episodes
FROM Series s;


-- =======================================================
-- DML (DATA MANIPULATION LANGUAGE) - UPDATE & DELETE
-- =======================================================

-- -------------------------------------------------------
-- REQUIREMENT 5: 1 UPDATE with a correlated subquery
-- -------------------------------------------------------

-- Query 5.1: Bumps the release year of a Series forward by 1 year IF that series has any episodes longer than 55 minutes.
UPDATE Series s
SET release_year = release_year + 1
WHERE 55 < (
    SELECT MAX(duration_mins)
    FROM Episode e
    WHERE e.Series_ID = s.ID
);


-- -------------------------------------------------------
-- REQUIREMENT 6: 1 DELETE with a subquery
-- -------------------------------------------------------

-- Query 6.1: Deletes user reactions for any movies that were released before the year 2000.
DELETE FROM Movie_Reaction
WHERE Movie_ID IN (
    SELECT ID 
    FROM Movie 
    WHERE release_year < 2000
);

-- Save the changes to the database
COMMIT;