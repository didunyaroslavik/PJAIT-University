-- SBD, exercises 4: equipment rental database (cleanup + setup in one file, no custom schema).
-- SQL Server 2016 or later. Tables are created in the default dbo schema.
-- Select your own practice database in your SQL client. This script does not create or drop a database.
--
-- WARNING: the script DROPS tables named RentalItems, Rentals, Equipment and Members in dbo and recreates them.
-- If your database already has tables with these names that you want to keep, do not run this file there.
-- The library set (library_setup.sql) also has a table named Members. Do not use both sets in one database.
--
-- Run the whole file with no transaction open: it can be repeated at any time and
-- always restores the initial data. To only remove the tables, run PART 1 alone.
-- GO is a batch separator.
SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

-- =====================================================================
-- PART 1: CLEANUP - drops only the four tables of this set
-- =====================================================================
BEGIN TRY
    BEGIN TRANSACTION;
    DROP TABLE IF EXISTS RentalItems;
    DROP TABLE IF EXISTS Rentals;
    DROP TABLE IF EXISTS Equipment;
    DROP TABLE IF EXISTS Members;
    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

-- =====================================================================
-- PART 2: SETUP - creates the tables and inserts the initial data
-- =====================================================================
BEGIN TRY
    BEGIN TRANSACTION;

    CREATE TABLE Members (
        MemberID INT NOT NULL CONSTRAINT PK_Members PRIMARY KEY,
        Name NVARCHAR(100) NOT NULL,
        Email NVARCHAR(200) NULL,
        IsActive BIT NOT NULL
    );

    CREATE TABLE Equipment (
        EquipmentID INT NOT NULL CONSTRAINT PK_Equipment PRIMARY KEY,
        Name NVARCHAR(100) NOT NULL,
        DailyRate DECIMAL(10,2) NOT NULL,
        AvailableQuantity INT NOT NULL,
        MinimumQuantity INT NOT NULL,
        PurchasePrice DECIMAL(10,2) NOT NULL,
        IsActive BIT NOT NULL,
        CONSTRAINT CK_Equipment_Rate CHECK (DailyRate > 0),
        CONSTRAINT CK_Equipment_Available CHECK (AvailableQuantity >= 0),
        CONSTRAINT CK_Equipment_Minimum CHECK (MinimumQuantity >= 0),
        CONSTRAINT CK_Equipment_Purchase CHECK (PurchasePrice > 0)
    );

    CREATE TABLE Rentals (
        RentalID INT NOT NULL CONSTRAINT PK_Rentals PRIMARY KEY,
        MemberID INT NOT NULL,
        StartDate DATE NOT NULL,
        RentalDays INT NOT NULL,
        Status NVARCHAR(12) NOT NULL,
        CONSTRAINT FK_Rentals_Members FOREIGN KEY (MemberID) REFERENCES Members (MemberID),
        CONSTRAINT CK_Rentals_Days CHECK (RentalDays > 0),
        CONSTRAINT CK_Rentals_Status
            CHECK (Status IN (N'Reserved', N'CheckedOut', N'Returned', N'Cancelled'))
    );

    CREATE TABLE RentalItems (
        RentalID INT NOT NULL,
        LineNumber INT NOT NULL,
        EquipmentID INT NOT NULL,
        Quantity INT NOT NULL,
        AgreedDailyRate DECIMAL(10,2) NOT NULL,
        CONSTRAINT PK_RentalItems PRIMARY KEY (RentalID, LineNumber),
        CONSTRAINT FK_RentalItems_Rentals FOREIGN KEY (RentalID) REFERENCES Rentals (RentalID),
        CONSTRAINT FK_RentalItems_Equipment FOREIGN KEY (EquipmentID) REFERENCES Equipment (EquipmentID),
        CONSTRAINT CK_RentalItems_Line CHECK (LineNumber > 0),
        CONSTRAINT CK_RentalItems_Quantity CHECK (Quantity > 0),
        CONSTRAINT CK_RentalItems_Rate CHECK (AgreedDailyRate > 0)
    );

    INSERT INTO Members
        (MemberID, Name, Email, IsActive) VALUES
        (1, N'Anna Nowak', N'anna@example.com', 1),
        (2, N'Frame Studio', N'frame@example.com', 1),
        (3, N'Old Camera Club', N'club@example.com', 0),
        (4, N'Marek Lis', N'marek@example.com', 1),
        (5, N'Campus Media', N'campus@example.com', 1),
        (6, N'Ewa Wilk', NULL, 1);

    INSERT INTO Equipment
        (EquipmentID, Name, DailyRate, AvailableQuantity, MinimumQuantity, PurchasePrice, IsActive) VALUES
        (101, N'Mirrorless camera', 50.00, 6, 4, 2500.00, 1),
        (205, N'Tripod', 30.00, 2, 5, 240.00, 1),
        (310, N'Projector', 90.00, 1, 3, 1200.00, 1),
        (420, N'Wireless microphone', 20.00, 0, 2, 150.00, 1),
        (550, N'Legacy camcorder', 40.00, 0, 4, 800.00, 0),
        (660, N'LED light panel', 25.00, 3, 3, 350.00, 1),
        (675, N'Audio recorder', 35.00, 7, 2, 600.00, 1);

    INSERT INTO Rentals
        (RentalID, MemberID, StartDate, RentalDays, Status) VALUES
        (2001, 1, '20261001', 2, N'Reserved'),
        (2002, 2, '20261002', 3, N'Reserved'),
        (2003, 1, '20261003', 4, N'Returned'),
        (2004, 2, '20261004', 1, N'Returned'),
        (2005, 3, '20261005', 2, N'Returned'),
        (2006, 2, '20261006', 3, N'Returned'),
        (2007, 1, '20260928', 2, N'Returned'),
        (2008, 5, '20261008', 1, N'Reserved'),
        (2009, 1, '20261009', 2, N'Cancelled'),
        (2010, 2, '20261010', 2, N'CheckedOut'),
        (2011, 4, '20261011', 1, N'Returned'),
        (2012, 2, '20261012', 2, N'CheckedOut'),
        (2013, 1, '20261013', 1, N'CheckedOut'),
        (2014, 2, '20261014', 3, N'CheckedOut'),
        (2015, 5, '20261014', 1, N'Reserved'),
        (2016, 4, '20261014', 2, N'Cancelled');

    INSERT INTO RentalItems
        (RentalID, LineNumber, EquipmentID, Quantity, AgreedDailyRate) VALUES
        (2001, 1, 101, 2, 45.00),
        (2001, 2, 205, 1, 30.00),
        (2002, 1, 310, 1, 80.00),
        (2002, 2, 420, 1, 20.00),
        (2003, 1, 101, 1, 45.00),
        (2003, 2, 101, 1, 40.00),
        (2003, 3, 205, 2, 25.00),
        (2004, 1, 101, 1, 50.00),
        (2004, 2, 420, 1, 20.00),
        (2005, 1, 660, 1, 25.00),
        (2006, 1, 310, 1, 80.00),
        (2007, 1, 101, 1, 45.00),
        (2008, 1, 205, 1, 30.00),
        (2009, 1, 550, 1, 40.00),
        (2010, 1, 660, 2, 25.00),
        (2011, 1, 675, 1, 35.00),
        (2012, 1, 101, 1, 50.00),
        (2014, 1, 101, 2, 45.00),
        (2014, 2, 101, 1, 40.00),
        (2015, 1, 660, 1, 25.00),
        (2015, 2, 675, 1, 35.00);

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

-- =====================================================================
-- PART 3: CHECK - expected row counts: 6, 7, 16, 21
-- =====================================================================
SELECT 'Members' AS TableName, COUNT(*) AS RecordCount FROM Members
UNION ALL SELECT 'Equipment', COUNT(*) FROM Equipment
UNION ALL SELECT 'Rentals', COUNT(*) FROM Rentals
UNION ALL SELECT 'RentalItems', COUNT(*) FROM RentalItems;
