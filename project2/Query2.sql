    -- GOALS: (A) Find the IDs, first names, and last names of users without any friends
    -- Be careful! Remember that if two users are friends, the Friends table only contains
    -- the one entry (U1, U2) where U1 < U2.


SELECT DISTINCT u.User_Id, u.First_Name, u.Last_Name 
FROM Users u 
WHERE NOT EXISTS(
    SELECT 1
    FROM Friends f
    WHERE f.User1_ID = u.User_id OR f.User2_ID = u.User_Id
    )
ORDER BY u.user_id;