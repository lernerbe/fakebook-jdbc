-- Optional: Drop the table first if it already exists
DROP TABLE Users CASCADE CONSTRAINTS;

-- Create the Users table
CREATE TABLE Users (
    user_id     NUMBER PRIMARY KEY,
    first_name  VARCHAR2(50),
    last_name   VARCHAR2(50)
);

-- Insert sample data
INSERT INTO Users (user_id, first_name, last_name) VALUES (1,  'Alice',      'Johnson');
INSERT INTO Users (user_id, first_name, last_name) VALUES (2,  'Bob',        'Adams');
INSERT INTO Users (user_id, first_name, last_name) VALUES (3,  'Cleopatra',  'Stone');
INSERT INTO Users (user_id, first_name, last_name) VALUES (4,  'Alice',      'Brown');
INSERT INTO Users (user_id, first_name, last_name) VALUES (5,  'John',       'Miller');
INSERT INTO Users (user_id, first_name, last_name) VALUES (6,  'Oz',         'Taylor');
INSERT INTO Users (user_id, first_name, last_name) VALUES (7,  'Maximilian', 'Green');
INSERT INTO Users (user_id, first_name, last_name) VALUES (8,  'Alexandria', 'Young');
INSERT INTO Users (user_id, first_name, last_name) VALUES (9,  'Bob',        'White');
INSERT INTO Users (user_id, first_name, last_name) VALUES (10, 'Bo',         'Black');
INSERT INTO Users (user_id, first_name, last_name) VALUES (11, 'Alice',      'Jones');
INSERT INTO Users (user_id, first_name, last_name) VALUES (12, 'Ann',        'Smith');
INSERT INTO Users (user_id, first_name, last_name) VALUES (13, 'Bob',        'Allen');

-- Commit the transaction
COMMIT;

-- Optional: verify data insertion
SELECT * FROM Users;
