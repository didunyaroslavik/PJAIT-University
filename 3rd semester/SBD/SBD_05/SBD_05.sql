-- Example 1

DECLARE @RentalID INT, @Name NVARCHAR(100), @Days INT;

DECLARE RentalCursor CURSOR LOCAL FAST_FORWARD FOR
    SELECT r.RentalID, m.Name, r.RentalDays
    FROM Rentals AS r
    JOIN Members AS m ON m.MemberID = r.MemberID
    WHERE r.Status = N'CheckedOut'
    ORDER BY r.RentalID;

OPEN RentalCursor;
FETCH NEXT FROM RentalCursor INTO @RentalID, @Name, @Days;

WHILE @@FETCH_STATUS = 0
BEGIN
    PRINT CONCAT('Rental ', @RentalID, ' - ', @Name, ' - days: ', @Days);

    FETCH NEXT FROM RentalCursor INTO @RentalID, @Name, @Days;
end;

CLOSE RentalCursor;
DEALLOCATE RentalCursor;





-- Example 1 (practice)

DECLARE @RentalID INT, @Name NVARCHAR(100), @StartDate DATE, @EndDate DATE, @RentalDays INT;
DECLARE @Count INT = 0;

DECLARE ReservedRentals CURSOR LOCAL FAST_FORWARD FOR
    SELECT r.RentalID, m.Name, r.StartDate, r.RentalDays
    FROM Members m JOIN Rentals r ON m.MemberID = r.MemberID
    WHERE r.Status = N'Reserved'
    ORDER BY r.RentalID;

OPEN ReservedRentals
FETCH NEXT FROM ReservedRentals INTO @RentalID, @Name, @StartDate, @RentalDays;

WHILE @@FETCH_STATUS = 0
BEGIN
    SET @EndDate = DATEADD(DAY, @RentalDays, @StartDate);
    PRINT CONCAT('Rental ', @RentalID, ' - ', @Name, ' - from ', @StartDate, ' to ', @EndDate);
    SET @Count = @Count + 1;

    FETCH NEXT FROM ReservedRentals INTO @RentalID, @Name, @StartDate, @RentalDays;
end;

CLOSE ReservedRentals;
DEALLOCATE ReservedRentals;

PRINT CONCAT('Reserved rentals: ', @Count);





-- Example 2

DROP TABLE IF EXISTS #Temp;

DECLARE @Var TABLE (EquipmentID INT);
CREATE TABLE #Temp (EquipmentID INT);

BEGIN TRANSACTION;

INSERT INTO @Var (EquipmentID)
SELECT EquipmentID
FROM Equipment
WHERE IsActive = 1 AND AvailableQuantity < MinimumQuantity;

INSERT INTO #Temp (EquipmentID)
SELECT EquipmentID
FROM Equipment
WHERE IsActive = 1 AND AvailableQuantity < MinimumQuantity;

ROLLBACK TRANSACTION;

