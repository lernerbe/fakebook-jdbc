-- CREATE VIEW Max_First_Name AS
SELECT DISTINCT First_Name
FROM Users
WHERE LENGTH(First_Name) = (SELECT MAX(LENGTH(First_Name)) FROM Users);

-- SELECT First_Name
-- FROM Max_First_Name
-- ORDER BY First_Name;

SELECT DISTINCT First_Name
FROM Users
WHERE LENGTH(First_Name) = (SELECT MIN(LENGTH(First_Name)) FROM Users)
ORDER BY First_Name;

CREATE VIEW Name_Counts AS
SELECT First_Name,
COUNT(*) AS Name_Count
FROM Users
GROUP BY First_Name;

SELECT First_Name, Name_Count
FROM Name_Counts
WHERE Name_Count = (SELECT MAX(Name_Count) FROM Name_Counts)
ORDER BY First_Name;
