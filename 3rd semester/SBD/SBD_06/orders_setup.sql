-- SBD, exercises 5: stored procedures. SQL Server 2016 SP1 or later.
-- Select your own practice database in SSMS and run the entire file.
-- The script drops the 12 exercise tables if they exist, creates them again and inserts the initial data.
-- Run the file again whenever you want the initial data back. Your procedures stay in the database.
-- Warning: tables with these names in the selected database are dropped. Use a practice database.
SET NOCOUNT ON;
SET XACT_ABORT ON;

IF DB_NAME() IN (N'master', N'model', N'msdb', N'tempdb')
    THROW 51000, N'Select your own practice database, not a system database.', 1;
IF @@TRANCOUNT <> 0
    THROW 51000, N'Run this script with no transaction open.', 1;

BEGIN TRY
    BEGIN TRANSACTION;

    -- Cleanup

    DROP TABLE IF EXISTS Notification;
    DROP TABLE IF EXISTS Payment;
    DROP TABLE IF EXISTS OrderItem;
    DROP TABLE IF EXISTS Orders;
    DROP TABLE IF EXISTS Inventory;
    DROP TABLE IF EXISTS Warehouse;
    DROP TABLE IF EXISTS Product;
    DROP TABLE IF EXISTS Supplier;
    DROP TABLE IF EXISTS Category;
    DROP TABLE IF EXISTS CustomerAddress;
    DROP TABLE IF EXISTS Customer;
    DROP TABLE IF EXISTS City;

    -- DDL

    CREATE TABLE City (
        ID INT NOT NULL,
        Name NVARCHAR(80) NOT NULL,
        PRIMARY KEY (ID),
        UNIQUE (Name)
    );

    CREATE TABLE Customer (
        ID INT NOT NULL,
        Name NVARCHAR(100) NOT NULL,
        Email NVARCHAR(200) NULL,
        IsActive BIT NOT NULL,
        City_ID INT NOT NULL,
        PRIMARY KEY (ID),
        FOREIGN KEY (City_ID) REFERENCES City (ID)
    );

    CREATE TABLE CustomerAddress (
        ID INT NOT NULL,
        Street NVARCHAR(120) NOT NULL,
        PostalCode NVARCHAR(12) NOT NULL,
        City_ID INT NOT NULL,
        Customer_ID INT NOT NULL,
        PRIMARY KEY (ID),
        UNIQUE (Customer_ID, ID),
        FOREIGN KEY (City_ID) REFERENCES City (ID),
        FOREIGN KEY (Customer_ID) REFERENCES Customer (ID)
    );

    CREATE TABLE Category (
        ID INT NOT NULL,
        Name NVARCHAR(80) NOT NULL,
        PRIMARY KEY (ID),
        UNIQUE (Name)
    );

    CREATE TABLE Supplier (
        ID INT NOT NULL,
        Name NVARCHAR(100) NOT NULL,
        PRIMARY KEY (ID)
    );

    CREATE TABLE Product (
        ID INT NOT NULL,
        Name NVARCHAR(100) NOT NULL,
        Price DECIMAL(10,2) NOT NULL,
        IsActive BIT NOT NULL,
        Supplier_ID INT NOT NULL,
        Category_ID INT NOT NULL,
        PRIMARY KEY (ID),
        CHECK (Price > 0),
        FOREIGN KEY (Supplier_ID) REFERENCES Supplier (ID),
        FOREIGN KEY (Category_ID) REFERENCES Category (ID)
    );

    CREATE TABLE Warehouse (
        ID INT NOT NULL,
        Name NVARCHAR(80) NOT NULL,
        IsActive BIT NOT NULL,
        PRIMARY KEY (ID)
    );

    CREATE TABLE Inventory (
        Product_ID INT NOT NULL,
        Warehouse_ID INT NOT NULL,
        Quantity INT NOT NULL,
        MinimumStock INT NOT NULL,
        PRIMARY KEY (Product_ID, Warehouse_ID),
        CHECK (Quantity >= 0),
        CHECK (MinimumStock >= 0),
        FOREIGN KEY (Product_ID) REFERENCES Product (ID),
        FOREIGN KEY (Warehouse_ID) REFERENCES Warehouse (ID)
    );

    CREATE TABLE Orders (
        ID INT NOT NULL,
        OrderDate DATE NOT NULL,
        PaymentDueDate DATE NOT NULL,
        Status NVARCHAR(12) NOT NULL,
        Customer_ID INT NOT NULL,
        Address_ID INT NOT NULL,
        PRIMARY KEY (ID),
        CHECK (Status IN (N'New', N'Paid', N'Shipped', N'Cancelled')),
        CHECK (PaymentDueDate >= OrderDate),
        FOREIGN KEY (Customer_ID) REFERENCES Customer (ID),
        FOREIGN KEY (Customer_ID, Address_ID) REFERENCES CustomerAddress (Customer_ID, ID)
    );

    CREATE TABLE OrderItem (
        Order_ID INT NOT NULL,
        LineNumber INT NOT NULL,
        Quantity INT NOT NULL,
        UnitPrice DECIMAL(10,2) NOT NULL,
        Product_ID INT NOT NULL,
        PRIMARY KEY (Order_ID, LineNumber),
        CHECK (LineNumber > 0),
        CHECK (Quantity > 0),
        CHECK (UnitPrice > 0),
        FOREIGN KEY (Order_ID) REFERENCES Orders (ID),
        FOREIGN KEY (Product_ID) REFERENCES Product (ID)
    );

    CREATE TABLE Payment (
        ID INT NOT NULL,
        PaymentDate DATE NOT NULL,
        Amount DECIMAL(12,2) NOT NULL,
        Status NVARCHAR(15) NOT NULL,
        Order_ID INT NOT NULL,
        PRIMARY KEY (ID),
        CHECK (Amount > 0),
        CHECK (Status IN (N'Posted', N'Rejected', N'Pending')),
        FOREIGN KEY (Order_ID) REFERENCES Orders (ID)
    );

    CREATE TABLE Notification (
        ID INT IDENTITY(1,1) NOT NULL,
        ReminderDate DATE NOT NULL,
        Type NVARCHAR(20) NOT NULL,
        MessageText NVARCHAR(500) NOT NULL,
        Order_ID INT NOT NULL,
        PRIMARY KEY (ID),
        UNIQUE (Order_ID, ReminderDate, Type),
        CHECK (Type = N'Payment'),
        FOREIGN KEY (Order_ID) REFERENCES Orders (ID)
    );

    -- DML

    INSERT INTO City (ID, Name) VALUES
        (1, N'Warsaw'),
        (2, N'Krakow'),
        (3, N'Gdansk'),
        (4, N'Berlin'),
        (5, N'Prague');

    INSERT INTO Customer (ID, City_ID, Name, Email, IsActive) VALUES
        (1, 1, N'Anna Kowalska', N'anna@example.com', 1),
        (2, 3, N'North Office', N'biuro@example.com', 1),
        (3, 2, N'Old Mill Foundation', N'fundacja@example.com', 0),
        (4, 4, N'Berlin Studio', N'pracownia@example.com', 1),
        (5, 1, N'Piotr Lis', NULL, 1),
        (6, 5, N'New Customer', N'nowy@example.com', 1);

    INSERT INTO CustomerAddress (ID, Customer_ID, City_ID, Street, PostalCode) VALUES
        (11, 1, 1, N'Garden Street 10', N'00-001'),
        (12, 1, 2, N'Long Street 5', N'30-001'),
        (21, 2, 3, N'Harbour Street 8', N'80-001'),
        (31, 3, 2, N'School Street 2', N'30-002'),
        (41, 4, 4, N'Main Street 7', N'10115'),
        (51, 5, 1, N'Forest Street 3', N'00-002'),
        (61, 6, 5, N'New Street 4', N'11000');

    INSERT INTO Category (ID, Name) VALUES
        (1, N'Peripherals'),
        (2, N'Monitors'),
        (3, N'Accessories'),
        (4, N'Furniture');

    INSERT INTO Supplier (ID, Name) VALUES
        (1, N'Tech Warsaw'),
        (2, N'Display Berlin'),
        (3, N'Office Gdansk');

    INSERT INTO Product (ID, Category_ID, Supplier_ID, Name, Price, IsActive) VALUES
        (101, 1, 1, N'Keyboard', 120, 1),
        (205, 1, 1, N'Wireless mouse', 80, 1),
        (310, 2, 2, N'Monitor', 900, 1),
        (420, 3, 1, N'Docking station', 250, 1),
        (550, 3, 3, N'Adapter — old model', 50, 0),
        (660, 1, 1, N'Webcam', 200, 1),
        (770, 3, 3, N'USB hub', 75, 1),
        (880, 3, 3, N'USB cable', 20, 1),
        (990, 3, 3, N'Label printer', 350, 1);

    INSERT INTO Warehouse (ID, Name, IsActive) VALUES
        (1, N'Warsaw — main', 1),
        (2, N'Gdansk — north', 1),
        (3, N'Berlin — closed', 0);

    INSERT INTO Inventory (Warehouse_ID, Product_ID, Quantity, MinimumStock) VALUES
        (1, 101, 8, 10),
        (1, 205, 3, 6),
        (1, 310, 2, 3),
        (1, 420, 0, 2),
        (1, 550, 10, 4),
        (1, 660, 4, 4),
        (1, 770, 1, 5),
        (2, 101, 4, 5),
        (2, 205, 5, 5),
        (2, 310, 1, 2),
        (2, 420, 1, 2),
        (2, 660, 0, 2),
        (2, 770, 0, 3),
        (3, 101, 100, 20),
        (3, 310, 20, 5),
        (3, 990, 5, 2);

    INSERT INTO Orders (ID, Customer_ID, Address_ID, OrderDate, PaymentDueDate, Status) VALUES
        (1001, 1, 11, N'20261001', N'20261005', N'New'),
        (1002, 2, 21, N'20261002', N'20261006', N'New'),
        (1003, 1, 12, N'20261003', N'20261007', N'Shipped'),
        (1004, 2, 21, N'20261004', N'20261008', N'Shipped'),
        (1005, 3, 31, N'20261005', N'20261009', N'New'),
        (1006, 2, 21, N'20261006', N'20261010', N'Shipped'),
        (1007, 1, 11, N'20260930', N'20261004', N'Shipped'),
        (1008, 5, 51, N'20261008', N'20261012', N'New'),
        (1009, 1, 11, N'20261009', N'20261013', N'Cancelled'),
        (1010, 2, 21, N'20261010', N'20261020', N'Shipped'),
        (1011, 1, 11, N'20261011', N'20261021', N'New'),
        (1012, 2, 21, N'20261012', N'20261022', N'Paid'),
        (1013, 1, 11, N'20261013', N'20261023', N'Shipped'),
        (1014, 2, 21, N'20261014', N'20261024', N'New'),
        (1015, 2, 21, N'20261014', N'20261025', N'New'),
        (1016, 4, 41, N'20261003', N'20261015', N'New');

    INSERT INTO OrderItem (Order_ID, LineNumber, Product_ID, Quantity, UnitPrice) VALUES
        (1001, 1, 101, 2, 120),
        (1001, 2, 205, 1, 80),
        (1002, 1, 310, 1, 900),
        (1002, 2, 420, 1, 250),
        (1003, 1, 101, 2, 120),
        (1003, 2, 101, 1, 110),
        (1003, 3, 205, 2, 80),
        (1004, 1, 101, 1, 120),
        (1004, 2, 420, 1, 250),
        (1005, 1, 660, 1, 200),
        (1006, 1, 310, 1, 850),
        (1007, 1, 101, 1, 110),
        (1008, 1, 205, 1, 80),
        (1009, 1, 550, 2, 50),
        (1010, 1, 420, 2, 250),
        (1012, 1, 550, 1, 50),
        (1013, 1, 880, 3, 20),
        (1013, 2, 770, 1, 75),
        (1014, 1, 101, 8, 120),
        (1014, 2, 101, 7, 120),
        (1014, 3, 420, 2, 250),
        (1015, 1, 660, 5, 200),
        (1016, 1, 770, 2, 75);

    INSERT INTO Payment (ID, Order_ID, PaymentDate, Amount, Status) VALUES
        (1, 1001, N'20261002', 100, N'Posted'),
        (2, 1002, N'20261003', 1150, N'Rejected'),
        (3, 1003, N'20261003', 300, N'Posted'),
        (4, 1003, N'20261004', 210, N'Posted'),
        (5, 1004, N'20261005', 400, N'Posted'),
        (6, 1006, N'20261006', 850, N'Posted'),
        (7, 1007, N'20261001', 110, N'Posted'),
        (8, 1010, N'20261011', 100, N'Posted'),
        (9, 1012, N'20261012', 50, N'Posted'),
        (10, 1013, N'20261014', 44.99, N'Posted'),
        (11, 1015, N'20261014', 1000, N'Pending');

    INSERT INTO Notification (Order_ID, ReminderDate, Type, MessageText) VALUES
        (1001, N'20261015', N'Payment', N'Reminder present before the exercises start.');

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

-- Check: row counts

SELECT N'City' AS TableName, COUNT(*) AS RecordCount FROM City
UNION ALL SELECT N'Customer', COUNT(*) FROM Customer
UNION ALL SELECT N'CustomerAddress', COUNT(*) FROM CustomerAddress
UNION ALL SELECT N'Category', COUNT(*) FROM Category
UNION ALL SELECT N'Supplier', COUNT(*) FROM Supplier
UNION ALL SELECT N'Product', COUNT(*) FROM Product
UNION ALL SELECT N'Warehouse', COUNT(*) FROM Warehouse
UNION ALL SELECT N'Inventory', COUNT(*) FROM Inventory
UNION ALL SELECT N'Orders', COUNT(*) FROM Orders
UNION ALL SELECT N'OrderItem', COUNT(*) FROM OrderItem
UNION ALL SELECT N'Payment', COUNT(*) FROM Payment
UNION ALL SELECT N'Notification', COUNT(*) FROM Notification;

-- Expected: City=5, Customer=6, CustomerAddress=7, Category=4, Supplier=3, Product=9, Warehouse=3, Inventory=16, Orders=16, OrderItem=23, Payment=11, Notification=1.