SELECT (SELECT COUNT(*) FROM @Var)  AS RowsInTableVariable,
       (SELECT COUNT(*) FROM #Temp) AS RowsInTempTable;
GO

SELECT COUNT(*) AS RowsInTempTable FROM #Temp;
GO

SELECT COUNT(*) AS RowsInTableVariable FROM @Var;
GO

-- What I see: the temporary table still exists after GO (the rollback only
-- removed its rows), but the table variable is gone after GO, because it
-- lives only in its own batch. The rollback did not undo the rows of the
-- table variable.

DROP TABLE IF EXISTS #Temp;





-- Example 2 (practice)

DROP TABLE IF EXISTS #After;

DECLARE @Before TABLE (EquipmentID INT, OldQuantity INT);
CREATE TABLE #After (EquipmentID INT, NewQuantity INT);

BEGIN TRANSACTION

INSERT INTO @Before (EquipmentID, OldQuantity)
SELECT EquipmentID, AvailableQuantity
FROM Equipment
WHERE EquipmentID IN (101, 205);

UPDATE Equipment
SET AvailableQuantity = AvailableQuantity + 10
WHERE EquipmentID IN (101, 205);

INSERT INTO #After (EquipmentID, NewQuantity)
SELECT EquipmentID, AvailableQuantity
FROM Equipment
WHERE EquipmentID IN (101, 205);

ROLLBACK TRANSACTION;

SELECT * FROM @Before;

SELECT COUNT(*) AS RowsInAfter FROM #After

SELECT EquipmentID, AvailableQuantity
FROM Equipment
WHERE EquipmentID IN (101, 205);

DROP TABLE #After;





-- Example 3

DECLARE @EquipmentID INT = 420;
DECLARE @PerUnit DECIMAL(10, 2);

BEGIN TRY
    SELECT @PerUnit = ROUND(PurchasePrice / AvailableQuantity, 2)
    FROM Equipment
    WHERE EquipmentID = @EquipmentID;

    PRINT CONCAT('Purchase price per available unit: ', @PerUnit);
end try
begin catch
    PRINT CONCAT('Error ', ERROR_NUMBER(), ': ', ERROR_MESSAGE());
end catch;





-- Example 3 (practice)

DECLARE @RentalID INT = 2013;
DECLARE @Value DECIMAL(12,2), @Lines INT, @Average DECIMAL(10,2);

BEGIN TRY
    SELECT @Value = COALESCE(SUM(Quantity * AgreedDailyRate), 0),
           @Lines = COUNT(*)
    FROM RentalItems
    WHERE RentalID = @RentalID;

    SET @Average = ROUND(@Value / @Lines, 2);

    PRINT CONCAT('Rental ', @RentalID, ': ', @Lines, ' lines, average line value ', @Average);
END TRY
BEGIN CATCH
    PRINT CONCAT('Error ', ERROR_NUMBER(), ': ', ERROR_MESSAGE());
END CATCH;





-- Task 1

BEGIN
    SET NOCOUNT ON;
    DECLARE @MemberID INT, @MemberName NVARCHAR(100);
    DECLARE @ReturnedCount INT, @Amount DECIMAL(12,2);
    DECLARE @Total DECIMAL(12,2) = 0;

    DECLARE MemberCursor CURSOR LOCAL FAST_FORWARD FOR
        SELECT MemberID, Name
        FROM Members
        WHERE IsActive = 1
        ORDER BY MemberID;

    OPEN MemberCursor;
    FETCH NEXT FROM MemberCursor INTO @MemberID, @MemberName;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        SELECT @ReturnedCount = COUNT(*)
        FROM Rentals
        WHERE MemberID = @MemberID AND Status = N'Returned';

        IF @ReturnedCount >= 2
        BEGIN
            SET @Amount = 30;
        END
        ELSE
        BEGIN
            SET @Amount = 0;
        END;

        SET @Total = @Total + @Amount;
        PRINT CONCAT('Member ', @MemberID, ' ', @MemberName, ': voucher ', @Amount);

        FETCH NEXT FROM MemberCursor INTO @MemberID, @MemberName;
    END;

    CLOSE MemberCursor;
    DEALLOCATE MemberCursor;

    PRINT CONCAT('Total vouchers: ', @Total);
END;





-- Task 1 (practice)

BEGIN
    DECLARE @MemberID INT, @MemberName NVARCHAR(100);
    DECLARE @TotalRentalDays INT, @Level NVARCHAR(100);
    DECLARE @CountGold INT = 0;


    DECLARE MemberCursor CURSOR LOCAL FAST_FORWARD FOR
        SELECT MemberID, Name
        FROM Members
        WHERE IsActive = 1
        ORDER BY MemberID;

    OPEN MemberCursor;
    FETCH NEXT FROM MemberCursor INTO @MemberID, @MemberName;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        SELECT @TotalRentalDays = COALESCE(SUM(RentalDays), 0)
        FROM Rentals
        WHERE Status <> N'Cancelled' AND MemberID = @MemberID;

        IF @TotalRentalDays >= 10
        BEGIN
            SET @Level = 'GOLD';
            SET @CountGold = @CountGold + 1;
        end
        ELSE IF @TotalRentalDays >= 5
        BEGIN
            SET @Level = 'SILVER';
        end
        ELSE
        BEGIN
            SET @Level = 'BASIC'
        end;

        PRINT CONCAT('Member ', @MemberID, ' ', @MemberName, ': days ', @TotalRentalDays, ', level ', @Level);

        FETCH NEXT FROM MemberCursor INTO @MemberID, @MemberName;
    end

    CLOSE MemberCursor;
    DEALLOCATE MemberCursor;

    PRINT CONCAT('Gold members: ', @CountGold);
END;





-- Task 2

BEGIN
    DECLARE @PurchaseList TABLE (
        EquipmentID INT,
        QuantityToBuy INT,
        EstimatedCost DECIMAL(12, 2)
    );

    INSERT INTO @PurchaseList (EquipmentID, QuantityToBuy, EstimatedCost)
    SELECT EquipmentID, MinimumQuantity - AvailableQuantity, (MinimumQuantity - AvailableQuantity) * PurchasePrice
    FROM Equipment
    WHERE IsActive = 1 AND AvailableQuantity < MinimumQuantity;

    SELECT EquipmentID, QuantityToBuy, EstimatedCost
    FROM @PurchaseList
    ORDER BY EquipmentID;

    SELECT COUNT(*) AS TypesToBuy, COALESCE(SUM(EstimatedCost), 0) AS TotalCost
    FROM @PurchaseList;
END;





-- Task 2 (practice)

BEGIN
    DECLARE @RentalValues TABLE(
        RentalID INT,
        MemberID INT,
        RentalValue DECIMAL(12, 2)
    )

    INSERT INTO @RentalValues (RentalID, MemberID, RentalValue)
    SELECT r.RentalID, m.MemberID, COALESCE(SUM(i.Quantity * i.AgreedDailyRate * r.RentalDays), 0)
    FROM Rentals r
    JOIN Members m ON m.MemberID = r.MemberID
    JOIN RentalItems i ON r.RentalID = i.RentalID
    WHERE r.Status = 'Returned' AND m.IsActive = 1
    GROUP BY r.RentalID, m.MemberID;

    SELECT RentalID, MemberID, RentalValue
    FROM @RentalValues
    ORDER BY RentalValue DESC, RentalID;

    SELECT COUNT(*) AS RentalCount, COALESCE(SUM(RentalValue), 0) AS TotalValue, COALESCE(MAX(RentalValue), 0) AS BiggestValue
    FROM @RentalValues;
END;





-- Task 3

BEGIN
    DECLARE @EquipmentID INT = 205, @RequestedQuantity INT = 2;
    DECLARE @AvailableQuantity INT;
    DECLARE @Decision NVARCHAR(100), @Shortage INT;

    SELECT @AvailableQuantity = AvailableQuantity
    FROM Equipment
    WHERE @EquipmentID = EquipmentID;

    IF @AvailableQuantity >= @RequestedQuantity
    BEGIN
        SET @Decision = 'READY';
        SET @Shortage = 0;
    end
    ELSE
    BEGIN
        SET @Decision = 'SHORT';
        SET @Shortage = @RequestedQuantity - @AvailableQuantity;
    end;

    SELECT @EquipmentID AS EquipmentID, @RequestedQuantity AS RequestedQuantity, @Shortage AS Shortage, @Decision AS Decision;
END;





-- Task 3 (practice)

BEGIN
    DECLARE @MemberID INT = 6;
    DECLARE @Name NVARCHAR(100), @IsActive BIT, @Email NVARCHAR(100), @CheckedOut INT;
    DECLARE @Decision NVARCHAR(100);

    SELECT @Name = m.Name, @IsActive = m.IsActive, @Email = m.Email
    FROM Members m
    WHERE m.MemberID = @MemberID;

    SELECT @CheckedOut = COUNT(Status)
    FROM Rentals
    WHERE Status = 'CheckedOut' AND MemberID = @MemberID;

    IF @Name IS NULL
    BEGIN
        PRINT CONCAT('Member ', @MemberID, ' not found.');
        RETURN;
    end
    ELSE IF @IsActive = 0
    BEGIN
        SET @Decision = 'BLOCKED';
    end
    ELSE IF @Email IS NULL
    BEGIN
        SET @Decision = 'NO EMAIL';
    end
    ELSE IF @CheckedOut >= 2
    BEGIN
        SET @Decision = 'LIMIT';
    end
    ELSE
    BEGIN
        SET @Decision = N'OK';
    end

    SELECT @MemberID AS MemberID, @Name AS Name, @CheckedOut AS OpenRentals, @Decision AS Decision;
END;





-- Task 4

BEGIN
    SET NOCOUNT ON;
    DECLARE @StartDate DATE = '20261013';
    DECLARE @Day DATE;
    DECLARE @Counter INT = 1;
    DECLARE @RentalCount INT, @DayType NVARCHAR(10);

    IF @StartDate IS NULL
    BEGIN
        PRINT 'Start date is missing.';
        RETURN;
    END;

    SET @Day = @StartDate;

    WHILE @Counter <= 3
    BEGIN
        SELECT @RentalCount = COUNT(*)
        FROM Rentals
        WHERE StartDate = @Day AND Status <> N'Cancelled';

        IF @RentalCount >= 2
        BEGIN
            SET @DayType = N'BUSY';
        END
        ELSE
        BEGIN
            SET @DayType = N'QUIET';
        END;

        SELECT @Day AS RentalDay, @RentalCount AS RentalCount, @DayType AS DayType;

        SET @Day = DATEADD(DAY, 1, @Day);
        SET @Counter = @Counter + 1;
    END;
END;





-- Task 4 (practice)
BEGIN
    DECLARE @DateFrom DATE = '20261012', @DateTo DATE = '20261016';
    DECLARE @Day DATE;
    DECLARE @Total INT;

    DECLARE @Report TABLE (RentalDay DATE, RentalCount INT, TotalDays INT)

    IF @DateFrom IS NULL OR @DateTo IS NULL
    BEGIN
        PRINT 'Dates are missing.';
        RETURN;
    end

    IF @DateFrom > @DateTo
    BEGIN
        PRINT 'The start date is after the end date.';
        RETURN;
    end

    SET @Day = @DateFrom;

    WHILE @Day <= @DateTo
    BEGIN

        INSERT INTO @Report (RentalDay, RentalCount, TotalDays)
        SELECT @Day, COUNT(*), COALESCE(SUM(RentalDays), 0)
        FROM Rentals
        WHERE StartDate = @Day AND Status <> N'Cancelled';

        SET @Day = DATEADD(DAY, 1, @Day);
    end

    SELECT RentalDay, RentalCount, TotalDays
    FROM @Report
    ORDER BY RentalDay

    SELECT @Total = SUM(RentalCount) FROM @Report;
    PRINT CONCAT('Rentals in the period: ', @Total);
END;





-- Task 5

DROP TABLE IF EXISTS #MemberReport;
CREATE TABLE #MemberReport (
    MemberID INT, Name NVARCHAR(100), RentalCount INT, TotalDays INT
);

