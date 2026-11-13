-- Task 1

DECLARE @OrderID INT = 1002;
DECLARE @Status NVARCHAR(12), @IsActive BIT;
DECLARE @Goods DECIMAL(12, 2), @DiscountPct INT, @DiscountAmount DECIMAL(12, 2);
DECLARE @AfterDiscount DECIMAL(12, 2), @Delivery DECIMAL(12, 2), @AmountDue DECIMAL(12, 2);

SELECT @Status = o.Status, @IsActive = c.IsActive
FROM Orders o
JOIN Customers c ON o.CustomerID = c.CustomerID
WHERE o.OrderID = @OrderID;

IF @@ROWCOUNT = 0
BEGIN
    PRINT CONCAT ('Order', @OrderID, ' was not found.');
    RETURN;
end;

IF @IsActive = 0
BEGIN
    PRINT CONCAT('Order ', @OrderID, ': the customer is inactive.');
    RETURN;
end;

IF @Status <> N'New'
BEGIN
    PRINT CONCAT ('Order ', @OrderID, ' is not new.');
end;

IF NOT EXISTS (SELECT 1 FROM OrderItems WHERE OrderID = @OrderID)
BEGIN
    PRINT CONCAT('Order ', @OrderID, ' has not items.');
    RETURN;
end;


SELECT @Goods = SUM(Quantity * UnitPrice)
FROM OrderItems
WHERE OrderID = @OrderID;

SET @DiscountPct = CASE
                        WHEN @Goods >= 1000 THEN 10
                        WHEN @Goods >= 500 THEN 5
                        ELSE 0
                    END;

SET @DiscountAmount = ROUND(@Goods * @DiscountPct / 100.0, 2)
SET @AfterDiscount = @Goods - @DiscountAmount;

SET @Delivery = CASE WHEN @AfterDiscount >= 500 THEN 0 ELSE 20 END;
SET @AmountDue = @AfterDiscount + @Delivery;

PRINT CONCAT('Order:           ', @OrderID);
PRINT CONCAT('Discount:        ', @DiscountPct, '% = ', @DiscountAmount);
PRINT CONCAT('After discount:  ', @AfterDiscount);
PRINT CONCAT('Delivery:        ', @Delivery);
PRINT CONCAT('Amount due:      ', @AmountDue);





-- Task 1 (practice)

DECLARE @OrderID INT = 1012;
DECLARE @Status NVARCHAR(12);
DECLARE @TotalNumOfUnits INT, @NumOfDiffItems INT;
DECLARE @ParcelSize NVARCHAR(20), @ParcelCost INT;

SELECT @Status = o.Status
FROM Orders o
WHERE @OrderID = o.OrderID;

IF @@ROWCOUNT = 0
BEGIN
    PRINT 'Not found'
    RETURN
end;

IF @Status <> N'Paid'
BEGIN
    PRINT 'Status is not paid'
    RETURN
end;

IF NOT EXISTS (SELECT 1 FROM OrderItems o WHERE @OrderID = OrderID)
BEGIN
    PRINT 'Order has not items'
    RETURN
end;

SELECT @TotalNumOfUnits = SUM(Quantity),
       @NumOfDiffItems = COUNT(DISTINCT ProductID)
FROM OrderItems
WHERE OrderID = @OrderID;

SET @ParcelSize = CASE
    WHEN @TotalNumOfUnits >= 8 THEN 'large'
    WHEN @TotalNumOfUnits >= 4 THEN 'medium'
    ELSE 'small'
END;

SET @ParcelCost = CASE
    WHEN @TotalNumOfUnits >= 8 THEN 40
    WHEN @TotalNumOfUnits >= 4 THEN 25
    ELSE 15
END;

IF EXISTS (
    SELECT 1
    FROM OrderItems oi
    JOIN Products p ON oi.ProductID = p.ProductID
    WHERE oi.OrderID = @OrderID
        AND p.IsActive = 0)
BEGIN
    PRINT 'You have inactive product'
end;

PRINT CONCAT('Order ID: ', @OrderID);
PRINT CONCAT('Units: ', @TotalNumOfUnits);
PRINT CONCAT('Different products: ', @NumOfDiffItems);
PRINT CONCAT('Parcel: ', @ParcelSize);
PRINT CONCAT('Cost: ', @ParcelCost);





-- Task 2

