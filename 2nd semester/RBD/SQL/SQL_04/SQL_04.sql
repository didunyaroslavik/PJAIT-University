-- Example tasks: 

-- 1.
SELECT e1.empno, e1.ename, e1.sal, e1.deptno
FROM Emp e1
WHERE e1.sal = (
  SELECT MAX(e2.sal)
  FROM Emp e2
  WHERE e1.deptno = e2.deptno
);

-- 2.
SELECT e1.empno, e1.ename
FROM Emp e1
WHERE EXISTS (
  SELECT 1
  FROM Emp e2
  WHERE e2.mgr = e1.empno
);

-- 3.
SELECT e1.empno, e1.ename, e1.sal, s1.grade
FROM Emp e1
JOIN Salgrade s1 ON e1.sal BETWEEN s1.losal AND s1.hisal
WHERE e1.sal = (
  SELECT MIN(e2.sal)
  FROM Emp e2
  JOIN Salgrade s2 ON e2.sal BETWEEN s2.losal AND s2.hisal
  WHERE s1.grade = s2.grade
)
ORDER BY s1.grade DESC;

-- 4.
SELECT e1.ename, e1.sal
FROM Emp e1
WHERE 3 > (
  SELECT COUNT(*)
  FROM Emp e2
  WHERE e2.sal > e1.sal
)
ORDER BY e1.sal DESC;





-- Individual tasks:

-- 1.
SELECT e1.empno, e1.ename, e1.sal
FROM Emp e1
WHERE e1.sal > (
  SELECT AVG(e2.sal)
  FROM Emp e2
  WHERE e1.deptno = e2.deptno
);

-- 2.
SELECT e1.empno, e1.ename, e1.sal, e1.deptno
FROM Emp e1
WHERE e1.sal = (
  SELECT MIN(e2.sal)
  FROM Emp e2
  WHERE e1.deptno = e2.deptno
);

-- 3.
SELECT e.empno, e.ename, e.deptno
FROM Emp e
WHERE NOT EXISTS (
  SELECT 1
  FROM Dept d
  WHERE d.deptno = e.deptno
);

-- 4.
SELECT d.dname, d.loc
FROM Dept d
WHERE NOT EXISTS (
  SELECT 1
  FROM Emp e
  WHERE e.deptno = d.deptno
);

-- 5.
SELECT e1.empno, e1.ename, e1.sal, e1.job
FROM Emp e1
WHERE e1.sal = (
  SELECT MAX(e2.sal)
  FROM Emp e2
  WHERE e1.job = e2.job
)
ORDER BY e1.sal DESC;

-- 6.
SELECT e1.empno, e1.ename, e1.hiredate, e1.deptno
FROM Emp e1
WHERE e1.hiredate = (
  SELECT MAX(e2.hiredate)
  FROM Emp e2
  WHERE e1.deptno = e2.deptno
)
ORDER BY e1.hiredate;

-- 7.
SELECT e1.ename, e1.sal, d1.dname
FROM Emp e1
JOIN Dept d1 ON e1.deptno = d1.deptno
JOIN Salgrade s1 ON e1.sal BETWEEN s1.losal AND s1.hisal
WHERE e1.sal > (
  SELECT AVG(e2.sal)
  FROM Emp e2
  JOIN Salgrade s2 ON e2.sal BETWEEN s2.losal AND s2.hisal
  WHERE s1.grade = s2.grade 
);

-- 8.
SELECT e.empno, e.ename, e.deptno
FROM Emp e
WHERE NOT EXISTS(
  SELECT 1
  FROM Dept d
  WHERE d.deptno = e.deptno
);





-- Additional tasks:

-- 1.
SELECT boss.ename AS "Boss Name", sub.ename AS "Subordinate Name", sub.sal
FROM Emp sub
JOIN Emp boss ON sub.mgr = boss.empno
WHERE sub.sal = (
  SELECT MIN(sub_search.sal)
  FROM Emp sub_search
  WHERE sub_search.mgr = boss.empno
);

-- 2.
SELECT p.projno, p.pname
FROM Proj p
WHERE NOT EXISTS (
  SELECT 1
  FROM Emp_Proj ep
  WHERE ep.projno = p.projno
);

-- 3.
SELECT e1.*, s1.grade
FROM Emp e1
JOIN Salgrade s1 ON e1.sal BETWEEN s1.losal AND s1.hisal
WHERE e1.sal > (
  SELECT AVG(e2.sal)
  FROM Emp e2
  JOIN Salgrade s2 ON e2.sal BETWEEN s2.losal AND s2.hisal
  WHERE s1.grade = s2.grade
)
ORDER BY s1.grade DESC;

-- 4.
SELECT e1.empno, e1.ename, e1.comm
FROM Emp e1
WHERE e1.comm IS NOT NULL AND 3 > (
  SELECT COUNT(*)
  FROM Emp e2
  WHERE e2.comm > e1.comm
);

-- 5.
SELECT p.pname, e1.ename, e1.sal
FROM Emp e1
JOIN Emp_Proj ep1 ON e1.empno = ep1.empno
JOIN Proj p ON p.projno = ep1.projno
WHERE e1.sal > (
  SELECT AVG(e2.sal)
  FROM Emp e2
  JOIN Emp_Proj ep2 ON e2.empno = ep2.empno
  WHERE ep1.projno = ep2.projno
);