INSERT INTO #MemberReport (MemberID, Name, RentalCount, TotalDays)
SELECT m.MemberID, m.Name, COUNT(r.RentalID), COALESCE(SUM(r.RentalDays), 0)
FROM Members AS m
LEFT JOIN Rentals AS r ON r.MemberID = m.MemberID AND r.Status<> N'Cancelled'
WHERE m.IsActive = 1
GROUP BY m.MemberID, m.Name;

GO

SELECT MemberID, Name, RentalCount, TotalDays
FROM #MemberReport
ORDER BY TotalDays DESC, MemberID;

SELECT COUNT(*) AS MemberCount, SUM(RentalCount) AS RentalCount, SUM(TotalDays) AS TotalDays
FROM #MemberReport;

DROP TABLE #MemberReport;
GO;





-- Task 5 (practice)

DROP TABLE IF EXISTS #OutReport;
CREATE TABLE #OutReport (EquipmentID INT, Name NVARCHAR(100), RentalCount INT, UnitsOut INT)

INSERT INTO #OutReport (EquipmentID, Name, RentalCount, UnitsOut)
SELECT e.EquipmentID,
       e.Name,
       COUNT(DISTINCT o.RentalID),
       COALESCE(SUM(o.Quantity), 0)
FROM Equipment AS e
LEFT JOIN (
    -- first: only items of CheckedOut rentals
    SELECT i.EquipmentID, i.RentalID, i.Quantity
    FROM RentalItems AS i
    JOIN Rentals AS r ON r.RentalID = i.RentalID
    WHERE r.Status = N'CheckedOut'
) AS o ON o.EquipmentID = e.EquipmentID
WHERE e.IsActive = 1
GROUP BY e.EquipmentID, e.Name;
GO

