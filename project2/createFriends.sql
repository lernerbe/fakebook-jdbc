DROP TABLE Users CASCADE CONTRAINTS;
DROP TABLE Friends CASCADE CONSTRAINTS;

CREATE TABLE Users (
  User_ID      NUMBER PRIMARY KEY,
  First_Name   VARCHAR2(50),
  Last_Name    VARCHAR2(50)
  -- possibly other columns: Month_of_Birth, Year_of_Birth, etc.
);

CREATE TABLE Friends (
  User1_ID NUMBER,
  User2_ID NUMBER,
  CONSTRAINT Friends_PK PRIMARY KEY(User1_ID, User2_ID),
  CONSTRAINT FK_User1 FOREIGN KEY(User1_ID) REFERENCES Users(User_ID),
  CONSTRAINT FK_User2 FOREIGN KEY(User2_ID) REFERENCES Users(User_ID)
  -- The rule that User1_ID < User2_ID typically enforced by the code 
  -- that inserts or by a check constraint.
);


-- If desired, clear existing rows first:
-- TRUNCATE TABLE Friends;
-- TRUNCATE TABLE Users;

INSERT INTO Users (User_ID, First_Name, Last_Name)
VALUES (101, 'Alice', 'Aaa');

INSERT INTO Users (User_ID, First_Name, Last_Name)
VALUES (102, 'Bob',   'Bbb');

INSERT INTO Users (User_ID, First_Name, Last_Name)
VALUES (103, 'Carol', 'Ccc');

INSERT INTO Users (User_ID, First_Name, Last_Name)
VALUES (104, 'Dave',  'Ddd');

INSERT INTO Users (User_ID, First_Name, Last_Name)
VALUES (105, 'Eve',   'Eee');

INSERT INTO Users (User_ID, First_Name, Last_Name)
VALUES (106, 'Frank', 'Fff');

INSERT INTO Users (User_ID, First_Name, Last_Name)
VALUES (107, 'Grace', 'Ggg');

INSERT INTO Users (User_ID, First_Name, Last_Name)
VALUES (108, 'Heidi', 'Hhh');


INSERT INTO Friends (User1_ID, User2_ID)
VALUES (101, 102);

INSERT INTO Friends (User1_ID, User2_ID)
VALUES (102, 104);

INSERT INTO Friends (User1_ID, User2_ID)
VALUES (106, 107);

