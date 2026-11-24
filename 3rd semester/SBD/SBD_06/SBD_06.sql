-- SBD 06 - Stored procedures (shop and warehouses)
-- Run orders_setup.sql first. Each test is a separate batch (ends with GO),
-- so a test that raises an error with THROW does not stop the other tests.





-- Example 1

DROP PROCEDURE IF EXISTS usp_CountOrders;
GO

CREATE PROCEDURE usp_CountOrders
    @CustomerID INT,
    @Status NVARCHAR(12) = NULL,
    @OrderCount INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET @OrderCount = 0;

    IF @CustomerID IS NULL OR @CustomerID <= 0
    BEGIN
        THROW 50001, N'CustomerID must be a positive number.', 1;
    END;

    IF NOT EXISTS (SELECT 1 FROM Customer WHERE ID = @CustomerID)
    BEGIN
        RETURN 1;
    END;

    SELECT @OrderCount = COUNT(*)
    FROM Orders
    WHERE Customer_ID = @CustomerID
      AND (@Status IS NULL OR Status = @Status);

    RETURN 0;
END;
GO

-- Tests

DECLARE @ReturnCode INT, @Count INT;
EXEC @ReturnCode = usp_CountOrders @CustomerID = 2, @OrderCount = @Count OUTPUT;
SELECT @ReturnCode AS ReturnCode, @Count AS OrderCount;          -- 0, 7
GO

DECLARE @ReturnCode INT, @Count INT;
EXEC @ReturnCode = usp_CountOrders @CustomerID = 2, @Status = N'New', @OrderCount = @Count OUTPUT;
SELECT @ReturnCode AS ReturnCode, @Count AS OrderCount;          -- 0, 3
GO

DECLARE @ReturnCode INT, @Count INT;
EXEC @ReturnCode = usp_CountOrders @CustomerID = 2, @Status = N'Cancelled', @OrderCount = @Count OUTPUT;
SELECT @ReturnCode AS ReturnCode, @Count AS OrderCount;          -- 0, 0
GO

DECLARE @ReturnCode INT, @Count INT;
EXEC @ReturnCode = usp_CountOrders @CustomerID = 6, @OrderCount = @Count OUTPUT;
SELECT @ReturnCode AS ReturnCode, @Count AS OrderCount;          -- 0, 0
GO

DECLARE @ReturnCode INT, @Count INT;
EXEC @ReturnCode = usp_CountOrders @CustomerID = 9999, @OrderCount = @Count OUTPUT;
SELECT @ReturnCode AS ReturnCode, @Count AS OrderCount;          -- 1, 0
GO

DECLARE @ReturnCode INT, @Count INT;
EXEC @ReturnCode = usp_CountOrders @CustomerID = NULL, @OrderCount = @Count OUTPUT;   -- error (THROW)
GO





-- Example 2

DROP PROCEDURE IF EXISTS usp_FindDateReached;
GO

CREATE PROCEDURE usp_FindDateReached
    @OrderID INT,
    @Target DECIMAL(12,2),
    @DateReached DATE OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET @DateReached = NULL;

    DECLARE @Amount DECIMAL(12,2), @PaymentDate DATE;
    DECLARE @RunningTotal DECIMAL(14,2) = 0;

    DECLARE PaymentCursor CURSOR LOCAL FAST_FORWARD FOR
        SELECT Amount, PaymentDate
        FROM Payment
        WHERE Order_ID = @OrderID AND Status = N'Posted'
        ORDER BY PaymentDate, ID;

    OPEN PaymentCursor;
    FETCH NEXT FROM PaymentCursor INTO @Amount, @PaymentDate;

    WHILE @@FETCH_STATUS = 0 AND @DateReached IS NULL
    BEGIN
        SET @RunningTotal += @Amount;

        IF @RunningTotal >= @Target
        BEGIN
            SET @DateReached = @PaymentDate;
        END;

        FETCH NEXT FROM PaymentCursor INTO @Amount, @PaymentDate;
    END;

    CLOSE PaymentCursor;
    DEALLOCATE PaymentCursor;
END;
GO

-- Tests

DECLARE @Date DATE;
EXEC usp_FindDateReached @OrderID = 1003, @Target = 500, @DateReached = @Date OUTPUT;
SELECT @Date AS DateReached;                                     -- 2026-10-04
GO

DECLARE @Date DATE;
EXEC usp_FindDateReached @OrderID = 1003, @Target = 300, @DateReached = @Date OUTPUT;
SELECT @Date AS DateReached;                                     -- 2026-10-03
GO

