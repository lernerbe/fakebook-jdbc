package project2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    The StudentFakebookOracle class is derived from the FakebookOracle class and implements
    the abstract query functions that investigate the database provided via the <connection>
    parameter of the constructor to discover specific information.
*/
public final class StudentFakebookOracle extends FakebookOracle {
    // [Constructor]
    // REQUIRES: <connection> is a valid JDBC connection
    public StudentFakebookOracle(Connection connection) {
        oracle = connection;
    }

    @Override
    // Query 0
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the total number of users for which a birth month is listed
    //        (B) Find the birth month in which the most users were born
    //        (C) Find the birth month in which the fewest users (at least one) were born
    //        (D) Find the IDs, first names, and last names of users born in the month
    //            identified in (B)
    //        (E) Find the IDs, first names, and last name of users born in the month
    //            identified in (C)
    //
    // This query is provided to you completed for reference. Below you will find the appropriate
    // mechanisms for opening up a statement, executing a query, walking through results, extracting
    // data, and more things that you will need to do for the remaining nine queries
    public BirthMonthInfo findMonthOfBirthInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            // Step 1
            // ------------
            // * Find the total number of users with birth month info
            // * Find the month in which the most users were born
            // * Find the month in which the fewest (but at least 1) users were born
            ResultSet rst = stmt.executeQuery(
                    "SELECT COUNT(*) AS Birthed, Month_of_Birth " + // select birth months and number of uses with that birth month
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth IS NOT NULL " + // for which a birth month is available
                            "GROUP BY Month_of_Birth " + // group into buckets by birth month
                            "ORDER BY Birthed DESC, Month_of_Birth ASC"); // sort by users born in that month, descending; break ties by birth month

            int mostMonth = 0;
            int leastMonth = 0;
            int total = 0;
            while (rst.next()) { // step through result rows/records one by one
                if (rst.isFirst()) { // if first record
                    mostMonth = rst.getInt(2); //   it is the month with the most
                }
                if (rst.isLast()) { // if last record
                    leastMonth = rst.getInt(2); //   it is the month with the least
                }
                total += rst.getInt(1); // get the first field's value as an integer
            }
            BirthMonthInfo info = new BirthMonthInfo(total, mostMonth, leastMonth);

