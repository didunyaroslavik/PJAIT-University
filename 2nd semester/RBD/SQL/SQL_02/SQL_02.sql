-- Example tasks:

-- 1.
SELECT AVG(sal) AS "Average Wage"
FROM Emp;

-- 2.
SELECT deptno, job, AVG(sal)
FROM Emp
GROUP BY deptno, job;

-- 3.
SELECT deptno, AVG(sal)
FROM Emp
GROUP BY deptno
HAVING COUNT(empno) > 3;

-- 4.
SELECT mgr, MIN(sal)
FROM Emp
WHERE mgr IS NOT NULL
GROUP BY mgr
HAVING MIN(sal) >= 1000
ORDER BY MIN(sal) DESC;





-- Individual tasks:

-- 1.
SELECT MIN(sal)
FROM Emp
WHERE job = 'CLERK';

-- 2.
SELECT COUNT(empno)
FROM Emp
WHERE deptno = 20;

-- 3.
SELECT job, AVG(sal)
FROM Emp
GROUP BY job;

-- 4.
SELECT job, AVG(sal)
FROM Emp
WHERE job != 'MANAGER'
GROUP BY job;

-- 5.
SELECT MAX(sal)
FROM Emp
GROUP BY job;

-- 6.
SELECT job, AVG(sal)
FROM Emp
GROUP BY job
HAVING AVG(sal) >= 3000;

-- 7.
SELECT job,
  AVG(sal + NVL(comm, 0)) AS monthly_salary,
  AVG(sal+ NVL(comm, 0)) * 12 AS yearly_salary
FROM Emp
GROUP BY job;

-- 8.
SELECT d.dname AS "Department name", COUNT(e.empno) AS "Number of employees"
FROM Emp e
JOIN Dept d ON d.deptno = e.deptno
GROUP BY d.dname
HAVING COUNT(e.empno) > 3;

-- 9.
SELECT empno, COUNT(*)
FROM Emp
GROUP BY empno
HAVING COUNT(*) > 1;

-- 10.
SELECT d.loc, d.dname, COUNT(e.empno) AS "Number of employees"
FROM Emp e
JOIN Dept d ON d.deptno = e.deptno
WHERE d.loc = 'DALLAS'
GROUP BY d.loc, d.dname;

-- 11.
SELECT s.grade, MAX(e.sal)
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN losal AND hisal
GROUP BY s.grade;

-- 12.
SELECT sal, COUNT(empno) AS "Number of workers"
FROM Emp
GROUP BY sal
HAVING COUNT(empno) > 1;

-- 13.
SELECT s.grade AS "Salary group", AVG(e.sal) AS "Average salary"
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN s.losal AND s.hisal
WHERE s.grade = 2
GROUP BY s.grade;

-- 14.
SELECT m.empno AS "Manager ID", m.ename AS "Manager Name", COUNT(e.empno) AS "Number of subordinates"
FROM Emp e
JOIN Emp m ON e.mgr = m.empno
GROUP BY m.empno, m.ename;

-- 15.
SELECT s.grade AS "Salary group", SUM(e.sal) AS "Total salary"
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN s.losal AND s.hisal
WHERE s.grade = 1
GROUP BY s.grade;





-- Additional tasks:

-- 1.
SELECT AVG(enddate - startdate) AS "AVERAGE TIME"
FROM Proj;

-- 2.
SELECT s.grade, MAX(sal)
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN s.losal AND s.hisal
WHERE (SYSDATE - e.hiredate) / 365 > 10
GROUP BY s.grade;

-- 3.
SELECT d.deptno, d.dname, AVG(e.sal) AS "Average Earnings"
FROM Dept d
LEFT JOIN Emp e ON e.deptno = d.deptno
GROUP BY d.deptno, d.dname
HAVING NVL(AVG(e.sal), 0) < 2000;

-- 4.
SELECT p.projno, p.pname, COUNT(e.empno) AS "Employees involved"
FROM Emp e
JOIN Dept d ON e.deptno = d.deptno
JOIN Emp_Proj ep ON e.empno = ep.empno
JOIN Proj p ON p.projno = ep.projno
WHERE d.dname = 'SALES' OR d.dname = 'RESEARCH'
GROUP BY p.projno, p.pname;

-- 5.
SELECT m.empno AS "Manager ID", m.ename AS "Manager Name", AVG(e.sal) "Average salary of subordinates", COUNT(e.empno) AS "Number of subordinates"
FROM Emp e
JOIN Emp m ON m.empno = e.mgr
GROUP BY m.empno, m.ename
ORDER BY COUNT(e.empno) ASC, m.ename DESC;