DECLARE @Date DATE;
EXEC usp_FindDateReached @OrderID = 1003, @Target = 600, @DateReached = @Date OUTPUT;
SELECT @Date AS DateReached;                                     -- NULL
GO

DECLARE @Date DATE;
EXEC usp_FindDateReached @OrderID = 1002, @Target = 100, @DateReached = @Date OUTPUT;
SELECT @Date AS DateReached;                                     -- NULL (only a rejected payment)
GO

DECLARE @Date DATE;
EXEC usp_FindDateReached @OrderID = 9999, @Target = 100, @DateReached = @Date OUTPUT;
SELECT @Date AS DateReached;                                     -- NULL
GO





-- Example 3

DROP PROCEDURE IF EXISTS usp_OrdersPerDay;
GO

CREATE PROCEDURE usp_OrdersPerDay
    @DateFrom DATE,
    @DateTo DATE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @Result TABLE (
        Day DATE PRIMARY KEY,
        OrderCount INT NOT NULL
    );

    DECLARE @Day DATE = @DateFrom;
    DECLARE @Count INT;

    WHILE @Day <= @DateTo
    BEGIN
        SELECT @Count = COUNT(*)
        FROM Orders
        WHERE OrderDate = @Day;

        INSERT INTO @Result (Day, OrderCount)
        VALUES (@Day, @Count);

        SET @Day = DATEADD(DAY, 1, @Day);
    END;

    SELECT Day, OrderCount
    FROM @Result
    ORDER BY Day;
END;
GO

-- Tests

EXEC usp_OrdersPerDay @DateFrom = '20261001', @DateTo = '20261007';   -- 1,1,2,1,1,1,0
EXEC usp_OrdersPerDay @DateFrom = '20261014', @DateTo = '20261016';   -- 2,0,0
EXEC usp_OrdersPerDay @DateFrom = '20261011', @DateTo = '20261011';   -- 1 (empty order 1011 also counts)
GO





-- Exercise 1

DROP PROCEDURE IF EXISTS usp_GetProduct;
GO

CREATE PROCEDURE usp_GetProduct
    @ProductID INT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @Name NVARCHAR(100), @Category NVARCHAR(80), @Supplier NVARCHAR(100);
    DECLARE @Price DECIMAL(10,2), @IsActive BIT;
    DECLARE @AvailableStock INT, @Availability NVARCHAR(20);

    IF @ProductID IS NULL OR @ProductID <= 0
    BEGIN
        THROW 50011, N'ProductID must be a positive number.', 1;
    END;

    SELECT @Name = p.Name, @Price = p.Price, @IsActive = p.IsActive,
           @Category = c.Name, @Supplier = s.Name
    FROM Product p
    JOIN Category c ON c.ID = p.Category_ID
    JOIN Supplier s ON s.ID = p.Supplier_ID
    WHERE p.ID = @ProductID;

    IF @Name IS NULL
    BEGIN
        RETURN 1;
    END;

    -- only active warehouses count
    SELECT @AvailableStock = COALESCE(SUM(i.Quantity), 0)
    FROM Inventory i
    JOIN Warehouse w ON w.ID = i.Warehouse_ID
    WHERE i.Product_ID = @ProductID AND w.IsActive = 1;

    IF @IsActive = 0
    BEGIN
        SET @Availability = N'Discontinued';
    END
    ELSE IF @AvailableStock = 0
    BEGIN
        SET @Availability = N'Out of stock';
    END
    ELSE
    BEGIN
        SET @Availability = N'Available';
    END;

    SELECT @ProductID AS ProductID, @Name AS Name, @Category AS Category, @Supplier AS Supplier,
           @Price AS Price, @AvailableStock AS AvailableStock, @Availability AS Availability;

    RETURN 0;
END;
GO

-- Tests

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetProduct @ProductID = 101;              -- Keyboard, 12, Available
SELECT @ReturnCode AS ReturnCode;                                -- 0
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetProduct @ProductID = 550;              -- Adapter, 10, Discontinued
SELECT @ReturnCode AS ReturnCode;                                -- 0
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetProduct @ProductID = 880;              -- USB cable, 0, Out of stock
SELECT @ReturnCode AS ReturnCode;                                -- 0
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetProduct @ProductID = 990;              -- Label printer, 0, Out of stock
SELECT @ReturnCode AS ReturnCode;                                -- 0
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetProduct @ProductID = 9999;             -- no result set
SELECT @ReturnCode AS ReturnCode;                                -- 1
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetProduct @ProductID = NULL;             -- error (THROW)
GO





