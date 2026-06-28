-- Example tasks:

-- 1.
INSERT INTO Emp (empno, ename, deptno, sal, comm, hiredate, job, mgr)
VALUES (9999, 'DIDUN', 10, 2000, 100, SYSDATE, 'INTERN', NULL);

-- 2.
UPDATE Emp
SET sal = 3000
WHERE ename = 'DIDUN';

-- 3.
DELETE FROM Emp
WHERE ename = 'DIDUN';





-- Individual tasks:

-- 1.
INSERT INTO Emp (empno, ename, sal, comm, hiredate, job, deptno, mgr)
VALUES (9001, 'ANDRII', 3000, 10, SYSDATE, 'STUDENT', 30, NULL);

INSERT INTO Emp (empno, ename, sal, comm, hiredate, job, deptno, mgr)
VALUES (9002, 'NIKITA', 4000, 30, SYSDATE, 'STUDENT', 20, NULL);

INSERT INTO Emp (empno, ename, sal, comm, hiredate, job, deptno, mgr)
VALUES (9003, 'ARESNII', 5000, 20, SYSDATE, 'STUDENT', 30, NULL);

-- 2.
INSERT INTO Emp (empno, ename, sal, job, hiredate, deptno, mgr)
VALUES(
  9004,
  'ANTONY',
  2700,
  'SALESMAN',
  SYSDATE,
  (SELECT deptno FROM dept WHERE dname = 'SALES'),
  (SELECT empno FROM Emp WHERE ename = 'KING')
);

-- 3.
UPDATE Emp
SET sal = (
  SELECT MIN(sal)
  FROM Emp
  WHERE deptno = 20
)
WHERE empno = 9004;

-- 4.
UPDATE Emp
SET comm = NVL(comm, 0) + 100
WHERE deptno = 10;

-- 5.
UPDATE Emp
SET comm = 1000
WHERE sal < (
  SELECT MAX(sal)
  FROM Emp
  WHERE job = 'CLERK'
);

-- 6.
UPDATE Emp e1
SET sal = (
  SELECT MAX(e2.sal)
  FROM Emp e2
  JOIN Salgrade s ON e2.sal BETWEEN s.losal AND s.hisal
  WHERE e1.sal BETWEEN s.losal AND s.hisal
)
WHERE empno = 9004;

-- 7.
UPDATE Emp e1
SET sal = (
  SELECT MAX(e2.sal)
  FROM Emp e2
  WHERE e1.job = e2.job
)
WHERE Empno = 9004;

-- 8.
DELETE FROM Emp
WHERE sal < (
  SELECT MIN(sal)
  FROM Emp
  WHERE job = 'CLERK'
);

-- 9.
DELETE FROM Emp
WHERE EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM hiredate) < 30;

-- 10.
DELETE FROM Emp
WHERE comm > sal OR deptno = (
  SELECT deptno
  FROM Dept
  WHERE dname = 'OPERATIONS'
);





-- Additional tasks:

-- 1.
INSERT INTO Proj (projno, pname, startdate)
VALUES(
  99,
  'PROJECT X',
  SYSDATE
);

INSERT INTO Emp_Proj (empno, projno)
SELECT empno, 99
FROM Emp;
WHERE ename LIKE 'S%';

-- 2.
UPDATE Proj
SET enddate = startdate + INTERVAL '1 year'
WHERE enddate IS NULL;

-- 3.
UPDATE Emp
SET deptno = (
    SELECT d.deptno
    FROM Dept d
    LEFT JOIN Emp e ON d.deptno = e.deptno
    GROUP BY d.deptno
    ORDER BY COUNT(e.empno) ASC
    LIMIT 1
)
WHERE empno = 7369;

-- 4.
DELETE FROM Proj
WHERE projno NOT IN (
  SELECT projno
  FROM Emp_Proj
);

-- 5.
DELETE FROM Emp e1
WHERE sal < (
  SELECT AVG(e2.sal)
  FROM Emp e2
  WHERE e1.job = e2.job
);