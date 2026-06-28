-- =======================================================
-- 1. CREATING TABLES
-- =======================================================

CREATE TABLE Plan (
    ID INTEGER PRIMARY KEY,
    name VARCHAR2(20),
    price INTEGER,
    max_screen INTEGER
);

CREATE TABLE DeviceType (
    ID INTEGER PRIMARY KEY,
    name VARCHAR2(20)
);

CREATE TABLE Genre (
    ID INTEGER PRIMARY KEY,
    name VARCHAR2(20)
);

CREATE TABLE Actor (
    ID INTEGER PRIMARY KEY,
    first_name VARCHAR2(20),
    last_name VARCHAR2(20)
);

CREATE TABLE Avatar (
    ID INTEGER PRIMARY KEY,
    image_url VARCHAR2(100),
    name VARCHAR2(20)
);

CREATE TABLE Account (
    ID INTEGER PRIMARY KEY,
    email VARCHAR2(20),
    password VARCHAR2(20),
    created_at TIMESTAMP,
    Plan_ID INTEGER,
    FOREIGN KEY (Plan_ID) REFERENCES Plan(ID)
);

CREATE TABLE Profile (
    ID INTEGER PRIMARY KEY,
    profile_name VARCHAR2(20),
    maturity_level VARCHAR2(20),
    Account_ID INTEGER,
    Avatar_ID INTEGER,
    FOREIGN KEY (Account_ID) REFERENCES Account(ID),
    FOREIGN KEY (Avatar_ID) REFERENCES Avatar(ID)
);

CREATE TABLE Device (
    ID INTEGER PRIMARY KEY,
    last_login TIMESTAMP,
    Account_ID INTEGER,
    DeviceType_ID INTEGER,
    FOREIGN KEY (Account_ID) REFERENCES Account(ID),
    FOREIGN KEY (DeviceType_ID) REFERENCES DeviceType(ID)
);

CREATE TABLE Movie (
    ID INTEGER PRIMARY KEY,
    title VARCHAR2(20),
    release_year INTEGER,
    duration_mins INTEGER,
    Genre_ID INTEGER,
    FOREIGN KEY (Genre_ID) REFERENCES Genre(ID)
);

CREATE TABLE Series (
    ID INTEGER PRIMARY KEY,
    title VARCHAR2(20),
    release_year INTEGER,
    description VARCHAR2(20),
    Genre_ID INTEGER,
    FOREIGN KEY (Genre_ID) REFERENCES Genre(ID)
);

CREATE TABLE Episode (
    ID INTEGER PRIMARY KEY,
    season_number INTEGER,
    episode_number INTEGER,
    duration_mins INTEGER,
    Series_ID INTEGER,
    FOREIGN KEY (Series_ID) REFERENCES Series(ID)
);

CREATE TABLE Movie_Actor (
    ID INTEGER PRIMARY KEY,
    character_name VARCHAR2(20),
    Movie_ID INTEGER,
    Actor_ID INTEGER,
    FOREIGN KEY (Movie_ID) REFERENCES Movie(ID),
    FOREIGN KEY (Actor_ID) REFERENCES Actor(ID)
);

CREATE TABLE Series_Actor (
    ID INTEGER PRIMARY KEY,
    character_name VARCHAR2(20),
    Series_ID INTEGER,
    Actor_ID INTEGER,
    FOREIGN KEY (Series_ID) REFERENCES Series(ID),
    FOREIGN KEY (Actor_ID) REFERENCES Actor(ID)
);

CREATE TABLE Movie_Reaction (
    ID INTEGER PRIMARY KEY,
    like_status SMALLINT,
    created_at TIMESTAMP,
    Profile_ID INTEGER,
    Movie_ID INTEGER,
    FOREIGN KEY (Profile_ID) REFERENCES Profile(ID),
    FOREIGN KEY (Movie_ID) REFERENCES Movie(ID)
);


-- =======================================================
-- 2. INSERTING DATA (4 records per table)
-- =======================================================