SELECT EquipmentID, Name, RentalCount, UnitsOut
FROM #OutReport
ORDER BY UnitsOut DESC, EquipmentID

SELECT COUNT(*) AS EquipmentCount, SUM(UnitsOut) AS TotalUnitsOut, SUM(CASE WHEN UnitsOut = 0 THEN 1 ELSE 0 END) AS NotOut
FROM #OutReport;

DROP TABLE #OutReport;
GO





-- Task 6

BEGIN
    DECLARE @EquipmentID INT = 205, @Quantity INT = 3;
    DECLARE @Available INT, @Message NVARCHAR(1000)

    BEGIN TRY
        SELECT @Available = AvailableQuantity
        FROM Equipment
        WHERE EquipmentID = @EquipmentID

        IF @Quantity > @Available
        BEGIN
            SET @Message = CONCAT('Equipment ', @EquipmentID, ': requested ', @Quantity, ', available ', @Available, '.');
            THROW 51001, @Message, 1;
        end

        SELECT @EquipmentID AS EquipmentID, @Quantity AS Quantity, @Available - @Quantity AS RemainingQuantity;
    end try
    begin catch
        PRINT CONCAT('Error ', ERROR_NUMBER(), ': ', ERROR_MESSAGE());
    end catch;
END;





-- Task 6 (practice)

