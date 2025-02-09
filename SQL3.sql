--  // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
--  // in their hometown (i.e. their current city and their hometown are different)

SELECT DISTINCT u.User_Id, u.First_Name, u.Last_Name
FROM Users u
JOIN HometownCitiesTable H
ON U.User_ID = H.User_ID
JOIN CurrentCitiesTable  C 
ON U.User_ID = C.User_ID
WHERE H.Hometown_City_ID <> C.Current_City_ID;