DECLARE @DateFrom  DATE          = '20261001';
DECLARE @DateTo    DATE          = '20261101';
DECLARE @Threshold DECIMAL(12,2) = 1000;

IF @DateFrom IS NULL OR @DateTo IS NULL OR @Threshold IS NULL
BEGIN
    PRINT 'Missing input: dates and threshold are required.';
    RETURN;
END;

IF @DateFrom >= @DateTo
BEGIN
    PRINT 'Invalid date range: the start date must be before the end date.';
    RETURN;
END;

IF @Threshold < 0
BEGIN
    PRINT 'The threshold cannot be negative.';
    RETURN;
END;

DROP TABLE IF EXISTS #CustomerReport;

CREATE TABLE #CustomerReport (
    CustomerID INT           PRIMARY KEY,
    Name       NVARCHAR(100) NOT NULL,
    OrderCount INT           NOT NULL,
    Sales      DECIMAL(12,2) NOT NULL
);

INSERT INTO #CustomerReport (CustomerID, Name, OrderCount, Sales)
SELECT c.CustomerID,
       c.Name,
       COUNT(o.OrderID),
       COALESCE(SUM(v.OrderValue), 0)
FROM Customers c
LEFT JOIN Orders o
       ON o.CustomerID = c.CustomerID
      AND o.Status IN (N'Paid', N'Shipped')
      AND o.OrderDate >= @DateFrom
      AND o.OrderDate <  @DateTo
LEFT JOIN (SELECT OrderID, SUM(Quantity * UnitPrice) AS OrderValue
           FROM OrderItems
           GROUP BY OrderID) v
       ON v.OrderID = o.OrderID
WHERE c.IsActive = 1
GROUP BY c.CustomerID, c.Name;

SELECT CustomerID, Name, OrderCount, Sales
FROM #CustomerReport
ORDER BY Sales DESC, CustomerID;

SELECT COUNT(*)                                              AS Customers,
       SUM(OrderCount)                                       AS Orders,
       SUM(Sales)                                            AS TotalSales,
       SUM(CASE WHEN Sales >= @Threshold THEN 1 ELSE 0 END)  AS CustomersAtThreshold
FROM #CustomerReport;





-- Task 2 (practice)

DECLARE @DateFrom DATE = '20261001',
        @DateTo   DATE = '20261101',
        @MinUnits INT  = 2;

IF @DateFrom IS NULL OR @DateTo IS NULL OR @MinUnits IS NULL
BEGIN
    PRINT 'Missing input';
    RETURN;
end;

IF @DateFrom >= @DateTo
BEGIN
    PRINT 'Bad dates';
    RETURN;
end;

IF @MinUnits < 0
BEGIN
    PRINT 'MinUnits negative';
    RETURN;
end;


DROP TABLE IF EXISTS #ProductReport;

CREATE TABLE #ProductReport(
    ProductID INT PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    OrderCount INT NOT NULL,
    UnitsSold INT NOT NULL,
    Sales DECIMAL(12, 2) NOT NULL
);

INSERT INTO #ProductReport (ProductID, Name, OrderCount, UnitsSold, Sales)
SELECT p.ProductID,
       p.Name,
       COUNT(DISTINCT s.OrderID),
       COALESCE(SUM(s.Quantity), 0),
       COALESCE(SUM(s.Quantity * s.UnitPrice), 0)
FROM Products p
LEFT JOIN (
    SELECT oi.ProductID, oi.OrderID, oi.Quantity, oi.UnitPrice
    FROM OrderItems oi
    JOIN Orders o ON o.OrderID = oi.OrderID
    WHERE o.Status IN (N'Paid', N'Shipped')
      AND o.OrderDate >= @DateFrom
      AND o.OrderDate <  @DateTo
) s ON s.ProductID = p.ProductID
WHERE p.IsActive = 1
GROUP BY p.ProductID, p.Name;

SELECT ProductID, Name, OrderCount, UnitsSold, Sales
FROM #ProductReport
ORDER BY Sales DESC, ProductID

SELECT COUNT(*) AS Products,
       SUM(UnitsSold) AS TotalUnits,
       SUM(Sales) AS TotalSales,
       SUM(CASE WHEN UnitsSold >= @MinUnits THEN 1 ELSE 0 END) AS ProductAtMinUnits
FROM #ProductReport;





-- Task 3

DECLARE @Budget DECIMAL(12, 2) = 1000, @ProductLimit INT = 2;

