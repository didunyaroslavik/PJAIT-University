-- Example tasks:

-- 1.
SELECT empno, ename, sal
FROM Emp
WHERE sal = (
  SELECT MIN(sal)
  FROM Emp
);

-- 2.
SELECT empno, ename, sal, deptno
FROM Emp
WHERE (deptno, sal) IN (
  SELECT deptno, MIN(sal)
  FROM Emp
  GROUP BY deptno
);

-- 3.
SELECT empno, ename, sal, deptno
FROM Emp
WHERE (deptno, sal) IN (
  SELECT deptno, MIN(sal)
  FROM Emp
  GROUP BY deptno
);

-- 4.
SELECT empno, ename, sal, deptno
FROM Emp
WHERE sal > ANY (
  SELECT sal
  FROM Emp
  WHERE deptno = 30
);

-- 5.
SELECT job, AVG(sal) AS "Average Earnings"
FROM Emp
GROUP BY job
HAVING AVG(sal) = (
  SELECT MAX(AVG(sal))
  FROM Emp
  GROUP BY job
);

-- 6.
SELECT job
FROM Emp
WHERE deptno = 10
INTERSECT
SELECT job
FROM Emp
WHERE deptno = 30;





-- Individual tasks:

-- 1.
SELECT empno, ename, job
FROM Emp
WHERE job = (
  SELECT job
  FROM Emp
  WHERE ename = 'BLAKE'
)
AND ename != 'BLAKE';

-- 2.
SELECT empno, ename, sal
FROM Emp
WHERE sal > ALL(
  SELECT sal
  FROM Emp
  WHERE deptno = 30
);

-- 3.
SELECT d.deptno, d.dname
FROM Emp e
JOIN Dept d ON d.deptno = e.deptno
GROUP BY d.deptno, d.dname
HAVING AVG(e.sal) > (
  SELECT AVG(sal)
  FROM Emp
  WHERE deptno = 30
);

-- 4.
SELECT empno, ename, sal
FROM Emp
WHERE sal > (
  SELECT MAX(e.sal)
  FROM Emp e
  JOIN Dept d ON e.deptno = d.deptno
  WHERE d.dname = 'SALES'
);

-- 5.
SELECT empno, ename, job, sal
FROM Emp
WHERE job = (
  SELECT job
  FROM Emp
  WHERE empno = 7369
) AND sal > (
  SELECT sal
  FROM Emp
  WHERE empno = 7876
);

-- 6.
SELECT dname
FROM Dept
WHERE deptno IN (
  SELECT deptno
  FROM Emp
  WHERE job = 'CLERK'
);

-- 7.
SELECT job
FROM Emp
WHERE deptno = 10
MINUS
SELECT job
FROM Emp
WHERE deptno = 30;

-- 8.
SELECT job
FROM Emp
WHERE deptno = 10
UNION
SELECT job
FROM Emp
WHERE deptno = 30;





-- Additional tasks:

-- 1.
SELECT e.empno, e.ename, (SYSDATE - e.hiredate) / 365 AS "Years Worked"
FROM Emp e
JOIN Emp_Proj ep ON ep.empno = e.empno
JOIN Proj p ON p.projno = ep.projno
WHERE p.pname = 'APOLLO XY'
INTERSECT
SELECT e.empno, e.ename, (SYSDATE - e.hiredate) / 365 AS "Years Worked"
FROM Emp e
JOIN Emp_Proj ep ON ep.empno = e.empno
JOIN Proj p ON p.projno = ep.projno
WHERE p.pname = 'ZEUS 13';

-- 2.
SELECT ename, hiredate, '*' AS MAXDATE
FROM Emp
WHERE hiredate = (
    SELECT MAX(hiredate) 
    FROM Emp
)
UNION
SELECT ename, hiredate, ' '
FROM Emp
WHERE hiredate < (
    SELECT MAX(hiredate) 
    FROM Emp
);

-- 3.
SELECT projno || ' - ' || pname || ' - ' || startdate ||  ' - '  || enddate AS "PROJECT"
From Proj
WHERE NVL(enddate, SYSDATE) - startdate = (
  SELECT MAX(NVL(enddate, SYSDATE) - startdate)
  FROM Proj
);

-- 4.
SELECT d.deptno, d.dname, COUNT(e.empno) AS "Number of employees"
FROM Emp e
JOIN Dept d ON d.deptno = e.deptno
WHERE e.sal BETWEEN 1000 AND 3000 AND NVL(e.comm, 0) != 0
GROUP BY d.deptno, d.dname 
HAVING COUNT(e.empno) = (
  SELECT MIN(COUNT(empno))
  FROM Emp
  WHERE sal BETWEEN 1000 AND 3000 AND NVL(comm, 0) != 0
  GROUP BY deptno
);

-- 5.
SELECT job, MIN(sal)
FROM Emp
WHERE LENGTH(job) BETWEEN 5 AND 7
GROUP BY job
HAVING MIN(sal) > 1000;
