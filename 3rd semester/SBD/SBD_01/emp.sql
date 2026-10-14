-- =====================================================
--  EMPLOYEES DATABASE - practice data (MySQL 8)
--  Paste everything into the "Schema SQL" panel.
-- =====================================================

DROP TABLE IF EXISTS Emp_Proj;
DROP TABLE IF EXISTS Emp;
DROP TABLE IF EXISTS Proj;
DROP TABLE IF EXISTS Dept;
DROP TABLE IF EXISTS Salgrade;

-- -----------------------------------------------------
--  TABLES
-- -----------------------------------------------------

CREATE TABLE Dept (
    deptno  INT          PRIMARY KEY,
    dname   VARCHAR(20)  NOT NULL,
    loc     VARCHAR(20)  NOT NULL
);

CREATE TABLE Emp (
    empno     INT            PRIMARY KEY,
    ename     VARCHAR(20)    NOT NULL,
    job       VARCHAR(20)    NOT NULL,
    mgr       INT            NULL,        -- the employee's boss (another empno)
    hiredate  DATE           NOT NULL,
    sal       DECIMAL(10,2)  NOT NULL,
    comm      DECIMAL(10,2)  NULL,        -- commission, can be NULL
    deptno    INT            NULL,
    FOREIGN KEY (mgr)    REFERENCES Emp(empno),
    FOREIGN KEY (deptno) REFERENCES Dept(deptno)
);

CREATE TABLE Salgrade (
    grade  INT            PRIMARY KEY,
    losal  DECIMAL(10,2)  NOT NULL,       -- lowest salary in this group
    hisal  DECIMAL(10,2)  NOT NULL        -- highest salary in this group
);

CREATE TABLE Proj (
    projno     INT          PRIMARY KEY,
    pname      VARCHAR(20)  NOT NULL,
    startdate  DATE         NOT NULL,
    enddate    DATE         NULL          -- NULL = project still running
);

CREATE TABLE Emp_Proj (
    empno   INT,
    projno  INT,
    PRIMARY KEY (empno, projno),
    FOREIGN KEY (empno)  REFERENCES Emp(empno),
    FOREIGN KEY (projno) REFERENCES Proj(projno)
);

-- -----------------------------------------------------
--  DATA
-- -----------------------------------------------------

INSERT INTO Dept (deptno, dname, loc) VALUES
(10, 'ACCOUNTING', 'NEW YORK'),
(20, 'RESEARCH',   'DALLAS'),
(30, 'SALES',      'CHICAGO'),
(40, 'OPERATIONS', 'BOSTON');      -- department with no employees

-- Bosses are inserted before their employees (because of the mgr foreign key)
INSERT INTO Emp (empno, ename, job, mgr, hiredate, sal, comm, deptno) VALUES
(7839, 'KING',   'PRESIDENT', NULL, '1981-11-17', 5000, NULL, 10),
(7566, 'JONES',  'MANAGER',   7839, '1981-04-02', 2975, NULL, 20),
(7698, 'BLAKE',  'MANAGER',   7839, '1981-05-01', 2850, NULL, 30),
(7782, 'CLARK',  'MANAGER',   7839, '1981-06-09', 2450, NULL, 10),
(7788, 'SCOTT',  'ANALYST',   7566, '1987-04-19', 3000, NULL, 20),
(7902, 'FORD',   'ANALYST',   7566, '1981-12-03', 3000, NULL, 20),
(7369, 'SMITH',  'CLERK',     7902, '1980-12-17',  800, NULL, 20),
(7499, 'ALLEN',  'SALESMAN',  7698, '1981-02-20', 1600,  300, 30),
(7521, 'WARD',   'SALESMAN',  7698, '1981-02-22', 1250,  500, 30),
(7654, 'MARTIN', 'SALESMAN',  7698, '1981-09-28', 1250, 1400, 30),
(7844, 'TURNER', 'SALESMAN',  7698, '1981-09-08', 1500,    0, 30),
(7876, 'ADAMS',  'CLERK',     7788, '1987-05-23', 1100, NULL, 20),
(7900, 'JAMES',  'CLERK',     7698, '1981-12-03',  950, NULL, 30),
(7934, 'MILLER', 'CLERK',     7782, '1982-01-23', 1300, NULL, 10),
-- two newer employees (so "working more than 10 years" filters something)
(7950, 'NOVAK',  'CLERK',     7782, '2021-06-14', 1250, NULL, 10),
(7960, 'LEE',    'SALESMAN',  7698, '2019-09-02', 1400,  300, 30);

INSERT INTO Salgrade (grade, losal, hisal) VALUES
(1,  700, 1200),
(2, 1201, 1400),
(3, 1401, 2000),
(4, 2001, 3000),
(5, 3001, 9999);

INSERT INTO Proj (projno, pname, startdate, enddate) VALUES
(1, 'APOLLO',  '2005-03-01', '2007-06-30'),
(2, 'ORION',   '2018-01-15', '2019-12-31'),
(3, 'HERMES',  '2021-04-01', '2022-10-15'),
(4, 'ATLAS',   '2022-09-01', NULL),          -- still running
(5, 'ZEUS',    '2005-11-20', '2006-02-28'),
(6, 'PEGASUS', '2023-02-01', '2024-08-31'),  -- no employees
(7, 'IRIS',    '2024-05-10', NULL);          -- no employees, still running

-- KING and LEE are not in any project
INSERT INTO Emp_Proj (empno, projno) VALUES
(7369, 1), (7499, 1), (7566, 1),
(7788, 2), (7902, 2), (7844, 2),
(7499, 3), (7521, 3), (7654, 3),
(7698, 4), (7876, 4), (7900, 4), (7950, 4),
(7782, 5), (7934, 5);