-- PLAN Data
INSERT INTO Plan (ID, name, price, max_screen) VALUES (1, 'Basic', 9, 1);
INSERT INTO Plan (ID, name, price, max_screen) VALUES (2, 'Standard', 13, 2);
INSERT INTO Plan (ID, name, price, max_screen) VALUES (3, 'Premium', 18, 4);
INSERT INTO Plan (ID, name, price, max_screen) VALUES (4, 'Mobile', 5, 1);

-- DEVICETYPE Data
INSERT INTO DeviceType (ID, name) VALUES (1, 'Smart TV');
INSERT INTO DeviceType (ID, name) VALUES (2, 'Smartphone');
INSERT INTO DeviceType (ID, name) VALUES (3, 'Tablet');
INSERT INTO DeviceType (ID, name) VALUES (4, 'Laptop');

-- GENRE Data
INSERT INTO Genre (ID, name) VALUES (1, 'Action');
INSERT INTO Genre (ID, name) VALUES (2, 'Comedy');
INSERT INTO Genre (ID, name) VALUES (3, 'Drama');
INSERT INTO Genre (ID, name) VALUES (4, 'Sci-Fi');

-- ACTOR Data
INSERT INTO Actor (ID, first_name, last_name) VALUES (1, 'Tom', 'Cruise');
INSERT INTO Actor (ID, first_name, last_name) VALUES (2, 'Zendaya', 'Coleman');
INSERT INTO Actor (ID, first_name, last_name) VALUES (3, 'Brad', 'Pitt');
INSERT INTO Actor (ID, first_name, last_name) VALUES (4, 'Emma', 'Stone');

-- AVATAR Data
INSERT INTO Avatar (ID, image_url, name) VALUES (1, '/img/hero.png', 'Superhero');
INSERT INTO Avatar (ID, image_url, name) VALUES (2, '/img/dog.png', 'Cute Dog');
INSERT INTO Avatar (ID, image_url, name) VALUES (3, '/img/alien.png', 'Green Alien');
INSERT INTO Avatar (ID, image_url, name) VALUES (4, '/img/car.png', 'Fast Car');