-- Exercise 2

DROP PROCEDURE IF EXISTS usp_GetOrderBalance;
GO

CREATE PROCEDURE usp_GetOrderBalance
    @OrderID INT,
    @AmountDue DECIMAL(14,2) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET @AmountDue = NULL;

    DECLARE @OrderValue DECIMAL(14,2), @AmountPaid DECIMAL(14,2);

    IF @OrderID IS NULL OR @OrderID <= 0
    BEGIN
        THROW 50021, N'OrderID must be a positive number.', 1;
    END;

    IF NOT EXISTS (SELECT 1 FROM Orders WHERE ID = @OrderID)
    BEGIN
        RETURN 1;
    END;

    -- two separate queries, so nothing is counted twice
    SELECT @OrderValue = COALESCE(SUM(Quantity * UnitPrice), 0)
    FROM OrderItem
    WHERE Order_ID = @OrderID;

    SELECT @AmountPaid = COALESCE(SUM(Amount), 0)
    FROM Payment
    WHERE Order_ID = @OrderID AND Status = N'Posted';

    SET @AmountDue = @OrderValue - @AmountPaid;

    SELECT @OrderID AS OrderID, @OrderValue AS OrderValue, @AmountPaid AS AmountPaid, @AmountDue AS AmountDue;

    RETURN 0;
END;
GO

-- Tests

DECLARE @ReturnCode INT, @Balance DECIMAL(14,2);
EXEC @ReturnCode = usp_GetOrderBalance @OrderID = 1001, @AmountDue = @Balance OUTPUT;   -- 320.00, 100.00, 220.00
SELECT @ReturnCode AS ReturnCode, @Balance AS AmountDue;                               -- 0, 220.00
GO

DECLARE @ReturnCode INT, @Balance DECIMAL(14,2);
EXEC @ReturnCode = usp_GetOrderBalance @OrderID = 1004, @AmountDue = @Balance OUTPUT;   -- 370.00, 400.00, -30.00
SELECT @ReturnCode AS ReturnCode, @Balance AS AmountDue;                               -- 0, -30.00
GO

DECLARE @ReturnCode INT, @Balance DECIMAL(14,2);
EXEC @ReturnCode = usp_GetOrderBalance @OrderID = 1011, @AmountDue = @Balance OUTPUT;   -- 0.00, 0.00, 0.00
SELECT @ReturnCode AS ReturnCode, @Balance AS AmountDue;                               -- 0, 0.00
GO

DECLARE @ReturnCode INT, @Balance DECIMAL(14,2);
EXEC @ReturnCode = usp_GetOrderBalance @OrderID = 1013, @AmountDue = @Balance OUTPUT;   -- 135.00, 44.99, 90.01
SELECT @ReturnCode AS ReturnCode, @Balance AS AmountDue;                               -- 0, 90.01
GO

DECLARE @ReturnCode INT, @Balance DECIMAL(14,2);
EXEC @ReturnCode = usp_GetOrderBalance @OrderID = 1002, @AmountDue = @Balance OUTPUT;   -- 1150.00, 0.00, 1150.00
SELECT @ReturnCode AS ReturnCode, @Balance AS AmountDue;                               -- 0, 1150.00
GO

DECLARE @ReturnCode INT, @Balance DECIMAL(14,2);
EXEC @ReturnCode = usp_GetOrderBalance @OrderID = 9999, @AmountDue = @Balance OUTPUT;   -- no result set
SELECT @ReturnCode AS ReturnCode, @Balance AS AmountDue;                               -- 1, NULL
GO

DECLARE @ReturnCode INT, @Balance DECIMAL(14,2);
EXEC @ReturnCode = usp_GetOrderBalance @OrderID = NULL, @AmountDue = @Balance OUTPUT;   -- error (THROW)
GO





-- Exercise 3

DROP PROCEDURE IF EXISTS usp_GetCustomerOrders;
GO

CREATE PROCEDURE usp_GetCustomerOrders
    @CustomerID INT,
    @Status NVARCHAR(12) = NULL,
    @DateFrom DATE = NULL