BEGIN
    DECLARE @RentalID INT = 2010;
    DECLARE @Status NVARCHAR(12), @Name NVARCHAR(100);
    DECLARE @Message NVARCHAR(2048);
    DECLARE @ItemLines INT, @Units INT;

    BEGIN TRY
        SELECT @Status = r.Status, @Name = m.Name
        FROM Members m
        JOIN Rentals r ON m.MemberID = r.MemberID
        WHERE r.RentalID = @RentalID;

        IF @Status IS NULL OR @Name IS NULL
        BEGIN
            SET @Message = CONCAT('Rental ', @RentalID, ' was not found.');
            THROW 51002, @Message, 1;
        end;

        IF @Status <> 'CheckedOut'
        BEGIN
            SET @Message = CONCAT('Rental ', @RentalID, ' has status ', @Status, '. Only CheckedOut rentals can be returned.');
            THROW 51003, @Message, 1;
        end;

        SELECT @ItemLines = COUNT(*), @Units = COALESCE(SUM(Quantity), 0)
        FROM RentalItems
        WHERE RentalID = @RentalID;

        SELECT @RentalID AS RentalID, @Name AS MemberName, @ItemLines AS ItemLines, @Units AS Units;

    end try
    begin catch
        PRINT CONCAT('Error ', ERROR_NUMBER(), ': ', ERROR_MESSAGE())
    end catch;
END;


















