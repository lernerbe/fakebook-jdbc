    -- // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    -- //            <num> photos with the most tagged users
    -- //        (B) For each photo identified in (A), find the IDs, first names, and last names
    -- //            of the users therein tagged
SET LINESIZE 200;
SET PAGESIZE 50;
COLUMN PHOTO_LINK FORMAT A30;
COLUMN ALBUM_NAME FORMAT A20;
COLUMN FIRST_NAME FORMAT A15;
COLUMN LAST_NAME FORMAT A15;


VARIABLE num NUMBER;
EXEC :num := 3;


CREATE OR REPLACE VIEW Photo_Tag_Counts AS
SELECT p.photo_id,
       p.album_id,
       p.photo_link,
       a.album_name,
       COUNT(t.tag_subject_id) AS TagCount
  FROM Photos p
  JOIN Albums a 
    ON p.album_id = a.album_id
  JOIN Tags t 
    ON p.photo_id = t.tag_photo_id
 GROUP BY p.photo_id, p.album_id, p.photo_link, a.album_name;

CREATE OR REPLACE VIEW Photo_Tag_Counts_Ordered AS
SELECT ptc.*,
       ROW_NUMBER() OVER (
         ORDER BY ptc.TagCount DESC, ptc.photo_id ASC
       ) AS rn
  FROM Photo_Tag_Counts ptc;

CREATE OR REPLACE VIEW Top_n_Tagged_Photos AS
SELECT o.photo_id,
       o.album_id,
       o.photo_link,
       o.album_name,
       t.tag_subject_id   AS taggeduser_id,
       u.first_name       AS taggeduser_firstname,
       u.last_name        AS taggeduser_lastname,
       o.TagCount
  FROM Photo_Tag_Counts_Ordered o
  JOIN Tags  t ON o.photo_id = t.tag_photo_id
  JOIN Users u ON t.tag_subject_id = u.user_id
 WHERE o.rn <= 3   -- <-- "Top 3" by TagCount
 ORDER BY o.TagCount DESC, o.photo_id ASC, taggeduser_id ASC;


VARIABLE num NUMBER;
EXEC :num := 3;

SELECT o.photo_id,
       o.album_id,
       o.photo_link,
       o.album_name,
       t.tag_subject_id   AS taggeduser_id,
       u.first_name       AS taggeduser_firstname,
       u.last_name        AS taggeduser_lastname,
       o.TagCount
  FROM Photo_Tag_Counts_Ordered o
  JOIN Tags  t ON o.photo_id = t.tag_photo_id
  JOIN Users u ON t.tag_subject_id = u.user_id
 WHERE o.rn <= :num
 ORDER BY o.TagCount DESC, o.photo_id ASC, taggeduser_id ASC;

-- -- SELECT sub.photo_id,
-- --        sub.album_id,
-- --        sub.photo_link,
-- --        sub.album_name,
--       --  sub.TagCount,
-- SELECT u.user_id       AS TaggedUser_ID,
--        u.first_name    AS TaggedUser_FirstName,
--        u.last_name     AS TaggedUser_LastName
-- FROM (
--    -- Subselect: read from the view, sort by TagCount DESC, Photo_ID ASC,
--    -- keep only top :num rows via ROWNUM
--    SELECT ptc.*
--    FROM Photo_Tag_Counts ptc
--    ORDER BY ptc.TagCount DESC, ptc.photo_id ASC
-- ) sub
-- JOIN Tags  t2  ON sub.photo_id = t2.tag_photo_id
-- JOIN Users u   ON t2.tag_subject_id  = u.user_id
-- WHERE ROWNUM <= :num   -- top :num by tag count
-- ORDER BY sub.TagCount DESC,
--          sub.photo_id ASC,
--          u.user_id ASC;