DECLARE @PurchasePlan TABLE (
    ProductId INT,
    Name NVARCHAR(100),
    Quantity INT,
    UnitPrice DECIMAL(12, 2),
    LineCost DECIMAL(12, 2)
);

DECLARE @NextID INT, @CurrentID INT;
DECLARE @Name NVARCHAR(100), @Price DECIMAL(10, 2), @Stock INT, @MinStock INT, @Active BIT;
DECLARE @Quantity INT, @LineCost DECIMAL(12, 2);
DECLARE @BudgetLeft DECIMAL(12, 2) = @Budget, @PlanCount INT = 0;

IF @Budget IS NULL OR @Budget < 0 OR @ProductLimit IS NULL OR @ProductLimit <= 0
BEGIN
    PRINT 'Invalid input.';
    RETURN;
end;

SELECT @NextID = MIN(ProductID) FROM Products;

WHILE @NextID IS NOT NULL AND @BudgetLeft > 0 AND @PlanCount < @ProductLimit
BEGIN
    SET @CurrentID = @NextID;
    SELECT @NextID = MIN(ProductID)
    FROM Products
    WHERE ProductID > @CurrentID;

    SELECT @Name = Name, @Price = ListPrice, @Stock = StockQuantity, @MinStock = MinimumStock, @Active = IsActive
    FROM Products
    WHERE ProductID = @CurrentID;

    IF @Active = 1 AND @Stock < @MinStock
    BEGIN
        SET @Quantity = @MinStock - @Stock;
        SET @LineCost = @Quantity * @Price;

        IF @LineCost <= @BudgetLeft
        BEGIN
            INSERT INTO @PurchasePlan (ProductID, Name, Quantity, UnitPrice, LineCost)
            VALUES (@CurrentID, @Name, @Quantity, @Price, @LineCost);

            SET @BudgetLeft = @BudgetLeft - @LineCost;
            SET @PlanCount = @PlanCount + 1;
        end;
    end;
end;

SELECT ProductID, Name, Quantity, UnitPrice, LineCost
FROM @PurchasePlan
ORDER BY ProductID;

SELECT COALESCE(SUM(LineCost), 0) AS TotalCost, @BudgetLeft AS BudgetLeft
FROM @PurchasePlan;





-- Task 3 (practice)

DECLARE @Budget DECIMAL(12, 2) = 300, @MaxVouchers INT = 5;

DECLARE @VouchersPlan TABLE (
    CustomerID INT,
    Name NVARCHAR(100),
    Sales DECIMAL(12, 2),
    Voucher DECIMAL(12, 2)
)

DECLARE @NextID INT, @CurrentID INT;
DECLARE @Name NVARCHAR(100), @IsActive BIT, @Sales DECIMAL(12,2), @Voucher DECIMAL(12,2);
DECLARE @BudgetLeft DECIMAL(12,2) = @Budget, @VoucherCount INT = 0;

IF @Budget IS NULL OR @Budget < 0 OR @MaxVouchers <= 0 OR @MaxVouchers IS NULL
BEGIN
    PRINT 'Invalid input';
    RETURN;
end

SELECT @NextID = MIN(CustomerID) FROM Customers;

WHILE @NextID IS NOT NULL
BEGIN
    SET @CurrentID = @NextID;
    SELECT @NextID = MIN(CustomerID)
    FROM Customers
    WHERE CustomerID > @CurrentID;

    SELECT @Name = Name, @IsActive = IsActive
    FROM Customers
    WHERE CustomerID = @CurrentID;

    IF @IsActive = 1
    BEGIN
        SET @Sales = (SELECT COALESCE(SUM(oi.Quantity * oi.UnitPrice), 0)
                      FROM Orders o
                      JOIN OrderItems oi ON oi.OrderID = o.OrderID
                      WHERE o.CustomerID = @CurrentID
                        AND o.Status IN (N'Paid', N'Shipped'));

        IF @Sales > 0
        BEGIN
            SET @Voucher = ROUND(@Sales * 0.10, 2);

            IF @Voucher <= @BudgetLeft
            BEGIN
                INSERT INTO @VouchersPlan (CustomerID, Name, Sales, Voucher)
                VALUES (@CurrentID, @Name, @Sales, @Voucher);

                SET @BudgetLeft = @BudgetLeft - @Voucher;
                SET @VoucherCount = @VoucherCount + 1;
            end
        end
    end
