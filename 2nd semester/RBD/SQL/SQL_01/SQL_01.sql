-- Example tasks:

-- 1.
SELECT ename, empno, job, sal, deptno FROM Emp
WHERE job = 'CLERK';

-- 2.
SELECT ename, empno FROM Emp
WHERE mgr IN (7902, 7566, 7788);

-- 3.
SELECT ename FROM Emp
WHERE LENGTH(ename) = 4;

-- 4.
SELECT * FROM Emp
WHERE mgr IS NOT NULL;

-- 5.
SELECT ename, sal * 12 AS annual_sal, comm
FROM Emp
WHERE job = 'SALESMAN'
  AND sal > NVL(comm, 0)
ORDER BY sal DESC, ename ASC;

-- 6.
SELECT e.ename, d.dname
FROM Emp e
JOIN Dept d ON e.deptno = d.deptno
ORDER BY e.ename ASC;

-- 7.
SELECT e.*
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN s.losal AND s.hisal
WHERE s.grade = 3;

-- 8.
SELECT *
FROM Emp
WHERE EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM hiredate) > 5;

-- 9.
SELECT p.pname, e.ename || ' - ' || e.job AS employee_details
FROM Proj p
JOIN Emp_Proj ep ON p.projno = ep.projno
JOIN Emp e ON ep.empno = e.empno;





-- Individual tasks:

-- 1.
SELECT ename
FROM Emp
WHERE comm > sal;

-- 2.
SELECT *
FROM Emp
WHERE sal < 1000 or sal > 2000;

-- 3.
SELECT *
FROM Emp
WHERE job = 'CLERK' OR (sal >= 1000 AND sal <= 2000);

-- 4.
SELECT *
FROM Emp
WHERE job = 'MANAGER' or (job = 'SALESMAN' AND sal > 1500);

-- 5.
SELECT *
FROM Salgrade;

-- 6.
SELECT *
FROM Emp
WHERE job = 'CLERK' AND (sal < 1000 OR sal > 2000);

-- 7.
SELECT DISTINCT job
FROM Emp;

-- 8.
SELECT ename, job, deptno
FROM Emp
WHERE deptno = 20 AND job = 'CLERK';

-- 9.
SELECT ename, job, sal
FROM Emp
WHERE mgr IS NOT NULL;

-- 10.
SELECT e.ename, d.loc, d.dname
FROM Emp e
JOIN Dept d ON d.deptno = e.deptno
WHERE e.sal > 1500;

-- 11.
SELECT e.ename, d.deptno, d.dname, d.loc
FROM Emp e
RIGHT JOIN Dept d ON e.deptno = d.deptno
WHERE d.deptno IN (30, 40);

-- 12.
SELECT e.ename, d.dname
FROM Emp e
FULL OUTER JOIN Dept d ON e.deptno = d.deptno;





-- Additional tasks:

-- 1.
SELECT e.ename, d.dname
FROM Emp e
JOIN Dept d ON d.deptno = e.deptno
WHERE (e.sal < 2000 OR e.sal > 3000) AND e.ename NOT LIKE 'K%';

-- 2.
SELECT p.pname || ' - ' || e.ename AS project_employee 
FROM Emp e
LEFT JOIN Emp_Proj ep ON e.empno = ep.empno
LEFT JOIN Proj p ON ep.projno = p.projno;

-- 3.
SELECT pname, enddate - startdate AS LENGTH
FROM Proj
WHERE EXTRACT(YEAR FROM startdate) > 2020;

-- 4.
SELECT e.ename AS employee, s.grade AS income_group, b.ename AS boss
FROM Emp e
JOIN Salgrade s ON e.sal BETWEEN losal AND hisal
JOIN Emp b ON e.mgr = b.empno;

-- 5.
SELECT pname AS NAME, startdate AS "START DATE", enddate AS "END DATE"
FROM Proj
ORDER BY startdate ASC, pname DESC;