-- ACCOUNT Data
INSERT INTO Account (ID, email, password, created_at, Plan_ID) VALUES (1, 'user1@gmail.com', 'pass123', TO_TIMESTAMP('2025-01-10 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3);
INSERT INTO Account (ID, email, password, created_at, Plan_ID) VALUES (2, 'user2@gmail.com', 'qwerty', TO_TIMESTAMP('2025-02-15 14:30:00', 'YYYY-MM-DD HH24:MI:SS'), 2);
INSERT INTO Account (ID, email, password, created_at, Plan_ID) VALUES (3, 'user3@gmail.com', 'admin1', TO_TIMESTAMP('2025-03-20 09:15:00', 'YYYY-MM-DD HH24:MI:SS'), 1);
INSERT INTO Account (ID, email, password, created_at, Plan_ID) VALUES (4, 'user4@gmail.com', 'hello99', TO_TIMESTAMP('2025-04-05 20:45:00', 'YYYY-MM-DD HH24:MI:SS'), 4);

-- PROFILE Data
INSERT INTO Profile (ID, profile_name, maturity_level, Account_ID, Avatar_ID) VALUES (1, 'Dad', 'Adult', 1, 1);
INSERT INTO Profile (ID, profile_name, maturity_level, Account_ID, Avatar_ID) VALUES (2, 'Timmy', 'Kids', 1, 2);
INSERT INTO Profile (ID, profile_name, maturity_level, Account_ID, Avatar_ID) VALUES (3, 'Sarah', 'Teen', 2, 3);
INSERT INTO Profile (ID, profile_name, maturity_level, Account_ID, Avatar_ID) VALUES (4, 'Roommate', 'Adult', 3, 4);

-- DEVICE Data
INSERT INTO Device (ID, last_login, Account_ID, DeviceType_ID) VALUES (1, TO_TIMESTAMP('2026-05-20 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 1);
INSERT INTO Device (ID, last_login, Account_ID, DeviceType_ID) VALUES (2, TO_TIMESTAMP('2026-05-21 12:30:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 2);
INSERT INTO Device (ID, last_login, Account_ID, DeviceType_ID) VALUES (3, TO_TIMESTAMP('2026-05-22 18:45:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 4);
INSERT INTO Device (ID, last_login, Account_ID, DeviceType_ID) VALUES (4, TO_TIMESTAMP('2026-05-23 21:10:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 3);

-- MOVIE Data
INSERT INTO Movie (ID, title, release_year, duration_mins, Genre_ID) VALUES (1, 'Dune', 2021, 155, 4);
INSERT INTO Movie (ID, title, release_year, duration_mins, Genre_ID) VALUES (2, 'Top Gun', 2022, 130, 1);
INSERT INTO Movie (ID, title, release_year, duration_mins, Genre_ID) VALUES (3, 'Superbad', 2007, 113, 2);
INSERT INTO Movie (ID, title, release_year, duration_mins, Genre_ID) VALUES (4, 'Fight Club', 1999, 139, 3);

-- SERIES Data
INSERT INTO Series (ID, title, release_year, description, Genre_ID) VALUES (1, 'Dark', 2017, 'Time travel mystery', 4);
INSERT INTO Series (ID, title, release_year, description, Genre_ID) VALUES (2, 'The Office', 2005, 'Workplace comedy', 2);
INSERT INTO Series (ID, title, release_year, description, Genre_ID) VALUES (3, 'Breaking Bad', 2008, 'Crime drama show', 3);
INSERT INTO Series (ID, title, release_year, description, Genre_ID) VALUES (4, 'Daredevil', 2015, 'Blind hero fights', 1);

-- EPISODE Data
INSERT INTO Episode (ID, season_number, episode_number, duration_mins, Series_ID) VALUES (1, 1, 1, 50, 1);
INSERT INTO Episode (ID, season_number, episode_number, duration_mins, Series_ID) VALUES (2, 1, 2, 52, 1);
INSERT INTO Episode (ID, season_number, episode_number, duration_mins, Series_ID) VALUES (3, 1, 1, 22, 2);
INSERT INTO Episode (ID, season_number, episode_number, duration_mins, Series_ID) VALUES (4, 1, 1, 58, 3);

-- MOVIE_ACTOR Data
INSERT INTO Movie_Actor (ID, character_name, Movie_ID, Actor_ID) VALUES (1, 'Maverick', 2, 1);
INSERT INTO Movie_Actor (ID, character_name, Movie_ID, Actor_ID) VALUES (2, 'Chani', 1, 2);
INSERT INTO Movie_Actor (ID, character_name, Movie_ID, Actor_ID) VALUES (3, 'Tyler Durden', 4, 3);
INSERT INTO Movie_Actor (ID, character_name, Movie_ID, Actor_ID) VALUES (4, 'Mia', 3, 4);

-- SERIES_ACTOR Data
INSERT INTO Series_Actor (ID, character_name, Series_ID, Actor_ID) VALUES (1, 'Jonas', 1, 1);
INSERT INTO Series_Actor (ID, character_name, Series_ID, Actor_ID) VALUES (2, 'Pam', 2, 4);
INSERT INTO Series_Actor (ID, character_name, Series_ID, Actor_ID) VALUES (3, 'Jesse', 3, 3);
INSERT INTO Series_Actor (ID, character_name, Series_ID, Actor_ID) VALUES (4, 'Matt', 4, 2);

-- MOVIE_REACTION Data
INSERT INTO Movie_Reaction (ID, like_status, created_at, Profile_ID, Movie_ID) VALUES (1, 1, TO_TIMESTAMP('2026-05-24 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 1);
INSERT INTO Movie_Reaction (ID, like_status, created_at, Profile_ID, Movie_ID) VALUES (2, 0, TO_TIMESTAMP('2026-05-24 11:30:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 4);
INSERT INTO Movie_Reaction (ID, like_status, created_at, Profile_ID, Movie_ID) VALUES (3, 1, TO_TIMESTAMP('2026-05-25 09:15:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 2);
INSERT INTO Movie_Reaction (ID, like_status, created_at, Profile_ID, Movie_ID) VALUES (4, 1, TO_TIMESTAMP('2026-05-25 20:45:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 3);

COMMIT;