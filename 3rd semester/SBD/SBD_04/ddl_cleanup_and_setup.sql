-- SBD, exercises 3: online store (cleanup + setup in one file, no custom schema).
-- SQL Server 2016 or later. Tables are created in the default dbo schema.
-- Select your own practice database in SSMS. This script does not create or drop a database.
--
-- WARNING: the script DROPS tables named OrderItems, Orders, Products and Customers
-- in dbo and recreates them. If your database already has tables with these names
-- that you want to keep, do not run this file there.
--
-- Run the whole file with no transaction open: it can be repeated at any time and
-- always restores the initial data. To only remove the tables, run PART 1 alone.
-- GO is a batch separator in SSMS.
SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

-- =====================================================================
-- PART 1: CLEANUP - drops only the four tables of this set
-- =====================================================================
BEGIN TRY
    BEGIN TRANSACTION;
    DROP TABLE IF EXISTS OrderItems;
    DROP TABLE IF EXISTS Orders;
    DROP TABLE IF EXISTS Products;
    DROP TABLE IF EXISTS Customers;
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

    CREATE TABLE Customers (
        CustomerID INT NOT NULL CONSTRAINT PK_Customers PRIMARY KEY,
        Name NVARCHAR(100) NOT NULL,
        Email NVARCHAR(200) NULL,
        IsActive BIT NOT NULL
    );

    CREATE TABLE Products (
        ProductID INT NOT NULL CONSTRAINT PK_Products PRIMARY KEY,
        Name NVARCHAR(100) NOT NULL,
        ListPrice DECIMAL(10,2) NOT NULL,
        StockQuantity INT NOT NULL,
        MinimumStock INT NOT NULL,
        IsActive BIT NOT NULL,
        CONSTRAINT CK_Products_Price CHECK (ListPrice > 0),
        CONSTRAINT CK_Products_Stock CHECK (StockQuantity >= 0),
        CONSTRAINT CK_Products_Minimum CHECK (MinimumStock >= 0)
    );

    CREATE TABLE Orders (
        OrderID INT NOT NULL CONSTRAINT PK_Orders PRIMARY KEY,
        CustomerID INT NOT NULL,
        OrderDate DATE NOT NULL,
        PaymentDueDate DATE NOT NULL,
        Status NVARCHAR(12) NOT NULL,
        CONSTRAINT FK_Orders_Customers FOREIGN KEY (CustomerID)
            REFERENCES Customers (CustomerID),
        CONSTRAINT CK_Orders_Status
            CHECK (Status IN (N'New', N'Paid', N'Shipped', N'Cancelled')),
        CONSTRAINT CK_Orders_DueDate CHECK (PaymentDueDate >= OrderDate)
    );

    CREATE TABLE OrderItems (
        OrderID INT NOT NULL,
        LineNumber INT NOT NULL,
        ProductID INT NOT NULL,
        Quantity INT NOT NULL,
        UnitPrice DECIMAL(10,2) NOT NULL,
        CONSTRAINT PK_Items PRIMARY KEY (OrderID, LineNumber),
        CONSTRAINT FK_Items_Orders FOREIGN KEY (OrderID)
            REFERENCES Orders (OrderID),
        CONSTRAINT FK_Items_Products FOREIGN KEY (ProductID)
            REFERENCES Products (ProductID),
        CONSTRAINT CK_Items_Number CHECK (LineNumber > 0),
        CONSTRAINT CK_Items_Quantity CHECK (Quantity > 0),
        CONSTRAINT CK_Items_Price CHECK (UnitPrice > 0)
    );

    INSERT INTO Customers (CustomerID, Name, Email, IsActive) VALUES
        (1, N'Anna Kowalska', N'anna@example.com', 1),
        (2, N'North Office', N'biuro@example.com', 1),
        (3, N'Old Mill Foundation', N'fundacja@example.com', 0),
        (4, N'New Studio', N'pracownia@example.com', 1),
        (5, N'Piotr Lis', NULL, 1);

    INSERT INTO Products
        (ProductID, Name, ListPrice, StockQuantity, MinimumStock, IsActive) VALUES
        (101, N'Keyboard', 120.00, 8, 5, 1),
        (205, N'Wireless mouse', 80.00, 3, 6, 1),
        (310, N'Monitor', 900.00, 2, 3, 1),
        (420, N'Docking station', 250.00, 0, 2, 1),
        (550, N'Adapter — old model', 50.00, 10, 4, 0),
        (660, N'Webcam', 200.00, 4, 4, 1);

    INSERT INTO Orders
        (OrderID, CustomerID, OrderDate, PaymentDueDate, Status) VALUES
        (1001, 1, '20261001', '20261005', N'New'),
        (1002, 2, '20261002', '20261006', N'New'),
        (1003, 1, '20261003', '20261007', N'Paid'),
        (1004, 2, '20261004', '20261008', N'Paid'),
        (1005, 3, '20261005', '20261009', N'New'),
        (1006, 2, '20261006', '20261010', N'Shipped'),
        (1007, 1, '20260928', '20261002', N'Shipped'),
        (1008, 5, '20261008', '20261012', N'New'),
        (1009, 1, '20261009', '20261013', N'Cancelled'),
        (1010, 2, '20261010', '20261020', N'New'),
        (1011, 1, '20261011', '20261021', N'New'),
        (1012, 2, '20261012', '20261022', N'Paid'),
        (1013, 1, '20261013', '20261023', N'Paid'),
        (1014, 2, '20261014', '20261024', N'Paid'),
        (1015, 2, '20261014', '20261025', N'New');

    INSERT INTO OrderItems
        (OrderID, LineNumber, ProductID, Quantity, UnitPrice) VALUES
        (1001, 1, 101, 2, 120.00),
        (1001, 2, 205, 1, 80.00),
        (1002, 1, 310, 1, 900.00),
        (1002, 2, 420, 1, 250.00),
        (1003, 1, 101, 2, 120.00),
        (1003, 2, 101, 1, 110.00),
        (1003, 3, 205, 2, 80.00),
        (1004, 1, 101, 1, 120.00),
        (1004, 2, 420, 1, 250.00),
        (1005, 1, 660, 1, 200.00),
        (1006, 1, 310, 1, 850.00),
        (1007, 1, 101, 1, 110.00),
        (1008, 1, 205, 1, 80.00),
        (1009, 1, 550, 2, 50.00),
        (1010, 1, 420, 2, 250.00),
        (1012, 1, 550, 1, 50.00),
        (1014, 1, 101, 5, 120.00),
        (1014, 2, 101, 4, 120.00),
        (1015, 1, 660, 5, 200.00);

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

-- =====================================================================
-- PART 3: CHECK - expected row counts: 5, 6, 15, 19
-- =====================================================================
SELECT N'Customers' AS TableName, COUNT(*) AS RecordCount FROM Customers
UNION ALL SELECT N'Products', COUNT(*) FROM Products
UNION ALL SELECT N'Orders', COUNT(*) FROM Orders
UNION ALL SELECT N'OrderItems', COUNT(*) FROM OrderItems;