AS
BEGIN
    SET NOCOUNT ON;

    IF @CustomerID IS NULL OR @CustomerID <= 0
    BEGIN
        THROW 50031, N'CustomerID must be a positive number.', 1;
    END;

    IF @Status IS NOT NULL AND @Status NOT IN (N'New', N'Paid', N'Shipped', N'Cancelled')
    BEGIN
        THROW 50032, N'Status must be New, Paid, Shipped or Cancelled.', 1;
    END;

    IF NOT EXISTS (SELECT 1 FROM Customer WHERE ID = @CustomerID)
    BEGIN
        RETURN 1;
    END;

    -- delivery city = city of the order address, not of the customer
    SELECT o.ID AS OrderID, o.OrderDate, o.PaymentDueDate, o.Status, c.Name AS DeliveryCity
    FROM Orders o
    JOIN CustomerAddress a ON a.ID = o.Address_ID
    JOIN City c ON c.ID = a.City_ID
    WHERE o.Customer_ID = @CustomerID
      AND (@Status IS NULL OR o.Status = @Status)
      AND (@DateFrom IS NULL OR o.OrderDate >= @DateFrom)
    ORDER BY o.OrderDate DESC, o.ID DESC;

    RETURN 0;
END;
GO

-- Tests

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetCustomerOrders @CustomerID = 2, @Status = N'New';                          -- 1015, 1014, 1002
SELECT @ReturnCode AS ReturnCode;                                                                   -- 0
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetCustomerOrders @CustomerID = 2, @Status = N'New', @DateFrom = '20261010';  -- 1015, 1014
SELECT @ReturnCode AS ReturnCode;                                                                   -- 0
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetCustomerOrders @CustomerID = 1, @Status = N'Shipped';                      -- 1013, 1003 (Krakow), 1007
SELECT @ReturnCode AS ReturnCode;                                                                   -- 0
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetCustomerOrders @CustomerID = 6;                                           -- empty result set
SELECT @ReturnCode AS ReturnCode;                                                                   -- 0
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetCustomerOrders @CustomerID = 9999;                                        -- no result set
SELECT @ReturnCode AS ReturnCode;                                                                   -- 1
GO

DECLARE @ReturnCode INT;
EXEC @ReturnCode = usp_GetCustomerOrders @CustomerID = 2, @Status = N'Invalid';                      -- error (THROW)
GO





-- Exercise 4

DROP PROCEDURE IF EXISTS usp_PreparePaymentReminders;
GO

CREATE PROCEDURE usp_PreparePaymentReminders
    @AsOfDate DATE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @OrderID INT, @Email NVARCHAR(200);
    DECLARE @OrderValue DECIMAL(14,2), @AmountPaid DECIMAL(14,2), @AmountDue DECIMAL(14,2);
    DECLARE @CreatedCount INT = 0, @SkippedCount INT = 0;

    IF @AsOfDate IS NULL
    BEGIN
        THROW 50041, N'AsOfDate is required.', 1;
    END;

    DECLARE OrderCursor CURSOR LOCAL FAST_FORWARD FOR
        SELECT o.ID, c.Email
        FROM Orders o
        JOIN Customer c ON c.ID = o.Customer_ID
        WHERE o.Status <> N'Cancelled' AND o.PaymentDueDate < @AsOfDate
        ORDER BY o.ID;

    OPEN OrderCursor;
    FETCH NEXT FROM OrderCursor INTO @OrderID, @Email;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        -- total to pay, the same as in Exercise 2
        SELECT @OrderValue = COALESCE(SUM(Quantity * UnitPrice), 0)
        FROM OrderItem
        WHERE Order_ID = @OrderID;

        SELECT @AmountPaid = COALESCE(SUM(Amount), 0)
        FROM Payment
        WHERE Order_ID = @OrderID AND Status = N'Posted';

        SET @AmountDue = @OrderValue - @AmountPaid;

        -- total to pay 0 or less: not counted at all
        IF @AmountDue > 0
        BEGIN
            IF @Email IS NULL
               OR EXISTS (SELECT 1 FROM Notification
                          WHERE Order_ID = @OrderID AND ReminderDate = @AsOfDate AND Type = N'Payment')
            BEGIN
                SET @SkippedCount += 1;
            END
            ELSE
            BEGIN
                INSERT INTO Notification (Order_ID, ReminderDate, Type, MessageText)
                VALUES (@OrderID, @AsOfDate, N'Payment',
                        CONCAT(N'Payment reminder for order ', @OrderID, N'. Total to pay: ', @AmountDue, N' PLN.'));

                SET @CreatedCount += 1;
            END;
        END;

        FETCH NEXT FROM OrderCursor INTO @OrderID, @Email;
    END;

    CLOSE OrderCursor;
    DEALLOCATE OrderCursor;

    SELECT @CreatedCount AS CreatedCount, @SkippedCount AS SkippedCount;

    RETURN 0;