end

SELECT CustomerID, Name, Sales, Voucher
FROM @VouchersPlan
ORDER BY CustomerID;

SELECT COALESCE(SUM(Voucher), 0) AS TotalVouchers, @BudgetLeft AS BudgetLeft
FROM @VouchersPlan;





-- 4

DECLARE @AnalysisDate DATE = '20261015'

IF @AnalysisDate IS NULL
BEGIN
    PRINT 'The analysis date is missing';
    RETURN;
end

DECLARE @Created INT = 0, @Skipped INT = 0;

BEGIN TRY
    DECLARE ReminderCursor CURSOR LOCAL FAST_FORWARD FOR
        SELECT o.OrderID, c.Email, SUM(i.Quantity * i.UnitPrice), DATEDIFF(DAY, o.PaymentDueDate, @AnalysisDate)
        FROM Orders o
        JOIN Customers c ON c.CustomerID = o.CustomerID
        JOIN OrderItems i ON i.OrderID = o.OrderID
        WHERE o.Status = 'New' AND c.IsActive = 1 AND o.PaymentDueDate < @AnalysisDate
        GROUP BY o.OrderID, c.Email, o.PaymentDueDate
        ORDER BY o.OrderID;

    DECLARE @OrderID INT, @Email NVARCHAR(200), @GoodsValue DECIMAL(12, 2), @DaysLate INT;

    OPEN ReminderCursor;
    FETCH NEXT FROM ReminderCursor INTO @OrderID, @Email, @GoodsValue, @DaysLate;

    WHILE @@FETCH_STATUS = 0
    BEGIN
        IF @Email IS NULL
        BEGIN
            PRINT CONCAT('Order ', @OrderID, ' skipped: no email address.');
            SET @Skipped = @Skipped + 1;
        end
        ELSE
        BEGIN
            PRINT CONCAT('Reminder | order ', @OrderID, ' | ', @Email, ' | goods value ', @GoodsValue, 'PLN | days late: ', @DaysLate)
            SET @Created = @Created + 1;
        end

        FETCH NEXT FROM ReminderCursor INTO @OrderID, @Email, @GoodsValue, @DaysLate;
    end;

    CLOSE ReminderCursor;
    DEALLOCATE ReminderCursor;
END TRY
BEGIN CATCH
    IF CURSOR_STATUS('local', 'ReminderCursor') >= 0
        CLOSE ReminderCursor;
    IF CURSOR_STATUS('local', 'ReminderCursor') >= -1
        DEALLOCATE ReminderCursor;
    THROW;
end catch;

PRINT CONCAT('Reminders created: ', @Created);
PRINT CONCAT('Orders skipped (no email address): ', @Skipped);





-- Task 4 (practice)

DECLARE @OutOfStock INT = 0;

DECLARE StockCursor CURSOR LOCAL FAST_FORWARD FOR
    SELECT p.ProductID, p.Name, p.StockQuantity
    FROM Products p
    WHERE p.IsActive = 1
    ORDER BY p.ProductID;

DECLARE @ProductID INT, @Name NVARCHAR(100), @StockQuantity INT;

OPEN StockCursor;
FETCH NEXT FROM StockCursor INTO @ProductID, @Name, @StockQuantity;

WHILE @@FETCH_STATUS = 0
BEGIN
    PRINT CONCAT('Product ', @Name, ': stock ', @StockQuantity);
    IF @StockQuantity = 0
    BEGIN
        PRINT 'OUT OF STOCK';
        SET @OutOfStock = @OutOfStock + 1;
    end;

    FETCH NEXT FROM StockCursor INTO @ProductID, @Name, @StockQuantity;
end;

CLOSE StockCursor;
DEALLOCATE StockCursor;

PRINT CONCAT('Products out of stock: ', @OutOfStock);





-- Task 5

SET XACT_ABORT ON;

DECLARE @OrderID INT = 1003;
DECLARE @Status NVARCHAR(12), @ItemCount INT, @UnitsShipped INT;
DECLARE @ProductID INT, @Needed INT, @InStock INT, @Message NVARCHAR(2048);
DECLARE @Need TABLE (ProductID INT, Quantity INT);