            // Step 2
            // ------------
            // * Get the names of users born in the most popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + mostMonth + " " + // born in the most popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addMostPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 3
            // ------------
            // * Get the names of users born in the least popular birth month
            rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " + // select ID, first name, and last name
                            "FROM " + UsersTable + " " + // from all users
                            "WHERE Month_of_Birth = " + leastMonth + " " + // born in the least popular birth month
                            "ORDER BY User_ID"); // sort smaller IDs first

            while (rst.next()) {
                info.addLeastPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close(); // if you close the statement first, the result set gets closed automatically

            return info;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new BirthMonthInfo(-1, -1, -1);
        }
    }

  @Override
    // Query 1
    // -----------------------------------------------------------------------------------
    // GOALS: (A) The first name(s) with the most letters
    //        (B) The first name(s) with the fewest letters
    //        (C) The first name held by the most users
    //        (D) The number of users whose first name is that identified in (C)
    public FirstNameInfo findNameInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            FirstNameInfo info = new FirstNameInfo();
            
            // 1) Find the max len of first names
            ResultSet rst = stmt.executeQuery(
                "SELECT MAX(LENGTH(First_Name)) AS MaxLen " +
                "FROM " + UsersTable);
            int maxLen = 0;
            if (rst.next()) {
                maxLen = rst.getInt("MaxLen");
            }

            // find all distinct names with that max len
            rst = stmt.executeQuery(
                "SELECT DISTINCT First_Name " +
                "FROM " + UsersTable + " " +
                "WHERE LENGTH(First_Name) = " + maxLen + " " +
                "ORDER BY First_Name");

            while (rst.next()) {
                info.addLongName(rst.getString("First_Name"));
            }

            // 2) Find min len of first names
            rst = stmt.executeQuery(
                "SELECT MIN(LENGTH(First_Name)) AS MinLen " +
                "FROM " + UsersTable);
            int minLen = 0;
            if (rst.next()) {
                minLen = rst.getInt("MinLen");
            }

            // find all distinct names w that min len
            rst = stmt.executeQuery(
                "SELECT DISTINCT First_Name " +
                "FROM " + UsersTable + " " +
                "WHERE LENGTH(First_Name) = " + minLen + " " +
                "ORDER BY First_Name");
            while (rst.next()) {
                info.addShortName(rst.getString("First_Name"));
            }

            // 3) Find the most common first name(s) and count
            rst = stmt.executeQuery(
                "SELECT First_Name, COUNT(*) AS C " +
                "FROM " + UsersTable + " " +
                "GROUP BY First_Name " +
                "ORDER BY C DESC, First_Name");
            long maxCount = -1;
            boolean firstRow = true;
            while (rst.next()) {
                long thisCount = rst.getLong("C");
                if (firstRow) {
                    maxCount = thisCount;
                    firstRow = false;
                }
                if (thisCount == maxCount) {
                    info.addCommonName(rst.getString("First_Name"));
                } else {
                    break;
                }
            }
            rst.close();
            if (maxCount > 0) {
                info.setCommonNameCount(maxCount);
            }
            return info;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new FirstNameInfo();
        }
    }


    @Override
    // Query 2
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users without any friends
    //
    // Be careful! Remember that if two users are friends, the Friends table only contains
    // the one entry (U1, U2) where U1 < U2.
    public FakebookArrayList<UserInfo> lonelyUsers() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(15, "Abraham", "Lincoln");
                UserInfo u2 = new UserInfo(39, "Margaret", "Thatcher");
                results.add(u1);
                results.add(u2);
            */

        // join users and friends to find no friends users
        // ResultSet rst = stmt.executeQuery(
        //             "SELECT DISTINCT u.User_ID, u.First_Name, u.Last_Name " + 
        //                     "FROM " + UsersTable + " u " + 
        //                     "LEFT JOIN " + FriendsTable + " f " +
        //                     "ON u.User_ID = f.User1_ID OR u.User_ID = f.User2_ID " +
        //                     "WHERE f.User1_ID IS NULL AND f.User2_ID IS NULL " + 
        //                     "ORDER BY u.User_ID"); 


        ResultSet rst = stmt.executeQuery(
                    "SELECT User_ID, First_Name, Last_Name " +
                   "FROM " + UsersTable + " u " +
                   "WHERE NOT EXISTS ( " +
                   "    SELECT 1 FROM " + FriendsTable + " f " +
                   "    WHERE u.User_ID = f.User1_ID OR u.User_ID = f.User2_ID " +
                   ") " +
                   "ORDER BY User_ID");

        while (rst.next()) {
            results.add(new UserInfo(
                // user id, first name, last name
                rst.getLong(1), rst.getString(2), rst.getString(3)  
            ));
        }
        rst.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }   
        return results;
    }

    @Override
    // Query 3
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
    //            in their hometown (i.e. their current city and their hometown are different)
    public FakebookArrayList<UserInfo> liveAwayFromHome() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(9, "Meryl", "Streep");
                UserInfo u2 = new UserInfo(104, "Tom", "Hanks");
                results.add(u1);
                results.add(u2);
            */

        // join users, hometowncities, and currentcities to find where current & hometown are dif
        ResultSet rst = stmt.executeQuery(
                    "SELECT DISTINCT u.User_ID, u.First_Name, u.Last_Name " + 
                            "FROM " + UsersTable + " u " + 
                            "JOIN " + HometownCitiesTable + " h ON u.User_ID = h.User_ID " +
                            "JOIN " + CurrentCitiesTable + " c ON u.User_ID = c.User_ID " +
                            "WHERE h.Hometown_City_ID <> c.Current_City_ID " + 
                            "ORDER BY u.User_ID"); 

        while (rst.next()) {
            results.add(new UserInfo(
                // user id, first name, last name
                rst.getLong("User_ID"), rst.getString("First_Name"), rst.getString("Last_Name")  
            ));
        }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new FakebookArrayList<UserInfo>(", ");
        }
        // rst.close();
        return results;
    }

    @Override
    // Query 4
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    //            <num> photos with the most tagged users
    //        (B) For each photo identified in (A), find the IDs, first names, and last names
    //            of the users therein tagged
    public FakebookArrayList<TaggedPhotoInfo> findPhotosWithMostTags(int num) throws SQLException {
        FakebookArrayList<TaggedPhotoInfo> results = new FakebookArrayList<TaggedPhotoInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                PhotoInfo p = new PhotoInfo(80, 5, "www.photolink.net", "Winterfell S1");
                UserInfo u1 = new UserInfo(3901, "Jon", "Snow");
                UserInfo u2 = new UserInfo(3902, "Arya", "Stark");
                UserInfo u3 = new UserInfo(3903, "Sansa", "Stark");
                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                tp.addTaggedUser(u1);
                tp.addTaggedUser(u2);
                tp.addTaggedUser(u3);
                results.add(tp);
            */

            // (A) Find the IDs, links, and IDs and names of the containing album of the top
            // <num> photos with the most tagged users
            
           // Step 1: Get top `num` photos based on the number of tagged users
            ResultSet photoResult = stmt.executeQuery(
                "SELECT p.Photo_ID, p.Album_ID, p.Photo_Link, a.Album_Name, "
            + "       COUNT(t.tag_subject_id) AS TagCount "
            + "FROM " + PhotosTable + " p "
            + "JOIN " + AlbumsTable + " a ON p.Album_ID = a.Album_ID "
            + "JOIN " + TagsTable + " t ON p.Photo_ID = t.tag_photo_id "
            + "GROUP BY p.Photo_ID, p.Album_ID, p.Photo_Link, a.Album_Name "
            + "ORDER BY TagCount DESC, p.Photo_ID ASC "
            + "FETCH FIRST " + num + " ROWS ONLY"
            );

            ArrayList<Long> photoIDs = new ArrayList<>();
            ArrayList<TaggedPhotoInfo> photoInfos = new ArrayList<>();

            while (photoResult.next()) {
                long photoID = photoResult.getLong("Photo_ID");
                long albumID = photoResult.getLong("Album_ID");
                String photoLink = photoResult.getString("Photo_Link");
                String albumName = photoResult.getString("Album_Name");

                PhotoInfo photoInfo = new PhotoInfo(photoID, albumID, photoLink, albumName);
                TaggedPhotoInfo taggedPhoto = new TaggedPhotoInfo(photoInfo);

                photoIDs.add(photoID);
                photoInfos.add(taggedPhoto);
            }
            photoResult.close();

            // Step 2: Retrieve tagged users for each photo
            for (int i = 0; i < photoIDs.size(); i++) {
                long photoID = photoIDs.get(i);
                TaggedPhotoInfo taggedPhoto = photoInfos.get(i);

                ResultSet userResult = stmt.executeQuery(
                    "SELECT u.User_ID, u.First_Name, u.Last_Name "
                + "FROM " + TagsTable + " t "
                + "JOIN " + UsersTable + " u ON t.tag_subject_id = u.User_ID "
                + "WHERE t.tag_photo_id = " + photoID + " "
                + "ORDER BY u.User_ID ASC"
                );

                while (userResult.next()) {
                    UserInfo user = new UserInfo(
                        userResult.getLong("User_ID"),
                        userResult.getString("First_Name"),
                        userResult.getString("Last_Name")
                    );
                    taggedPhoto.addTaggedUser(user);
                }
                userResult.close();
                results.add(taggedPhoto);
            }
    } catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    return results;
}


    @Override
    // Query 5
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
    //            users in the top <num> pairs of users that meet each of the following
    //            criteria:
    //              (i) same gender
    //              (ii) tagged in at least one common photo
    //              (iii) difference in birth years is no more than <yearDiff>
    //              (iv) not friends
    //        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
    //            the containing album of each photo in which they are tagged together
    public FakebookArrayList<MatchPair> matchMaker(int num, int yearDiff) throws SQLException {
        FakebookArrayList<MatchPair> results = new FakebookArrayList<MatchPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(93103, "Romeo", "Montague");
                UserInfo u2 = new UserInfo(93113, "Juliet", "Capulet");
                MatchPair mp = new MatchPair(u1, 1597, u2, 1597);
                PhotoInfo p = new PhotoInfo(167, 309, "www.photolink.net", "Tragedy");
                mp.addSharedPhoto(p);
                results.add(mp);
            */

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 6
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    //            the top <num> pairs of users who are not friends but have a lot of
    //            common friends
    //        (B) For each pair identified in (A), find the IDs, first names, and last names
    //            of all the two users' common friends
    public FakebookArrayList<UsersPair> suggestFriends(int num) throws SQLException {
        FakebookArrayList<UsersPair> results = new FakebookArrayList<UsersPair>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(16, "The", "Hacker");
                UserInfo u2 = new UserInfo(80, "Dr.", "Marbles");
                UserInfo u3 = new UserInfo(192, "Digit", "Le Boid");
                UsersPair up = new UsersPair(u1, u2);
                up.addSharedFriend(u3);
                results.add(up);
            */

        // step 1: make a subquery for the bidirectional friends usind  CE. 

        ResultSet rst = stmt.executeQuery(
            "WITH bd_friends AS ( " 
          + "  SELECT user1_id AS user_id, user2_id AS friend_id FROM " + FriendsTable + " "
          + "  UNION ALL "
          + "  SELECT user2_id AS user_id, user1_id AS friend_id FROM " + FriendsTable + " "
          + ") "
          + "SELECT bd1.user_id AS User1_ID, u1.First_Name AS First1, u1.Last_Name AS Last1, "
          + "       bd2.user_id AS User2_ID, u2.First_Name AS First2, u2.Last_Name AS Last2, "
          + "       COUNT(*) AS MutualCount "
          + "FROM bd_friends bd1 "
          + "JOIN bd_friends bd2 "
          + "  ON bd1.friend_id = bd2.friend_id "
          + " AND bd1.user_id < bd2.user_id "  // each pair (u1,u2) only once
          + "JOIN " + UsersTable + " u1 ON bd1.user_id = u1.User_ID "
          + "JOIN " + UsersTable + " u2 ON bd2.user_id = u2.User_ID "
          + "LEFT JOIN " + FriendsTable + " f " 
          + "  ON ( (bd1.user_id = f.User1_ID AND bd2.user_id = f.User2_ID) "
          + "       OR (bd1.user_id = f.User2_ID AND bd2.user_id = f.User1_ID) ) "
          + "WHERE f.User1_ID IS NULL "         // not friends in either direction
          + "GROUP BY bd1.user_id, u1.First_Name, u1.Last_Name, "
          + "         bd2.user_id, u2.First_Name, u2.Last_Name "
          + "ORDER BY MutualCount DESC, bd1.user_id ASC, bd2.user_id ASC "
          + "FETCH FIRST " + num + " ROWS ONLY");

        ArrayList<Long> topUser1IDs = new ArrayList<>();
        ArrayList<Long> topUser2IDs = new ArrayList<>();
        ArrayList<UsersPair> userPairs = new ArrayList<>();

        while (rst.next()) {
            long u1ID = rst.getLong("User1_ID");
            long u2ID = rst.getLong("User2_ID");
            UserInfo u1 = new UserInfo(u1ID, rst.getString("First1"), rst.getString("Last1"));
            UserInfo u2 = new UserInfo(u2ID, rst.getString("First2"), rst.getString("Last2"));

            UsersPair pair = new UsersPair(u1, u2);
            userPairs.add(pair);

            topUser1IDs.add(u1ID);
            topUser2IDs.add(u2ID);
        }
        rst.close();

        // Step 2: For each pair, find the actual mutual friends.

        for (int i = 0; i < userPairs.size(); i++) {
            long u1ID = topUser1IDs.get(i);
            long u2ID = topUser2IDs.get(i);
            UsersPair pair = userPairs.get(i);

            ResultSet mutualRST = stmt.executeQuery(
                    "WITH bd_friends AS ( "
                + "  SELECT user1_id AS user_id, user2_id AS friend_id FROM " + FriendsTable + " "
                + "  UNION ALL "
                + "  SELECT user2_id AS user_id, user1_id AS friend_id FROM " + FriendsTable + " "
                + ") "
                + "SELECT u.User_ID, u.First_Name, u.Last_Name "
                + "FROM bd_friends f1 "
                + "JOIN bd_friends f2 ON f1.friend_id = f2.friend_id "
                + "JOIN " + UsersTable + " u ON f1.friend_id = u.User_ID "
                + "WHERE f1.user_id = " + u1ID
                + "  AND f2.user_id = " + u2ID
                + "ORDER BY u.User_ID ASC");


                while (mutualRST.next()) {
                    pair.addSharedFriend(new UserInfo(
                        mutualRST.getLong("User_ID"),
                        mutualRST.getString("First_Name"),
                        mutualRST.getString("Last_Name")
                    ));
                }
                mutualRST.close();
                // Finally add to results
                results.add(pair);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    @Override
    // Query 7
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the name of the state or states in which the most events are held
    //        (B) Find the number of events held in the states identified in (A)
    public EventStateInfo findEventStates() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                EventStateInfo info = new EventStateInfo(50);
                info.addState("Kentucky");
                info.addState("Hawaii");
                info.addState("New Hampshire");
                return info;
            */

        EventStateInfo info = new EventStateInfo(0); // initialize
        // combine citiestable (state name) & user_events
        // distinct?? - no 
        ResultSet rst = stmt.executeQuery(
                    "SELECT c.State_Name, COUNT(*) AS Num_Events " + 
                            "FROM " + EventsTable + " e " + 
                            "JOIN " + CitiesTable + " c " +
                            "ON c.City_ID = e.Event_City_ID " +
                            "GROUP BY c.State_Name " + 
                            "ORDER BY Num_Events DESC, c.State_Name ASC"); 

        if (rst.next()) {
            long Num_Most_Events = rst.getLong("Num_Events");
            info = new EventStateInfo(Num_Most_Events);
            // update based on rst
            
            // info.eventCount = Num_Most_Events; does not work

            do {
                if (rst.getLong("Num_Events") == Num_Most_Events) {
                    info.addState(rst.getString("State_Name"));
                    // adds if at most event num
                } else {
                    break; 
                }
                } while (rst.next());
            }

        rst.close();
        return info;
        
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new EventStateInfo(-1);
        }
    }

    @Override
    // Query 8
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
    //            with User ID <userID>
    //        (B) Find the ID, first name, and last name of the youngest friend of the user
    //            with User ID <userID>
    public AgeInfo findAgeInfo(long userID) throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo old = new UserInfo(12000000, "Galileo", "Galilei");
                UserInfo young = new UserInfo(80000000, "Neil", "deGrasse Tyson");
                return new AgeInfo(old, young);
            */
        
        UserInfo oldest = null;
        UserInfo youngest = null; // default
        //  need friends & users tables
        // find oldest first - asc order
        ResultSet rst = stmt.executeQuery(
                    "SELECT u.User_ID, u.First_Name, u.Last_Name " + 
                            "FROM " + UsersTable + " u " + 
                            "JOIN " + FriendsTable + " f " +
                            "ON u.User_ID = f.User1_ID OR u.User_ID = f.User2_ID " +
                            "WHERE (f.User1_ID = " + userID + " OR f.User2_ID = " + userID + ") " + 
                            "AND u.User_ID <> " + userID + " " + 
                            // makes sure friends but not same id
                            "ORDER BY u.Year_Of_Birth ASC, u.Month_Of_Birth ASC, u.Day_Of_Birth ASC, u.User_ID DESC " +
                            // orders by oldest 
                            "FETCH FIRST ROW ONLY");

        if (rst.next()) {
            oldest = new UserInfo(rst.getLong("User_ID"), rst.getString("First_Name"), rst.getString("Last_Name"));
        } // finish oldest

        // find youngest - desc order
        ResultSet rst2 = stmt.executeQuery(
                    "SELECT u.User_ID, u.First_Name, u.Last_Name " + 
                            "FROM " + UsersTable + " u " + 
                            "JOIN " + FriendsTable + " f " +
                            "ON u.User_ID = f.User1_ID OR u.User_ID = f.User2_ID " +
                            "WHERE (f.User1_ID = " + userID + " OR f.User2_ID = " + userID + ") " + 
                            "AND u.User_ID <> " + userID + " " + 
                            // makes sure friends but not same id
                            "ORDER BY u.Year_Of_Birth DESC, u.Month_Of_Birth DESC, u.Day_Of_Birth DESC, u.User_ID DESC " +
                            // orders by youngest
                            "FETCH FIRST ROW ONLY");

        if (rst2.next()) {
            youngest = new UserInfo(rst2.getLong("User_ID"), rst2.getString("First_Name"), rst2.getString("Last_Name"));
        } // finish youngest

        AgeInfo info = new AgeInfo(oldest, youngest); 
        rst.close();
        rst2.close();

        // make sure exists??
        return (youngest != null && oldest != null) ? info : null; 

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new AgeInfo(new UserInfo(-1, "ERROR", "ERROR"), new UserInfo(-1, "ERROR", "ERROR"));
        }
    }

    @Override
    // Query 9
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find all pairs of users that meet each of the following criteria
    //              (i) same last name
    //              (ii) same hometown
    //              (iii) are friends
    //              (iv) less than 10 birth years apart
    public FakebookArrayList<SiblingInfo> findPotentialSiblings() throws SQLException {
        FakebookArrayList<SiblingInfo> results = new FakebookArrayList<SiblingInfo>("\n");

        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll,
                FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(81023, "Kim", "Kardashian");
                UserInfo u2 = new UserInfo(17231, "Kourtney", "Kardashian");
                SiblingInfo si = new SiblingInfo(u1, u2);
                results.add(si);
            */

        // need users table - 2 for each pair
        // check for last name, hometown, friends, and age gap <10 
        // need friends table, need hometown table
        ResultSet rst = stmt.executeQuery(
                    "SELECT user1.User_ID AS ID1, user1.First_Name AS FName1, user1.Last_Name AS LName1, " + 
                            "user2.User_ID AS ID2, user2.First_Name AS FName2, user2.Last_Name AS LName2 " + 
                            //  id , first , last -- need for UserInfo
                            "FROM " + UsersTable + " user1 " + 
                            "JOIN " + UsersTable + " user2 ON user1.Last_Name = user2.Last_Name " + 
                            // same last name
                            "AND ABS(user2.Year_Of_Birth - user1.Year_Of_Birth) < 10 " +
                            // users, age gap check
                            "JOIN " + HometownCitiesTable + " home1 ON home1.User_ID = user1.User_ID " + 
                            "JOIN " + HometownCitiesTable + " home2 ON home2.User_ID = user2.User_ID " + 
                            "AND home1.Hometown_City_ID = home2.Hometown_City_ID " + 
                            // hometown same
                            "JOIN " + FriendsTable + " f " + 
                            "ON (user1.User_ID = f.User1_ID AND user2.User_ID = f.User2_ID) " + 
                            "OR (user1.User_ID = f.User2_ID AND user2.User_ID = f.User1_ID) " + 
                            // check for friends, find more efficient way?
                            "WHERE user1.User_ID < user2.User_ID " + 
                            "ORDER BY user1.User_ID ASC, user2.User_ID ASC");

        while (rst.next()) {
            // find first then second user infos, then add to results
            UserInfo first = new UserInfo(
                rst.getLong("ID1"), rst.getString("FName1"), rst.getString("LName1"));
            UserInfo second = new UserInfo(
                rst.getLong("ID2"), rst.getString("FName2"), rst.getString("LName2"));

            results.add(new SiblingInfo(first, second));

        } // while
        rst.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return results;
    }

    // Member Variables
    private Connection oracle;
    private final String UsersTable = FakebookOracleConstants.UsersTable;
    private final String CitiesTable = FakebookOracleConstants.CitiesTable;
    private final String FriendsTable = FakebookOracleConstants.FriendsTable;
    private final String CurrentCitiesTable = FakebookOracleConstants.CurrentCitiesTable;
    private final String HometownCitiesTable = FakebookOracleConstants.HometownCitiesTable;
    private final String ProgramsTable = FakebookOracleConstants.ProgramsTable;
    private final String EducationTable = FakebookOracleConstants.EducationTable;
    private final String EventsTable = FakebookOracleConstants.EventsTable;
    private final String AlbumsTable = FakebookOracleConstants.AlbumsTable;
    private final String PhotosTable = FakebookOracleConstants.PhotosTable;
    private final String TagsTable = FakebookOracleConstants.TagsTable;
}