END;
GO

-- Tests
-- Before each test the data should be restored (run orders_setup.sql again).
-- Instead of that, the DELETE below brings Notification back to its initial
-- state (only the reminder for order 1001 on 2026-10-15 stays).

DELETE FROM Notification WHERE NOT (Order_ID = 1001 AND ReminderDate = '20261015');
EXEC usp_PreparePaymentReminders @AsOfDate = '20261015';         -- 2, 2  (created: 1002, 1005; skipped: 1001, 1008)
SELECT COUNT(*) AS RowsInNotification FROM Notification;         -- 3
SELECT * FROM Notification ORDER BY ID;
GO

-- the same call again, without restoring the data - no duplicates
EXEC usp_PreparePaymentReminders @AsOfDate = '20261015';         -- 0, 4
SELECT COUNT(*) AS RowsInNotification FROM Notification;         -- 3
GO

DELETE FROM Notification WHERE NOT (Order_ID = 1001 AND ReminderDate = '20261015');
EXEC usp_PreparePaymentReminders @AsOfDate = '20261001';         -- 0, 0
SELECT COUNT(*) AS RowsInNotification FROM Notification;         -- 1
GO

DELETE FROM Notification WHERE NOT (Order_ID = 1001 AND ReminderDate = '20261015');
EXEC usp_PreparePaymentReminders @AsOfDate = '20261016';         -- 4, 1  (created: 1001, 1002, 1005, 1016; skipped: 1008)
SELECT COUNT(*) AS RowsInNotification FROM Notification;         -- 5
GO

EXEC usp_PreparePaymentReminders @AsOfDate = NULL;               -- error (THROW)
GO

-- back to the initial state
DELETE FROM Notification WHERE NOT (Order_ID = 1001 AND ReminderDate = '20261015');
GO





-- Exercise 5

DROP PROCEDURE IF EXISTS usp_PlanReplenishment;
GO

CREATE PROCEDURE usp_PlanReplenishment
    @WarehouseID INT,
    @Budget DECIMAL(12,2)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @ProductID INT, @Name NVARCHAR(100), @Price DECIMAL(10,2);
    DECLARE @Quantity INT, @MinimumStock INT;
    DECLARE @Needed INT, @Affordable INT, @ToBuy INT;
    DECLARE @BudgetLeft DECIMAL(12,2);

    DECLARE @Plan TABLE (
        ProductID INT PRIMARY KEY,
        Name NVARCHAR(100) NOT NULL,
        QuantityToBuy INT NOT NULL,
        Price DECIMAL(10,2) NOT NULL,
        Cost DECIMAL(12,2) NOT NULL
    );

    IF @WarehouseID IS NULL OR @WarehouseID <= 0
    BEGIN
        THROW 50051, N'WarehouseID must be a positive number.', 1;
    END;

    IF @Budget IS NULL OR @Budget < 0
    BEGIN
        THROW 50052, N'Budget must not be NULL or negative.', 1;
    END;

    SET @BudgetLeft = @Budget;

    DECLARE InventoryCursor CURSOR LOCAL FAST_FORWARD FOR
        SELECT i.Product_ID, p.Name, p.Price, i.Quantity, i.MinimumStock
        FROM Inventory i
        JOIN Product p ON p.ID = i.Product_ID
        WHERE i.Warehouse_ID = @WarehouseID
          AND p.IsActive = 1
          AND i.Quantity < i.MinimumStock
        ORDER BY i.Product_ID;

    OPEN InventoryCursor;
    FETCH NEXT FROM InventoryCursor INTO @ProductID, @Name, @Price, @Quantity, @MinimumStock;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        SET @Needed = @MinimumStock - @Quantity;
        SET @Affordable = FLOOR(@BudgetLeft / @Price);

        IF @Needed < @Affordable
        BEGIN
            SET @ToBuy = @Needed;
        END
        ELSE
        BEGIN
            SET @ToBuy = @Affordable;
        END;

        -- nothing to buy: skip this product, but go on with the next one
        IF @ToBuy > 0
        BEGIN
            INSERT INTO @Plan (ProductID, Name, QuantityToBuy, Price, Cost)
            VALUES (@ProductID, @Name, @ToBuy, @Price, @ToBuy * @Price);

            SET @BudgetLeft -= @ToBuy * @Price;
        END;

        FETCH NEXT FROM InventoryCursor INTO @ProductID, @Name, @Price, @Quantity, @MinimumStock;
    END;

    CLOSE InventoryCursor;
    DEALLOCATE InventoryCursor;

    SELECT ProductID, Name, QuantityToBuy, Price, Cost
    FROM @Plan
    ORDER BY ProductID;

    RETURN 0;