BEGIN TRY
    BEGIN TRANSACTION;

    -- Check 1: exists
    SELECT @Status = Status FROM Orders WHERE OrderID = @OrderID;

    IF @Status IS NULL
    BEGIN
        SET @Message = CONCAT('Order ', @OrderID, ' was not found.');
        THROW 50001, @Message, 1;
    END;

    -- Check 2: status
    IF @Status <> 'Paid'
    BEGIN
        SET @Message = CONCAT('Order ', @OrderID, ' has status ', @Status,
                              '. Only Paid orders can be shipped.');
        THROW 50002, @Message, 1;
    END;

    -- Total quantity per product
    INSERT INTO @Need (ProductID, Quantity)
    SELECT ProductID, SUM(Quantity)
    FROM OrderItems
    WHERE OrderID = @OrderID
    GROUP BY ProductID;

    -- Check 3: no items
    SELECT @ItemCount = COUNT(*) FROM @Need;

    IF @ItemCount = 0
    BEGIN
        SET @Message = CONCAT('Order ', @OrderID, ' has no items.');
        THROW 50003, @Message, 1;
    END;

    -- Check 4: inactive product
    SELECT TOP (1) @ProductID = n.ProductID
    FROM @Need AS n
    JOIN Products AS p ON p.ProductID = n.ProductID
    WHERE p.IsActive = 0
    ORDER BY n.ProductID;

    IF @ProductID IS NOT NULL
    BEGIN
        SET @Message = CONCAT('Order ', @OrderID, ' has an inactive product: ', @ProductID, '.');
        THROW 50004, @Message, 1;
    END;

    -- Check 5: enough stock
    SELECT TOP (1) @ProductID = n.ProductID, @Needed = n.Quantity, @InStock = p.StockQuantity
    FROM @Need AS n
    JOIN Products AS p ON p.ProductID = n.ProductID
    WHERE p.StockQuantity < n.Quantity
    ORDER BY n.ProductID;

    IF @ProductID IS NOT NULL
    BEGIN
        SET @Message = CONCAT('Not enough stock of product ', @ProductID,
                              ': needed ', @Needed, ', in stock ', @InStock, '.');
        THROW 50005, @Message, 1;
    END;

    -- Change 1: reduce stock
    UPDATE p
    SET p.StockQuantity = p.StockQuantity - n.Quantity
    FROM Products AS p
    JOIN @Need AS n ON n.ProductID = p.ProductID;

    -- Change 2: status
    UPDATE Orders
    SET Status = 'Shipped'
    WHERE OrderID = @OrderID;

    SELECT @UnitsShipped = SUM(Quantity) FROM @Need;

    COMMIT TRANSACTION;

    SELECT @OrderID AS OrderID, 'Shipped' AS NewStatus, @UnitsShipped AS UnitsShipped;
END TRY
BEGIN CATCH
    PRINT CONCAT('Error ', ERROR_NUMBER(), ': ', ERROR_MESSAGE());

    IF XACT_STATE() <> 0
    BEGIN
        ROLLBACK TRANSACTION;
    END;

    THROW;
END CATCH;





-- Task 5 (practice)

SET XACT_ABORT ON;

DECLARE @OrderID INT = 1008;
DECLARE @Status NVARCHAR(12), @Message NVARCHAR(2048);

BEGIN TRY
    BEGIN TRANSACTION;

    -- 1. READ the order
    SELECT @Status = Status FROM Orders WHERE OrderID = @OrderID;

    -- 2. Check: not found (works for 9999 AND NULL)
    IF @Status IS NULL
    BEGIN
        SET @Message = CONCAT('Order ', @OrderID, ' was not found.');
        THROW 50001, @Message, 1;
    END;

    -- 3. Check: only New orders
    IF @Status <> 'New'
    BEGIN
        SET @Message = CONCAT('Order ', @OrderID, ' has status ', @Status,
                              '. Only New orders can be cancelled.');
        THROW 50002, @Message, 1;
    END;

    -- 4. Change the REAL data
    UPDATE Orders
    SET Status = 'Cancelled'
    WHERE OrderID = @OrderID;

    COMMIT TRANSACTION;

    SELECT @OrderID AS OrderID, 'Cancelled' AS NewStatus;
END TRY
BEGIN CATCH
    PRINT CONCAT('Error ', ERROR_NUMBER(), ': ', ERROR_MESSAGE());

    IF XACT_STATE() <> 0
    BEGIN
        ROLLBACK TRANSACTION;
    END;

    THROW;
END CATCH;