END;
GO

-- Tests

EXEC usp_PlanReplenishment @WarehouseID = 1, @Budget = 500;      -- 101 x2 = 240.00, 205 x3 = 240.00
EXEC usp_PlanReplenishment @WarehouseID = 1, @Budget = 150;      -- 101 x1 = 120.00
EXEC usp_PlanReplenishment @WarehouseID = 1, @Budget = 100;      -- 205 x1 = 80.00 (101 is too expensive)
EXEC usp_PlanReplenishment @WarehouseID = 1, @Budget = 0;        -- empty result set
EXEC usp_PlanReplenishment @WarehouseID = 9999, @Budget = 100;   -- empty result set
GO

EXEC usp_PlanReplenishment @WarehouseID = 1, @Budget = -1;       -- error (THROW)
GO





-- Exercise 6

DROP PROCEDURE IF EXISTS usp_SplitAmount;
GO

CREATE PROCEDURE usp_SplitAmount
    @Amount DECIMAL(12,2),
    @InstalmentCount INT,
    @FirstDate DATE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @TotalGrosz INT, @BaseGrosz INT, @RestGrosz INT, @InstalmentGrosz INT;
    DECLARE @Number INT = 1;

    DECLARE @Instalments TABLE (
        InstalmentNumber INT PRIMARY KEY,
        DueDate DATE NOT NULL,
        InstalmentAmount DECIMAL(12,2) NOT NULL
    );

    IF @Amount IS NULL OR @Amount <= 0
    BEGIN
        THROW 50061, N'Amount must be more than 0.', 1;
    END;

    IF @InstalmentCount IS NULL OR @InstalmentCount < 1 OR @InstalmentCount > 12
    BEGIN
        THROW 50062, N'InstalmentCount must be from 1 to 12.', 1;
    END;

    IF @FirstDate IS NULL
    BEGIN
        THROW 50063, N'FirstDate must not be NULL.', 1;
    END;

    -- work in grosz (whole numbers), so the instalments add up exactly
    SET @TotalGrosz = CAST(@Amount * 100 AS INT);
    SET @BaseGrosz = @TotalGrosz / @InstalmentCount;    -- INT / INT = whole number
    SET @RestGrosz = @TotalGrosz % @InstalmentCount;    -- remainder

    WHILE @Number <= @InstalmentCount
    BEGIN
        IF @Number = @InstalmentCount
        BEGIN
            SET @InstalmentGrosz = @BaseGrosz + @RestGrosz;   -- the last one gets the remainder
        END
        ELSE
        BEGIN
            SET @InstalmentGrosz = @BaseGrosz;
        END;

        -- always counted from @FirstDate, so 31.01 -> 28.02 -> 31.03 (not 28.03)
        INSERT INTO @Instalments (InstalmentNumber, DueDate, InstalmentAmount)
        VALUES (@Number, DATEADD(MONTH, @Number - 1, @FirstDate), @InstalmentGrosz / 100.0);

        SET @Number += 1;
    END;

    SELECT InstalmentNumber, DueDate, InstalmentAmount
    FROM @Instalments
    ORDER BY InstalmentNumber;

    RETURN 0;
END;
GO

-- Tests

EXEC usp_SplitAmount @Amount = 220.00, @InstalmentCount = 3, @FirstDate = '20261031';   -- 73.33, 73.33, 73.34
EXEC usp_SplitAmount @Amount = 90.01,  @InstalmentCount = 3, @FirstDate = '20261031';   -- 30.00, 30.00, 30.01
EXEC usp_SplitAmount @Amount = 0.02,   @InstalmentCount = 3, @FirstDate = '20261031';   -- 0.00, 0.00, 0.02
EXEC usp_SplitAmount @Amount = 100.00, @InstalmentCount = 4, @FirstDate = '20260131';   -- 01-31, 02-28, 03-31, 04-30
EXEC usp_SplitAmount @Amount = 100.00, @InstalmentCount = 1, @FirstDate = '20261031';   -- 100.00
GO

EXEC usp_SplitAmount @Amount = 0, @InstalmentCount = 3, @FirstDate = '20261031';        -- error (THROW)
GO

EXEC usp_SplitAmount @Amount = 100.00, @InstalmentCount = 13, @FirstDate = '20261031';  -- error (THROW)
GO
